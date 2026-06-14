package franca.java.office.document.typography_blocks;

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

  public String getClassName() {
    return "HeadingBlock";
  }
}
