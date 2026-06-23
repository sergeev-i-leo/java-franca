package franca.java.office.document.structure;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;

public class HorizontalRuleBlock extends Block {

  public String type = "***";

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    jsonObject.putStringValue("type", type);
    super.fillJsonObject(jsonObject);
  }

  @Override
  public void serialize(BufferedString targetBufferedString, int spacesBefore) {
    addQuotedAttribute("data-type", type);
    super.serialize(targetBufferedString, spacesBefore);
  }

  @Override
  public String getSerializationTag() {
    return "hr";
  }

  @Override
  public String getDataBlock() {
    return "horizontal-rule-block";
  }
}
