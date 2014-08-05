package stupaq.translation.lv2vhdl.parsing;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import stupaq.labview.VIPath;
import stupaq.labview.parsing.VIElementsVisitor;
import stupaq.labview.scripting.ScriptingTools;
import stupaq.translation.lv2vhdl.errors.ErrorMarkingVisitor;

public class ParsedVI extends stupaq.labview.parsing.ParsedVI {
  private final VIPath viPath;

  public ParsedVI(ScriptingTools tools, VIPath viPath)
      throws IOException, SAXException, JAXBException {
    super(tools, viPath);
    this.viPath = viPath;
  }

  @Override
  public <E extends Exception> void accept(VIElementsVisitor<E> visitor) throws E {
    super.accept(ErrorMarkingVisitor.wrapVisitor(viPath, visitor));
  }
}
