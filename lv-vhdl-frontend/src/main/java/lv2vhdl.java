import com.google.common.base.Function;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.file.Paths;
import java.util.Arrays;

import stupaq.project.LVProject;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.design_file;

import static com.google.common.collect.FluentIterable.from;
import static java.util.Collections.enumeration;

public class lv2vhdl {
  public static void main(String args[]) throws Exception {
    if (args.length >= 2) {
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
              .toList();
      try {
        // FIXME
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    } else {
      System.out.println("usage: <filename1> <filename2>...");
    }
  }
}
