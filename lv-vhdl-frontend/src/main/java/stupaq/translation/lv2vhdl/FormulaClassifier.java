package stupaq.translation.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.VerifyException;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import stupaq.labview.UID;
import stupaq.labview.parsing.NoOpVisitor;

import static stupaq.translation.TranslationConventions.*;

class FormulaClassifier extends NoOpVisitor<Exception> {
  private final EndpointsMap terminals;
  private final Map<UID, FormulaContext> formulaContext = Maps.newHashMap();

  public FormulaClassifier(EndpointsMap terminals) {
    this.terminals = terminals;
  }

  @Override
  public Iterable<String> parsersOrder() {
    throw new VerifyException();
  }

  @Override
  public void FormulaNode(UID ownerUID, UID uid, String expression, Optional<String> label,
      List<UID> termUIDs) throws Exception {
    VHDL93PartialParser parser = VHDL93PartialParser.parser(expression);
    if (label.equals(ENTITY_CONTEXT)) {
    } else if (label.equals(ENTITY_EXTRA_DECLARATIONS)) {
    } else if (label.equals(ARCHITECTURE_CONTEXT)) {
    } else if (label.equals(ARCHITECTURE_EXTRA_DECLARATIONS)) {
    } else if (label.equals(ARCHITECTURE_EXTRA_STATEMENTS)) {
    } else if (label.equals(PROCESS_STATEMENT)) {
    } else {
      boolean lvalue = false, rvalue = false;
      for (UID term : termUIDs) {
        Endpoint terminal = terminals.get(term);
        String param = terminal.name();
        lvalue |= param.equals(LVALUE_PARAMETER);
        rvalue |= param.equals(RVALUE_PARAMETER);
      }
    }
  }

  public enum FormulaContext {
  }
}
