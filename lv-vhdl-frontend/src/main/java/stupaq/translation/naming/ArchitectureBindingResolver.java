package stupaq.translation.naming;

import com.google.common.collect.Maps;

import java.util.Map;

public class ArchitectureBindingResolver {
  final Map<EntityName, ArchitectureName> architectures = Maps.newHashMap();

  public ArchitectureBindingResolver() {
  }

  public void add(ArchitectureName name) {
    architectures.put(name.entity(), name);
  }

  public ArchitectureName getDefault(EntityName name) {
    return architectures.get(name);
  }
}
