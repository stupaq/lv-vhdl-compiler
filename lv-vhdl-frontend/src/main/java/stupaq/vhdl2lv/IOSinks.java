package stupaq.vhdl2lv;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.List;

import stupaq.naming.IOReference;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.vhdl2lv.IOSinks.Sink;

class IOSinks extends ForwardingMultimap<IOReference, Sink> {
  private final Multimap<IOReference, Sink> sinks =
      Multimaps.newListMultimap(Maps.<IOReference, Collection<Sink>>newHashMap(),
          new Supplier<List<Sink>>() {
            @Override
            public List<Sink> get() {
              return Lists.newArrayList();
            }
          });

  @Override
  protected Multimap<IOReference, Sink> delegate() {
    return sinks;
  }

  public boolean put(IOReference ref, Terminal terminal) {
    return put(ref, new Sink(terminal));
  }

  public static class Sink {
    private final Terminal terminal;

    public Sink(Terminal terminal) {
      Preconditions.checkNotNull(terminal);
      this.terminal = terminal;
    }

    public Terminal terminal() {
      return terminal;
    }

    @Override
    public String toString() {
      return "Sink{" + terminal + '}';
    }
  }
}
