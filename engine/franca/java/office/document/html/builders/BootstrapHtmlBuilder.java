package franca.java.office.document.html.builders;

import franca.java.expected.BufferedString;
import franca.java.expected.TranspilableClass;
import franca.java.office.document.DocumentBlock;

public class BootstrapHtmlBuilder extends TranspilableClass {

  public BufferedString outputBufferedString = null;

  @Override
  public String getClassName() {
    return "BootstrapHtmlBuilder";
  }

  public BufferedString build(DocumentBlock documentBlock) {
    outputBufferedString = new BufferedString();

    return outputBufferedString;
  }
}
