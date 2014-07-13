package stupaq.vhdl2lv;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Map;

import stupaq.concepts.ComponentDeclaration;
import stupaq.concepts.ComponentName;
import stupaq.concepts.EntityDeclaration;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.block_declarative_item;
import stupaq.vhdl93.ast.component_declaration;
import stupaq.vhdl93.ast.package_declarative_item;
import stupaq.vhdl93.visitor.NonTerminalsNoOpVisitor;

public class ComponentResolver {
  private final Map<ComponentName, EntityDeclaration> global = Maps.newHashMap();
  private Map<ComponentName, ComponentDeclaration> local;

  public EntityDeclaration getGlobal(ComponentName name) {
    return global.get(name);
  }

  public void addGlobal(EntityDeclaration entity) {
    global.put(entity.name(), entity);
  }

  public ComponentDeclaration getLocal(ComponentName name) {
    Preconditions.checkState(local != null);
    return local.get(name);
  }

  public void enterLocal(SimpleNode n) {
    local = Maps.newHashMap();
    n.accept(new NonTerminalsNoOpVisitor<Void>() {
      @Override
      public void visit(block_declarative_item n) {
        n.nodeChoice.accept(this);
      }

      @Override
      public void visit(package_declarative_item n) {
        n.nodeChoice.accept(this);
      }

      @Override
      public void visit(component_declaration n) {
        ComponentDeclaration component = new ComponentDeclaration(n);
        local.put(component.name(), component);
      }
    });
  }

  public void exitLocal() {
    local = null;
  }

  public ComponentDeclaration get(ComponentName name) {
    ComponentDeclaration component = getLocal(name);
    if (component == null) {
      component = getGlobal(name);
    }
    return component;
  }
}
