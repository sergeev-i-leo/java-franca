package franca.java.office.document;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.expected.TranspilableClass;
import franca.java.data.json.JsonArray;
import franca.java.office.document.factory.DocumentFactory;

import java.util.ArrayList;

public class Block extends TranspilableClass {

  private static ArrayList<Block> emptyBlocks = null;

  public Block parentBlock = null;

  // array of strings className, className, className ...
  public JsonArray classesJsonArray = new JsonArray();
  // style
  public JsonObject styleJsonObject = new JsonObject();
  // single attributes, non-quoted attributes, quoted attributes
  public JsonArray attributesJsonArray = new JsonArray();

  private ArrayList<Block> blocks = null;

  public String getDataBlock() {
    return "Block";
  }

  public void serialize(BufferedString targetBufferedString, int spacesBefore) {
    targetBufferedString.appendChars(' ', spacesBefore);

    String serializationTag = getSerializationTag();
    targetBufferedString.appendString("<" + serializationTag + " data-block=\"" + getDataBlock() + "\"");
    targetBufferedString.appendEndLine();

    serializeClassesJsonArray(targetBufferedString, spacesBefore + 1 + serializationTag.length() + 1);

    serializeStyleJsonObject(targetBufferedString, spacesBefore + 1 + serializationTag.length() + 1);

    serializeAttributesJsonArray(targetBufferedString, spacesBefore + 1 + serializationTag.length() + 1);

    serializeContents(targetBufferedString, serializationTag, spacesBefore);
  }

  public String getSerializationTag() {
    return "div";
  }

  public void serializeClassesJsonArray(BufferedString targetBufferedString, int spacesBefore) {
    if (classesJsonArray.isEmpty()) {
      return;
    }
    targetBufferedString.appendChars(' ', spacesBefore);
    targetBufferedString.appendString("class=\"");
    for (int i = 0; i < classesJsonArray.size(); i++) {
      String string = classesJsonArray.get(i).asStringValue();
      if (string != null) {
        if (i > 0) {
          targetBufferedString.appendChar(' ');
        }
        targetBufferedString.appendString(string);
      }
    }
    targetBufferedString.appendString("\"");
    targetBufferedString.appendEndLine();
  }

  public void serializeStyleJsonObject(BufferedString targetBufferedString, int spacesBefore) {
    ArrayList<String> keys = styleJsonObject.keys();
    if (keys.isEmpty()) {
      return;
    }

    targetBufferedString.appendChars(' ', spacesBefore);
    targetBufferedString.appendString("style=\"");

    for (String key : keys) {
      String value = styleJsonObject.getStringValue(key);
      if (value != null) {
        targetBufferedString.appendString(key + "=" + value + ";");
      }
    }
    targetBufferedString.appendString("\"");
    targetBufferedString.appendEndLine();
  }

  public void serializeAttributesJsonArray(BufferedString targetBufferedString, int spacesBefore) {
    for (int i = 0; i < attributesJsonArray.size(); i++) {
      String string = attributesJsonArray.getStringValue(i);
      if (string != null) {
        targetBufferedString.appendChars(' ', spacesBefore);
        targetBufferedString.appendString(string);
        targetBufferedString.appendEndLine();
        continue;
      }
      JsonObject jsonObject = attributesJsonArray.getJsonObject(i);
      if (jsonObject == null) {
        continue;
      }
      string = jsonObject.getStringValue("name");
      if (string == null) {
        continue;
      }
      targetBufferedString.appendChars(' ', spacesBefore);
      targetBufferedString.appendString(string);
      string = jsonObject.getStringValue("value");
      if (string != null) {
        targetBufferedString.appendString(" = " + string);
      } else {
        string = jsonObject.getStringValue("quoted-value");
        if (string != null) {
          targetBufferedString.appendString(" = \"" + string + "\"");
        }
      }
      targetBufferedString.appendEndLine();
    }
  }

  public void serializeContents(BufferedString targetBufferedString, String serializationTag, int spacesBefore) {

    if (DocumentFactory.htmlTagIsSelfClosing(serializationTag)) {
      targetBufferedString.appendChars(' ', spacesBefore);
      targetBufferedString.appendString(">");
      targetBufferedString.appendEndLine();
      return;
    } else if (getBlocks().isEmpty()) {
      targetBufferedString.appendChars(' ', spacesBefore);
      targetBufferedString.appendString("/>");
      targetBufferedString.appendEndLine();
      return;
    }

    targetBufferedString.appendChars(' ', spacesBefore);
    targetBufferedString.appendString(">");
    targetBufferedString.appendEndLine();

    for (Block block : getBlocks()) {
      block.serialize(targetBufferedString, spacesBefore + 4);
    }

    targetBufferedString.appendChars(' ', spacesBefore);
    targetBufferedString.appendString("</" + serializationTag + ">");
    targetBufferedString.appendEndLine();
  }

  public void addBlock(Block block) {
    if (blocks == null) {
      blocks = new ArrayList<>();
    }
    blocks.add(block);
    block.parentBlock = this;
  }

  public ArrayList<Block> getBlocks() {
    if (blocks != null) {
      return blocks;
    }
    if (Block.emptyBlocks == null) {
      Block.emptyBlocks = new ArrayList<>();
    }
    return Block.emptyBlocks;
  }

  public void clearBlocks() {
    if (blocks != null) {
      blocks.clear();
    }
    blocks = null;
  }

}
