package stupaq.naming;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import stupaq.MissingFeature;
import stupaq.concepts.ComponentBindingResolver;
import stupaq.lvproject.InstanceName;
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

public class Identifier {
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

  public static EntityName entity(entity_declaration n) {
    return new EntityName(LibraryName.DEFAULT_LIBRARY,
        new Identifier(n.entity_identifier.identifier));
  }

  public static EntityName entity(entity_name n) {
    return new EntityName(LibraryName.DEFAULT_LIBRARY, new Identifier(n.firstName()));
  }

  public static ComponentName component(ArchitectureName arch, component_declaration n) {
    return new ComponentName(arch, new Identifier(n.component_identifier.identifier));
  }

  public static ArchitectureName architecture(EntityName entity, architecture_declaration n) {
    return new ArchitectureName(entity, new Identifier(n.architecture_identifier.identifier));
  }

  public static InstanceName instantiation(final ComponentBindingResolver resolver,
      final ArchitectureName architecture, instantiated_unit n) {
    return (new NonTerminalsNoOpVisitor<InstanceName>() {
      InstanceName name;

      @Override
      public InstanceName apply(Node n) {
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
        EntityName entity = entity(n);
        name = resolver.defaultArchitecture(entity);
        Verify.verifyNotNull(name, "Could not find default architecture for: %s", entity);
      }

      @Override
      public void visit(configuration_name n) {
        MissingFeature.missing("Configurations are not supported.", n);
      }
    }).apply(n.nodeChoice.choice);
  }
}
