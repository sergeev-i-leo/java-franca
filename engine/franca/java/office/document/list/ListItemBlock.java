package franca.java.office.document.list;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;

public class ListItemBlock extends Block {

  public String type = "*";

  @Override
  public void toHtml(BufferedString targetBufferedString, int spacesBefore) {
    addQuotedAttribute("data-type", type);
    super.toHtml(targetBufferedString, spacesBefore);
  }

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
