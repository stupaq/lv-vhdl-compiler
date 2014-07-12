package stupaq.concepts;

import stupaq.MissingFeature;
import stupaq.vhdl93.ast.context_clause;
import stupaq.vhdl93.ast.entity_declaration;

public class EntityDeclaration extends ComponentDeclaration {
  private final entity_declaration node;
  private final context_clause context;

  public EntityDeclaration(entity_declaration node, context_clause context) {
    super(new ComponentName(node), node.entity_header);
    this.node = node;
    this.context = context;
    MissingFeature.missingIf(node.nodeOptional.present(),
        "Entities with statements are not supported.", node.nodeOptional);
  }

  public entity_declaration node() {
    return node;
  }

  public context_clause context() {
    return context;
  }
}
