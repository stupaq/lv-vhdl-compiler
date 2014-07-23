package stupaq.translation.lv2vhdl;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.ni.labview.VIDump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import stupaq.Configuration;
import stupaq.MissingFeatureException;
import stupaq.SemanticException;
import stupaq.commons.IntegerMap;
import stupaq.labview.UID;
import stupaq.labview.VIPath;
import stupaq.labview.hierarchy.Bundler;
import stupaq.labview.hierarchy.Control;
import stupaq.labview.hierarchy.ControlCluster;
import stupaq.labview.hierarchy.FormulaNode;
import stupaq.labview.hierarchy.RingConstant;
import stupaq.labview.hierarchy.SubVI;
import stupaq.labview.hierarchy.Terminal;
import stupaq.labview.hierarchy.Tunnel;
import stupaq.labview.hierarchy.Unbundler;
import stupaq.labview.hierarchy.Wire;
import stupaq.labview.parsing.MultiplexerVisitor;
import stupaq.labview.parsing.NoOpVisitor;
import stupaq.labview.parsing.TracingVisitor;
import stupaq.labview.parsing.VIParser;
import stupaq.labview.scripting.tools.ControlStyle;
import stupaq.translation.naming.ArchitectureName;
import stupaq.translation.naming.ComponentName;
import stupaq.translation.naming.Identifier;
import stupaq.translation.naming.InstantiableName;
import stupaq.translation.project.VHDLProject;
import stupaq.vhdl93.ast.*;

import static com.google.common.collect.FluentIterable.from;
import static stupaq.SemanticException.semanticCheck;
import static stupaq.TranslationConventions.*;
import static stupaq.translation.lv2vhdl.VHDL93PartialParser.parser;
import static stupaq.vhdl93.VHDL93Parser.tokenString;
import static stupaq.vhdl93.VHDL93ParserConstants.*;
import static stupaq.vhdl93.builders.ASTBuilders.*;

class ArchitectureDefinition extends NoOpVisitor<Exception> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ArchitectureDefinition.class);
  private static final boolean FOLLOW_DEPENDENCIES = Configuration.getDependenciesFollow();
  private final EndpointsMap terminals = new EndpointsMap();
  private final UniversalVIReader universalVI = new UniversalVIReader(terminals);
  private final SignalsInferenceRules inferenceRules = new SignalsInferenceRules();
  private final VHDLProject project;
  private final InterfaceDeclarationCache interfaceCache;
  private final Set<ComponentName> emittedComponents = Sets.newHashSet();
  private final NodeListOptional architectureDeclarations = new NodeListOptional();
  private final NodeListOptional concurrentStatements = new NodeListOptional();
  private int nextLabelNum = 0;
  private context_clause context;

  public ArchitectureDefinition(VHDLProject project, VIDump theVi) throws Exception {
    this.project = project;
    this.interfaceCache = new InterfaceDeclarationCache(project);
    // Prepare all visitors using common order and run them.
    MultiplexerVisitor multiplexer =
        new MultiplexerVisitor(Terminal.XML_NAME, Wire.XML_NAME, Tunnel.XML_NAME,
            FormulaNode.XML_NAME, Bundler.XML_NAME, Unbundler.XML_NAME, ControlCluster.XML_NAME,
            Control.NUMERIC_XML_NAME, RingConstant.XML_NAME, SubVI.XML_NAME);
    multiplexer.addVisitor(TracingVisitor.create());
    multiplexer.addVisitor(new EndpointWiringRules(terminals));
    multiplexer.addVisitor(universalVI);
    multiplexer.addVisitor(this);
    VIParser.visitVI(theVi, multiplexer);
  }

  private static association_list emitAssociationList(List<named_association_element> elements) {
    NodeListOptional rest = listOptional();
    named_association_element first = split(elements, tokenSupplier(COMMA), rest);
    return new association_list(choice(new named_association_list(first, rest)));
  }

  public Optional<context_clause> getContext() {
    return Optional.fromNullable(context);
  }

  public design_unit emitAsArchitecture(ArchitectureName name, InterfaceDeclaration declaration)
      throws Exception {
    context_clause context =
        getContext().or(declaration.getContext()).or(new context_clause(listOptional()));
    architecture_identifier identifier =
        parser(name.architecture().toString()).architecture_identifier();
    entity_name entity = parser(name.entity().entity().toString()).entity_name();
    architecture_declaration definition = new architecture_declaration(identifier, entity,
        new architecture_declarative_part(architectureDeclarations),
        new architecture_statement_part(concurrentStatements), optional(), optional());
    return new design_unit(context,
        new library_unit(choice(new secondary_unit(choice(definition)))));
  }

  @Override
  public Iterable<String> parsersOrder() {
    throw new VerifyException();
  }

  @Override
  public void FormulaNode(UID ownerUID, UID uid, String expression, Optional<String> label,
      List<UID> termUIDs) throws Exception {
    VHDL93PartialParser parser = parser(expression);
    if (label.equals(ENTITY_CONTEXT)) {
      // We are not interested in this.
      return;
    } else if (label.equals(ENTITY_EXTRA_DECLARATIONS)) {
      // We are not interested in this.
      return;
    } else if (label.equals(ARCHITECTURE_CONTEXT)) {
      context = parser.context_clause();
    } else if (label.equals(ARCHITECTURE_EXTRA_DECLARATIONS)) {
      NodeListOptional extra = parser.architecture_declarative_part().nodeListOptional;
      architectureDeclarations.nodes.addAll(extra.nodes);
    } else if (label.equals(ARCHITECTURE_EXTRA_STATEMENTS)) {
      NodeListOptional extra = parser.architecture_statement_part().nodeListOptional;
      concurrentStatements.nodes.addAll(extra.nodes);
    } else if (label.equals(PROCESS_STATEMENT)) {
      concurrent_statement process = parser.concurrent_statement();
      semanticCheck(process.nodeChoice.choice instanceof process_statement,
          "Statement is not a process declaration contrary to what label claims.");
      concurrentStatements.nodes.add(process);
      for (UID term : termUIDs) {
        Endpoint terminal = terminals.get(term);
        String param = terminal.name();
        // Set parameter name as a fallback. This one should have very low priority.
        for (Endpoint connected : terminal.connected()) {
          connected.valueIfEmpty(param);
        }
      }
    } else {
      boolean lvalue = false, rvalue = false;
      for (UID term : termUIDs) {
        Endpoint terminal = terminals.get(term);
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
          for (Endpoint connected : terminal.connected()) {
            connected.valueOverride(expression);
          }
        } else {
          // Set parameter name as a fallback. This one should have very low priority.
          for (Endpoint connected : terminal.connected()) {
            connected.valueIfEmpty(param);
          }
        }
      }
      semanticCheck(!lvalue || !rvalue, "Expression cannot be both l- and r-value.");
      if (!lvalue && !rvalue) {
        // It must be a concurrent statement then...
        concurrentStatements.nodes.add(parser.concurrent_statement());
      }
    }
  }

  @Override
  public void Control(UID ownerUID, UID uid, Optional<String> label, UID terminalUID,
      boolean isIndicator, ControlStyle style, String description) throws Exception {
    semanticCheck(label.isPresent(), "Missing control label (should contain port declaration).");
    String declaration = label.get().trim();
    VHDL93PartialParser labelParser = parser(declaration);
    identifier signal;
    if (style == ControlStyle.NUMERIC_I32) {
      // This is a generic.
      interface_constant_declaration generic = labelParser.interface_constant_declaration();
      semanticCheck(!generic.identifier_list.nodeListOptional.present(),
          "Multiple identifiers in generic declaration.");
      signal = generic.identifier_list.identifier;
    } else if (style == ControlStyle.NUMERIC_DBL) {
      // This is a port.
      interface_signal_declaration port = labelParser.interface_signal_declaration();
      semanticCheck(!port.identifier_list.nodeListOptional.present(),
          "Multiple identifiers in port declaration.");
      signal = port.identifier_list.identifier;
    } else {
      throw new SemanticException("Control style not recognised: %s", style);
    }
    Iterable<Endpoint> connected = universalVI.findMultiplexedConnections(uid);
    if (connected == null) {
      connected = terminals.get(terminalUID).connected();
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
      VHDL93PartialParser parser =
          parser(label.get() + tokenString(ASSIGN) + constantString + tokenString(SEMICOLON));
      constant_declaration constant = parser.constant_declaration();
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
    for (Endpoint connected : terminal.connected()) {
      Verify.verify(!connected.isSource());
      connected.valueIfEmpty(valueString);
    }
  }

  @Override
  public void SubVI(UID owner, UID uid, List<UID> termUIDs, VIPath viPath, String description)
      throws Exception {
    InterfaceDeclaration declaration = interfaceCache.get(viPath);
    InstantiableName element = Identifier.parse(viPath.getBaseName());
    instantiated_unit unit;
    if (element instanceof ComponentName) {
      ComponentName name = (ComponentName) element;
      // Emit block declarative item.
      if (!emittedComponents.contains(name)) {
        emittedComponents.add(name);
        component_declaration component = declaration.emitAsComponent(name);
        architectureDeclarations.addNode(new block_declarative_item(choice(component)));
      }
      unit = parser(tokenString(COMPONENT) + ' ' + name.component().toString()).instantiated_unit();
    } else if (element instanceof ArchitectureName) {
      ArchitectureName name = (ArchitectureName) element;
      // Schedule for processing.
      if (FOLLOW_DEPENDENCIES) {
        project.add(viPath);
      }
      unit = parser(tokenString(ENTITY) + ' ' + name.toString()).instantiated_unit();
    } else {
      throw new VerifyException("Unknown instantiable name.");
    }
    // Determine whether this is a clustered VI.
    // Prepare all endpoints to process.
    Iterable<Endpoint> endpoints;
    if (declaration.isClustered()) {
      LOGGER.debug("Clustered SubVI: {}.", viPath);
      Verify.verify(termUIDs.size() == 2);
      Endpoint input = terminals.get(termUIDs.get(INPUTS_CONN_INDEX));
      Multiplexer bundler = universalVI.findMultiplexer(input.onlyConnected());
      Endpoint output = terminals.get(termUIDs.get(OUTPUTS_CONN_INDEX));
      Multiplexer unbundler = universalVI.findMultiplexer(output.onlyConnected());
      // To resolve (un)bundler terminal names, we will set them manually from
      // interface definition taken from the appropriate VI.
      IntegerMap<String> newNames = declaration.clusteredNames(INPUTS_CONN_INDEX);
      for (int index = 0; index < bundler.size(); ++index) {
        bundler.get(index).rename(newNames.getPresent(index));
      }
      endpoints = Iterables.concat(bundler, unbundler);
    } else {
      endpoints = from(termUIDs).transform(new Function<UID, Endpoint>() {
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
    }
    // Split them into generic and port lists and assign values.
    List<named_association_element> generics = Lists.newArrayList(), ports = Lists.newArrayList();
    for (Endpoint terminal : endpoints) {
      VHDL93PartialParser parser = parser(terminal.name());
      Node node = parser.interface_declaration().nodeChoice.choice;
      if (node instanceof interface_constant_declaration) {
        // Generics without assigned value should be left alone.
        // Assigning "open" makes no sense.
        if (terminal.hasValue()) {
          interface_constant_declaration generic = (interface_constant_declaration) node;
          semanticCheck(generic.nodeOptional.present(), "Missing signal/constant specifier.");
          formal_part formal = new formal_part(generic.identifier_list.identifier);
          actual_part actual = new actual_part(choice(terminal.value()));
          generics.add(new named_association_element(formal, actual));
        }
      } else if (node instanceof interface_signal_declaration) {
        interface_signal_declaration port = (interface_signal_declaration) node;
        semanticCheck(port.nodeOptional.present(), "Missing signal/constant specifier.");
        formal_part formal = new formal_part(port.identifier_list.identifier);
        actual_part actual;
        if (terminal.hasValue()) {
          actual = new actual_part(choice(terminal.value()));
        } else {
          actual = new actual_part(
              choice(inferenceRules.inferExpression(terminal).or(new actual_part_open())));
        }
        ports.add(new named_association_element(formal, actual));
      } else {
        throw new MissingFeatureException("Interface element of specified type is not supported.");
      }
    }
    VHDL93PartialParser parser =
        parser(description.isEmpty() ? "label" + ++nextLabelNum : description);
    instantiation_label instantiationLabel = parser.instantiation_label();
    NodeOptional genericAspect = generics.isEmpty() ? optional()
        : optional(new generic_map_aspect(emitAssociationList(generics)));
    NodeOptional portAspect =
        ports.isEmpty() ? optional() : optional(new port_map_aspect(emitAssociationList(ports)));
    concurrentStatements.addNode(
        new component_instantiation_statement(instantiationLabel, unit, genericAspect, portAspect));
  }
}
