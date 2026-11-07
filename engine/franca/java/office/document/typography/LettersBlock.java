package franca.java.office.document.typography;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;

public class LettersBlock extends Block {

  public static final String TYPE_LETTERS = "letters";
  public static final String TYPE_SPACE = "space";
  public static final String TYPE_NON_BREAKABLE_SPACE = "non-breakable-space";
  public static final String TYPE_LINE_BREAK = "line-break";

  public String type = LettersBlock.TYPE_LETTERS;

  public String letters = "";

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
    jsonObject.putStringValue("type", type);
    jsonObject.putStringValue("letters", letters);
  }

  @Override
  public void serialize(BufferedString targetBufferedString, int spacesBefore) {
    addQuotedAttribute("data-type", type);
    super.serialize(targetBufferedString, spacesBefore);
  }

  @Override
  public String getSerializationTag() {
    return "span";
  }

  @Override
  public String getDataBlock() {
    return "LettersBlock";
  }

  @Override
  public void serializeContents(BufferedString targetBufferedString, String serializationTag, int spacesBefore) {
    targetBufferedString.appendString(">");

    if (type.equals(LettersBlock.TYPE_LETTERS)) {
      for (int i = 0; i < letters.length(); i++) {
        char c = letters.charAt(i);
        String string = encodeHtmChar(c);
        if (string != null) {
          targetBufferedString.appendString(string);
        } else {
          targetBufferedString.appendChar(c);
        }
      }
    }
    if (type.equals(LettersBlock.TYPE_SPACE)) {
      targetBufferedString.appendChar(' ');
    }
    if (type.equals(LettersBlock.TYPE_NON_BREAKABLE_SPACE)) {
      targetBufferedString.appendString("&nbsp;");
    }
    if (type.equals(LettersBlock.TYPE_LINE_BREAK)) {
      targetBufferedString.appendString("<br>");
    }

    targetBufferedString.appendString("</" + serializationTag + ">");
    if (spacesBefore >= 0) {
      targetBufferedString.finishLine();
    }
  }

  public String encodeHtmChar(char c) {
    switch (c) {
      case '&':
        return "&amp;";
      case '<':
        return "&lt;";
      case '>':
        return "&gt;";
      case '"':
        return "&quot;";
      case '\'':
        return "&#39;";
    }
    return null;
  }

  public String getLetters() {
    return letters;
  }

  public void setLetters(String letters) {
    this.letters = letters;
  }

}
