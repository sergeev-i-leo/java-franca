package franca.java.data.blocks.table;

import franca.java.data.json.JsonObject;
import franca.java.data.blocks.Block;

public class TableCellBlock extends Block {

  public boolean isHeaderCellBlock;

  public TableCellBlock(boolean isHeaderCellBlock) {
    super();
    this.isHeaderCellBlock = isHeaderCellBlock;
  }

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
    jsonObject.putBooleanValue("isHeaderCellBlock", isHeaderCellBlock);
  }

  @Override
  public String getHtmlTag() {
    return isHeaderCellBlock ? "th" : "td";
  }

  @Override
  public String getDataBlock() {
    return "TableCellBlock";
  }
}
