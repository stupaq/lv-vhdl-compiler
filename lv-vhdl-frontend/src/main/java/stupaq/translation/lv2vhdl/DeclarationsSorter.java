package stupaq.translation.lv2vhdl;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import stupaq.commons.TopologicalComparator;
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

final class DeclarationsSorter {
  private DeclarationsSorter() {
  }

  public static void sort(final NodeListOptional declarations) {
    if (!declarations.present()) {
      return;
    }
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
    // Sort declarations.
    Collections.sort(declarations.nodes,
        new DeclarationComparator(new TopologicalComparator<>(outgoing, allLHS, true)));
  }

  private static final class DeclarationComparator implements Comparator<Node> {
    private final DeclarationLHSExtractor lhsExtractor = new DeclarationLHSExtractor();
    private final TopologicalComparator<IOReference> comparator;

    public DeclarationComparator(TopologicalComparator<IOReference> comparator) {
      this.comparator = comparator;
    }

    @Override
    public int compare(Node o1, Node o2) {
      IOReference ref1 = lhsExtractor.apply(o1), ref2 = lhsExtractor.apply(o2);
      if (ref1 == null) {
        return 1;
      } else if (ref2 == null) {
        return -1;
      } else {
        return comparator.compare(ref1, ref2);
      }
    }
  }

  private static class DeclarationLHSExtractor extends NonTerminalsNoOpVisitor<IOReference> {
    private IOReference reference;

    @Override
    public IOReference apply(Node n) {
      super.apply(n);
      return reference;
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
    public void visit(type_declaration n) {
      n.nodeChoice.choice.accept(this);
    }

    @Override
    public void visit(variable_declaration n) {
      reference = new IOReference(n.identifier_list.identifier);
    }

    @Override
    public void visit(signal_declaration n) {
      reference = new IOReference(n.identifier_list.identifier);
    }

    @Override
    public void visit(shared_variable_declaration n) {
      reference = new IOReference(n.identifier_list.identifier);
    }

    @Override
    public void visit(constant_declaration n) {
      reference = new IOReference(n.identifier_list.identifier);
    }

    @Override
    public void visit(subtype_declaration n) {
      reference = new IOReference(n.identifier);
    }

    @Override
    public void visit(incomplete_type_declaration n) {
      reference = new IOReference(n.identifier);
    }

    @Override
    public void visit(full_type_declaration n) {
      reference = new IOReference(n.identifier);
    }
  }
}
