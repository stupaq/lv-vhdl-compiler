package stupaq.vhdl2lv;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import stupaq.labview.scripting.hierarchy.Formula;
import stupaq.labview.scripting.hierarchy.FormulaParameter;
import stupaq.labview.scripting.hierarchy.LeftShiftRegister;
import stupaq.labview.scripting.hierarchy.Loop;
import stupaq.labview.scripting.hierarchy.RightShiftRegister;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.naming.IOReference;
import stupaq.vhdl93.VHDL93ParserConstants;
import stupaq.vhdl93.ast.*;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.NonTerminalsNoOpVisitor;

import static stupaq.vhdl93.ast.ASTBuilders.sequence;

public class ProcessSignalsEmitter extends NonTerminalsNoOpVisitor<Void> {
  private final Loop loop;
  private final Formula formula;
  private final IOSinks danglingSinks;
  private final IOSources namedSources;
  private final WiresBlacklist blacklist;
  private final RValueVisitor rvalueVisitor;
  private final LValueVisitor lvalueVisitor;
  private Set<IOReference> allReads;
  private Set<IOReference> allWrites;
  private Set<IOReference> latched;
  private Set<IOReference> nestedWrites;
  private Set<IOReference> sustainedReads;
  private Set<IOReference> certainWrites;
  private int sequentialStatementLevel;

  public ProcessSignalsEmitter(Loop loop, Formula formula, IOSinks danglingSinks,
      IOSources namedSources, WiresBlacklist blacklist) {
    this.loop = loop;
    this.formula = formula;
    this.danglingSinks = danglingSinks;
    this.namedSources = namedSources;
    this.blacklist = blacklist;
    rvalueVisitor = new RValueVisitor() {
      @Override
      public void topLevelScope(IOReference ref) {
        // Not much of a logic over here.
        if (!certainWrites.contains(ref)) {
          sustainedReads.add(ref);
        }
        allReads.add(ref);
      }
    };
    lvalueVisitor = new LValueVisitor(rvalueVisitor) {
      @Override
      public void topLevel(IOReference ref) {
        if (!certainWrites.contains(ref)) {
          if (sustainedReads.contains(ref)) {
            latched.add(ref);
          }
          certainWrites.add(ref);
        }
        nestedWrites.add(ref);
        allWrites.add(ref);
      }
    };
  }

  private static Set<IOReference> union(List<Set<IOReference>> sets) {
    Set<IOReference> union = Collections.emptySet();
    for (Set<IOReference> set : sets) {
      union = Sets.union(union, set);
    }
    return union;
  }

  private void signalsRead(SimpleNode root) {
    root.accept(rvalueVisitor);
  }

  private void signalsWritten(SimpleNode root) {
    root.accept(lvalueVisitor);
  }

  private void processBranches(SimpleNode n, boolean hasElse) {
    final List<Set<IOReference>> branchWrites = Lists.newArrayList();
    final List<Set<IOReference>> branchReads = Lists.newArrayList();
    n.accept(new DepthFirstVisitor() {
      @Override
      public void visit(sequence_of_statements n) {
        // Save and reset nested writes.
        Set<IOReference> savedNestedWrites = nestedWrites;
        nestedWrites = Sets.newHashSet();
        // Save certain writes.
        Set<IOReference> savedCertainWrites = Sets.newHashSet(certainWrites);
        // Save sustained reads.
        Set<IOReference> savedSustainedReads = Sets.newHashSet(sustainedReads);
        // Descend recursively.
        n.accept(ProcessSignalsEmitter.this);
        // Restore sustained reads.
        branchReads.add(sustainedReads);
        sustainedReads = savedSustainedReads;
        // Restore certain writes.
        certainWrites = savedCertainWrites;
        // Restore nested writes.
        branchWrites.add(nestedWrites);
        savedNestedWrites.addAll(nestedWrites);
        nestedWrites = savedNestedWrites;
      }
    });
    Set<IOReference> alwaysWritten = intersection(branchWrites);
    // Fill in certain writes.
    if (hasElse) {
      certainWrites.addAll(alwaysWritten);
    }
    // Fill in sustained reads.
    sustainedReads.addAll(union(branchReads));
    if (sequentialStatementLevel <= 1) {
      // We do not emit latches for incomplete top level ifs.
      return;
    }
    Set<IOReference> partiallyWritten = union(branchWrites);
    if (hasElse) {
      partiallyWritten = Sets.difference(partiallyWritten, alwaysWritten);
    }
    this.latched.addAll(partiallyWritten);
  }

  private Set<IOReference> intersection(List<Set<IOReference>> sets) {
    if (sets.isEmpty()) {
      return Collections.emptySet();
    }
    Set<IOReference> intersection = sets.get(0);
    for (Set<IOReference> set : sets) {
      intersection = Sets.intersection(intersection, set);
    }
    return intersection;
  }

  @Override
  public void visit(process_statement n) {
    // Setup.
    allReads = Sets.newHashSet();
    allWrites = Sets.newHashSet();
    latched = Sets.newHashSet();
    nestedWrites = Sets.newHashSet();
    sustainedReads = Sets.newHashSet();
    certainWrites = Sets.newHashSet();
    sequentialStatementLevel = 0;
    // Harvest all signals in the process body.
    n.process_statement_part.accept(this);
    // Include signals from sensitivity list.
    signalsRead(n.nodeOptional2);
    // Exclude signals from internal scope.
    n.process_declarative_part.accept(new DepthFirstVisitor() {
      private void exclude(IOReference ref) {
        allReads.remove(ref);
        allWrites.remove(ref);
        latched.remove(ref);
      }

      @Override
      public void visit(constant_declaration n) {
        exclude(new IOReference(n.identifier_list.identifier));
      }

      @Override
      public void visit(signal_declaration n) {
        exclude(new IOReference(n.identifier_list.identifier));
      }

      @Override
      public void visit(variable_declaration n) {
        exclude(new IOReference(n.identifier_list.identifier));
      }
    });
    // Emit all latched signals.
    for (IOReference latch : latched) {
      RightShiftRegister right = loop.addShiftRegister();
      LeftShiftRegister left = right.left(0);
      Terminal<FormulaParameter> in = formula.addInput(latch.toString());
      left.inner().connectTo(in);
      Terminal<FormulaParameter> out = formula.addOutput(latch.toString());
      out.connectTo(right.inner());
      namedSources.put(latch, right.outer());
      danglingSinks.put(latch, left.outer());
      // Otherwise we will have a meaningless cycle.
      blacklist.add(right.outer(), left.outer());
      // Exclude from normal signals, so we won't duplicate inputs and outputs.
      allReads.remove(latch);
      allWrites.remove(latch);
    }
    // And everything else too.
    for (IOReference ref : allReads) {
      Terminal<FormulaParameter> in = formula.addInput(ref.toString());
      danglingSinks.put(ref, in);
    }
    for (IOReference ref : allWrites) {
      Terminal<FormulaParameter> out = formula.addOutput(ref.toString());
      namedSources.put(ref, out);
    }
    // Reset.
    allReads = null;
    allWrites = null;
    latched = null;
    nestedWrites = null;
    sustainedReads = null;
    certainWrites = null;
  }

  @Override
  public void visit(process_statement_part n) {
    n.nodeListOptional.accept(this);
  }

  @Override
  public void visit(sequential_statement n) {
    // We handle each of them individually. See below.
    sequentialStatementLevel++;
    n.nodeChoice.accept(this);
    sequentialStatementLevel--;
  }

  @Override
  public void visit(null_statement n) {
    // No identifiers here.
  }

  @Override
  public void visit(assertion_statement n) {
    signalsRead(n.assertion);
  }

  @Override
  public void visit(case_statement n) {
    sequence(n.expression, n.nodeList).accept(new DepthFirstVisitor() {
      @Override
      public void visit(choices n) {
        signalsRead(n);
      }

      @Override
      public void visit(sequence_of_statements n) {
        // Do not descend any further.
      }
    });
    signalsRead(n.expression);
    boolean hasElse = (new NonTerminalsNoOpVisitor<Boolean>() {
      boolean hasElse = false;

      @Override
      public Boolean apply(Node n) {
        super.apply(n);
        return hasElse;
      }

      @Override
      public void visit(case_statement_alternative n) {
        n.choices.accept(this);
      }

      @Override
      public void visit(choices n) {
        n.nodeListOptional.accept(this);
      }

      @Override
      public void visit(choice n) {
        n.nodeChoice.accept(this);
      }

      @Override
      public void visit(NodeToken n) {
        if (n.kind == VHDL93ParserConstants.OTHERS) {
          hasElse = true;
        }
      }
    }).apply(sequence(n.nodeList));
    processBranches(n.nodeList, hasElse);
  }

  @Override
  public void visit(exit_statement n) {
    signalsRead(n.nodeOptional2);
  }

  @Override
  public void visit(if_statement n) {
    sequence(n.condition, n.nodeListOptional).accept(new DepthFirstVisitor() {
      @Override
      public void visit(condition n) {
        signalsRead(n);
      }

      @Override
      public void visit(sequence_of_statements n) {
        // Do not descend any further.
      }
    });
    boolean hasElse = n.nodeOptional1.present();
    processBranches(sequence(n), hasElse);
  }

  @Override
  public void visit(loop_statement n) {
    n.nodeOptional1.accept(new DepthFirstVisitor() {
      @Override
      public void visit(condition n) {
        signalsRead(n);
      }

      @Override
      public void visit(discrete_range n) {
        signalsRead(n);
      }
    });
    n.sequence_of_statements.accept(this);
  }

  @Override
  public void visit(next_statement n) {
    signalsRead(n.nodeOptional2);
  }

  @Override
  public void visit(procedure_call_statement n) {
    signalsRead(n);
  }

  @Override
  public void visit(report_statement n) {
    signalsRead(n);
  }

  @Override
  public void visit(return_statement n) {
    signalsRead(n.nodeOptional1);
  }

  @Override
  public void visit(sequence_of_statements n) {
    n.nodeListOptional.accept(this);
  }

  @Override
  public void visit(signal_assignment_statement n) {
    signalsRead(n.nodeOptional1);
    signalsRead(n.waveform);
    signalsWritten(n.target);
  }

  @Override
  public void visit(variable_assignment_statement n) {
    signalsRead(n.expression);
    signalsWritten(n.target);
  }

  @Override
  public void visit(wait_statement n) {
    signalsRead(n);
  }
}
