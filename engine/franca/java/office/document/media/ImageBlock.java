package franca.java.office.document.media;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;

public class ImageBlock extends Block {

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
  }

  @Override
  public void toHtml(BufferedString targetBufferedString, int spacesBefore) {
    super.toHtml(targetBufferedString, spacesBefore);
  }

  @Override
  public String getHtmlTag() {
    return "img";
  }

  @Override
  public String getDataBlock() {
    return "ImageBlock";
  }

}
