package franca.java.office.document.typography;

import franca.java.expected.ExpectedRuntime;

public class HeadingBlock extends TextBlock {

  public int level;

  public HeadingBlock(int level) {
    super();
    if ((level >= 1) && (level <= 6)) {
      this.level = level;
    } else {
      this.level = 1;
    }
  }

  @Override
  public String getSerializationTag() {
    return "h" + ExpectedRuntime.intToString(level);
  }

  @Override
  public String getDataBlock() {
    return "heading-block";
  }
}
