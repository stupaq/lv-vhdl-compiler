package stupaq.concepts;

import com.google.common.base.Verify;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.ast.interface_constant_declaration;
import stupaq.vhdl93.ast.interface_signal_declaration;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

public class EntityDeclaration extends VHDLElement<entity_declaration> {
  private final EntityName name;
  private final List<ConstantDeclaration> generics = Lists.newArrayList();
  private final List<PortDeclaration> ports = Lists.newArrayList();
  private final Map<IOReference, Integer> listIndex = Maps.newHashMap();

  public EntityDeclaration(entity_declaration node) {
    super(node);
    name = new EntityName(node);
    node.entity_header.accept(new DepthFirstVisitor() {
      @Override
      public void visit(interface_constant_declaration n) {
        addDeclaration(generics, new ConstantDeclaration(n));
      }

      @Override
      public void visit(interface_signal_declaration n) {
        addDeclaration(ports, new PortDeclaration(n));
      }

      private <T extends TypedReferenceDeclaration> void addDeclaration(List<T> list,
          T declaration) {
        Integer previous = listIndex.put(declaration.reference(), list.size());
        Verify.verify(previous == null);
        list.add(declaration);
      }
    });
  }

  public EntityName name() {
    return name;
  }

  public List<ConstantDeclaration> generics() {
    return generics;
  }

  public List<PortDeclaration> ports() {
    return ports;
  }

  public Map<IOReference, Integer> listIndex() {
    return listIndex;
  }

  @Override
  public String toString() {
    return "EntityDeclaration{" + "generics=" + generics + ", ports=" + ports + '}';
  }
}
