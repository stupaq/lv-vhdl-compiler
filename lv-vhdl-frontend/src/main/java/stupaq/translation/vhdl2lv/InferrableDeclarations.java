package stupaq.translation.vhdl2lv;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import stupaq.translation.naming.ArchitectureName;
import stupaq.translation.naming.IOReference;
import stupaq.translation.naming.Identifier;
import stupaq.translation.naming.InstantiableName;
import stupaq.translation.semantic.ExpressionClassifier;
import stupaq.translation.semantic.InferenceContext;
import stupaq.translation.semantic.SubtypeInstantiator;
import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.architecture_statement_part;
import stupaq.vhdl93.ast.component_instantiation_statement;
import stupaq.vhdl93.ast.concurrent_statement;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.generic_map_aspect;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.port_map_aspect;
import stupaq.vhdl93.ast.subtype_indication;
import stupaq.vhdl93.visitor.NonTerminalsNoOpVisitor;

import static stupaq.translation.errors.LocalisedSemanticException.semanticNotNull;
import static stupaq.translation.parsing.NodeRepr.repr;
import static stupaq.vhdl93.ast.Builders.sequence;

class InferrableDeclarations extends NonTerminalsNoOpVisitor<Void> {
  private static final Logger LOGGER = LoggerFactory.getLogger(InferrableDeclarations.class);
  private final Set<IOReference> inferrable = Sets.newHashSet();
  private final ComponentBindingResolver resolver;
  private final ArchitectureName architecture;

  public InferrableDeclarations(ComponentBindingResolver resolver, ArchitectureName architecture) {
    this.resolver = resolver;
    this.architecture = architecture;
  }

  public boolean contains(IOReference ref) {
    return inferrable.contains(ref);
  }

  @Override
  public void visit(architecture_statement_part n) {
    n.nodeListOptional.accept(this);
  }

  @Override
  public void visit(concurrent_statement n) {
    n.nodeChoice.choice.accept(this);
  }

  @Override
  public void visit(component_instantiation_statement n) {
    final InstantiableName instance =
        Identifier.instantiation(resolver.architectures(), architecture, n.instantiated_unit);
    final InterfaceDeclaration entity = resolver.get(instance.interfaceName());
    semanticNotNull(entity, n, "Missing component or entity declaration: %s.",
        instance.interfaceName());
    // Note that we must visit generic map aspect first.
    sequence(n.nodeOptional, n.nodeOptional1).accept(new NonTerminalsNoOpVisitor<Void>() {
      /** Context of {@link #visit(port_map_aspect)}. */
      SubtypeInstantiator instantiator;

      @Override
      public void visit(generic_map_aspect n) {
        // We do not apply any inference to constants, but we do want to collect inference context.
        InferenceContext context = new ContextHarvestingVisitor(entity).apply(n.association_list);
        instantiator = new SubtypeInstantiator(context);
      }

      @Override
      public void visit(port_map_aspect n) {
        if (instantiator == null) {
          instantiator = new SubtypeInstantiator();
        }
        n.association_list.accept(new DeclarationHarvestingVisitor(entity, instantiator));
      }
    });
  }

  private static class ContextHarvestingVisitor extends AssociationListVisitor<InferenceContext> {
    /** Context of {@link ContextHarvestingVisitor}. */
    private final InterfaceDeclaration entity;
    /** Result of {@link #visit(generic_map_aspect)}. */
    private final InferenceContext context = new InferenceContext();

    private ContextHarvestingVisitor(InterfaceDeclaration entity) {
      this.entity = entity;
    }

    @Override
    public InferenceContext apply(Node n) {
      super.apply(n);
      return context;
    }

    @Override
    protected void actualPartOpen(int elementIndex) {
      // No context comes from here.
    }

    @Override
    protected void actualPartOpen(IOReference name) {
      // No context comes from here.
    }

    @Override
    protected void actualPartExpression(int elementIndex, expression n) {
      harvest(entity.resolveGeneric(elementIndex), n);
    }

    @Override
    protected void actualPartExpression(IOReference name, expression n) {
      harvest(entity.resolveGeneric(name), n);
    }

    private void harvest(GenericDeclaration declaration, expression n) {
      context.put(declaration.reference(), repr(n));
    }
  }

  private class DeclarationHarvestingVisitor extends AssociationListVisitor<Void> {
    /** Context of {@link DeclarationHarvestingVisitor}. */
    private final InterfaceDeclaration entity;
    /** Context of {@link DeclarationHarvestingVisitor}. */
    private final SubtypeInstantiator instantiator;

    public DeclarationHarvestingVisitor(InterfaceDeclaration entity,
        SubtypeInstantiator instantiator) {
      this.entity = entity;
      this.instantiator = instantiator;
    }


    @Override
    protected void actualPartOpen(int elementIndex) {
      // This gives us no type information.
    }

    @Override
    protected void actualPartOpen(IOReference name) {
      // This gives us no type information.
    }

    @Override
    protected void actualPartExpression(int elementIndex, expression n) {
      harvest(entity.resolvePort(elementIndex), n);
    }

    @Override
    protected void actualPartExpression(IOReference name, expression n) {
      harvest(entity.resolvePort(name), n);
    }

    private void harvest(PortDeclaration declaration, expression n) {
      // Note that we do not visit recursively in current setting, so we are sure,
      // that this is the top-level expression context.
      Optional<IOReference> ref =
          ExpressionClassifier.asIdentifier(n).transform(new Function<identifier, IOReference>() {
            @Override
            public IOReference apply(identifier identifier) {
              return new IOReference(identifier);
            }
          });
      if (ref.isPresent()) {
        Optional<subtype_indication> resolvedType =
            instantiator.apply(declaration.type().indication());
        if (resolvedType.isPresent()) {
          LOGGER.debug("Definition of signal: {} is inferrable from instantiation of: {}",
              ref.get(), entity.name());
          inferrable.add(ref.get());
        } else {
          LOGGER.info("Signal: {} connected to parametrised type: <{}>", ref.get(),
              declaration.type().indication().representation());
        }
      }
    }
  }
}
