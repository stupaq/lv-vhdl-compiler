package stupaq.translation.lv2vhdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stupaq.labview.VIPath;
import stupaq.labview.scripting.tools.HighlightByUID;
import stupaq.translation.naming.ArchitectureName;
import stupaq.translation.naming.Identifier;
import stupaq.translation.naming.InstantiableName;
import stupaq.translation.project.LVProjectReader;
import stupaq.translation.project.VHDLProjectWriter;

class TranslationContext {
  private static final Logger LOGGER = LoggerFactory.getLogger(TranslationContext.class);
  private final LVProjectReader projectFrom;
  private final VHDLProjectWriter projectTo;
  private final InterfaceDeclarationCache declarationCache;

  public TranslationContext(LVProjectReader projectFrom, VHDLProjectWriter projectTo)
      throws Exception {
    this.projectFrom = projectFrom;
    this.projectTo = projectTo;
    declarationCache = new InterfaceDeclarationCache(projectFrom);
  }

  public void translate(VIPath path) throws Exception {
    try {
      InstantiableName element = Identifier.parse(path.getBaseName());
      LOGGER.debug("VI instance: {} refers to: {}", path, element);
      if (!(element instanceof ArchitectureName)) {
        LOGGER.warn("Component will be emitted together with accompanying architecture.");
        return;
      }
      ArchitectureName name = (ArchitectureName) element;
      ParsedVI theVi = new ParsedVI(projectFrom.tools(), path);
      InterfaceDeclaration entity = new InterfaceDeclaration(theVi);
      declarationCache.fill(path, entity);
      projectTo.writeEntity(name.entity(), entity.emitAsEntity(name.entity()));
      ArchitectureDefinition architecture =
          new ArchitectureDefinition(declarationCache, projectFrom, theVi);
      projectTo.writeArchitecture(name, architecture.emitAsArchitecture(name));
    } catch (LocalisedException e) {
      if (e.isLocalised()) {
        projectFrom.tools().get(HighlightByUID.class).apply(e.getVI(), e.getUID());
      }
      throw e.getCause();
    }
  }
}
