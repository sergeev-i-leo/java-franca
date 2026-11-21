package franca.java.data.blocks.structure;

import franca.java.data.json.JsonObject;
import franca.java.data.blocks.Block;

public class HorizontalRuleBlock extends Block {

  public String type = "***";

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
    jsonObject.putStringValue("type", type);
  }

  @Override
  public String getHtmlTag() {
    return "hr";
  }

  @Override
  public String getDataBlock() {
    return "HorizontalRuleBlock";
  }
}
