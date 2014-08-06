package stupaq.translation.lv2vhdl;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import stupaq.commons.TopologicalOrdering;
import stupaq.translation.naming.IOReference;
import stupaq.translation.semantic.RValueVisitor;
import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.NodeListOptional;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.block_declarative_item;
import stupaq.vhdl93.ast.constant_declaration;
import stupaq.vhdl93.ast.entity_declarative_item;
import stupaq.vhdl93.ast.full_type_declaration;
import stupaq.vhdl93.ast.incomplete_type_declaration;
import stupaq.vhdl93.ast.process_declarative_item;
import stupaq.vhdl93.ast.shared_variable_declaration;
import stupaq.vhdl93.ast.signal_declaration;
import stupaq.vhdl93.ast.subtype_declaration;
import stupaq.vhdl93.ast.type_declaration;
import stupaq.vhdl93.ast.variable_declaration;
import stupaq.vhdl93.visitor.NonTerminalsNoOpVisitor;

import static stupaq.vhdl93.ast.Builders.optional;
import static stupaq.vhdl93.ast.Builders.sequence;

class DeclarationOrdering extends Ordering<Node> {
  private static final int DECLARATIONS_SORTING_LOOKUP = 2;
  private final Ordering<Node> ordering;

  public DeclarationOrdering(NodeListOptional declarations) {
    final Set<IOReference> allLHS = Sets.newHashSet();
    final Multimap<IOReference, IOReference> outgoing =
        Multimaps.newSetMultimap(Maps.<IOReference, Collection<IOReference>>newHashMap(),
            new Supplier<Set<IOReference>>() {
              @Override
              public Set<IOReference> get() {
                return Sets.newHashSet();
              }
            });
    // Harvest direct dependencies.
    for (Node node : declarations.nodes) {
      node.accept(new DeclarationLHSExtractor() {
        DeclarationLHSExtractor lhsExtractor = new DeclarationLHSExtractor();

        void add(final IOReference lhs, SimpleNode... nodes) {
          allLHS.add(lhs);
          sequence(nodes).accept(new RValueVisitor() {
            @Override
            protected void topLevelScope(IOReference rhs) {
              outgoing.put(rhs, lhs);
            }
          });
        }

        @Override
        public void visit(variable_declaration n) {
          add(lhsExtractor.apply(n), n.subtype_indication, n.nodeOptional1);
        }

        @Override
        public void visit(signal_declaration n) {
          add(lhsExtractor.apply(n), n.subtype_indication, n.nodeOptional1);
        }

        @Override
        public void visit(shared_variable_declaration n) {
          add(lhsExtractor.apply(n), n.subtype_indication, n.nodeOptional1);
        }

        @Override
        public void visit(constant_declaration n) {
          add(lhsExtractor.apply(n), n.subtype_indication, n.nodeOptional);
        }

        @Override
        public void visit(subtype_declaration n) {
          add(lhsExtractor.apply(n), n.subtype_indication);
        }

        @Override
        public void visit(incomplete_type_declaration n) {
          add(lhsExtractor.apply(n), optional());
        }

        @Override
        public void visit(full_type_declaration n) {
          add(lhsExtractor.apply(n), n.type_definition);
        }
      });
    }
    // Remove non-declarations.
    Iterator<IOReference> it = outgoing.keySet().iterator();
    while (it.hasNext()) {
      IOReference rhs = it.next();
      if (!allLHS.contains(rhs)) {
        it.remove();
      }
    }
    // Create our ordering.
    ordering = new TopologicalOrdering<>(outgoing, allLHS, true).onResultOf(
        new Function<Node, IOReference>() {
          DeclarationLHSExtractor lhsExtractor = new DeclarationLHSExtractor();

          @Override
          public IOReference apply(Node node) {
            return lhsExtractor.apply(node);
          }
        }).nullsLast().compound(new FirstFewTokensOrdering(DECLARATIONS_SORTING_LOOKUP));
  }

  @Override
  public int compare(Node o1, Node o2) {
    return ordering.compare(o1, o2);
  }

  private static class DeclarationLHSExtractor extends NonTerminalsNoOpVisitor<IOReference> {
    private IOReference reference;

    @Override
    public IOReference apply(Node n) {
      super.apply(n);
      return reference;
    }

    @Override
    public void visit(full_type_declaration n) {
      reference = new IOReference(n.identifier);
    }

    @Override
    public void visit(incomplete_type_declaration n) {
      reference = new IOReference(n.identifier);
    }

    @Override
    public void visit(subtype_declaration n) {
      reference = new IOReference(n.identifier);
    }

    @Override
    public void visit(type_declaration n) {
      n.nodeChoice.choice.accept(this);
    }

    @Override
    public void visit(entity_declarative_item n) {
      n.nodeChoice.choice.accept(this);
    }

    @Override
    public void visit(process_declarative_item n) {
      n.nodeChoice.choice.accept(this);
    }

    @Override
    public void visit(block_declarative_item n) {
      n.nodeChoice.choice.accept(this);
    }

    @Override
    public void visit(constant_declaration n) {
      reference = new IOReference(n.identifier_list.identifier);
    }

    @Override
    public void visit(shared_variable_declaration n) {
      reference = new IOReference(n.identifier_list.identifier);
    }

    @Override
    public void visit(signal_declaration n) {
      reference = new IOReference(n.identifier_list.identifier);
    }

    @Override
    public void visit(variable_declaration n) {
      reference = new IOReference(n.identifier_list.identifier);
    }
  }
}
