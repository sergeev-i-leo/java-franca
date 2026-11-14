package franca.java.office.document.structure;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;

public class HorizontalRuleBlock extends Block {

  public String type = "***";

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
    jsonObject.putStringValue("type", type);
  }

  @Override
  public void toHtml(BufferedString targetBufferedString, int spacesBefore) {
    addQuotedAttribute("data-type", type);
    super.toHtml(targetBufferedString, spacesBefore);
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
