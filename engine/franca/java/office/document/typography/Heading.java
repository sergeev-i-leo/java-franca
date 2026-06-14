package franca.java.office.document.typography;

public class Heading extends TextBlock {

  public int level;

  public Heading(int level) {
    super();
    if ((level >= 1) && (level <= 6)) {
      this.level = level;
    } else {
      this.level = 1;
    }
  }

  public String getClassName() {
    return "Heading";
  }
}
