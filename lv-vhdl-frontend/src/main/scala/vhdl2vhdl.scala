import java.io.FileInputStream

import stupaq.translation.ExceptionPrinter
import stupaq.translation.semantic.FlattenNestedListsVisitor
import stupaq.vhdl93.VHDL93ParserTotal
import stupaq.vhdl93.formatting.VHDLTreeFormatter
import stupaq.vhdl93.visitor.TreeDumper

object vhdl2vhdl {

  def main(args: Array[String]) {
    try {
      if (args.length == 1) {
        val file = new FileInputStream(args(0))
        val parser = new VHDL93ParserTotal(file)
        val root = parser.design_file()
        root.accept(new TreeDumper(System.out))
        println()
        println(new String(new Array[Char](40)).replace('\u0000', '-'))
        root.accept(new FlattenNestedListsVisitor())
        root.accept(new VHDLTreeFormatter())
        root.accept(new TreeDumper(System.out))
        println()
      } else {
        println("usage: filename")
      }
    } catch {
      case e: Exception => ExceptionPrinter.print(e, System.err)
    }
  }
}
