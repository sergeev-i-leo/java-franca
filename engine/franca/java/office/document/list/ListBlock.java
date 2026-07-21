package franca.java.office.document.list;

import franca.java.data.json.JsonObject;
import franca.java.office.document.Block;

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
  public String getSerializationTag() {
    return isOrdered ? "ol" : "ul";
  }

  @Override
  public String getDataBlock() {
    return "ListBlock";
  }
}
