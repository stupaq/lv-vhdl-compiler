package stupaq.vhdl2lv;

import com.google.common.base.Function;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.file.Paths;
import java.util.Arrays;

import stupaq.ExceptionPrinter;
import stupaq.project.LVProject;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.design_file;

import static com.google.common.collect.FluentIterable.from;
import static java.util.Collections.enumeration;

public class TranslationDriver {
  public static void main(String args[]) {
    try {
      if (args.length >= 2) {
        InputStream source = new SequenceInputStream(enumeration(
            from(Arrays.asList(args)).limit(args.length - 1)
                .transform(new Function<String, InputStream>() {
                  @Override
                  public InputStream apply(String input) {
                    try {
                      return new FileInputStream(input);
                    } catch (FileNotFoundException e) {
                      throw new RuntimeException(e);
                    }
                  }
              })
              .toList()));
        VHDL93Parser parser = new VHDL93Parser(source);
        design_file root = parser.design_file();
        LVProject project = new LVProject(Paths.get(args[args.length - 1]));
        root.accept(new DesignFileEmitter(project));
      } else {
        System.out.println("usage: <filename1> <filename2>...");
      }
    } catch (Exception e) {
      ExceptionPrinter.print(e, System.err);
    }
  }
}
