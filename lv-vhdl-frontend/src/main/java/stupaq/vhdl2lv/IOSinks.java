package stupaq.vhdl2lv;

import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.Terminal;

class IOSinks extends ForwardingMultimap<IOReference, Terminal> {
  private final Multimap<IOReference, Terminal> sinks =
      Multimaps.newSetMultimap(Maps.<IOReference, Collection<Terminal>>newHashMap(),
          new Supplier<Set<Terminal>>() {
            @Override
            public Set<Terminal> get() {
              return Sets.newHashSet();
            }
          });

  @Override
  protected Multimap<IOReference, Terminal> delegate() {
    return sinks;
  }
}
