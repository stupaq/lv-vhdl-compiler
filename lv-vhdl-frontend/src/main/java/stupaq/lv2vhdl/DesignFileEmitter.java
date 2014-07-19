package stupaq.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Verify;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import com.ni.labview.VIDump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import stupaq.SemanticException;
import stupaq.labview.UID;
import stupaq.labview.VIPath;
import stupaq.labview.parsing.PrintingVisitor;
import stupaq.labview.parsing.VIElementsVisitor;
import stupaq.labview.parsing.VIParser;
import stupaq.labview.scripting.tools.ControlStyle;
import stupaq.lv2vhdl.SinkTerminals.Sink;
import stupaq.lv2vhdl.SourceTerminals.Source;
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
import static stupaq.vhdl93.VHDL93ParserConstants.ASSIGN;
import static stupaq.vhdl93.VHDL93ParserConstants.SEMICOLON;
import static stupaq.vhdl93.ast.ASTBuilders.*;

class DesignFileEmitter implements VIElementsVisitor<Exception> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DesignFileEmitter.class);
  private final VHDLProject project;
  /** Context. */
  private UID rootPanel;
  private Map<UID, Endpoint> terminals;
  private Multimap<UID, Endpoint> wiresToEndpoints;
  private boolean clustered;
  /** Results. */
  private context_clause entityContext, architectureContext;
  private NodeListOptional architectureDeclarations, entityDeclarations, concurrentStatements;
  private List<interface_constant_declaration> generics;
  private List<interface_signal_declaration> ports;

  public DesignFileEmitter(VHDLProject project) {
    this.project = project;
  }

  public void emit(VIPath path) throws Exception {
    LOGGER.debug("Emitting VHDL from VI: {}", path);
    InstantiableName element = Identifier.parse(path.getBaseName());
    LOGGER.debug("Target project element: {}", element);
    if (element instanceof ComponentName) {
      LOGGER.info("Component will be emitted together with accompanying architecture.");
    } else if (element instanceof EntityName) {
      LOGGER.info("Entity will be emitted together with any of accompanying architectures.");
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
      interface_constant_declaration first = split(generics, tokenSupplier(SEMICOLON), rest);
      return optional(new formal_generic_clause(
          new generic_clause(new generic_list(new generic_interface_list(first, rest)))));
    }
  }

  private NodeOptional emitPorts() throws Exception {
    if (ports.isEmpty()) {
      return optional();
    } else {
      NodeListOptional rest = listOptional();
      interface_signal_declaration first = split(ports, tokenSupplier(SEMICOLON), rest);
      return optional(new formal_port_clause(
          new port_clause(new port_list(new port_interface_list(first, rest)))));
    }
  }

  private design_unit emitArchitecture(ArchitectureName name) throws Exception {
    context_clause context =
        architectureContext != null ? architectureContext : new context_clause(listOptional());
    architecture_identifier identifier =
        parser(name.identifier().toString()).architecture_identifier();
    entity_name entity = parser(name.entity().identifier().toString()).entity_name();
    return new design_unit(context, new library_unit(choice(new secondary_unit(choice(
        new architecture_declaration(identifier, entity,
            new architecture_declarative_part(architectureDeclarations),
            new architecture_statement_part(concurrentStatements), optional(), optional()))))));
  }

  private void reset() {
    // Context.
    rootPanel = null;
    terminals = Maps.newHashMap();
    wiresToEndpoints = Multimaps.newListMultimap(Maps.<UID, Collection<Endpoint>>newHashMap(),
        new Supplier<List<Endpoint>>() {
          @Override
          public List<Endpoint> get() {
            return Lists.newArrayList();
          }
        });
    clustered = false;
    // Results.
    entityContext = architectureContext = null;
    concurrentStatements = listOptional();
    architectureDeclarations = listOptional();
    entityDeclarations = listOptional();
    generics = Lists.newArrayList();
    ports = Lists.newArrayList();
  }

  private static VHDL93Parser parser(String string) {
    LOGGER.trace("Parsing: {}", string);
    return new VHDL93Parser(new StringReader(string));
  }

  @Override
  public void Diagram(Optional<UID> owner, UID uid) {
    // No need to maintain diagrams as of today.
  }

  @Override
  public void Panel(Optional<UID> owner, UID uid) {
    if (!owner.isPresent()) {
      Verify.verify(rootPanel == null);
      rootPanel = uid;
    }
  }

  @Override
  public void Terminal(UID owner, UID uid, UID wire, boolean isSource, String name) {
    Endpoint terminal = isSource ? new Source(name) : new Sink(name);
    terminals.put(uid, terminal);
    wiresToEndpoints.put(wire, terminal);
  }

  @Override
  public void Wire(UID owner, UID uid, Optional<String> label) throws Exception {
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
      VHDL93Parser labelParser = parser(label.get());
      expression parsed = labelParser.expression();
      labelParser.eof();
      for (Endpoint terminal : terms) {
        terminal.value(parsed);
      }
    }
  }

  @Override
  public void InlineCNode(UID owner, UID uid, String expression, Optional<String> label,
      List<UID> terms) {
    // This element is not currently in use.
  }

  @Override
  public void FormulaNode(UID owner, UID uid, String expression, Optional<String> label,
      List<UID> terms) throws Exception {
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
      expression parsed = null;
      for (UID term : terms) {
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
          if (parsed == null) {
            parsed = contentParser.expression();
          }
          // This way we set the value in actual destination.
          // The terminal is just a reference to all receivers of the value in the formula.
          for (Endpoint connected : terminal) {
            connected.valueOverride(parsed);
          }
        }
      }
      semanticCheck(!lvalue || !rvalue, "Expression cannot be both l- and r-value.");
      if (!lvalue && !rvalue) {
        Verify.verify(parsed == null);
        // It must be a concurrent statement then...
        concurrentStatements.nodes.add(contentParser.concurrent_statement());
      }
    }
    contentParser.eof();
  }

  @Override
  public void CompoundArithmetic(UID owner, UID uid, List<UID> terms) {
    // We look at these for error checking only.
    // TODO
  }

  @Override
  public void Bundler(UID owner, UID uid, List<UID> terms) {
    // TODO assuming we do not have the bundling VI
  }

  @Override
  public void Unbundler(UID owner, UID uid, List<UID> terms) {
    // TODO assuming we do not have the bundling VI
  }

  @Override
  public void ControlCluster(UID owner, UID uid, Optional<String> label, UID terminal,
      boolean isIndicator, int controlIndex, List<UID> controls) {
    clustered = true;
    // TODO assuming we do not have the bundling VI
  }

  @Override
  public void ControlArray(UID owner, UID uid, Optional<String> label, UID terminal,
      boolean isIndicator, int controlIndex) {
    // This should not even be in the VI.
  }

  @Override
  public void Control(UID owner, UID uid, Optional<String> label, UID terminal, boolean isIndicator,
      ControlStyle style, int controlIndex) throws Exception {
    Verify.verifyNotNull(rootPanel);
    // TODO assuming we do not have the bundling VI
    Verify.verify(rootPanel.equals(owner));
    semanticCheck(label.isPresent(), "Missing control label (should contain port declaration).");
    String declaration = label.get().trim();
    VHDL93Parser labelParser = parser(declaration);
    identifier signal;
    if (style == ControlStyle.NUMERIC_I32) {
      // This is a generic.
      interface_constant_declaration generic = labelParser.interface_constant_declaration();
      generics.add(generic);
      signal = generic.identifier_list.identifier;
    } else if (style == ControlStyle.NUMERIC_DBL) {
      // This is a port.
      interface_signal_declaration port = labelParser.interface_signal_declaration();
      ports.add(port);
      signal = port.identifier_list.identifier;
    } else {
      throw new SemanticException("Control style not recognised: %s", style);
    }
    labelParser.eof();
    expression value = parser(signal.representation()).expression();
    Endpoint endpoint = terminals.get(terminal);
    if (!endpoint.hasValue()) {
      // Populate value through all connected wires.
      // If conflict found, fallback to existing one.
      for (Endpoint connected : endpoint) {
        if (!connected.hasValue()) {
          connected.valueIfEmpty(value);
        }
      }
    }
  }

  @Override
  public void RingConstant(UID owner, UID uid, Optional<String> label, UID terminal,
      Map<String, Object> stringsAndValues) throws Exception {
    Verify.verify(!stringsAndValues.isEmpty());
    String valueString = stringsAndValues.keySet().iterator().next();
    VHDL93Parser parser;
    if (label.isPresent()) {
      parser = parser(label.get() + tokenString(ASSIGN) + valueString + tokenString(SEMICOLON));
      block_declarative_item constant = parser.block_declarative_item();
      semanticCheck(constant.nodeChoice.choice instanceof constant_declaration,
          "Expected constant declaration.");
    } else {
      parser = parser(valueString);
      expression value = parser.expression();
      Endpoint constantTerminal = terminals.get(terminal);
      Verify.verify(constantTerminal.isSource());
      // Set the value of all connected sinks.
      for (Endpoint connected : constantTerminal) {
        Verify.verify(!connected.isSource());
        connected.valueIfEmpty(value);
      }
    }
    parser.eof();
  }

  @Override
  public void SubVI(UID owner, UID uid, List<UID> terms, VIPath viPath) {
    // TODO assuming we do not have the bundling VI
    // TODO
  }
}
