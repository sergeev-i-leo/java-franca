package franca.java.data.blocks.table;

import franca.java.data.blocks.Block;

public class TableBlock extends Block {

  @Override
  public String getHtmlTag() {
    return "table";
  }

  @Override
  public String getDataBlock() {
    return "TableBlock";
  }
}
