package stupaq.vhdl2lv;

import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.Formula;
import stupaq.labview.scripting.hierarchy.Generic;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.vhdl93.ast.SimpleNode;

abstract class ExpressionEmitter {
  protected static final Logger LOGGER = LoggerFactory.getLogger(ExpressionEmitter.class);
  /** Context of {@link ExpressionEmitter}. */
  protected final Generic owner;

  protected ExpressionEmitter(Generic owner) {
    this.owner = owner;
  }

  public abstract Terminal formula(SimpleNode n);

  public abstract void terminals(Formula formula, Set<IOReference> blacklist, SimpleNode n);

  public void terminals(Formula formula, SimpleNode n) {
    terminals(formula, Sets.<IOReference>newHashSet(), n);
  }
}
