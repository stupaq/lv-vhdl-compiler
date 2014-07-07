package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.Formula;
import stupaq.labview.scripting.hierarchy.FormulaNode;
import stupaq.labview.scripting.hierarchy.Generic;
import stupaq.labview.scripting.hierarchy.Wire;

import static com.google.common.base.Optional.fromNullable;

public class WiringRules {
  private static final Logger LOGGER = LoggerFactory.getLogger(WiringRules.class);
  private final Generic owner;
  private final IOSources sources;
  private final IOSinks sinks;
  private final LabellingRules labelling;

  public WiringRules(Generic owner, IOSources sources,
      IOSinks sinks, LabellingRules labelling) {
    this.owner = owner;
    this.sources = sources;
    this.sinks = sinks;
    this.labelling = labelling;
  }

  private Wire connect(IOReference ref, Endpoint source, Endpoint sink) {
    // The label might be different on each invocation.
    return new Wire(owner, source.terminal(), sink.terminal(), labelling.choose(ref, source, sink));
  }

  private void applyInternal(IOReference ref) {
    Collection<Endpoint> sources = this.sources.get(ref);
    Collection<Endpoint> sinks = this.sinks.get(ref);
    if (sources.size() > 0 && sinks.size() > 0) {
      Endpoint source;
      if (sources.size() == 1) {
        source = sources.iterator().next();
        LOGGER.debug("Single-source: {} connects: {}", ref, source);
      } else {
        LOGGER.debug("Multi-source: {} merges:", ref);
        Formula assembly =
            new FormulaNode(owner, "merging signal sources", Optional.<String>absent());
        for (Endpoint partial : sources) {
          LOGGER.debug("\t{} =>", partial);
          Endpoint input = new Endpoint(assembly.addInput(ref.toString()));
          connect(ref, partial, input);
        }
        source = new Endpoint(assembly.addOutput(ref.toString()));
        LOGGER.debug("Source {} connects: {}", ref, source);
      }
      for (Endpoint sink : sinks) {
        LOGGER.debug("\t=> {}", sink);
        connect(ref, source, sink);
      }
    }
  }

  public void applyAll() {
    Iterator<IOReference> iterator = sinks.keySet().iterator();
    while (iterator.hasNext()) {
      IOReference ref = iterator.next();
      applyInternal(ref);
      sources.removeAll(ref);
      iterator.remove();
    }
  }

  public static class FallbackLabels extends ForwardingMap<IOReference, String>
      implements LabellingRules {
    private final Map<IOReference, String> delegate = Maps.newHashMap();
    private final Set<IOReference> labelled = Sets.newHashSet();

    @Override
    protected Map<IOReference, String> delegate() {
      return delegate;
    }

    @Override
    public String put(IOReference key, String value) {
      Preconditions.checkArgument(!containsKey(key) || !value.equals(get(key)),
          "Fallback labels for: {} collide", key);
      return super.put(key, value);
    }

    @Override
    public Optional<String> choose(IOReference ref, Endpoint source, Endpoint sink) {
      Optional<String> labelSource = source.label(), labelSink = sink.label();
      if (labelSource.isPresent() && labelSink.isPresent() && !labelSink.equals(labelSource)) {
        LOGGER.error("Sink-source label conflict: {}", ref);
      }
      if (labelSource.isPresent()) {
        return labelSource;
      } else if (labelSink.isPresent()) {
        return labelSink;
      } else {
        labelled.add(ref);
        return fromNullable(get(ref));
      }
    }

    public Iterable<Entry<IOReference, String>> remainingLabels() {
      return FluentIterable.from(delegate.entrySet())
          .filter(new Predicate<Entry<IOReference, String>>() {
            @Override
            public boolean apply(@Nullable Entry<IOReference, String> input) {
              return !labelled.contains(input.getKey());
            }
          });
    }
  }

  public interface LabellingRules {
    public Optional<String> choose(IOReference ref, Endpoint source, Endpoint sink);
  }
}
