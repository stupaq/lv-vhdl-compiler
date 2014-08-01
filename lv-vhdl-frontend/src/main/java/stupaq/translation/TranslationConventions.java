package stupaq.translation;

import com.google.common.base.Optional;

import static com.google.common.base.Optional.of;

public interface TranslationConventions {
  Optional<String> ENTITY_CONTEXT = of("ENTITY CONTEXT");
  Optional<String> ENTITY_EXTRA_DECLARATIONS = of("ENTITY EXTRA DECLARATIONS");
  Optional<String> ARCHITECTURE_CONTEXT = of("ARCHITECTURE CONTEXT");
  Optional<String> ARCHITECTURE_EXTRA_DECLARATIONS = of("ARCHITECTURE EXTRA DECLARATIONS");
  String RVALUE_PARAMETER = "RESULT";
  String LVALUE_PARAMETER = "ASSIGNEE";
  int INPUTS_CONN_INDEX = 1;
  int OUTPUTS_CONN_INDEX = 0;
}
