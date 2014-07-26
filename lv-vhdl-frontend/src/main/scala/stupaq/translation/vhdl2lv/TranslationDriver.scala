package stupaq.translation.vhdl2lv

import java.io.FileInputStream
import java.nio.file.Paths

import stupaq.translation.ExceptionPrinter
import stupaq.translation.project.LVProject
import stupaq.vhdl93.VHDL93Parser
import stupaq.vhdl93.ast.{NodeList, design_file}

import scala.collection.JavaConverters._

object TranslationDriver {

  def main(args: Array[String]) {
    try {
      if (args.length >= 2) {
        val units = args.toStream dropRight 1 map (new FileInputStream(_)) flatMap
                    (new VHDL93Parser(_).design_file().nodeList.nodes.asScala)
        val list = new NodeList()
        for (unit <- units) {
          list addNode unit
        }
        val root = new design_file(list)
        val project = new LVProject(Paths get args.last)
        root accept new DesignFileEmitter(project)
      } else {
        println("usage: <filename1> <filename2> ... <destination>")
      }
    } catch {
      case e: Exception => ExceptionPrinter print(e, System.err)
    }
  }
}
