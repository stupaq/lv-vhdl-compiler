package stupaq.lv2vhdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import stupaq.labview.VIPath;
import stupaq.labview.parsing.PrintingVisitor;
import stupaq.labview.parsing.VIElementsParser;
import stupaq.project.ProjectElementName;
import stupaq.project.VHDLProject;

public class TranslationUnitEmitter {
  private static final Logger LOGGER = LoggerFactory.getLogger(TranslationUnitEmitter.class);
  private final VHDLProject project;

  public TranslationUnitEmitter(VHDLProject project) {
    this.project = project;
  }

  public void emit(VIPath path) throws IOException, JAXBException, SAXException {
    LOGGER.debug("Emitting VHDL from VI: {}", path);
    ProjectElementName element = ProjectElementName.parse(path);
    LOGGER.debug("Target project element: {}", element);
    // FIXME
    VIElementsParser.visitVI(project.tools(), path, PrintingVisitor.create());
  }
}
