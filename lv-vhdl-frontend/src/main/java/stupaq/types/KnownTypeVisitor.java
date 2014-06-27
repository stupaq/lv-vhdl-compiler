package stupaq.types;

import com.github.sviperll.adt4j.ValueVisitor;

@ValueVisitor(resultVariableName = "R", valueClassIsPublic = true)
public interface KnownTypeVisitor<R> {
  R integer();

  R natural();

  R bit();

  R bit_vector(int from, int to);

  R std_logic();

  R std_logic_vector(int from, int to);

  R std_ulogic();

  R std_ulogic_vector(int from, int to);

  R unsigned(int from, int to);

  R signed(int from, int to);
}
