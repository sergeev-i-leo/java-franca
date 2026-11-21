package franca.java.data.blocks.media;

import franca.java.data.blocks.Block;
import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;

public class AudioBlock extends Block {

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
  }

  @Override
  public void toHtml(BufferedString targetBufferedString, int spacesBefore) {
    super.toHtml(targetBufferedString, spacesBefore);
  }

  @Override
  public String getDataBlock() {
    return "AudioBlock";
  }

}
