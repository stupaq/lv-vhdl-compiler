package stupaq.concepts;

import stupaq.naming.ArchitectureName;
import stupaq.naming.ComponentName;
import stupaq.naming.Identifier;
import stupaq.vhdl93.ast.component_declaration;

public class ComponentDeclaration extends InterfaceDeclaration {
  public ComponentDeclaration(ArchitectureName arch, component_declaration node) {
    super(Identifier.component(arch, node), node.component_header);
  }

  @Override
  public ComponentName name() {
    return (ComponentName) super.name();
  }
}
