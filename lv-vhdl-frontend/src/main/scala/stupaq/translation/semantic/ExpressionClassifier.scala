package stupaq.translation.semantic

import java.io.StringReader
import java.util

import com.google.common.base.Optional
import stupaq.translation.naming.IOReference
import stupaq.vhdl93.ast.Builders.sequence
import stupaq.vhdl93.ast.{SimpleNode, identifier, subtype_indication}
import stupaq.vhdl93.visitor.DepthFirstVisitor
import stupaq.vhdl93.{ParseException, VHDL93Parser}

object ExpressionClassifier {

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
      val parser = new VHDL93Parser(new StringReader(rep))
      val id = parser identifier()
      parser eof()
      Optional of id
    } catch {
      case ignored: ParseException => Optional absent()
    }
  }

  def isParametrisedType(indication: subtype_indication): Boolean = {
    try {
      indication.accept(new DepthFirstVisitor() {

        override def visit(n: subtype_indication) {
          if ((n.nodeOptional present()) || (n.nodeOptional1 present())) {
            throw new VisitorBreakingException()
          }
          n.type_name.nodeOptional accept this
        }

        override def visit(n: identifier) {
          throw new VisitorBreakingException()
        }
      })
      false
    } catch {
      case ignored: VisitorBreakingException => true
    }
  }

  private class VisitorBreakingException extends RuntimeException

}

