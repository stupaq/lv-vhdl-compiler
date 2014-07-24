package stupaq.translation.vhdl2lv;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import stupaq.translation.naming.IOReference;
import stupaq.translation.naming.InterfaceName;
import stupaq.translation.vhdl2lv.PortDeclaration.DirectionPredicate;
import stupaq.translation.vhdl2lv.PortDeclaration.PortDirection;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.interface_constant_declaration;
import stupaq.vhdl93.ast.interface_signal_declaration;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

abstract class InterfaceDeclaration {
  private final InterfaceName name;
  private final List<GenericDeclaration> generics = Lists.newArrayList();
  private final Map<IOReference, GenericDeclaration> genericsMap = Maps.newHashMap();
  private final List<PortDeclaration> ports = Lists.newArrayList();
  private final Map<IOReference, PortDeclaration> portsMap = Maps.newHashMap();
  private int inputs;
  private int outputs;

  protected InterfaceDeclaration(InterfaceName name, SimpleNode header) {
    this.name = name;
    header.accept(new DepthFirstVisitor() {
      @Override
      public void visit(interface_constant_declaration n) {
        GenericDeclaration generic = new GenericDeclaration(n);
        generics.add(generic);
        genericsMap.put(generic.reference(), generic);
      }

      @Override
      public void visit(interface_signal_declaration n) {
        PortDeclaration port = new PortDeclaration(n);
        ports.add(port);
        portsMap.put(port.reference(), port);
      }
    });
    // Now, that we are aware of all inputs/outputs...
    int index = 0;
    for (ConnectorPaneTerminal terminal : allTerminals()) {
      terminal.connectorIndex(index++);
      if (terminal.isInput()) {
        ++inputs;
      } else {
        ++outputs;
      }
    }
  }

  public InterfaceName name() {
    return name;
  }

  public Iterable<GenericDeclaration> generics() {
    return generics;
  }

  public Iterable<PortDeclaration> portsIn() {
    return FluentIterable.from(ports).filter(new DirectionPredicate(PortDirection.IN));
  }

  public Iterable<PortDeclaration> portsOut() {
    return FluentIterable.from(ports).filter(new DirectionPredicate(PortDirection.OUT));
  }

  public Iterable<ConnectorPaneTerminal> allTerminals() {
    return Iterables.<ConnectorPaneTerminal>concat(generics(), portsIn(), portsOut());
  }

  public GenericDeclaration resolveGeneric(int index) {
    return generics.get(index);
  }

  public GenericDeclaration resolveGeneric(IOReference ref) {
    return genericsMap.get(ref);
  }

  public PortDeclaration resolvePort(int index) {
    return ports.get(index);
  }

  public PortDeclaration resolvePort(IOReference ref) {
    return portsMap.get(ref);
  }

  public int inputs() {
    return inputs;
  }

  public int outputs() {
    return outputs;
  }
}
