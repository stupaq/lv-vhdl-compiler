package stupaq.translation.vhdl2lv;

import stupaq.translation.naming.ArchitectureName;
import stupaq.translation.naming.ComponentName;
import stupaq.translation.naming.Identifier;
import stupaq.vhdl93.ast.component_declaration;

class ComponentDeclaration extends InterfaceDeclaration {
  public ComponentDeclaration(ArchitectureName arch, component_declaration node) {
    super(Identifier.component(arch, node), node.component_header);
  }

  @Override
  public ComponentName name() {
    return (ComponentName) super.name();
  }
}
