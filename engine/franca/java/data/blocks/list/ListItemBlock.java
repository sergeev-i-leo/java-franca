package franca.java.data.blocks.list;

import franca.java.data.blocks.Block;
import franca.java.data.json.JsonObject;

public class ListItemBlock extends Block {

  public String type = "*";

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
    jsonObject.putStringValue("type", type);
  }

  @Override
  public String getHtmlTag() {
    return "li";
  }

  @Override
  public String getDataBlock() {
    return "ListItemBlock";
  }
}
