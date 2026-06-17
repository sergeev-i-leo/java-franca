package franca.java.office.document.structure;

import franca.java.expected.BufferedString;
import franca.java.office.document.Block;

public class HorizontalRuleBlock extends Block {

  public String type = "";

  @Override
  public void serialize(BufferedString targetBufferedString, int spacesBefore) {
    addQuotedAttribute("data-type", type);
    super.serialize(targetBufferedString, spacesBefore);
  }

  @Override
  public String getSerializationTag() {
    return "hr";
  }

  @Override
  public String getDataBlock() {
    return "HorizontalRuleBlock";
  }
}
