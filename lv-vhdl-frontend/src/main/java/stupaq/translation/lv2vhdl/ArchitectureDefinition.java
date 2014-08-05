package stupaq.translation.lv2vhdl;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import stupaq.commons.IntegerMap;
import stupaq.labview.UID;
import stupaq.labview.VIPath;
import stupaq.labview.hierarchy.Control;
import stupaq.labview.hierarchy.ControlCluster;
import stupaq.labview.hierarchy.FormulaNode;
import stupaq.labview.hierarchy.RingConstant;
import stupaq.labview.hierarchy.SubVI;
import stupaq.labview.hierarchy.WhileLoop;
import stupaq.labview.hierarchy.Wire;
import stupaq.labview.scripting.tools.ControlStyle;
import stupaq.translation.Configuration;
import stupaq.translation.MissingFeatureException;
import stupaq.translation.SemanticException;
import stupaq.translation.lv2vhdl.inference.DeclarationInferenceRules;
import stupaq.translation.lv2vhdl.inference.ValueInferenceRules;
import stupaq.translation.lv2vhdl.miscellanea.DeclarationOrdering;
import stupaq.translation.lv2vhdl.miscellanea.FirstFewTokensOrdering;
import stupaq.translation.lv2vhdl.parsing.ParsedVI;
import stupaq.translation.lv2vhdl.parsing.VHDL93ParserPartial;
import stupaq.translation.lv2vhdl.parsing.VIElementsVisitor;
import stupaq.translation.lv2vhdl.wiring.Endpoint;
import stupaq.translation.lv2vhdl.wiring.EndpointsMap;
import stupaq.translation.lv2vhdl.wiring.Multiplexer;
import stupaq.translation.lv2vhdl.wiring.MultiplexersMap;
import stupaq.translation.naming.ArchitectureName;
import stupaq.translation.naming.ComponentName;
import stupaq.translation.naming.Identifier;
import stupaq.translation.naming.InstantiableName;
import stupaq.translation.project.LVProjectReader;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.ast.*;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.collect.FluentIterable.from;
import static stupaq.translation.SemanticException.semanticCheck;
import static stupaq.translation.TranslationConventions.INPUTS_CONN_INDEX;
import static stupaq.translation.TranslationConventions.OUTPUTS_CONN_INDEX;
import static stupaq.translation.lv2vhdl.parsing.VHDL93ParserPartial.Parsers.forString;
import static stupaq.vhdl93.VHDL93ParserConstants.*;
import static stupaq.vhdl93.VHDL93ParserTotal.tokenString;
import static stupaq.vhdl93.ast.Builders.*;

public class ArchitectureDefinition {
  private static final Logger LOGGER = LoggerFactory.getLogger(ArchitectureDefinition.class);
  private static final boolean FOLLOW_DEPENDENCIES = Configuration.getDependenciesFollow();
  private static final int STATEMENTS_SORTING_LOOKUP = 6;
  private final LVProjectReader project;
  private final InterfaceDeclarationCache interfaceCache;
  private final EndpointsMap endpoints;
  private final MultiplexersMap multiplexers;
  private final DeclarationInferenceRules declarationInference;
  private final NodeListOptional architectureDeclarations = new NodeListOptional();
  private final NodeListOptional concurrentStatements = new NodeListOptional();
  private context_clause context;

  public ArchitectureDefinition(InterfaceDeclarationCache interfaceCache, LVProjectReader project,
      ParsedVI theVi) throws Exception {
    this.project = project;
    this.interfaceCache = interfaceCache;
    // Collect information about endpoints and connections between them.
    endpoints = new EndpointsMap(theVi);
    // Resolve universal VI conventions.
    multiplexers = new MultiplexersMap(endpoints, theVi);
    // Prepare inference rules.
    declarationInference = new DeclarationInferenceRules(theVi);
    // Prepare all visitors using common order and run them.
    theVi.accept(new BuilderVisitor());
    architectureDeclarations.nodes.addAll(declarationInference.inferredDeclarations());
    Collections.sort(architectureDeclarations.nodes,
        new DeclarationOrdering(architectureDeclarations));
    Collections.sort(concurrentStatements.nodes,
        new FirstFewTokensOrdering(STATEMENTS_SORTING_LOOKUP));
  }

  private static association_list emitAssociationList(List<named_association_element> elements) {
    NodeListOptional rest = listOptional();
    named_association_element first = split(elements, tokenSupplier(COMMA), rest);
    return new association_list(choice(new named_association_list(first, rest)));
  }

  private static void setNamesAsFallback(Iterable<Endpoint> parameters) {
    for (Endpoint terminal : parameters) {
      String param = terminal.name();
      // Set parameter name as a fallback. This one should have very low priority.
      for (Endpoint connected : terminal.connected()) {
        connected.valueIfEmpty(param);
      }
    }
  }

  public design_unit emitAsArchitecture(ArchitectureName name) throws Exception {
    context_clause context = fromNullable(this.context).or(new context_clause(listOptional()));
    architecture_identifier identifier =
        forString(name.architecture().toString()).architecture_identifier();
    entity_name entity = forString(name.entity().entity().toString()).entity_name();
    architecture_declaration definition = new architecture_declaration(identifier, entity,
        new architecture_declarative_part(architectureDeclarations),
        new architecture_statement_part(concurrentStatements), optional(), optional());
    return new design_unit(context,
        new library_unit(choice(new secondary_unit(choice(definition)))));
  }

  private class BuilderVisitor extends VIElementsVisitor<Exception> {
    private final ValueInferenceRules valueInference = new ValueInferenceRules();
    private final Set<ComponentName> emittedComponents = Sets.newHashSet();
    private int nextLabelNum = 0;

    public BuilderVisitor() {
      super(endpoints);
    }

    @Override
    public Iterable<String> parsersOrder() {
      return Arrays.asList(Wire.XML_NAME, WhileLoop.XML_NAME, FormulaNode.XML_NAME,
          ControlCluster.XML_NAME, Control.NUMERIC_XML_NAME, RingConstant.XML_NAME, SubVI.XML_NAME);
    }

    @Override
    protected void WireWithSignalDeclaration(UID uid, String label,
        signal_declaration declaration) {
      architectureDeclarations.addNode(new block_declarative_item(choice(declaration)));
    }

    @Override
    protected void FormulaWithArchitectureContext(UID uid, String expression)
        throws ParseException {
      VHDL93ParserPartial parser = forString(expression);
      context = parser.context_clause();
    }

    @Override
    protected void FormulaWithArchitectureDeclarations(UID uid, String expression)
        throws ParseException {
      VHDL93ParserPartial parser = forString(expression);
      NodeListOptional extra = parser.architecture_declarative_part().nodeListOptional;
      architectureDeclarations.nodes.addAll(extra.nodes);
    }

    @Override
    protected void FormulaWithConcurrentStatements(UID uid, String expression,
        Iterable<Endpoint> parameters) throws ParseException {
      for (Endpoint param : parameters) {
        String valueString = param.name();
        // Set parameter name as a fallback. This one should have very low priority.
        for (Endpoint connected : param.connected()) {
          connected.valueIfEmpty(valueString);
        }
      }
      VHDL93ParserPartial parser = forString(expression);
      concurrentStatements.nodes.addAll(
          parser.architecture_statement_part().nodeListOptional.nodes);
    }

    @Override
    protected void FormulaWithProcessStatement(UID uid, String expression,
        Iterable<Endpoint> parameters) throws ParseException {
      VHDL93ParserPartial parser = forString(expression);
      concurrent_statement process = parser.concurrent_statement();
      semanticCheck(process.nodeChoice.choice instanceof process_statement, uid,
          "Statement is not a process declaration contrary to what label claims.");
      concurrentStatements.nodes.add(process);
      setNamesAsFallback(parameters);
    }

    @Override
    protected void FormulaWithLvalue(UID uid, String expression, Endpoint lvalue,
        Iterable<Endpoint> otherParameters) {
      semanticCheck(!lvalue.isSource(), uid, "L-value must be data sink.");
      // This way we set the value in actual destination.
      for (Endpoint connected : lvalue.connected()) {
        connected.valueOverride(expression);
      }
      setNamesAsFallback(otherParameters);
    }

    @Override
    protected void FormulaWithRvalue(UID uid, String expression, Endpoint rvalue,
        Iterable<Endpoint> otherParameters) {
      semanticCheck(rvalue.isSource(), uid, "R-value must be data source.");
      // This way we set the value in actual destination.
      for (Endpoint connected : rvalue.connected()) {
        connected.valueOverride(expression);
      }
      setNamesAsFallback(otherParameters);
    }

    @Override
    protected void FormulaWithDeclaredConstant(UID owner, constant_declaration constant,
        Iterable<Endpoint> parameters) throws Exception {
      architectureDeclarations.addNode(new block_declarative_item(choice(constant)));
      setNamesAsFallback(parameters);
    }

    @Override
    public void Control(UID ownerUID, UID uid, Optional<String> label, UID terminalUID,
        boolean isIndicator, ControlStyle style, String description) throws Exception {
      semanticCheck(label.isPresent(), uid,
          "Missing control label (should contain port declaration).");
      String declaration = label.get().trim();
      VHDL93ParserPartial labelParser = forString(declaration);
      identifier signal;
      if (style == ControlStyle.NUMERIC_I32) {
        // This is a generic.
        interface_constant_declaration generic = labelParser.interface_constant_declaration();
        signal = generic.identifier_list.identifier;
      } else if (style == ControlStyle.NUMERIC_DBL) {
        // This is a port.
        interface_signal_declaration port = labelParser.interface_signal_declaration();
        signal = port.identifier_list.identifier;
      } else {
        throw new SemanticException("Control style not recognised: %s", style);
      }
      Iterable<Endpoint> connected = multiplexers.findMultiplexedConnections(uid);
      if (connected == null) {
        connected = endpoints.get(terminalUID).connected();
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
        VHDL93ParserPartial parser =
            forString(label.get() + tokenString(ASSIGN) + constantString + tokenString(SEMICOLON));
        constant_declaration constant = parser.constant_declaration();
        architectureDeclarations.addNode(new block_declarative_item(choice(constant)));
        valueString = constant.identifier_list.identifier.representation();
      } else {
        valueString = constantString;
      }
      Endpoint terminal = endpoints.get(terminalUID);
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
        unit = forString(
            tokenString(COMPONENT) + ' ' + name.component().toString()).instantiated_unit();
      } else if (element instanceof ArchitectureName) {
        ArchitectureName name = (ArchitectureName) element;
        // Schedule for processing.
        if (FOLLOW_DEPENDENCIES) {
          project.addDependency(viPath);
        }
        unit = forString(tokenString(ENTITY) + ' ' + name.toString()).instantiated_unit();
      } else {
        throw new VerifyException("Unknown instantiable name.");
      }
      // Determine whether this is a clustered VI.
      // Prepare all endpoints to process.
      Iterable<Endpoint> endpoints;
      if (declaration.isClustered()) {
        LOGGER.debug("Clustered SubVI: {}.", viPath);
        Verify.verify(termUIDs.size() == 2);
        Endpoint input = ArchitectureDefinition.this.endpoints.get(termUIDs.get(INPUTS_CONN_INDEX));
        Multiplexer bundler = multiplexers.findMultiplexer(input.onlyConnected());
        Endpoint output =
            ArchitectureDefinition.this.endpoints.get(termUIDs.get(OUTPUTS_CONN_INDEX));
        Multiplexer unbundler = multiplexers.findMultiplexer(output.onlyConnected());
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
            return ArchitectureDefinition.this.endpoints.get(uid);
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
        VHDL93ParserPartial parser = forString(terminal.name());
        Node node = parser.interface_declaration().nodeChoice.choice;
        if (node instanceof interface_constant_declaration) {
          // Generics without assigned value should be left alone.
          // Assigning "open" makes no sense.
          if (terminal.hasValue()) {
            interface_constant_declaration generic = (interface_constant_declaration) node;
            semanticCheck(generic.nodeOptional.present(), uid,
                "Missing signal/constant specifier.");
            formal_part formal = new formal_part(generic.identifier_list.identifier);
            actual_part actual = new actual_part(choice(terminal.value()));
            generics.add(new named_association_element(formal, actual));
          }
        } else if (node instanceof interface_signal_declaration) {
          interface_signal_declaration port = (interface_signal_declaration) node;
          semanticCheck(port.nodeOptional.present(), uid, "Missing signal/constant specifier.");
          formal_part formal = new formal_part(port.identifier_list.identifier);
          // Apply signal inference rules to instance endpoints.
          valueInference.inferValue(terminal);
          declarationInference.inferDeclaration(terminal);
          actual_part actual = new actual_part(
              choice(terminal.hasValue() ? terminal.value() : new actual_part_open()));
          ports.add(new named_association_element(formal, actual));
        } else {
          throw new MissingFeatureException(
              "Interface element of specified type is not supported.");
        }
      }
      VHDL93ParserPartial parser =
          forString(description.isEmpty() ? "label" + ++nextLabelNum : description);
      instantiation_label instantiationLabel = parser.instantiation_label();
      NodeOptional genericAspect = generics.isEmpty() ? optional()
          : optional(new generic_map_aspect(emitAssociationList(generics)));
      NodeOptional portAspect =
          ports.isEmpty() ? optional() : optional(new port_map_aspect(emitAssociationList(ports)));
      concurrentStatements.addNode(
          new component_instantiation_statement(instantiationLabel, unit, genericAspect,
              portAspect));
    }
  }
}
