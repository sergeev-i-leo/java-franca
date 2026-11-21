package franca.java.data.blocks.list;

import franca.java.data.blocks.Block;
import franca.java.data.json.JsonObject;

public class ListBlock extends Block {

  public boolean isOrdered;

  public ListBlock(boolean isOrdered) {
    super();
    this.isOrdered = isOrdered;
  }

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
    jsonObject.putBooleanValue("isOrdered", isOrdered);
  }

  @Override
  public String getHtmlTag() {
    return isOrdered ? "ol" : "ul";
  }

  @Override
  public String getDataBlock() {
    return "ListBlock";
  }
}
