package stupaq.translation.lv2vhdl.parsing;

import com.google.common.reflect.Reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import stupaq.translation.semantic.FlattenNestedListsVisitor;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.VHDL93ParserTotal;
import stupaq.vhdl93.ast.Node;

public interface VHDL93ParserPartial extends VHDL93Parser {
  public static final Logger LOGGER = LoggerFactory.getLogger(VHDL93ParserPartial.class);

  public static final class Parsers {
    private Parsers() {
    }

    public static VHDL93ParserPartial forString(String string) {
      LOGGER.trace("Parsing: {}", string);
      return Reflection.newProxy(VHDL93ParserPartial.class, new ParserHandler(string));
    }

    private static final class ParserHandler implements InvocationHandler {
      private final VHDL93ParserTotal parser;

      public ParserHandler(String string) {
        parser = new VHDL93ParserTotal(new StringReader(string));
      }

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
          Object result = method.invoke(parser, args);
          parser.eof();
          if (result instanceof Node) {
            ((Node) result).accept(new FlattenNestedListsVisitor());
          }
          return result;
        } catch (InvocationTargetException e) {
          Throwable t = e.getTargetException();
          if (t instanceof ParseException) {
            throw t;
          } else {
            throw e;
          }
        }
      }
    }
  }
}
