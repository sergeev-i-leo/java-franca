package franca.java.office.document.list;

import franca.java.office.document.Block;

public class ListBlock extends Block {

  public boolean isOrdered;

  public ListBlock(boolean isOrdered) {
    super();
    this.isOrdered = isOrdered;
  }

  @Override
  public String getSerializationTag() {
    return isOrdered ? "ol" : "ul";
  }

  @Override
  public String getDataBlock() {
    return "ListBlock";
  }
}
