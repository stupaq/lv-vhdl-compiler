package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.List;

import stupaq.labview.hierarchy.Terminal;
import stupaq.naming.IOReference;
import stupaq.vhdl2lv.IOSources.Source;

public class IOSources extends ForwardingMultimap<IOReference, Source> {
  private final Multimap<IOReference, Source> sinks =
      Multimaps.newListMultimap(Maps.<IOReference, Collection<Source>>newHashMap(),
          new Supplier<List<Source>>() {
            @Override
            public List<Source> get() {
              return Lists.newArrayList();
            }
          });

  @Override
  protected Multimap<IOReference, Source> delegate() {
    return sinks;
  }

  public boolean put(IOReference ref, Terminal terminal) {
    return put(ref, new Source(terminal));
  }

  public boolean put(IOReference ref, Terminal terminal, String node) {
    return put(ref, new Source(terminal, node));
  }

  public static class Source {
    private final Terminal terminal;
    private final Optional<String> label;

    public Source(Terminal terminal) {
      this(terminal, Optional.<String>absent());
    }

    public Source(Terminal terminal, String label) {
      this(terminal, Optional.of(label));
    }

    public Source(Terminal terminal, Optional<String> label) {
      Preconditions.checkNotNull(terminal);
      Preconditions.checkNotNull(label);
      this.terminal = terminal;
      this.label = label;
    }

    public Terminal terminal() {
      return terminal;
    }

    public Optional<String> label() {
      return label;
    }

    @Override
    public String toString() {
      return "Source{" + terminal + (label.isPresent() ? ", " + label.get() : "") + '}';
    }
  }
}
