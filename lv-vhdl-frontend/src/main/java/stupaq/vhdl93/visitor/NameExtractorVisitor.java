package stupaq.vhdl93.visitor;

import com.google.common.base.Optional;

import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.name;

import static com.google.common.base.Optional.of;

public class NameExtractorVisitor extends DepthFirstVisitor {
  private Optional<String> name;

  public Optional<String> extract(Node n) {
    name = Optional.absent();
    n.accept(this);
    return name;
  }

  @Override
  public void visit(name n) {
    name = name.or(of(n.representation()));
  }
}
