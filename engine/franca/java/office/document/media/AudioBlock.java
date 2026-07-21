package franca.java.office.document.media;

import franca.java.expected.BufferedString;
import franca.java.office.document.Block;

public class AudioBlock extends Block {

  public String source = "";

  @Override
  public void serialize(BufferedString targetBufferedString, int spacesBefore) {
    addQuotedAttribute("data-source", source);
    super.serialize(targetBufferedString, spacesBefore);
  }

  @Override
  public String getDataBlock() {
    return "AudioBlock";
  }

}
