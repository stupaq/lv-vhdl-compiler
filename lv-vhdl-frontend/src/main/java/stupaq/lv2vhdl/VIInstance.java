package stupaq.lv2vhdl;

import com.ni.labview.VIDump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import stupaq.labview.VIPath;
import stupaq.labview.parsing.PrintingVisitor;
import stupaq.labview.parsing.VIParser;
import stupaq.naming.ArchitectureName;
import stupaq.naming.Identifier;
import stupaq.naming.InstantiableName;
import stupaq.project.VHDLProject;
import stupaq.vhdl93.ast.design_file;
import stupaq.vhdl93.visitor.PositionResettingVisitor;
import stupaq.vhdl93.visitor.TreeDumper;
import stupaq.vhdl93.visitor.VHDLTreeFormatter;

import static com.google.common.collect.Maps.immutableEntry;
import static stupaq.vhdl93.ast.ASTBuilders.list;

class VIInstance {
  private static final Logger LOGGER = LoggerFactory.getLogger(VIInstance.class);
  private final VHDLProject project;
  private final ArchitectureName name;
  private final ArchitectureDefinition architecture;
  private final InterfaceDeclaration entity;

  public VIInstance(VHDLProject project, VIPath path) throws Exception {
    this.project = project;
    InstantiableName element = Identifier.parse(path.getBaseName());
    LOGGER.debug("VI instance: {} refers to: {}", path, element);
    if (!(element instanceof ArchitectureName)) {
      throw new IllegalArgumentException(
          "Component will be emitted together with accompanying architecture.");
    }
    name = (ArchitectureName) element;
    VIDump theVi = VIParser.parseVI(project.tools(), path);
    VIParser.visitVI(theVi, PrintingVisitor.create());
    entity = new InterfaceDeclaration(theVi);
    architecture = new ArchitectureDefinition(project, entity, theVi);
  }

  public void emitAsVHDL() throws Exception {
    List<Entry<Path, design_file>> files = Arrays.asList(
        immutableEntry(project.allocate(name.entity(), true),
            new design_file(list(entity.emitAsEntity(name.entity())))),
        immutableEntry(project.allocate(name, true),
            new design_file(list(architecture.emitAsArchitecture(name)))));
    for (Entry<Path, design_file> entry : files) {
      Path path = entry.getKey();
      design_file file = entry.getValue();
      try (OutputStream output = new FileOutputStream(path.toFile())) {
        file.accept(new PositionResettingVisitor());
        file.accept(new VHDLTreeFormatter());
        System.out.println("DESIGN UNIT");
        file.accept(new TreeDumper(System.out));
        System.out.println();
        // TODO unit.accept(new TreeDumper(output));
      }
    }
  }
}