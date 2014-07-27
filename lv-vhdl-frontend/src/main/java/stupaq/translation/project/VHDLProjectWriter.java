package stupaq.translation.project;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Map;

import stupaq.translation.naming.ArchitectureName;
import stupaq.translation.naming.EntityName;
import stupaq.vhdl93.ast.design_unit;
import stupaq.vhdl93.formatting.VHDLTreeFormatter;
import stupaq.vhdl93.visitor.TreeDumper;

public class VHDLProjectWriter {
  private static final Logger LOGGER = LoggerFactory.getLogger(VHDLProjectWriter.class);
  private static final CharMatcher FILE_REPLACE_CHARS = CharMatcher.anyOf(".(");
  private static final CharMatcher FILE_REMOVE_CHARS = CharMatcher.anyOf(")/\\");
  private static final CharSequence FILE_REPLACEMENT = "-";
  private static final String FILE_EXTENSION = ".vhd";
  private final Path root;
  private final Map<EntityName, File> entities = Maps.newHashMap();

  public VHDLProjectWriter(Path root) {
    this.root = root;
  }

  private Path resolvePath(EntityName name) {
    String filename = name.toString();
    if (filename.contains(FILE_REPLACEMENT)) {
      LOGGER.warn("Entity name: {} contains replacement char: {}.", filename, FILE_REPLACEMENT);
    }
    filename = FILE_REPLACE_CHARS.replaceFrom(filename, FILE_REPLACEMENT);
    filename = FILE_REMOVE_CHARS.removeFrom(filename);
    return root.resolve(filename + FILE_EXTENSION);
  }

  private static void writeVHDL(File file, design_unit unit, boolean append) throws IOException {
    try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, append)))) {
      unit.accept(new VHDLTreeFormatter());
      unit.accept(new TreeDumper(writer));
      writer.println();
      writer.println();
    }
  }

  public void writeEntity(EntityName name, design_unit unit) throws IOException {
    if (entities.containsKey(name)) {
      LOGGER.warn("Multiple entity: {} declarations, skipping all but first one.", name);
      return;
    }
    File file = resolvePath(name).toFile();
    writeVHDL(file, unit, false);
    entities.put(name, file);
  }

  public void writeArchitecture(ArchitectureName name, design_unit unit) throws IOException {
    File file = entities.get(name.entity());
    Preconditions.checkState(file != null, "Missing file for entity: {}.", name.entity());
    writeVHDL(file, unit, true);
  }
}
