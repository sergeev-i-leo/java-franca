package franca.java.office.document.typography;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;
import franca.java.office.document.factory.DocumentFactory;

public class CharsBlock extends Block {

  public static final String TYPE_CHARS = "chars";
  public static final String TYPE_SPACE = "space";
  public static final String TYPE_NON_BREAKABLE_SPACE = "non-breakable-space";
  public static final String TYPE_LINE_BREAK = "line-break";

  public String type = CharsBlock.TYPE_CHARS;

  private String chars = "";

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    jsonObject.putStringValue("type", type);
    String chars = this.chars.replace("\"", "\\\"");
    jsonObject.putStringValue("chars", chars);
    super.fillJsonObject(jsonObject);
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
    return "chars-block";
  }

  @Override
  public void serializeContents(BufferedString targetBufferedString, String serializationTag, int spacesBefore) {
    targetBufferedString.appendString(">");

    if (type.equals(CharsBlock.TYPE_CHARS)) {
      for (int i = 0; i < chars.length(); i++) {
        char c = chars.charAt(i);
        String string = encodeHtmChar(c);
        if (string != null) {
          targetBufferedString.appendString(string);
        } else {
          targetBufferedString.appendChar(c);
        }
      }
    }
    if (type.equals(CharsBlock.TYPE_SPACE)) {
      targetBufferedString.appendChar(' ');
    }
    if (type.equals(CharsBlock.TYPE_NON_BREAKABLE_SPACE)) {
      targetBufferedString.appendString("&nbsp;");
    }
    if (type.equals(CharsBlock.TYPE_LINE_BREAK)) {
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

  public String getChars() {
    return chars;
  }

  public void setChars(String chars) {
    this.chars = chars;
  }

}
