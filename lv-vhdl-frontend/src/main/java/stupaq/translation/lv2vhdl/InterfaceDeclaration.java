package stupaq.translation.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import stupaq.commons.IntegerMap;
import stupaq.labview.UID;
import stupaq.labview.hierarchy.ConnectorPane;
import stupaq.labview.hierarchy.Control;
import stupaq.labview.hierarchy.ControlCluster;
import stupaq.labview.hierarchy.FormulaNode;
import stupaq.labview.hierarchy.Panel;
import stupaq.labview.scripting.tools.ControlStyle;
import stupaq.translation.errors.SemanticException;
import stupaq.translation.errors.TranslationException;
import stupaq.translation.lv2vhdl.miscellanea.DeclarationOrdering;
import stupaq.translation.lv2vhdl.parsing.VHDL93ParserPartial;
import stupaq.translation.lv2vhdl.parsing.VIElementsVisitor;
import stupaq.translation.naming.ComponentName;
import stupaq.translation.naming.EntityName;
import stupaq.vhdl93.ast.*;

import static java.util.Arrays.asList;
import static stupaq.translation.errors.LocalisedSemanticException.semanticCheck;
import static stupaq.translation.lv2vhdl.parsing.VHDL93ParserPartial.Parsers.forString;
import static stupaq.vhdl93.VHDL93ParserConstants.IS;
import static stupaq.vhdl93.VHDL93ParserConstants.SEMICOLON;
import static stupaq.vhdl93.ast.Builders.*;

public class InterfaceDeclaration {
  private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceDeclaration.class);
  private final NodeListOptional entityDeclarations = new NodeListOptional();
  private final IntegerMap<interface_constant_declaration> generics = new IntegerMap<>();
  private final IntegerMap<interface_signal_declaration> ports = new IntegerMap<>();
  private final IntegerMap<IntegerMap<String>> paneIndexToNames = new IntegerMap<>();
  private boolean clustered = false;
  private context_clause entityContext;

  public InterfaceDeclaration(stupaq.labview.parsing.ParsedVI theVi) {
    theVi.accept(new BuilderVisitor());
    Collections.sort(entityDeclarations.nodes, new DeclarationOrdering(entityDeclarations));
  }

  public boolean isClustered() {
    return clustered;
  }

  public IntegerMap<String> clusteredNames(int connPaneIndex) {
    Preconditions.checkState(clustered);
    return paneIndexToNames.getPresent(connPaneIndex);
  }

  public design_unit emitAsEntity(EntityName name) {
    context_clause context =
        entityContext != null ? entityContext : new context_clause(listOptional());
    entity_identifier identifier = forString(name.entity().toString()).entity_identifier();
    entity_header header = new entity_header(createGenerics(), createPorts());
    entity_declaration declaration =
        new entity_declaration(identifier, header, new entity_declarative_part(entityDeclarations),
            optional(), optional(), optional());
    return new design_unit(context,
        new library_unit(choice(new primary_unit(choice(declaration)))));
  }

  public component_declaration emitAsComponent(ComponentName name) {
    component_identifier identifier = forString(name.component().toString()).component_identifier();
    component_header header = new component_header(createGenerics(), createPorts());
    return new component_declaration(identifier, optional(token(IS)), header, optional());
  }

  private NodeOptional createGenerics() {
    if (generics.isEmpty()) {
      return optional();
    } else {
      NodeListOptional rest = listOptional();
      interface_constant_declaration first =
          split(generics.values(), tokenSupplier(SEMICOLON), rest);
      return optional(new formal_generic_clause(
          new generic_clause(new generic_list(new generic_interface_list(first, rest)))));
    }
  }

  private NodeOptional createPorts() {
    if (ports.isEmpty()) {
      return optional();
    } else {
      NodeListOptional rest = listOptional();
      interface_signal_declaration first = split(ports.values(), tokenSupplier(SEMICOLON), rest);
      return optional(new formal_port_clause(
          new port_clause(new port_list(new port_interface_list(first, rest)))));
    }
  }

  private class BuilderVisitor extends VIElementsVisitor<TranslationException> {
    private final Map<UID, Integer> controlToPaneIndex = Maps.newHashMap();
    private final Map<UID, Integer> controlToClusterIndex = Maps.newHashMap();
    private final Map<UID, IntegerMap<String>> controlOwnerToNames = Maps.newHashMap();
    private UID rootPanel;

    private Optional<Integer> connPaneIndex(UID control) {
      return Optional.fromNullable(controlToPaneIndex.get(control));
    }

    @Override
    public Iterable<String> parsersOrder() {
      return asList(Panel.XML_NAME, ConnectorPane.XML_NAME, FormulaNode.XML_NAME,
          ControlCluster.XML_NAME, Control.NUMERIC_XML_NAME);
    }

    @Override
    public void Panel(Optional<UID> ownerUID, UID uid) {
      if (!ownerUID.isPresent()) {
        Verify.verify(rootPanel == null);
        rootPanel = uid;
      }
    }

    @Override
    public void ConnectorPane(List<UID> controls) {
      int index = 0;
      for (UID control : controls) {
        controlToPaneIndex.put(control, index++);
      }
    }

    @Override
    public void ControlCluster(UID ownerUID, UID uid, Optional<String> label, UID terminalUID,
        boolean isIndicator, List<UID> controlUIDs) {
      if (!clustered) {
        clustered = true;
        LOGGER.debug("Interface is clustered.");
      }
      int index = 0;
      Optional<Integer> connPaneIndex = connPaneIndex(uid);
      semanticCheck(connPaneIndex.isPresent(), "Control is not connected to the ConnPane.");
      IntegerMap<String> names = new IntegerMap<>();
      paneIndexToNames.put(connPaneIndex.get(), names);
      controlOwnerToNames.put(uid, names);
      for (UID control : controlUIDs) {
        controlToClusterIndex.put(control, index);
        ++index;
      }
    }

    @Override
    public void Control(UID ownerUID, UID uid, Optional<String> label, UID terminalUID,
        boolean isIndicator, ControlStyle style, String description) {
      Verify.verifyNotNull(rootPanel);
      semanticCheck(label.isPresent(), "Missing control label (should contain port declaration).");
      int connPaneIndex;
      if (clustered) {
        semanticCheck(!rootPanel.equals(ownerUID),
            "VI is clustered, but some control has front panel as an owner.");
        // Fill information about clustered control.
        IntegerMap<String> names = controlOwnerToNames.get(ownerUID);
        names.put(controlToClusterIndex.get(uid), label.get());
        // Read virtual controlIndex.
        try {
          connPaneIndex = UnsignedInteger.valueOf(description.trim()).intValue();
        } catch (NumberFormatException e) {
          throw new SemanticException(
              "Control description: %s does not contain port or generic index.", description);
        }
      } else {
        semanticCheck(rootPanel.equals(ownerUID),
            "VI is not clustered, but some control has owner other than front panel.");
        Optional<Integer> index = connPaneIndex(uid);
        semanticCheck(index.isPresent(), "Control is not connected to the ConnPane.");
        connPaneIndex = index.get();
      }
      String declaration = label.get().trim();
      VHDL93ParserPartial labelParser = forString(declaration);
      if (style == ControlStyle.NUMERIC_I32) {
        // This is a generic.
        interface_constant_declaration generic = labelParser.interface_constant_declaration();
        // Make the output less verbose.
        generic.nodeOptional = optional();
        generic.nodeOptional1 = optional();
        generics.put(connPaneIndex, generic);
      } else if (style == ControlStyle.NUMERIC_DBL) {
        // This is a port.
        interface_signal_declaration port = labelParser.interface_signal_declaration();
        port.nodeOptional = optional();
        ports.put(connPaneIndex, port);
      } else {
        throw new SemanticException("Control style not recognised: %s", style);
      }
    }

    @Override
    protected void FormulaWithEntityContext(UID uid, String expression) {
      VHDL93ParserPartial parser = forString(expression);
      entityContext = parser.context_clause();
    }

    @Override
    protected void FormulaWithEntityDeclarations(UID uid, String expression) {
      VHDL93ParserPartial parser = forString(expression);
      NodeListOptional extra = parser.entity_declarative_part().nodeListOptional;
      entityDeclarations.nodes.addAll(extra.nodes);
    }
  }
}
