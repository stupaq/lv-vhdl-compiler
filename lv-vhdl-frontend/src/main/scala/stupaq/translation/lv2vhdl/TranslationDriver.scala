package stupaq.translation.lv2vhdl

import java.nio.file.Paths

import stupaq.labview.VIPath
import stupaq.translation.ExceptionPrinter
import stupaq.translation.project.VHDLProject

import scala.collection.JavaConverters._

object TranslationDriver {

  def main(args: Array[String]) {
    try {
      if (args.length >= 2) {
        val roots = args.toStream dropRight 1 map (new VIPath(_))
        val project = new VHDLProject(Paths get args.last, roots.asJava)
        for (path <- project.iterator.asScala) {
          new VIInstance(project, path) emitAsVHDL()
        }
      } else {
        println("usage: <filename1> <filename2> ... <destination>")
      }
    } catch {
      case e: Exception => ExceptionPrinter print(e, System.err)
    }
  }
}

