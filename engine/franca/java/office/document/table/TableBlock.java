package franca.java.office.document.table;

import franca.java.office.document.Block;

public class TableBlock extends Block {

  @Override
  public String getSerializationTag() {
    return "table";
  }

  @Override
  public String getDataBlock() {
    return "TableBlock";
  }
}
