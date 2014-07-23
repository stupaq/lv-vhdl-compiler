package stupaq.translation.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.VerifyException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import stupaq.labview.UID;
import stupaq.labview.parsing.NoOpVisitor;
import stupaq.labview.scripting.tools.ControlStyle;
import stupaq.translation.SemanticException;
import stupaq.translation.naming.IOReference;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.NodeListOptional;
import stupaq.vhdl93.ast.block_declarative_item;
import stupaq.vhdl93.ast.identifier_list;
import stupaq.vhdl93.ast.interface_constant_declaration;
import stupaq.vhdl93.ast.interface_signal_declaration;
import stupaq.vhdl93.ast.signal_declaration;
import stupaq.vhdl93.ast.subtype_indication;
import stupaq.vhdl93.ast.variable_declaration;

import static stupaq.translation.SemanticException.semanticCheck;
import static stupaq.translation.TranslationConventions.ARCHITECTURE_EXTRA_DECLARATIONS;
import static stupaq.translation.TranslationConventions.ENTITY_EXTRA_DECLARATIONS;
import static stupaq.translation.lv2vhdl.VHDL93PartialParser.parser;
import static stupaq.vhdl93.builders.ASTBuilders.choice;
import static stupaq.vhdl93.builders.ASTBuilders.listOptional;
import static stupaq.vhdl93.builders.ASTBuilders.optional;

class DeclarationInferenceRules extends NoOpVisitor<Exception> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeclarationInferenceRules.class);
  private final Set<IOReference> declared = Sets.newHashSet();
  private final List<block_declarative_item> inferred = Lists.newArrayList();

  public void inferDeclaration(Endpoint terminal) {
    // Infer declaration if necessary.
    String valueString = terminal.valueString();
    IOReference ref;
    try {
      ref = new IOReference(parser(valueString).identifier());
    } catch (ParseException e) {
      LOGGER.debug("Skipping declaration inference (not an identifier) for: {}.", terminal);
      return;
    }
    if (!declared.contains(ref)) {
      subtype_indication type = null;
      try {
        type = parser(terminal.name()).interface_signal_declaration().subtype_indication;
      } catch (ParseException e) {
        LOGGER.debug("Skipping declaration inference (not a declaration) for: {}.", terminal);
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

  @Override
  public Iterable<String> parsersOrder() {
    throw new VerifyException();
  }

  @Override
  public void Control(UID ownerUID, UID uid, Optional<String> label, UID terminalUID,
      boolean isIndicator, ControlStyle style, String description) throws Exception {
    semanticCheck(label.isPresent(), "Missing control label (should contain port declaration).");
    String declaration = label.get().trim();
    VHDL93PartialParser labelParser = parser(declaration);
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
  public void FormulaNode(UID ownerUID, UID uid, String expression, Optional<String> label,
      List<UID> termUIDs) throws ParseException {
    VHDL93PartialParser parser = parser(expression);
    NodeListOptional declarations;
    if (label.equals(ENTITY_EXTRA_DECLARATIONS)) {
      declarations = parser.entity_declarative_part().nodeListOptional;
    } else if (label.equals(ARCHITECTURE_EXTRA_DECLARATIONS)) {
      declarations = parser.architecture_declarative_part().nodeListOptional;
    } else {
      // We're not interested in anything else.
      return;
    }
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