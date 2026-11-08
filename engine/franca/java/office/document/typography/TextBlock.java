package franca.java.office.document.typography;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;

public class TextBlock extends Block {

  @Override
  public String getDataBlock() {
    return "TextBlock";
  }

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
    jsonObject.putStringValue("text", getText());
  }

  @Override
  public void serializeContents(BufferedString targetBufferedString, String serializationTag, int spacesBefore) {
    super.serializeContents(targetBufferedString, serializationTag, -1);
    if (spacesBefore >= 0) {
      targetBufferedString.finishLine();
    }
  }

  public String getText() {
    if (getInnerBlocks() == null) {
      return "";
    }
    BufferedString bufferedString = new BufferedString();
    for (Block block : getInnerBlocks()) {
      if (block instanceof LettersBlock) {
        bufferedString.appendString(((LettersBlock) block).getLetters());
      }
    }
    return bufferedString.getString();
  }
}
