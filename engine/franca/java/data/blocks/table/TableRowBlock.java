package franca.java.data.blocks.table;

import franca.java.data.blocks.Block;

public class TableRowBlock extends Block {

  @Override
  public String getHtmlTag() {
    return "tr";
  }

  @Override
  public String getDataBlock() {
    return "TableRowBlock";
  }
}
