package stupaq.concepts;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Map;

import stupaq.naming.ArchitectureName;
import stupaq.naming.EntityName;
import stupaq.naming.InterfaceName;
import stupaq.vhdl93.ast.architecture_declarative_part;
import stupaq.vhdl93.ast.block_declarative_item;
import stupaq.vhdl93.ast.component_declaration;
import stupaq.vhdl93.ast.package_declarative_item;
import stupaq.vhdl93.visitor.NonTerminalsNoOpVisitor;

public class ComponentBindingResolver {
  private final Map<EntityName, ArchitectureName> architectures = Maps.newHashMap();
  private final Map<InterfaceName, EntityDeclaration> global = Maps.newHashMap();
  private Map<InterfaceName, ComponentDeclaration> local;

  public void defaultArchitecture(ArchitectureName name) {
    architectures.put(name.entity(), name);
  }

  public ArchitectureName defaultArchitecture(EntityName name) {
    return architectures.get(name);
  }

  public EntityDeclaration getGlobal(InterfaceName name) {
    return global.get(name);
  }

  public void addGlobal(EntityDeclaration entity) {
    global.put(entity.name(), entity);
  }

  public ComponentDeclaration getLocal(InterfaceName name) {
    Preconditions.checkState(local != null);
    return local.get(name);
  }

  public void enterLocal(final ArchitectureName name, architecture_declarative_part n) {
    local = Maps.newHashMap();
    n.accept(new NonTerminalsNoOpVisitor<Void>() {
      @Override
      public void visit(architecture_declarative_part n) {
        n.nodeListOptional.accept(this);
      }

      @Override
      public void visit(block_declarative_item n) {
        n.nodeChoice.choice.accept(this);
      }

      @Override
      public void visit(component_declaration n) {
        ComponentDeclaration component = new ComponentDeclaration(name, n);
        local.put(component.name(), component);
      }

      @Override
      public void visit(package_declarative_item n) {
        n.nodeChoice.choice.accept(this);
      }
    });
  }

  public void exitLocal() {
    local = null;
  }

  public InterfaceDeclaration get(InterfaceName name) {
    InterfaceDeclaration iface = getLocal(name);
    if (iface == null) {
      iface = getGlobal(name);
    }
    return iface;
  }

  public Iterable<ComponentDeclaration> getLocalComponents() {
    return local.values();
  }
}
