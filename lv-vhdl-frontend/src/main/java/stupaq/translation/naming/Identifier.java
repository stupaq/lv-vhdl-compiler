package stupaq.translation.naming;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stupaq.translation.errors.MissingFeatureException;
import stupaq.translation.vhdl2lv.ComponentBindingResolver;
import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.architecture_declaration;
import stupaq.vhdl93.ast.component_declaration;
import stupaq.vhdl93.ast.component_identifier;
import stupaq.vhdl93.ast.configuration_name;
import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.ast.entity_name;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.instantiated_unit;
import stupaq.vhdl93.visitor.NonTerminalsNoOpVisitor;

import static java.util.regex.Pattern.compile;
import static stupaq.translation.errors.LocalisedSemanticException.semanticCheck;
import static stupaq.translation.errors.LocalisedSemanticException.semanticNotNull;
import static stupaq.translation.errors.MissingFeatureException.missingIf;
import static stupaq.translation.naming.LibraryName.DEFAULT_LIBRARY;
import static stupaq.translation.naming.LibraryName.LIBRARY_SEPARATOR;

public class Identifier {
  private static final Pattern INSTANTIABLE_NAME_PATTERN = compile(
      "(?:|(?<lib>[^()]+))\\.(?<ent>[^.()]+)\\((?<arch>[^.()]+)\\)(?:|\\.(?<comp>[^.()]+))");
  private static final Pattern ENTITY_INSTANTIATION_PATTERN =
      compile("(?:|(?<lib>[^()]+))\\.(?<ent>[^.()]+)(?:|\\((?<arch>[^.()]+)\\))");
  private final String string;

  public Identifier(identifier n) {
    this(n.representation());
  }

  protected Identifier(String string) {
    Preconditions.checkNotNull(string);
    string = string.trim().toLowerCase();
    Preconditions.checkArgument(!CharMatcher.WHITESPACE.matchesAnyOf(string));
    this.string = string;
  }

  public static EntityName entity(entity_declaration n) {
    return new EntityName(DEFAULT_LIBRARY, new Identifier(n.entity_identifier.identifier));
  }

  public static EntityName entity(entity_name n) {
    String id = n.firstName();
    id = id.substring(id.lastIndexOf(LIBRARY_SEPARATOR) + 1);
    return new EntityName(DEFAULT_LIBRARY, new Identifier(id));
  }

  public static ComponentName component(ArchitectureName arch, component_declaration n) {
    return new ComponentName(arch, new Identifier(n.component_identifier.identifier));
  }

  public static ArchitectureName architecture(EntityName entity, architecture_declaration n) {
    return new ArchitectureName(entity, new Identifier(n.architecture_identifier.identifier));
  }

  public static InstantiableName instantiation(final ComponentBindingResolver resolver,
      final ArchitectureName architecture, instantiated_unit n) {
    return (new NonTerminalsNoOpVisitor<InstantiableName>() {
      InstantiableName name;

      @Override
      public InstantiableName apply(Node n) {
        super.apply(n);
        Verify.verifyNotNull(name);
        return name;
      }

      @Override
      public void visit(component_identifier n) {
        name = new ComponentName(architecture, new Identifier(n.identifier));
      }

      @Override
      public void visit(entity_name n) {
        String string = n.representation();
        Matcher matcher = ENTITY_INSTANTIATION_PATTERN.matcher(string);
        semanticCheck(matcher.matches(), "Invalid entity instantiation: %s.", string);
        String library = matcher.group("lib"), entity = matcher.group("ent"), architecture =
            matcher.group("arch");
        missingIf(!DEFAULT_LIBRARY.toString().equals(library),
            "Non-default libraries: %s are not supported.", library);
        EntityName entityName = new EntityName(DEFAULT_LIBRARY, new Identifier(entity));
        if (architecture != null) {
          name = new ArchitectureName(entityName, new Identifier(architecture));
        } else {
          name = resolver.defaultArchitecture(entityName);
          semanticNotNull(name, n, "Missing default architecture for: %s", entity);
        }
      }

      @Override
      public void visit(configuration_name n) {
        throw new MissingFeatureException(n, "Configurations are not supported.");
      }
    }).apply(n.nodeChoice.choice);
  }

  public static InstantiableName parse(String string) {
    Matcher matcher = INSTANTIABLE_NAME_PATTERN.matcher(string);
    semanticCheck(matcher.matches(), "Invalid instantiable name: %s.", string);
    String library = matcher.group("lib"), entity = matcher.group("ent"), architecture =
        matcher.group("arch"), component = matcher.group("comp");
    missingIf(!DEFAULT_LIBRARY.toString().equals(library),
        "Non-default libraries: %s are not supported.", library);
    EntityName entityName = new EntityName(DEFAULT_LIBRARY, new Identifier(entity));
    ArchitectureName archName = new ArchitectureName(entityName, new Identifier(architecture));
    return component == null ? archName : new ComponentName(archName, new Identifier(component));
  }

  @Override
  public int hashCode() {
    return string.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    return this == o ||
        !(o == null || getClass() != o.getClass()) && string.equals(((Identifier) o).string);
  }

  @Override
  public String toString() {
    return string;
  }
}
