package stupaq.types;

import com.github.sviperll.adt4j.ValueVisitor;

import javax.annotation.Nonnull;

@ValueVisitor(resultVariableName = "R", valueClassIsPublic = true)
public interface NamedTypeVisitor<R> {
  R named(@Nonnull String name);
}
