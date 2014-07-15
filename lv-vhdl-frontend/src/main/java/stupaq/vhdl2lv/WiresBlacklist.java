package stupaq.vhdl2lv;

import com.google.common.collect.Sets;

import java.util.Set;

import stupaq.labview.hierarchy.Terminal;

public class WiresBlacklist {
  private final Set<WireEndpoints> set = Sets.newHashSet();

  public void add(Terminal source, Terminal sink) {
    set.add(new WireEndpoints(source, sink));
  }

  public boolean contains(Terminal source, Terminal sink) {
    return set.contains(new WireEndpoints(source, sink));
  }

  private static class WireEndpoints {
    public final Terminal source;
    public final Terminal sink;

    public WireEndpoints(Terminal source, Terminal sink) {
      this.source = source;
      this.sink = sink;
    }

    @Override
    public int hashCode() {
      int result = source.hashCode();
      result = 31 * result + sink.hashCode();
      return result;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      WireEndpoints that = (WireEndpoints) o;
      return sink.equals(that.sink) && source.equals(that.source);
    }
  }
}
