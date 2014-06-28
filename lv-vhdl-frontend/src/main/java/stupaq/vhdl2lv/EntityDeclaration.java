package stupaq.vhdl2lv;

import com.google.common.collect.Lists;

import java.util.List;

import stupaq.MissingFeature;
import stupaq.vhdl93.VHDL93ParserConstants;
import stupaq.vhdl93.ast.NodeToken;
import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.ast.interface_constant_declaration;
import stupaq.vhdl93.ast.interface_signal_declaration;
import stupaq.vhdl93.ast.mode;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static stupaq.vhdl93.ast.ASTGetters.representation;

public class EntityDeclaration extends VHDLElement<entity_declaration> {
  public static final String DEFAULT_LIBRARY_PREFIX = "work.";
  public static final String LIBRARY_SEPARATOR = ".";
  private final String name;
  private final List<ConstantDeclaration> generics = Lists.newArrayList();
  private final List<SignalDeclaration> portsIn = Lists.newArrayList();
  private final List<SignalDeclaration> portsOut = Lists.newArrayList();

  public EntityDeclaration(entity_declaration node) {
    super(node);
    String id = representation(node().entity_identifier.identifier);
    name = id.contains(LIBRARY_SEPARATOR) ? id : DEFAULT_LIBRARY_PREFIX + id;
    node.entity_header.accept(new DepthFirstVisitor() {
      /** Mode of currently processed interface element. */
      int mode;

      @Override
      public void visit(interface_constant_declaration n) {
        generics.add(new ConstantDeclaration(n));
      }

      @Override
      public void visit(interface_signal_declaration n) {
        mode = VHDL93ParserConstants.IN;
        n.nodeOptional1.accept(this);
        switch (mode) {
          case VHDL93ParserConstants.IN:
            portsIn.add(new SignalDeclaration(n));
            break;
          case VHDL93ParserConstants.OUT:
            portsOut.add(new SignalDeclaration(n));
            break;
          default:
            throw new MissingFeature("Mode: " + VHDL93ParserConstants.tokenImage[mode] +
                " is not supported for ports.");
        }
      }

      @Override
      public void visit(mode n) {
        mode = ((NodeToken) n.nodeChoice.choice).kind;
      }
    });
  }

  public String name() {
    return name;
  }

  public List<ConstantDeclaration> generics() {
    return generics;
  }

  public List<SignalDeclaration> portsIn() {
    return portsIn;
  }

  public List<SignalDeclaration> portsOut() {
    return portsOut;
  }

  @Override
  public String toString() {
    return "EntityDeclaration{" + "generics=" + generics + ", portsIn=" + portsIn +
        ", portsOut=" + portsOut + '}';
  }
}
