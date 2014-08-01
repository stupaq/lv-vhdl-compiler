package stupaq.translation.vhdl2lv;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map.Entry;

import stupaq.labview.hierarchy.FormulaNode;
import stupaq.labview.hierarchy.Terminal;
import stupaq.translation.TranslationConventions;
import stupaq.translation.naming.ArchitectureName;
import stupaq.translation.naming.IOReference;
import stupaq.translation.naming.Identifier;
import stupaq.translation.project.LVProject;
import stupaq.translation.semantic.FlattenNestedListsVisitor;
import stupaq.translation.vhdl2lv.IOSinks.Sink;
import stupaq.translation.vhdl2lv.IOSources.Source;
import stupaq.translation.vhdl2lv.WiringRules.PassLabels;
import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.architecture_declaration;
import stupaq.vhdl93.ast.block_declarative_item;
import stupaq.vhdl93.ast.component_declaration;
import stupaq.vhdl93.ast.constant_declaration;
import stupaq.vhdl93.ast.context_clause;
import stupaq.vhdl93.ast.design_file;
import stupaq.vhdl93.ast.design_unit;
import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.signal_declaration;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.NonTerminalsNoOpVisitor;

import static com.google.common.base.Optional.of;
import static stupaq.translation.SemanticException.semanticCheck;
import static stupaq.translation.SemanticException.semanticNotNull;
import static stupaq.vhdl93.ast.Builders.sequence;

class DesignFileEmitter extends DepthFirstVisitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(DesignFileEmitter.class);
  /** Context of {@link #visit(design_file)}. */
  private final ComponentBindingResolver resolver = new ComponentBindingResolver();
  /** Context of {@link #visit(design_file)}. */
  private final LVProject project;
  /** Context of {@link #visit(design_unit)}. */
  private context_clause lastContext;
  /** Context of {@link #visit(architecture_declaration)}. */
  private UniversalVI currentVi;
  /** Context of {@link #visit(architecture_declaration)}. */
  private IOSources namedSources;
  /** Context of {@link #visit(architecture_declaration)}. */
  private IOSinks danglingSinks;

  public DesignFileEmitter(LVProject project) {
    this.project = project;
  }

  @Override
  public void visit(design_file n) {
    n.accept(new FlattenNestedListsVisitor());
    super.visit(n);
  }

  @Override
  public void visit(context_clause n) {
    lastContext = n;
  }

  @Override
  public void visit(entity_declaration n) {
    EntityDeclaration entity = new EntityDeclaration(resolver, n, lastContext);
    resolver.addGlobal(entity);
  }

  @Override
  public void visit(architecture_declaration n) {
    danglingSinks = new IOSinks();
    namedSources = new IOSources();
    EntityDeclaration entity = resolver.getGlobal(Identifier.entity(n.entity_name));
    final ArchitectureName arch = Identifier.architecture(entity.name(), n);
    resolver.defaultArchitecture(arch);
    LOGGER.debug("Architecture: {}", arch);
    // Create all generics, ports and eventually the VI itself.
    currentVi = new UniversalVI(project, arch, entity, namedSources, danglingSinks);
    // Fill local scope with component declarations.
    resolver.enterLocal(arch, n.architecture_declarative_part);
    // Emit all locally declared components.
    for (ComponentDeclaration component : resolver.getLocalComponents()) {
      // Create all generics, ports and eventually the VI itself.
      // Note that we create separate dangling sinks and sources, since this will be a whole
      // different VI.
      new UniversalVI(project, component.name(), component, new IOSources(), new IOSinks());
    }
    // Emit architecture body.
    ConcurrentStatementsEmitter concurrentStatements =
        new ConcurrentStatementsEmitter(resolver, project, arch, currentVi, namedSources,
            danglingSinks);
    n.architecture_statement_part.accept(concurrentStatements);
    // Print out what we have found when it comes to wiring.
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Named sources:");
      for (Entry<IOReference, Source> entry : namedSources.entries()) {
        LOGGER.debug("\t{}", entry);
      }
      LOGGER.debug("Dangling sinks:");
      for (Entry<IOReference, Sink> entry : danglingSinks.entries()) {
        LOGGER.debug("\t{}", entry);
      }
    }
    // Collect information about inferrability fo signals declarations.
    final InferrableDeclarations inferrableDeclarations =
        new InferrableDeclarations(resolver, arch);
    n.architecture_statement_part.accept(inferrableDeclarations);
    // Emit entries from architecture declarative part.
    final StringBuilder declarativePartFallbacked = new StringBuilder();
    n.architecture_declarative_part.accept(new DepthFirstVisitor() {
      /** Context of {@link #visit(block_declarative_item)}. */
      boolean declarativePartFallback;

      @Override
      public void visit(constant_declaration n) {
        declarativePartFallback = false;
        IOReference ref = new IOReference(n.identifier_list.identifier);
        final String label = sequence(n.nodeToken, n.identifier_list, n.nodeToken1,
            n.subtype_indication).representation();
        Terminal terminal = (new NonTerminalsNoOpVisitor<Terminal>() {
          Terminal terminal = null;

          @Override
          public Terminal apply(Node n) {
            super.apply(n);
            return terminal;
          }

          @Override
          public void visit(expression n) {
            terminal = new SourceEmitter(currentVi, danglingSinks, namedSources).emitAsConstant(n,
                of(label));
          }
        }).apply(n.nodeOptional);
        semanticNotNull(terminal, n, "Missing value for constant: %s.", ref);
        semanticCheck(!namedSources.containsKey(ref), n, "Multiple definitions of constant: %s.",
            ref);
        namedSources.put(ref, terminal);
      }

      @Override
      public void visit(component_declaration n) {
        declarativePartFallback = false;
        // These will be emitted as a separate files.
      }

      @Override
      public void visit(signal_declaration n) {
        if (!n.nodeOptional.present() && !n.nodeOptional1.present()) {
          // Signals of special kind or with assigned value are not inferrable.
          IOReference ref = new IOReference(n.identifier_list.identifier);
          if (inferrableDeclarations.contains(ref)) {
            // We skip this declaration.
            declarativePartFallback = false;
            LOGGER.info("Excluding declaration of: {}, it is inferrable.", ref);
          }
        }
        // Otherwise we follow the fallback path which involves emitting declaration among other
        // ones from architecture declarative part.
      }

      @Override
      public void visit(block_declarative_item n) {
        declarativePartFallback = true;
        super.visit(n);
        if (declarativePartFallback) {
          declarativePartFallbacked.append(n.representation()).append(System.lineSeparator());
        }
      }
    });
    // Emit contexts.
    if (entity.context() != null) {
      String rep = entity.context().representation();
      if (!rep.isEmpty()) {
        new FormulaNode(currentVi, rep, TranslationConventions.ENTITY_CONTEXT);
      }
    }
    if (lastContext != null) {
      String rep = lastContext.representation();
      if (!rep.isEmpty()) {
        new FormulaNode(currentVi, rep, TranslationConventions.ARCHITECTURE_CONTEXT);
      }
    }
    // Emit declarative part leftovers.
    if (declarativePartFallbacked.length() > 0) {
      new FormulaNode(currentVi, declarativePartFallbacked.toString(),
          TranslationConventions.ARCHITECTURE_EXTRA_DECLARATIONS);
    }
    // Emit statement part leftovers.
    for (StringBuilder text : concurrentStatements.fallbackText().asSet()) {
      new FormulaNode(currentVi, text.toString(), Optional.<String>absent());
    }
    // All references and labels are resolved now.
    new WiringRules(currentVi, namedSources, danglingSinks, new PassLabels(),
        concurrentStatements.wiresBlacklist()).applyAll();
    // Fallback for missing signal declarations and wires.
    for (Entry<IOReference, Source> entry : namedSources.entries()) {
      IOReference ref = entry.getKey();
      Source source = entry.getValue();
      if (source.label().isPresent()) {
        LOGGER.warn("Dangling source: {} with label: {}", ref, source.label().get());
      } else {
        LOGGER.info("Unconnected source: {}", ref);
      }
    }
    for (Entry<IOReference, Sink> entry : danglingSinks.entries()) {
      LOGGER.error("Dangling sink: {}", entry.getKey());
    }
    currentVi.cleanUpDiagram();
    currentVi = null;
    namedSources = null;
    danglingSinks = null;
    resolver.exitLocal();
  }
}
