package franca.java.office.document.table;

import franca.java.data.json.JsonObject;
import franca.java.office.document.Block;

public class TableCellBlock extends Block {

  public boolean isHeader = false;

  public TableCellBlock(boolean isHeader) {
    super();
    this.isHeader = isHeader;
  }

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    jsonObject.putBooleanValue("isHeader", isHeader);
    super.fillJsonObject(jsonObject);
  }

  @Override
  public String getSerializationTag() {
    return isHeader ? "th" : "td";
  }

  @Override
  public String getDataBlock() {
    return "table-cell-block";
  }
}
