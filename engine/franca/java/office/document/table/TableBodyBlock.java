package franca.java.office.document.table;

import franca.java.office.document.Block;

public class TableBodyBlock extends Block {

  @Override
  public String getSerializationTag() {
    return "tbody";
  }

  @Override
  public String getDataBlock() {
    return "TableBodyBlock";
  }
}
