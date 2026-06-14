package franca.java.office.document.typography_blocks;

import franca.java.expected.StringBuffer;
import franca.java.office.document.Block;

public class TextBlock extends Block {

  public String getClassName() {
    return "TextBlock";
  }

  public String getText() {
    if (getBlocks() == null) {
      return "";
    }
    StringBuffer stringBuffer = new StringBuffer();
    for (Block block : getBlocks()) {
      if (block instanceof LettersBlock) {
        stringBuffer.appendString(((LettersBlock) block).getText());
      }
    }
    return stringBuffer.getString();
  }

  public void setText(String text) {
    if (getBlocks() != null) {
      getBlocks().clear();
    }

    LettersBlock letters = new LettersBlock();
    letters.setText(text);
    addBlock(letters);
  }
}
