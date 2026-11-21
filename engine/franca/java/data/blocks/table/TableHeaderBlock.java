package franca.java.data.blocks.table;

import franca.java.data.blocks.Block;

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
