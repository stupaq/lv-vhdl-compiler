package stupaq.translation

import java.io.PrintStream

import stupaq.translation.errors.TranslationException
import stupaq.translation.lv2vhdl.errors.LocalisedException

object ExceptionPrinter {

  def print(ex: Exception, stream: PrintStream) {
    ex match {
      case _: LocalisedException =>
        throw new VerifyError("Localised exception found in exception reporter.")
      case _: TranslationException =>
        stream println "Translation error encountered:"
        stream println ex.getMessage
      case _ =>
        ex printStackTrace stream
    }
  }
}

