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

import javax.annotation.Nonnull;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.CompoundArithmetic;
import stupaq.labview.scripting.hierarchy.Generic;
import stupaq.labview.scripting.hierarchy.Wire;
import stupaq.labview.scripting.tools.CompoundArithmeticCreate.ArithmeticMode;
import stupaq.vhdl2lv.IOSinks.Sink;
import stupaq.vhdl2lv.IOSources.Source;

import static com.google.common.base.Optional.fromNullable;

/**
 * The wiring process is source-oriented in the sense that we pick labels (filter nodes) from the
 * source and in case of multiple sources/sinks, we conduct the wiring process for each signal
 * starting from source(s). Note that the LabVIEW logic for creating wires works in a very same
 * way.
 */
public class WiringRules {
  private static final Logger LOGGER = LoggerFactory.getLogger(WiringRules.class);
  private final Generic owner;
  private final IOSources sources;
  private final IOSinks sinks;
  private final LabellingRules labelling;

  public WiringRules(Generic owner, IOSources sources, IOSinks sinks, LabellingRules labelling) {
    this.owner = owner;
    this.sources = sources;
    this.sinks = sinks;
    this.labelling = labelling;
  }

  public static CompoundArithmetic mergeNode(Generic owner, int inputs) {
    return new CompoundArithmetic(owner, ArithmeticMode.MULTIPLY, inputs,
        Optional.<String>absent());
  }

  private Wire connect(IOReference ref, Source source, Sink sink) {
    // The label might be different on each invocation.
    return new Wire(owner, source.terminal(), sink.terminal(), labelling.choose(ref, source));
  }

  private void applyInternal(IOReference ref) {
    Collection<Source> sources = this.sources.get(ref);
    Collection<Sink> sinks = this.sinks.get(ref);
    if (sources.size() > 0 && sinks.size() > 0) {
      Source source;
      if (sources.size() == 1) {
        source = sources.iterator().next();
        LOGGER.debug("Single-source: {} connects: {}", ref, source);
      } else {
        LOGGER.debug("Multi-source: {} merges:", ref);
        String refName = ref.toString();
        CompoundArithmetic assembly = mergeNode(owner, sources.size());
        int index = 0;
        for (Source partial : sources) {
          LOGGER.debug("\t{} =>", partial);
          Sink input = new Sink(assembly.inputs().get(index));
          connect(ref, partial, input);
          ++index;
        }
        source = new Source(assembly.output(), refName);
        LOGGER.debug("Source {} connects: {}", ref, source);
      }
      for (Sink sink : sinks) {
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
    public String put(@Nonnull IOReference key, @Nonnull String value) {
      Preconditions.checkArgument(!containsKey(key) || !value.equals(get(key)),
          "Fallback labels for: {} collide", key);
      return super.put(key, value);
    }

    @Override
    public Optional<String> choose(IOReference ref, Source source) {
      Optional<String> labelSource = source.label();
      if (labelSource.isPresent()) {
        return labelSource;
      } else {
        labelled.add(ref);
        return fromNullable(get(ref));
      }
    }

    public Iterable<Entry<IOReference, String>> remainingLabels() {
      return FluentIterable.from(delegate.entrySet())
          .filter(new Predicate<Entry<IOReference, String>>() {
            @Override
            public boolean apply(Entry<IOReference, String> input) {
              return !labelled.contains(input.getKey());
            }
          });
    }
  }

  public interface LabellingRules {
    public Optional<String> choose(IOReference ref, Source source);
  }
}
