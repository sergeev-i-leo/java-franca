package franca.java.office.document.list;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;

public class ListItemBlock extends Block {

  public String type = "*";

  @Override
  public void serialize(BufferedString targetBufferedString, int spacesBefore) {
    addQuotedAttribute("data-type", type);
    super.serialize(targetBufferedString, spacesBefore);
  }

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    jsonObject.putStringValue("type", type);
    super.fillJsonObject(jsonObject);
  }

  @Override
  public String getSerializationTag() {
    return "li";
  }

  @Override
  public String getDataBlock() {
    return "ListItemBlock";
  }
}
