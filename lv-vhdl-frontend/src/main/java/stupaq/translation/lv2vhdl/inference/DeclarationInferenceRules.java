package stupaq.translation.lv2vhdl.inference;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import stupaq.labview.UID;
import stupaq.labview.hierarchy.Control;
import stupaq.labview.hierarchy.FormulaNode;
import stupaq.labview.scripting.tools.ControlStyle;
import stupaq.translation.errors.SemanticException;
import stupaq.translation.errors.SyntaxException;
import stupaq.translation.errors.TranslationException;
import stupaq.translation.lv2vhdl.parsing.ParsedVI;
import stupaq.translation.lv2vhdl.parsing.VHDL93ParserPartial;
import stupaq.translation.lv2vhdl.parsing.VIElementsVisitor;
import stupaq.translation.lv2vhdl.wiring.Endpoint;
import stupaq.translation.naming.IOReference;
import stupaq.translation.semantic.ExpressionClassifier;
import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.NodeListOptional;
import stupaq.vhdl93.ast.block_declarative_item;
import stupaq.vhdl93.ast.identifier_list;
import stupaq.vhdl93.ast.interface_constant_declaration;
import stupaq.vhdl93.ast.interface_signal_declaration;
import stupaq.vhdl93.ast.signal_declaration;
import stupaq.vhdl93.ast.subtype_indication;
import stupaq.vhdl93.ast.variable_declaration;

import static java.util.Arrays.asList;
import static stupaq.translation.errors.LocalisedSemanticException.semanticCheck;
import static stupaq.translation.lv2vhdl.parsing.VHDL93ParserPartial.Parsers.forString;
import static stupaq.vhdl93.ast.Builders.choice;
import static stupaq.vhdl93.ast.Builders.listOptional;
import static stupaq.vhdl93.ast.Builders.optional;

public class DeclarationInferenceRules {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeclarationInferenceRules.class);
  private final Set<IOReference> declared = Sets.newHashSet();
  private final List<block_declarative_item> inferred = Lists.newArrayList();

  public DeclarationInferenceRules(ParsedVI theVi) throws Exception {
    theVi.accept(new BuilderVisitor());
  }

  public void inferDeclaration(Endpoint terminal) {
    // Infer declaration if necessary and possible.
    if (!terminal.hasValue()) {
      return;
    }
    String valueString = terminal.valueString();
    IOReference ref;
    try {
      ref = new IOReference(forString(valueString).identifier());
    } catch (SyntaxException e) {
      LOGGER.debug("Skipping declaration inference (not an identifier) for: {}.", terminal);
      return;
    }
    if (!declared.contains(ref)) {
      subtype_indication type;
      try {
        type = forString(terminal.name()).interface_signal_declaration().subtype_indication;
      } catch (SyntaxException e) {
        LOGGER.debug("Skipping declaration inference (not a declaration) for: {}.", terminal);
        return;
      }
      if (ExpressionClassifier.isParametrisedType(type)) {
        LOGGER.debug("Skipping declaration inference (parameters) for: {}.", terminal);
        return;
      }
      declared.add(ref);
      block_declarative_item item = new block_declarative_item(choice(
          new signal_declaration(new identifier_list(ref.asIdentifier(), listOptional()), type,
              optional(), optional())));
      inferred.add(item);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Inferred declaration: <{}> for: {}", item.representation(), terminal);
      }
    }
  }

  public Collection<block_declarative_item> inferredDeclarations() {
    return inferred;
  }

  private class BuilderVisitor extends VIElementsVisitor<TranslationException> {
    @Override
    public Iterable<String> parsersOrder() {
      return asList(Control.NUMERIC_XML_NAME, FormulaNode.XML_NAME);
    }

    @Override
    public void Control(UID ownerUID, UID uid, Optional<String> label, UID terminalUID,
        boolean isIndicator, ControlStyle style, String description) {
      semanticCheck(label.isPresent(), "Missing control label (should contain port declaration).");
      String declaration = label.get().trim();
      VHDL93ParserPartial labelParser = forString(declaration);
      if (style == ControlStyle.NUMERIC_I32) {
        // This is a generic.
        interface_constant_declaration generic = labelParser.interface_constant_declaration();
        declared.add(new IOReference(generic.identifier_list.identifier));
      } else if (style == ControlStyle.NUMERIC_DBL) {
        // This is a port.
        interface_signal_declaration port = labelParser.interface_signal_declaration();
        declared.add(new IOReference(port.identifier_list.identifier));
      } else {
        throw new SemanticException("Control style not recognised: %s", style);
      }
    }

    @Override
    protected void FormulaWithEntityDeclarations(UID uid, String expression) {
      VHDL93ParserPartial parser = forString(expression);
      NodeListOptional declarations = parser.entity_declarative_part().nodeListOptional;
      addDeclarations(declarations);
    }

    @Override
    protected void FormulaWithArchitectureDeclarations(UID uid, String expression) {
      VHDL93ParserPartial parser = forString(expression);
      NodeListOptional declarations = parser.architecture_declarative_part().nodeListOptional;
      addDeclarations(declarations);
    }

    private void addDeclarations(NodeListOptional declarations) {
      for (Node node : declarations.nodes) {
        Node declaration = ((block_declarative_item) node).nodeChoice.choice;
        if (declaration instanceof signal_declaration) {
          signal_declaration signal = (signal_declaration) declaration;
          declared.add(new IOReference(signal.identifier_list.identifier));
        } else if (declaration instanceof variable_declaration) {
          variable_declaration signal = (variable_declaration) declaration;
          declared.add(new IOReference(signal.identifier_list.identifier));
        }
      }
    }
  }
}
