package stupaq.lv2vhdl;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.primitives.UnsignedInteger;

import com.ni.labview.VIDump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import stupaq.MissingFeatureException;
import stupaq.SemanticException;
import stupaq.commons.IntegerMap;
import stupaq.labview.UID;
import stupaq.labview.VIPath;
import stupaq.labview.hierarchy.Bundler;
import stupaq.labview.hierarchy.Control;
import stupaq.labview.hierarchy.ControlCluster;
import stupaq.labview.hierarchy.FormulaNode;
import stupaq.labview.hierarchy.Panel;
import stupaq.labview.hierarchy.RingConstant;
import stupaq.labview.hierarchy.SubVI;
import stupaq.labview.hierarchy.Terminal;
import stupaq.labview.hierarchy.Unbundler;
import stupaq.labview.hierarchy.Wire;
import stupaq.labview.parsing.NoOpVisitor;
import stupaq.labview.parsing.PrintingVisitor;
import stupaq.labview.parsing.VIParser;
import stupaq.labview.scripting.tools.ControlStyle;
import stupaq.naming.ArchitectureName;
import stupaq.naming.ComponentName;
import stupaq.naming.EntityName;
import stupaq.naming.Identifier;
import stupaq.naming.InstantiableName;
import stupaq.project.VHDLProject;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.*;
import stupaq.vhdl93.visitor.PositionResettingVisitor;
import stupaq.vhdl93.visitor.TreeDumper;
import stupaq.vhdl93.visitor.VHDLTreeFormatter;

import static stupaq.SemanticException.semanticCheck;
import static stupaq.TranslationConventions.*;
import static stupaq.vhdl93.VHDL93Parser.tokenString;
import static stupaq.vhdl93.VHDL93ParserConstants.*;
import static stupaq.vhdl93.ast.ASTBuilders.*;

class DesignFileEmitter extends NoOpVisitor<Exception> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DesignFileEmitter.class);
  private final VHDLProject project;
  /** Context. */
  private UID rootPanel;
  private EndpointsResolver terminals;
  private Multimap<UID, Endpoint> wiresToEndpoints;
  private Map<Endpoint, Multiplexer> multiplexers;
  private Map<UID, Endpoint> clusteredControls;
  private int nextLabelNum;
  /** Results. */
  private context_clause entityContext, architectureContext;
  private NodeListOptional architectureDeclarations, entityDeclarations, concurrentStatements;
  private IntegerMap<interface_constant_declaration> generics;
  private IntegerMap<interface_signal_declaration> ports;

  public DesignFileEmitter(VHDLProject project) {
    this.project = project;
  }

  public void emit(VIPath path) throws Exception {
    LOGGER.debug("Emitting VHDL from VI: {}", path);
    InstantiableName element = Identifier.parse(path.getBaseName());
    LOGGER.debug("Target project element: {}", element);
    if (element instanceof ComponentName) {
      LOGGER.info("Component will be emitted together with accompanying architecture.");
    } else if (element instanceof ArchitectureName) {
      ArchitectureName name = (ArchitectureName) element;
      VIDump theVi = VIParser.parseVI(project.tools(), path);
      reset();
      VIParser.visitVI(theVi, PrintingVisitor.create());
      VIParser.visitVI(theVi, this);
      design_file file = new design_file(list(emitEntity(name.entity()), emitArchitecture(name)));
      Path destination = project.allocate(element, true);
      try (OutputStream output = new FileOutputStream(destination.toFile())) {
        file.accept(new PositionResettingVisitor());
        file.accept(new VHDLTreeFormatter());
        System.out.println("DESIGN UNIT FOR: " + path);
        file.accept(new TreeDumper(System.out));
        System.out.println();
        // TODO unit.accept(new TreeDumper(output));
      }
    }
  }

  private design_unit emitEntity(EntityName name) throws Exception {
    context_clause context =
        entityContext != null ? entityContext : new context_clause(listOptional());
    entity_identifier identifier = parser(name.identifier().toString()).entity_identifier();
    entity_header header = new entity_header(emitGenerics(), emitPorts());
    return new design_unit(context, new library_unit(choice(new primary_unit(choice(
        new entity_declaration(identifier, header, new entity_declarative_part(entityDeclarations),
            optional(), optional(), optional()))))));
  }

  private NodeOptional emitGenerics() throws Exception {
    if (generics.isEmpty()) {
      return optional();
    } else {
      NodeListOptional rest = listOptional();
      interface_constant_declaration first =
          split(generics.values(), tokenSupplier(SEMICOLON), rest);
      return optional(new formal_generic_clause(
          new generic_clause(new generic_list(new generic_interface_list(first, rest)))));
    }
  }

  private NodeOptional emitPorts() throws Exception {
    if (ports.isEmpty()) {
      return optional();
    } else {
      NodeListOptional rest = listOptional();
      interface_signal_declaration first = split(ports.values(), tokenSupplier(SEMICOLON), rest);
      return optional(new formal_port_clause(
          new port_clause(new port_list(new port_interface_list(first, rest)))));
    }
  }

  private design_unit emitArchitecture(ArchitectureName name) throws Exception {
    context_clause context =
        architectureContext != null ? architectureContext : new context_clause(listOptional());
    architecture_identifier identifier =
        parser(name.architecture().toString()).architecture_identifier();
    entity_name entity = parser(name.entity().identifier().toString()).entity_name();
    return new design_unit(context, new library_unit(choice(new secondary_unit(choice(
        new architecture_declaration(identifier, entity,
            new architecture_declarative_part(architectureDeclarations),
            new architecture_statement_part(concurrentStatements), optional(), optional()))))));
  }

  private void reset() {
    // Context.
    rootPanel = null;
    terminals = new EndpointsResolver();
    wiresToEndpoints = Multimaps.newListMultimap(Maps.<UID, Collection<Endpoint>>newHashMap(),
        new Supplier<List<Endpoint>>() {
          @Override
          public List<Endpoint> get() {
            return Lists.newArrayList();
          }
        });
    multiplexers = Maps.newHashMap();
    clusteredControls = null;
    nextLabelNum = 0;
    // Results.
    entityContext = null;
    architectureContext = null;
    concurrentStatements = listOptional();
    architectureDeclarations = listOptional();
    entityDeclarations = listOptional();
    generics = new IntegerMap<>();
    ports = new IntegerMap<>();
  }

  private static VHDL93Parser parser(String string) {
    LOGGER.trace("Parsing: {}", string);
    return new VHDL93Parser(new StringReader(string));
  }

  @Override
  public Iterable<String> parsersOrder() {
    return Arrays.asList(Panel.XML_NAME, Terminal.XML_NAME, Wire.XML_NAME, FormulaNode.XML_NAME,
        Bundler.XML_NAME, Unbundler.XML_NAME, ControlCluster.XML_NAME, Control.NUMERIC_XML_NAME,
        RingConstant.XML_NAME, SubVI.XML_NAME);
  }

  @Override
  public void Panel(Optional<UID> ownerUID, UID uid) {
    if (!ownerUID.isPresent()) {
      Verify.verify(rootPanel == null);
      rootPanel = uid;
    }
  }

  @Override
  public void Terminal(UID ownerUID, UID uid, UID wireUID, boolean isSource, String name) {
    Endpoint terminal = new Endpoint(uid, isSource, name);
    terminals.put(uid, terminal);
    wiresToEndpoints.put(wireUID, terminal);
  }

  @Override
  public void Wire(UID ownerUID, UID uid, Optional<String> label) throws Exception {
    // We know that there will be no more terminals.
    Collection<Endpoint> terms = wiresToEndpoints.removeAll(uid);
    for (Endpoint term1 : terms) {
      for (Endpoint term2 : terms) {
        if (term1.isSource() ^ term2.isSource()) {
          term1.addConnected(term2);
        }
        // Otherwise do nothing.
        // Note that this can happen for single source and multiple sinks,
        // as wires in LV are undirected and the graph itself is a multi-graph.
      }
    }
    if (label.isPresent()) {
      for (Endpoint terminal : terms) {
        terminal.value(label.get());
      }
    }
  }

  @Override
  public void FormulaNode(UID ownerUID, UID uid, String expression, Optional<String> label,
      List<UID> termUIDs) throws Exception {
    final VHDL93Parser contentParser = parser(expression);
    if (label.equals(ENTITY_CONTEXT)) {
      entityContext = contentParser.context_clause();
    } else if (label.equals(ARCHITECTURE_CONTEXT)) {
      architectureContext = contentParser.context_clause();
    } else if (label.equals(ARCHITECTURE_EXTRA_DECLARATIONS)) {
      NodeListOptional extra = contentParser.architecture_declarative_part().nodeListOptional;
      architectureDeclarations.nodes.addAll(extra.nodes);
    } else if (label.equals(ARCHITECTURE_EXTRA_STATEMENTS)) {
      NodeListOptional extra = contentParser.architecture_statement_part().nodeListOptional;
      concurrentStatements.nodes.addAll(extra.nodes);
    } else if (label.equals(PROCESS_STATEMENT)) {
      concurrent_statement process = contentParser.concurrent_statement();
      semanticCheck(process.nodeChoice.choice instanceof process_statement,
          "Statement is not a process declaration contrary to what label claims.");
      concurrentStatements.nodes.add(process);
    } else {
      boolean lvalue = false, rvalue = false;
      for (UID term : termUIDs) {
        Endpoint terminal = terminals.get(term);
        Verify.verifyNotNull(terminal);
        String param = terminal.name();
        lvalue |= param.equals(LVALUE_PARAMETER);
        rvalue |= param.equals(RVALUE_PARAMETER);
        if (lvalue) {
          semanticCheck(!terminal.isSource(), "L-value must be data sink.");
        } else if (rvalue) {
          semanticCheck(terminal.isSource(), "R-value must be data source.");
        }
        if (lvalue || rvalue) {
          // This way we set the value in actual destination.
          // The terminal is just a reference to all receivers of the value in the formula.
          for (Endpoint connected : terminal) {
            connected.valueOverride(expression);
          }
        }
      }
      semanticCheck(!lvalue || !rvalue, "Expression cannot be both l- and r-value.");
      if (!lvalue && !rvalue) {
        // It must be a concurrent statement then...
        concurrentStatements.nodes.add(contentParser.concurrent_statement());
      } else {
        contentParser.expression();
      }
    }
    contentParser.eof();
  }

  @Override
  public void Bundler(UID ownerUID, UID uid, UID outputUIDs, List<UID> inputUIDs) {
    Endpoint terminal = terminals.get(outputUIDs);
    Verify.verifyNotNull(terminal);
    multiplexers.put(terminal, Multiplexer.create(terminals, inputUIDs));
  }

  @Override
  public void Unbundler(UID ownerUID, UID uid, UID inputUID, List<UID> outputUIDs) {
    Endpoint terminal = terminals.get(inputUID);
    Verify.verifyNotNull(terminal);
    multiplexers.put(terminal, Multiplexer.create(terminals, outputUIDs));
  }

  @Override
  public void ControlCluster(UID ownerUID, UID uid, Optional<String> label, UID terminalUID,
      boolean isIndicator, int controlIndex, List<UID> controlUIDs) {
    if (clusteredControls == null) {
      clusteredControls = Maps.newHashMap();
    }
    for (Endpoint other : terminals.get(terminalUID)) {
      Multiplexer multiplexer = multiplexers.get(other);
      if (multiplexer != null) {
        semanticCheck(multiplexer.size() == controlUIDs.size(),
            "Different size of (un)bundler and clustered control.");
        for (int i = 0, n = controlUIDs.size(); i < n; ++i) {
          UID control = controlUIDs.get(i);
          Endpoint endpoint = multiplexer.get(i);
          clusteredControls.put(control, endpoint);
        }
      }
    }
  }

  @Override
  public void Control(UID ownerUID, UID uid, Optional<String> label, UID terminalUID,
      boolean isIndicator, ControlStyle style, int controlIndex, String description)
      throws Exception {
    Verify.verifyNotNull(rootPanel);
    if (clusteredControls != null) {
      try {
        controlIndex = UnsignedInteger.valueOf(description.trim()).intValue();
      } catch (NumberFormatException e) {
        throw new SemanticException(
            "Control description: %s does not contain port or generic index.", description);
      }
    } else {
      semanticCheck(rootPanel.equals(ownerUID),
          "VI is not clustered, but some control has owner other than front panel.");
    }
    semanticCheck(label.isPresent(), "Missing control label (should contain port declaration).");
    String declaration = label.get().trim();
    VHDL93Parser labelParser = parser(declaration);
    identifier signal;
    if (style == ControlStyle.NUMERIC_I32) {
      // This is a generic.
      interface_constant_declaration generic = labelParser.interface_constant_declaration();
      semanticCheck(!generic.identifier_list.nodeListOptional.present(),
          "Multiple identifiers in generic declaration.");
      generics.put(controlIndex, generic);
      signal = generic.identifier_list.identifier;
    } else if (style == ControlStyle.NUMERIC_DBL) {
      // This is a port.
      interface_signal_declaration port = labelParser.interface_signal_declaration();
      semanticCheck(!port.identifier_list.nodeListOptional.present(),
          "Multiple identifiers in port declaration.");
      ports.put(controlIndex, port);
      signal = port.identifier_list.identifier;
    } else {
      throw new SemanticException("Control style not recognised: %s", style);
    }
    labelParser.eof();
    Iterable<Endpoint> connected;
    if (clusteredControls != null) {
      Endpoint virtual = clusteredControls.get(uid);
      connected = Iterables.concat(virtual);
    } else {
      connected = terminals.get(terminalUID);
    }
    // Populate value through all connected wires.
    // If conflict found, fallback to existing one.
    for (Endpoint term : connected) {
      term.valueIfEmpty(signal.representation());
    }
  }

  @Override
  public void RingConstant(UID owner, UID uid, Optional<String> label, UID terminalUID,
      Map<String, Object> stringsAndValues) throws Exception {
    Verify.verify(!stringsAndValues.isEmpty());
    String constantString = stringsAndValues.keySet().iterator().next();
    String valueString;
    if (label.isPresent()) {
      VHDL93Parser parser =
          parser(label.get() + tokenString(ASSIGN) + constantString + tokenString(SEMICOLON));
      constant_declaration constant = parser.constant_declaration();
      parser.eof();
      architectureDeclarations.addNode(new block_declarative_item(choice(constant)));
      identifier_list identifiers = constant.identifier_list;
      semanticCheck(!identifiers.nodeListOptional.present(),
          "Multiple identifiers in constant declaration.");
      valueString = identifiers.representation();
    } else {
      valueString = constantString;
    }
    Endpoint terminal = terminals.get(terminalUID);
    Verify.verify(terminal.isSource());
    // Set the value of all connected sinks.
    for (Endpoint connected : terminal) {
      Verify.verify(!connected.isSource());
      connected.valueIfEmpty(valueString);
    }
  }

  @Override
  public void SubVI(UID owner, UID uid, List<UID> termUIDs, VIPath viPath, String description)
      throws Exception {
    InstantiableName element = Identifier.parse(viPath.getBaseName());
    instantiated_unit unit;
    if (element instanceof ComponentName) {
      ComponentName name = (ComponentName) element;
      // Note that given information in the terminal name property,
      // we need not to read the VI itself.
      // On the other hand it is so much more convenient to use existing logic.
      // TODO emit block declarative item
      unit = parser(tokenString(COMPONENT) + ' ' + name.component().toString()).instantiated_unit();
    } else if (element instanceof ArchitectureName) {
      ArchitectureName name = (ArchitectureName) element;
      unit = parser(tokenString(ENTITY) + ' ' + name.toString()).instantiated_unit();
    } else {
      throw new VerifyException("Unknown instantiable name.");
    }
    // TODO assuming we do not have the bundling VI
    Iterable<Endpoint> endpoints =
        FluentIterable.from(termUIDs).transform(new Function<UID, Endpoint>() {
          @Override
          public Endpoint apply(UID uid) {
            return terminals.get(uid);
          }
        }).filter(new Predicate<Endpoint>() {
          @Override
          public boolean apply(Endpoint endpoint) {
            return !endpoint.name().isEmpty();
          }
        });
    // TODO end of assumptions
    List<named_association_element> generics = Lists.newArrayList(), ports = Lists.newArrayList();
    for (Endpoint terminal : endpoints) {
      actual_part actual = new actual_part(
          choice(terminal.hasValue() ? terminal.value() : new actual_part_open()));
      VHDL93Parser parser = parser(terminal.name());
      Node node = parser.interface_declaration().nodeChoice.choice;
      parser.eof();
      if (node instanceof interface_constant_declaration) {
        interface_constant_declaration generic = (interface_constant_declaration) node;
        semanticCheck(generic.nodeOptional.present(), "Missing signal/constant specifier.");
        formal_part formal = new formal_part(generic.identifier_list.identifier);
        generics.add(new named_association_element(formal, actual));
      } else if (node instanceof interface_signal_declaration) {
        interface_signal_declaration port = (interface_signal_declaration) node;
        semanticCheck(port.nodeOptional.present(), "Missing signal/constant specifier.");
        formal_part formal = new formal_part(port.identifier_list.identifier);
        ports.add(new named_association_element(formal, actual));
      } else {
        throw new MissingFeatureException("Interface element of specified type is not supported.");
      }
    }
    VHDL93Parser parser = parser(description.isEmpty() ? "label" + ++nextLabelNum : description);
    instantiation_label instantiationLabel = parser.instantiation_label();
    parser.eof();
    NodeOptional genericAspect = generics.isEmpty() ? optional()
        : optional(new generic_map_aspect(emitAssociationList(generics)));
    NodeOptional portAspect =
        ports.isEmpty() ? optional() : optional(new port_map_aspect(emitAssociationList(ports)));
    concurrentStatements.addNode(
        new component_instantiation_statement(instantiationLabel, unit, genericAspect, portAspect));
  }

  private static association_list emitAssociationList(List<named_association_element> elements) {
    NodeListOptional rest = listOptional();
    named_association_element first = split(elements, tokenSupplier(COMMA), rest);
    return new association_list(choice(new named_association_list(first, rest)));
  }
}
