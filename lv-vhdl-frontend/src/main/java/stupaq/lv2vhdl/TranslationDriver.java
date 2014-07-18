package stupaq.lv2vhdl;

import com.google.common.base.Function;

import java.nio.file.Paths;
import java.util.Arrays;

import stupaq.ExceptionPrinter;
import stupaq.labview.VIPath;
import stupaq.project.VHDLProject;

import static com.google.common.collect.FluentIterable.from;

public class TranslationDriver {
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
        DesignFileEmitter emitter = new DesignFileEmitter(project);
        for (VIPath path : project) {
          emitter.emit(path);
        }
      } else {
        System.out.println("usage: <filename1> <filename2>...");
      }
    } catch (Exception e) {
      ExceptionPrinter.print(e, System.err);
    }
  }
}
