package stupaq.concepts;

import stupaq.naming.ArchitectureName;
import stupaq.naming.ComponentName;
import stupaq.naming.Identifier;
import stupaq.project.LVProject;
import stupaq.vhdl2lv.IOSinks;
import stupaq.vhdl2lv.IOSources;
import stupaq.vhdl2lv.UniversalVI;
import stupaq.vhdl93.ast.component_declaration;

public class ComponentDeclaration extends InterfaceDeclaration {
  private boolean materialised = false;

  public ComponentDeclaration(ArchitectureName arch, component_declaration node) {
    super(Identifier.component(arch, node), node.component_header);
  }

  @Override
  public ComponentName name() {
    return (ComponentName) super.name();
  }

  @Override
  public void materialiseVI(LVProject project, IOSources namedSources, IOSinks danglingSinks) {
    if (materialised) {
      return;
    }
    materialised = true;
    // Create all generics, ports and eventually the VI itself.
    new UniversalVI(project, name(), this, namedSources, danglingSinks);
  }
}
