package stupaq.translation.vhdl2lv;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import stupaq.translation.naming.ArchitectureName;
import stupaq.translation.naming.IOReference;
import stupaq.translation.naming.Identifier;
import stupaq.translation.naming.InstantiableName;
import stupaq.translation.semantic.ExpressionClassifier;
import stupaq.translation.semantic.SubtypeInstantiator;
import stupaq.vhdl93.ast.actual_part;
import stupaq.vhdl93.ast.actual_part_open;
import stupaq.vhdl93.ast.architecture_statement_part;
import stupaq.vhdl93.ast.association_list;
import stupaq.vhdl93.ast.component_instantiation_statement;
import stupaq.vhdl93.ast.concurrent_statement;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.generic_map_aspect;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.named_association_element;
import stupaq.vhdl93.ast.named_association_list;
import stupaq.vhdl93.ast.port_map_aspect;
import stupaq.vhdl93.ast.positional_association_element;
import stupaq.vhdl93.ast.positional_association_list;
import stupaq.vhdl93.visitor.NonTerminalsNoOpVisitor;

import static stupaq.translation.errors.LocalisedSemanticException.semanticNotNull;
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
    InstantiableName instance =
        Identifier.instantiation(resolver.architectures(), architecture, n.instantiated_unit);
    final InterfaceDeclaration entity = resolver.get(instance.interfaceName());
    semanticNotNull(entity, n, "Missing component or entity declaration: %s.",
        instance.interfaceName());
    sequence(n.nodeOptional, n.nodeOptional1).accept(new NonTerminalsNoOpVisitor() {
      // FIXME add context here
      /** Context of {@link #visit(port_map_aspect)}. */
      final SubtypeInstantiator instantiator = new SubtypeInstantiator();
      /** Context of {@link #visit(positional_association_list)}. */
      int elementIndex;
      /** Context of {@link #visit(actual_part)}. */
      PortDeclaration portDeclaration;

      @Override
      public void visit(association_list n) {
        n.nodeChoice.accept(this);
      }

      @Override
      public void visit(named_association_list n) {
        elementIndex = Integer.MIN_VALUE;
        n.named_association_element.accept(this);
        n.nodeListOptional.accept(this);
      }

      @Override
      public void visit(positional_association_list n) {
        elementIndex = 0;
        n.positional_association_element.accept(this);
        n.nodeListOptional.accept(this);
      }

      @Override
      public void visit(named_association_element n) {
        Preconditions.checkState(elementIndex == Integer.MIN_VALUE);
        IOReference ref = new IOReference(n.formal_part.identifier);
        // We do not infer anything for generics, therefore we know we are dealing with port.
        portDeclaration = entity.resolvePort(ref);
        n.actual_part.accept(this);
        portDeclaration = null;
      }

      @Override
      public void visit(positional_association_element n) {
        Preconditions.checkState(elementIndex >= 0);
        // We do not infer anything for generics, therefore we know we are dealing with port.
        portDeclaration = entity.resolvePort(elementIndex);
        n.actual_part.accept(this);
        ++elementIndex;
        portDeclaration = null;
      }

      @Override
      public void visit(actual_part n) {
        n.nodeChoice.choice.accept(this);
      }

      @Override
      public void visit(actual_part_open n) {
        // Nothing new when it comes to types inference.
      }

      @Override
      public void visit(expression n) {
        Preconditions.checkState(portDeclaration != null);
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
          if (instantiator.apply(portDeclaration.type().indication()).isPresent()) {
            LOGGER.debug("Definition of signal: {} is inferrable from instantiation of: {}",
                ref.get(), entity.name());
            inferrable.add(ref.get());
          } else {
            LOGGER.info("Signal: {} connected to parametrised type: <{}>", ref.get(),
                portDeclaration.type().indication().representation());
          }
        }
      }

      @Override
      public void visit(generic_map_aspect n) {
        // We do not apply any inference to constants.
      }

      @Override
      public void visit(port_map_aspect n) {
        n.association_list.accept(this);
      }
    });
  }
}
