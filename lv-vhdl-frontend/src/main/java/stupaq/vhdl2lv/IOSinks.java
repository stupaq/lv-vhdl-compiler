package stupaq.vhdl2lv;

import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

import stupaq.labview.UID;

public class IOSinks extends ForwardingMultimap<IOReference, UID> {
  private final Multimap<IOReference, UID> sinks =
      Multimaps.newSetMultimap(Maps.<IOReference, Collection<UID>>newHashMap(),
          new Supplier<Set<UID>>() {
            @Override
            public Set<UID> get() {
              return Sets.newHashSet();
            }
          });

  @Override
  protected Multimap<IOReference, UID> delegate() {
    return sinks;
  }
}
