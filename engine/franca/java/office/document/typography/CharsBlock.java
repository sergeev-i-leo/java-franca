package franca.java.office.document.typography;

import franca.java.data.json.JsonElement;
import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
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

  @Override
  public void serialize(BufferedString targetBufferedString, int spacesBefore) {
    int i = 0;
    while (i < attributesJsonArray.size()) {
      JsonObject jsonObject = attributesJsonArray.get(i).asJsonObject();
      if (jsonObject == null) {
        continue;
      }
      String string = jsonObject.getStringValue("name");
      if (string == null) {
        i++;
        continue;
      }
      if (string.equals("data-type")) {
        break;
      }
      i++;
    }
    if (i == attributesJsonArray.size()) {
      JsonObject jsonObject = new JsonObject();
      attributesJsonArray.add(jsonObject);
      jsonObject.putStringValue("name", "data-type");
      jsonObject.putStringValue("string-value", type);
    }
    super.serialize(targetBufferedString, spacesBefore);
  }

  public String getSerializationTag() {
    return "span";
  }

  public String getChars() {
    return chars;
  }

  public void setChars(String chars) {
    this.chars = chars;
  }

}
