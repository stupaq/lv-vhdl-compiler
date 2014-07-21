package stupaq;

import com.google.common.base.Optional;

import static com.google.common.base.Optional.of;

public interface TranslationConventions {
  Optional<String> ENTITY_CONTEXT = of("ENTITY CONTEXT");
  Optional<String> ENTITY_EXTRA_DECLARATIONS = of("ENTITY EXTRA DECLARATIONS");
  Optional<String> ARCHITECTURE_CONTEXT = of("ARCHITECTURE CONTEXT");
  Optional<String> ARCHITECTURE_EXTRA_DECLARATIONS = of("ARCHITECTURE EXTRA DECLARATIONS");
  Optional<String> ARCHITECTURE_EXTRA_STATEMENTS = of("ARCHITECTURE EXTRA STATEMENTS");
  Optional<String> PROCESS_STATEMENT = of("PROCESS");
  String RVALUE_PARAMETER = "RESULT";
  String LVALUE_PARAMETER = "ASSIGNEE";
  String INPUTS_CONTROL = "INPUTS";
  String OUTPUTS_CONTROL = "OUTPUTS";
}
