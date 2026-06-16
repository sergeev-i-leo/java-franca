package franca.java.office.document.structure;

import franca.java.office.document.Block;

public class HorizontalRuleBlock extends Block {

  public String text = "";

  @Override
  public String getDataBlock() {
    return "HorizontalRuleBlock";
  }
}
