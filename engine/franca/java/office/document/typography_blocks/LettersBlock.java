package franca.java.office.document.typography_blocks;

import franca.java.office.document.Block;

public class LettersBlock extends Block {

  public static final String TYPE_TEXT = "text";
  public static final String TYPE_SPACE = "space";
  public static final String TYPE_NON_BREAKABLE_SPACE = "non-breakable-space";
  public static final String TYPE_LINE_BREAK = "line-break";

  public String type = LettersBlock.TYPE_TEXT;

  private String text = "";

  public String getClassName() {
    return "TextBlock";
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

}
