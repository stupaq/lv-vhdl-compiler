package stupaq.translation.lv2vhdl;

import com.google.common.base.Function;

import java.nio.file.Paths;
import java.util.Arrays;

import stupaq.translation.ExceptionPrinter;
import stupaq.labview.VIPath;
import stupaq.translation.project.VHDLProject;

import static com.google.common.collect.FluentIterable.from;

public final class TranslationDriver {
  private TranslationDriver() {
  }

  public static void main(String args[]) {
    try {
      if (args.length >= 2) {
        Iterable<VIPath> roots = from(Arrays.asList(args)).limit(args.length - 1)
            .transform(new Function<String, VIPath>() {
              @Override
              public VIPath apply(String input) {
                return new VIPath(input);
              }
            });
        VHDLProject project = new VHDLProject(Paths.get(args[args.length - 1]), roots);
        for (VIPath path : project) {
          new VIInstance(project, path).emitAsVHDL();
        }
      } else {
        System.out.println("usage: <filename1> <filename2>...");
      }
    } catch (Exception e) {
      ExceptionPrinter.print(e, System.err);
    }
  }
}
