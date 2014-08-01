package stupaq.translation.vhdl2lv;

import com.google.common.base.Optional;

import static com.google.common.base.Optional.of;

interface TranslationConventions {
  Optional<String> INPUTS_CONTROL = of("INPUTS");
  Optional<String> OUTPUTS_CONTROL = of("OUTPUTS");
}
