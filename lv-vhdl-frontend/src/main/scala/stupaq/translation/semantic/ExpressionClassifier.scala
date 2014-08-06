package stupaq.translation.semantic

import java.io.StringReader
import java.util

import com.google.common.base.Optional
import stupaq.translation.naming.IOReference
import stupaq.vhdl93.ast.Builders.sequence
import stupaq.vhdl93.ast.{SimpleNode, identifier}
import stupaq.vhdl93.{ParseException, VHDL93ParserTotal}

import scala.annotation.tailrec

object ExpressionClassifier {

  @tailrec
  private def unwrapParentheses(expression: String): String = {
    if ((expression startsWith "(") && (expression endsWith ")")) {
      unwrapParentheses(expression substring(1, expression.length - 1))
    } else {
      expression
    }
  }

  def topLevelReferences(n: SimpleNode): util.List[IOReference] = {
    val identifiers = new util.ArrayList[IOReference]()
    sequence(n).accept(new RValueVisitor() {

      override def topLevelScope(ref: IOReference) {
        identifiers add ref
      }
    })
    identifiers
  }

  def isIdentifier(n: SimpleNode): Boolean = asIdentifier(n).isPresent

  def asIdentifier(n: SimpleNode): Optional[identifier] = {
    val rep = unwrapParentheses(n representation())
    try {
      val parser = new VHDL93ParserTotal(new StringReader(rep))
      val id = parser identifier()
      parser eof()
      Optional of id
    } catch {
      case ignored: ParseException => Optional absent()
    }
  }
}

