package stupaq.lv2vhdl;

import com.google.common.base.Function;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

import stupaq.labview.UID;

import static com.google.common.collect.FluentIterable.from;

public class Multiplexer extends ForwardingList<Endpoint> {
  private List<Endpoint> delegate = Lists.newArrayList();

  public Multiplexer(List<Endpoint> endpoints) {
    this.delegate = endpoints;
  }

  @Override
  protected List<Endpoint> delegate() {
    return delegate;
  }

  public static Multiplexer create(final Map<UID, Endpoint> terminals, List<UID> multiple) {
    return new Multiplexer(from(multiple).transform(new Function<UID, Endpoint>() {
      @Override
      public Endpoint apply(UID input) {
        return terminals.get(input);
      }
    }).toList());
  }
}
