package stupaq.translation.lv2vhdl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBException;

import stupaq.labview.VIPath;
import stupaq.translation.project.LVProjectReader;

class InterfaceDeclarationCache {
  private final LVProjectReader project;
  private final LoadingCache<VIPath, InterfaceDeclaration> cache = CacheBuilder.newBuilder()
      .concurrencyLevel(1)
      .build(new CacheLoader<VIPath, InterfaceDeclaration>() {
        @Override
        public InterfaceDeclaration load(VIPath viPath)
            throws JAXBException, SAXException, IOException {
          ParsedVI theVi = new ParsedVI(project.tools(), viPath);
          return new InterfaceDeclaration(theVi);
        }
      });

  public InterfaceDeclarationCache(LVProjectReader project) {
    this.project = project;
  }

  public InterfaceDeclaration get(VIPath viPath) {
    try {
      return cache.get(viPath);
    } catch (ExecutionException e) {
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  public void fill(VIPath path, InterfaceDeclaration entity) {
    cache.put(path, entity);
  }
}
