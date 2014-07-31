package stupaq.translation

import java.io.PrintStream

import stupaq.vhdl93.ParseException

object ExceptionPrinter {

  def print(ex: Exception, stream: PrintStream) {
    stream println "Translation error encountered:"
    ex match {
      case _: SemanticException =>
        stream println ex.getMessage
      case _: MissingFeatureException =>
        stream println ex.getMessage
      case _ =>
        ex printStackTrace stream
    }
  }
}

