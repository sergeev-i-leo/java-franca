package franca.java.office.document.list;

import franca.java.office.document.Block;

public class ListBlock extends Block {

  public boolean ordered;

  public ListBlock(boolean ordered) {
    super();
    this.ordered = ordered;
  }

  @Override
  public String getDataBlock() {
    return "ListBlock";
  }
}
