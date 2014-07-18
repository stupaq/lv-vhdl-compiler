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

import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import stupaq.SemanticException;
import stupaq.labview.UID;
import stupaq.labview.VIPath;
import stupaq.labview.parsing.PrintingVisitor;
import stupaq.labview.parsing.VIElementsVisitor;
import stupaq.labview.parsing.VIParser;
import stupaq.naming.ComponentName;
import stupaq.naming.Identifier;
import stupaq.naming.InstantiableName;
import stupaq.project.VHDLProject;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.NodeListOptional;
import stupaq.vhdl93.ast.concurrent_statement;
import stupaq.vhdl93.ast.context_clause;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.process_statement;

import static stupaq.TranslationConventions.*;

class DesignFileEmitter implements VIElementsVisitor<Exception> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DesignFileEmitter.class);
  private final VHDLProject project;
  /** Context. */
  private UID rootDiagram, rootPanel;
  private Map<UID, TerminalMetadata> terminals;
  private Multimap<UID, TerminalMetadata> wires;
  /** Results. */
  private context_clause entityContext, architectureContext;
  private NodeListOptional architectureDeclarations, concurrentStatements;

  public DesignFileEmitter(VHDLProject project) {
    this.project = project;
  }

  public void emit(VIPath path) throws Exception {
    LOGGER.debug("Emitting VHDL from VI: {}", path);
    InstantiableName element = Identifier.parse(path.getBaseName());
    LOGGER.debug("Target project element: {}", element);
    if (element instanceof ComponentName) {
      LOGGER.info("Component will be emitted together with accompanying architecture.");
      return;
    }
    VIDump theVi = VIParser.parseVI(project.tools(), path);
    reset();
    VIParser.visitVI(theVi, PrintingVisitor.create());
    VIParser.visitVI(theVi, this);
  }

  private void reset() {
    // Context.
    rootDiagram = rootPanel = null;
    terminals = Maps.newHashMap();
    wires = Multimaps.newListMultimap(Maps.<UID, Collection<TerminalMetadata>>newHashMap(),
        new Supplier<List<TerminalMetadata>>() {
          @Override
          public List<TerminalMetadata> get() {
            return Lists.newArrayList();
          }
        });
    // Results.
    entityContext = architectureContext = null;
    concurrentStatements = new NodeListOptional();
    architectureDeclarations = new NodeListOptional();
  }

  private VHDL93Parser parser(String string) {
    return new VHDL93Parser(new StringReader(string));
  }

  @Override
  public void Diagram(Optional<UID> owner, UID uid) {
    if (!owner.isPresent()) {
      rootDiagram = uid;
    }
  }

  @Override
  public void Panel(Optional<UID> owner, UID uid) {
    if (!owner.isPresent()) {
      rootPanel = uid;
    }
  }

  @Override
  public void Terminal(UID owner, UID uid, UID wire, boolean isSource, String name) {
    TerminalMetadata terminal = new TerminalMetadata(isSource, name);
    terminals.put(uid, terminal);
    wires.put(wire, terminal);
  }

  @Override
  public void Wire(UID owner, UID uid, Optional<String> label) throws Exception {
    if (label.isPresent()) {
      Collection<TerminalMetadata> terms = wires.get(uid);
      VHDL93Parser labelParser = parser(label.get());
      expression parsed = labelParser.expression();
      labelParser.eof();
      for (TerminalMetadata terminal : terms) {
        if (terminal.isSource()) {
          SemanticException.check(!terminal.lvalue().isPresent(),
              "Multiple l-value specifications for terminal.");
          terminal.lvalue(parsed);
        } else {
          SemanticException.check(!terminal.rvalue().isPresent(),
              "Multiple r-value specifications for terminal.");
          terminal.rvalue(parsed);
        }
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
      SemanticException.check(process.nodeChoice.choice instanceof process_statement,
          "Statement is not a process declaration contrary to what label claims.");
      concurrentStatements.nodes.add(process);
    } else {
      boolean lvalue = false, rvalue = false;
      expression parsed = null;
      for (UID term : terms) {
        TerminalMetadata terminal = terminals.get(term);
        Verify.verifyNotNull(terminal);
        String param = terminal.name();
        lvalue |= param.equals(LVALUE_PARAMETER);
        rvalue |= param.equals(RVALUE_PARAMETER);
        if (lvalue || rvalue) {
          SemanticException.check(!terminal.lvalue().isPresent(),
              "Multiple l-value specifications for terminal.");
          SemanticException.check(!terminal.rvalue().isPresent(),
              "Multiple r-value specifications for terminal.");
          if (parsed == null) {
            parsed = contentParser.expression();
          }
        }
        if (lvalue) {
          SemanticException.check(!terminal.isSource(), "L-value must be data sink.");
          terminal.lvalue(parsed);
        } else if (rvalue) {
          SemanticException.check(terminal.isSource(), "R-value must be data source.");
          terminal.rvalue(parsed);
        }
      }
      SemanticException.check(!lvalue || !rvalue, "Expression cannot be both l- and r-value.");
      if (!lvalue && !rvalue) {
        // It must be a concurrent statement then...
        concurrentStatements.nodes.add(contentParser.concurrent_statement());
      }
    }
    contentParser.eof();
  }

  @Override
  public void CompoundArithmetic(UID owner, UID uid, List<UID> terms) {
    // We do not need to loo at these.
  }

  @Override
  public void Bundler(UID owner, UID uid, List<UID> terms) {
    // TODO
  }

  @Override
  public void Unbundler(UID owner, UID uid, List<UID> terms) {
    // TODO
  }

  @Override
  public void Control(UID owner, UID uid, Optional<String> label, UID terminal, boolean isIndicator,
      int style, Optional<Integer> representation, int controlIndex) {
    // TODO
  }

  @Override
  public void RingConstant(UID owner, UID uid, UID terminal, Map<String, Object> stringsAndValues) {
    // TODO
  }

  @Override
  public void SubVI(UID owner, UID uid, List<UID> terms, VIPath viPath) {
    // TODO
  }
}
