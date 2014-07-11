package stupaq.vhdl2lv;

import com.google.common.collect.Sets;

import java.util.Set;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.Formula;
import stupaq.labview.scripting.hierarchy.FormulaParameter;
import stupaq.labview.scripting.hierarchy.LeftShiftRegister;
import stupaq.labview.scripting.hierarchy.Loop;
import stupaq.labview.scripting.hierarchy.RightShiftRegister;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.labview.scripting.hierarchy.Wire;
import stupaq.vhdl93.ast.*;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static stupaq.vhdl93.ast.ASTBuilders.sequence;

public class ProcessSignalsEmitter extends DepthFirstVisitor {
  private final Loop loop;
  private final Formula formula;
  private final IOSinks danglingSinks;
  private final IOSources namedSources;
  private final RValueVisitor rvalueVisitor = new RValueVisitor() {
    @Override
    public void topLevelScope(IOReference ref) {
      // Not much of a logic over here.
      allReads.add(ref);
    }
  };
  private final LValueVisitor lvalueVisitor = new LValueVisitor(rvalueVisitor) {
    @Override
    public void topLevel(IOReference ref) {
      if (!allWrites.contains(ref)) {
        if (allReads.contains(ref)) {
          latched.add(ref);
        }
        allWrites.add(ref);
      }
      nestedWrites.add(ref);
    }
  };
  private Set<IOReference> allReads;
  private Set<IOReference> allWrites;
  private Set<IOReference> nestedWrites;
  private Set<IOReference> latched;

  public ProcessSignalsEmitter(Loop loop, Formula formula, IOSinks danglingSinks,
      IOSources namedSources) {
    this.loop = loop;
    this.formula = formula;
    this.danglingSinks = danglingSinks;
    this.namedSources = namedSources;
  }

  private void signalsRead(SimpleNode root) {
    root.accept(rvalueVisitor);
  }

  private void signalsWritten(SimpleNode root) {
    root.accept(lvalueVisitor);
  }

  @Override
  public void visit(process_statement n) {
    allReads = Sets.newHashSet();
    allWrites = Sets.newHashSet();
    nestedWrites = Sets.newHashSet();
    latched = Sets.newHashSet();
    // Harvest all signals in the process body.
    n.process_statement_part.accept(this);
    // Include signals from sensitivity list.
    signalsRead(n.nodeOptional2);
    // Exclude signals from internal scope.
    n.process_declarative_part.accept(new DepthFirstVisitor() {
      private void exclude(IOReference ref) {
        allReads.remove(ref);
        allWrites.remove(ref);
        nestedWrites.remove(ref);
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
      new Wire(loop.diagram(), left.inner(), in);
      Terminal<FormulaParameter> out = formula.addOutput(latch.toString());
      new Wire(loop.diagram(), out, right.inner());
      namedSources.put(latch, right.outer());
      danglingSinks.put(latch, left.outer());
      // Exclude from normal signals.
      allReads.remove(latch);
      allWrites.remove(latch);
      nestedWrites.remove(latch);
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
    latched = null;
    nestedWrites = null;
    allWrites = null;
    allReads = null;
  }

  @Override
  public void visit(sequential_statement n) {
    // We handle each of them individually. See below.
    super.visit(n);
  }

  @Override
  public void visit(null_statement n) {
    // No identifiers here.
  }

  @Override
  public void visit(wait_statement n) {
    signalsRead(n);
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
    n.nodeList.accept(new DepthFirstVisitor() {
      @Override
      public void visit(sequence_of_statements n) {
        // TODO collect writes and compare among branches
        n.accept(ProcessSignalsEmitter.this);
      }
    });
  }

  @Override
  public void visit(report_statement n) {
    signalsRead(n);
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
  public void visit(procedure_call_statement n) {
    signalsRead(n);
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
    // Process branches.
    sequence(n).accept(new DepthFirstVisitor() {
      @Override
      public void visit(sequence_of_statements n) {
        // TODO collect writes and compare among branches
        n.accept(ProcessSignalsEmitter.this);
      }
    });
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
  public void visit(exit_statement n) {
    signalsRead(n.nodeOptional2);
  }

  @Override
  public void visit(return_statement n) {
    signalsRead(n.nodeOptional1);
  }

}
