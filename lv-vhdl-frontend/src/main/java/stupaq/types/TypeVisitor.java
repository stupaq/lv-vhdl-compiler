package stupaq.types;

import com.github.sviperll.adt4j.ValueVisitor;

import javax.annotation.Nonnull;

@ValueVisitor(resultVariableName = "R", valueClassIsPublic = true)
public interface TypeVisitor<R, NamedType, KnownType> {
  R arbitrary(@Nonnull NamedType type);

  R known(@Nonnull KnownType known);
}
