package stupaq.vhdl2lv;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import stupaq.MissingFeature;
import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.labview.scripting.hierarchy.Wire;

public class WiringRules {
  private static final Logger LOGGER = LoggerFactory.getLogger(WiringRules.class);
  private final IOSources sources;
  private final IOSinks sinks;
  private final Labelling labelling;

  public WiringRules(IOSources sources, IOSinks sinks, Labelling labelling) {
    this.sources = sources;
    this.sinks = sinks;
    this.labelling = labelling;
  }

  public void applyInternal(IOReference ref, Optional<String> label) {
    Collection<Terminal> sources = this.sources.get(ref);
    Collection<Terminal> sinks = this.sinks.get(ref);
    if (sources.size() > 0) {
      if (sources.size() == 1) {
        Terminal source = sources.iterator().next();
        LOGGER.debug("Single-source: {}, connects:", ref);
        for (Terminal sink : sinks) {
          LOGGER.debug("\t{} => {}", source, sink);
          new Wire(source, sink, label);
        }
      } else {
        throw new MissingFeature("Multiple sources for: " + ref);
      }
    }
  }

  public void applyAll() {
    Iterator<IOReference> iterator = sinks.keySet().iterator();
    while (iterator.hasNext()) {
      IOReference ref = iterator.next();
      applyInternal(ref, labelling.apply(ref));
      sources.removeAll(ref);
      iterator.remove();
    }
  }

  public void applyTo(IOReference ref, Optional<String> label) {
    applyInternal(ref, label);
    sources.removeAll(ref);
    sinks.removeAll(ref);
  }

  public static class LabellingAbsent implements Labelling {
    public static final Labelling INSTANCE = new LabellingAbsent();

    private LabellingAbsent() {
    }

    @Override
    public Optional<String> apply(IOReference input) {
      return Optional.absent();
    }
  }

  public static class LabellingMap extends ForwardingMap<IOReference, String> implements Labelling {
    private Map<IOReference, String> delegate = Maps.newHashMap();

    @Override
    protected Map<IOReference, String> delegate() {
      return delegate;
    }

    @Override
    public Optional<String> apply(IOReference input) {
      return Optional.fromNullable(get(input));
    }
  }

  public interface Labelling extends Function<IOReference, Optional<String>> {
    @Override
    public Optional<String> apply(IOReference input);
  }
}
