package stupaq.translation.project

import java.io.IOException
import java.nio.file.{Files, Path}

import stupaq.labview.VIPath
import stupaq.labview.scripting.ScriptingTools
import stupaq.labview.scripting.activex.ActiveXScriptingTools

class LVProject(private val root: Path) {

  var tools: ScriptingTools = new ActiveXScriptingTools()

  def allocate(name: ProjectElementName, overwrite: Boolean): VIPath = {
    val path = resolve(name)
    if (overwrite) {
      try
        Files deleteIfExists path.path
      catch {
        case ex: IOException => throw new RuntimeException(ex);
      }
    }
    try
      Files createDirectories path.path().getParent
    catch {
      case ignored: IOException =>
    }
    path
  }

  def resolve(lvName: ProjectElementName): VIPath = new
      VIPath(root, s"${lvName.elementName}.vi")
}
