package franca.java.office.document.media;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;

public class ImageBlock extends Block {

  public String source = "";

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
    jsonObject.putStringValue("source", source);
  }

  @Override
  public void serialize(BufferedString targetBufferedString, int spacesBefore) {
    addQuotedAttribute("data-source", source);
    super.serialize(targetBufferedString, spacesBefore);
  }

  @Override
  public String getSerializationTag() {
    return "img";
  }

  @Override
  public String getDataBlock() {
    return "ImageBlock";
  }

}
