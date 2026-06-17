package franca.java.office.document.table;

import franca.java.office.document.Block;

public class TableCellBlock extends Block {

  public boolean isHeader = false;

  public TableCellBlock(boolean isHeader) {
    super();
    this.isHeader = isHeader;
  }

  @Override
  public String getSerializationTag() {
    return isHeader ? "th" : "td";
  }

  @Override
  public String getDataBlock() {
    return "TableCellBlock";
  }
}
