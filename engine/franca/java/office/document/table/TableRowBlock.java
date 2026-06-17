package franca.java.office.document.table;

import franca.java.office.document.Block;

public class TableRowBlock extends Block {

  @Override
  public String getSerializationTag() {
    return "tr";
  }

  @Override
  public String getDataBlock() {
    return "table-row-block";
  }
}
