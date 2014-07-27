package stupaq.translation.lv2vhdl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;

import stupaq.labview.VIPath;
import stupaq.translation.project.LVProjectReader;

class InterfaceDeclarationCache {
  private final LVProjectReader project;
  private final LoadingCache<VIPath, InterfaceDeclaration> cache = CacheBuilder.newBuilder()
      .concurrencyLevel(1)
      .build(new CacheLoader<VIPath, InterfaceDeclaration>() {
        @Override
        public InterfaceDeclaration load(VIPath viPath) throws Exception {
          return new InterfaceDeclaration(project, viPath);
        }
      });

  public InterfaceDeclarationCache(LVProjectReader project) {
    this.project = project;
  }

  public InterfaceDeclaration get(VIPath viPath) throws Exception {
    try {
      return cache.get(viPath);
    } catch (ExecutionException e) {
      if (e.getCause() != null && e.getCause() instanceof Exception) {
        throw (Exception) e.getCause();
      } else {
        throw e;
      }
    }
  }

  public void fill(VIPath path, InterfaceDeclaration declaration) {
    cache.put(path, declaration);
  }
}
