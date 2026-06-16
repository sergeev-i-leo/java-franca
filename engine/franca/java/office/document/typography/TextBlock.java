package franca.java.office.document.typography;

import franca.java.expected.BufferedString;
import franca.java.office.document.Block;

public class TextBlock extends Block {

  public String getClassName() {
    return "TextBlock";
  }

  public String getText() {
    if (getBlocks() == null) {
      return "";
    }
    BufferedString bufferedString = new BufferedString();
    for (Block block : getBlocks()) {
      if (block instanceof CharsBlock) {
        bufferedString.appendString(((CharsBlock) block).getChars());
      }
    }
    return bufferedString.getString();
  }

  public void setText(String text) {
    if (getBlocks() != null) {
      getBlocks().clear();
    }

    CharsBlock letters = new CharsBlock();
    letters.setChars(text);
    addBlock(letters);
  }
}
