package franca.java.office.document.table;

import franca.java.office.document.Block;

public class TableHeaderBlock extends Block {

  @Override
  public String getSerializationTag() {
    return "thead";
  }

  @Override
  public String getDataBlock() {
    return "table-header-block";
  }
}
