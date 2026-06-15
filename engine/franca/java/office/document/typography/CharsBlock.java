package franca.java.office.document.typography;

import franca.java.office.document.Block;

public class CharsBlock extends Block {

  public static final String TYPE_CHARS = "chars";
  public static final String TYPE_SPACE = "space";
  public static final String TYPE_NON_BREAKABLE_SPACE = "non-breakable-space";
  public static final String TYPE_LINE_BREAK = "line-break";

  public String type = CharsBlock.TYPE_CHARS;

  private String chars = "";

  public String getClassName() {
    return "CharsBlock";
  }

  public String getChars() {
    return chars;
  }

  public void setChars(String chars) {
    this.chars = chars;
  }

}
