package stupaq.translation.lv2vhdl.errors;

import com.google.common.reflect.Reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import stupaq.labview.UID;
import stupaq.labview.VIPath;
import stupaq.labview.parsing.VIElementsVisitor;
import stupaq.translation.errors.TranslationException;

public final class ErrorMarkingVisitor {
  private static final int UID_ARG_INDEX = 1;

  private ErrorMarkingVisitor() {
  }

  @SuppressWarnings("unchecked")
  public static <E extends Exception> VIElementsVisitor<E> wrapVisitor(final VIPath vi,
      final VIElementsVisitor<E> delegate) {
    return (VIElementsVisitor<E>) Reflection.newProxy(VIElementsVisitor.class,
        new InvocationHandler() {
          @Override
          public Object invoke(Object proxy, Method method, Object[] args)
              throws IllegalAccessException, InvocationTargetException {
            try {
              return method.invoke(delegate, args);
            } catch (InvocationTargetException e) {
              Throwable t = e.getTargetException();
              if (t instanceof LocalisedException) {
                throw (LocalisedException) t;
              } else if (t instanceof TranslationException) {
                LocalisedException localised = new LocalisedException((TranslationException) t);
                localised.setVI(vi);
                if (args.length > UID_ARG_INDEX && args[UID_ARG_INDEX] instanceof UID) {
                  localised.setUID((UID) args[UID_ARG_INDEX]);
                }
                throw localised;
              } else {
                throw e;
              }
            }
          }
        });
  }
}
