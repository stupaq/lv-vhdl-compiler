package stupaq.lv2vhdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;

import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.architecture_declarative_part;
import stupaq.vhdl93.ast.architecture_identifier;
import stupaq.vhdl93.ast.architecture_statement_part;
import stupaq.vhdl93.ast.component_identifier;
import stupaq.vhdl93.ast.concurrent_statement;
import stupaq.vhdl93.ast.constant_declaration;
import stupaq.vhdl93.ast.context_clause;
import stupaq.vhdl93.ast.entity_declarative_part;
import stupaq.vhdl93.ast.entity_identifier;
import stupaq.vhdl93.ast.entity_name;
import stupaq.vhdl93.ast.instantiated_unit;
import stupaq.vhdl93.ast.instantiation_label;
import stupaq.vhdl93.ast.interface_constant_declaration;
import stupaq.vhdl93.ast.interface_declaration;
import stupaq.vhdl93.ast.interface_signal_declaration;

class VHDL93PartialParser {
  private static final Logger LOGGER = LoggerFactory.getLogger(VHDL93PartialParser.class);
  private final VHDL93Parser parser;

  private VHDL93PartialParser(VHDL93Parser parser) {
    this.parser = parser;
  }

  public static VHDL93PartialParser parser(String string) {
    LOGGER.trace("Parsing: {}", string);
    return new VHDL93PartialParser(new VHDL93Parser(new StringReader(string)));
  }

  public entity_identifier entity_identifier() throws ParseException {
    entity_identifier r = parser.entity_identifier();
    parser.eof();
    return r;
  }

  public component_identifier component_identifier() throws ParseException {
    component_identifier r = parser.component_identifier();
    parser.eof();
    return r;
  }

  public interface_constant_declaration interface_constant_declaration() throws ParseException {
    interface_constant_declaration r = parser.interface_constant_declaration();
    parser.eof();
    return r;
  }

  public interface_signal_declaration interface_signal_declaration() throws ParseException {
    interface_signal_declaration r = parser.interface_signal_declaration();
    parser.eof();
    return r;
  }

  public context_clause context_clause() throws ParseException {
    context_clause r = parser.context_clause();
    parser.eof();
    return r;
  }

  public entity_declarative_part entity_declarative_part() throws ParseException {
    entity_declarative_part r = parser.entity_declarative_part();
    parser.eof();
    return r;
  }

  public architecture_identifier architecture_identifier() throws ParseException {
    architecture_identifier r = parser.architecture_identifier();
    parser.eof();
    return r;
  }

  public entity_name entity_name() throws ParseException {
    entity_name r = parser.entity_name();
    parser.eof();
    return r;
  }

  public architecture_declarative_part architecture_declarative_part() throws ParseException {
    architecture_declarative_part r = parser.architecture_declarative_part();
    parser.eof();
    return r;
  }

  public architecture_statement_part architecture_statement_part() throws ParseException {
    architecture_statement_part r = parser.architecture_statement_part();
    parser.eof();
    return r;
  }

  public concurrent_statement concurrent_statement() throws ParseException {
    concurrent_statement r = parser.concurrent_statement();
    parser.eof();
    return r;
  }

  public constant_declaration constant_declaration() throws ParseException {
    constant_declaration r = parser.constant_declaration();
    parser.eof();
    return r;
  }

  public instantiated_unit instantiated_unit() throws ParseException {
    instantiated_unit r = parser.instantiated_unit();
    parser.eof();
    return r;
  }

  public interface_declaration interface_declaration() throws ParseException {
    interface_declaration r = parser.interface_declaration();
    parser.eof();
    return r;
  }

  public instantiation_label instantiation_label() throws ParseException {
    instantiation_label r = parser.instantiation_label();
    parser.eof();
    return r;
  }
}
