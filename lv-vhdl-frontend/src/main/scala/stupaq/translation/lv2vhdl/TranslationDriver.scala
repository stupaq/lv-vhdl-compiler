package stupaq.translation.lv2vhdl

import java.nio.file.Paths

import stupaq.labview.VIPath
import stupaq.translation.ExceptionPrinter
import stupaq.translation.project.{LVProjectReader, VHDLProjectWriter}

import scala.collection.JavaConverters._

object TranslationDriver {

  def main(args: Array[String]) {
    try {
      if (args.length >= 2) {
        val roots = args.toStream dropRight 1 map (new VIPath(_))
        val projectFrom = new LVProjectReader(roots.asJava)
        val projectTo = new VHDLProjectWriter(Paths get args.last)
        val context = new TranslationContext(projectFrom, projectTo)
        for (path <- projectFrom.asScala) {
          context translate path
        }
      } else {
        println("usage: <filename1> <filename2> ... <destination>")
      }
    } catch {
      case e: Exception => ExceptionPrinter print(e, System.err)
    }
  }
}

