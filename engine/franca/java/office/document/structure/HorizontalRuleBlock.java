package franca.java.office.document.structure;

import franca.java.office.document.Block;

public class HorizontalRuleBlock extends Block {

  public String type = "";

  @Override
  public String getSerializationTag() {
    return "hr";
  }

  @Override
  public String getDataBlock() {
    return "HorizontalRuleBlock";
  }
}
