package franca.java.data.blocks.table;

import franca.java.data.blocks.Block;

public class TableBodyBlock extends Block {

  @Override
  public String getHtmlTag() {
    return "tbody";
  }

  @Override
  public String getDataBlock() {
    return "TableBodyBlock";
  }
}
