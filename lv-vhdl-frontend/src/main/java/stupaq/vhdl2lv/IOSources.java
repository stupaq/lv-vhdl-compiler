package stupaq.vhdl2lv;

import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.List;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.Terminal;

class IOSources extends ForwardingMultimap<IOReference, Terminal> {
  private final Multimap<IOReference, Terminal> sinks =
      Multimaps.newListMultimap(Maps.<IOReference, Collection<Terminal>>newHashMap(),
          new Supplier<List<Terminal>>() {
            @Override
            public List<Terminal> get() {
              return Lists.newArrayList();
            }
          });

  @Override
  protected Multimap<IOReference, Terminal> delegate() {
    return sinks;
  }
}
