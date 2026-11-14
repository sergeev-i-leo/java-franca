package franca.java.office.document.table;

import franca.java.office.document.Block;

public class TableHeaderBlock extends Block {

  @Override
  public String getHtmlTag() {
    return "thead";
  }

  @Override
  public String getDataBlock() {
    return "TableHeaderBlock";
  }
}
