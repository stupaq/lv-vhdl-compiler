package stupaq.translation.vhdl2lv;

import stupaq.translation.naming.EntityName;
import stupaq.translation.naming.Identifier;
import stupaq.vhdl93.ast.context_clause;
import stupaq.vhdl93.ast.entity_declaration;

import static stupaq.translation.MissingFeatureException.missingIf;

class EntityDeclaration extends InterfaceDeclaration {
  private final entity_declaration node;
  private final context_clause context;

  public EntityDeclaration(ComponentBindingResolver resolver, entity_declaration node,
      context_clause context) {
    super(Identifier.entity(node), node.entity_header);
    this.node = node;
    this.context = context;
    missingIf(node.entity_declarative_part.nodeListOptional.present(), node,
        "Entity-scoped declarations are not supported.");
    missingIf(node.nodeOptional.present(), node, "Entities with statements are not supported.");
  }

  public entity_declaration node() {
    return node;
  }

  public context_clause context() {
    return context;
  }

  @Override
  public EntityName name() {
    return (EntityName) super.name();
  }
}
