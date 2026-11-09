package franca.java.office.document;

import java.util.ArrayList;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.data.json.JsonArray;
import franca.java.office.document.factory.DocumentFactory;

public class Block {

  private static ArrayList<Block> emptyChildBlocks = null;

  public Block outerBlock = null;

  // array of strings className, className, className ...
  public JsonArray classesJsonArray = new JsonArray();
  // single attributes, non-quoted attributes, quoted attributes
  public JsonArray attributesJsonArray = new JsonArray();
  // style
  public JsonObject styleJsonObject = new JsonObject();
  // flavoured style, not html-compatible styles styles
  public JsonObject flavouredStyleJsonObject = new JsonObject();

  private ArrayList<Block> innerBlocks = null;

  public void fillJsonObject(JsonObject jsonObject) {
    jsonObject.putStringValue("dataBlock", getDataBlock());
    JsonArray classesJsonArray = this.classesJsonArray.createCopy().asJsonArray();
    if ((classesJsonArray != null) && (classesJsonArray.isNotEmpty())) {
      jsonObject.put("classesJsonArray", classesJsonArray);
    }
    JsonArray attributesJsonArray = this.attributesJsonArray.createCopy().asJsonArray();
    if ((attributesJsonArray != null) && (attributesJsonArray.isNotEmpty())) {
      jsonObject.put("attributesJsonArray", attributesJsonArray);
    }
    JsonObject styleJsonObject = this.styleJsonObject.createCopy().asJsonObject();
    if ((styleJsonObject != null) && (styleJsonObject.isNotEmpty())) {
      jsonObject.put("styleJsonObject", styleJsonObject);
    }
    JsonObject flavouredStyleJsonObject = this.flavouredStyleJsonObject.createCopy().asJsonObject();
    if ((flavouredStyleJsonObject != null) && (flavouredStyleJsonObject.isNotEmpty())) {
      jsonObject.put("flavouredStyleJsonObject", flavouredStyleJsonObject);
    }
    if (innerBlocks == null) {
      return;
    }
    if (innerBlocks.isEmpty()) {
      return;
    }
    JsonArray jsonArray = new JsonArray();
    jsonObject.put("innerBlocks", jsonArray);
    addInnerBlocksToJsonArray(jsonArray);
  }

  public void addInnerBlocksToJsonArray(JsonArray jsonArray) {
    for (int i = 0; i < innerBlocks.size(); i++) {
      JsonObject jsonObject1 = new JsonObject();
      jsonArray.add(jsonObject1);
      innerBlocks.get(i).fillJsonObject(jsonObject1);
    }
  }

  public void serialize(BufferedString targetBufferedString, int spacesBefore) {
    if (spacesBefore >= 0) {
      targetBufferedString.appendChars(' ', spacesBefore);
    }

    String serializationTag = getSerializationTag();
    targetBufferedString.appendString("<" + serializationTag + " data-block=\"" + getDataBlock() + "\"");

    serializeClassesJsonArray(targetBufferedString);

    serializeAttributesJsonArray(targetBufferedString);

    serializeStyleJsonObject(targetBufferedString);

    serializeFlavouredStyleJsonObject(targetBufferedString);

    if (spacesBefore >= 0) {
      serializeContents(targetBufferedString, serializationTag, spacesBefore + 2);
    } else {
      serializeContents(targetBufferedString, serializationTag, spacesBefore);
    }
  }

  public String getSerializationTag() {
    return "div";
  }

  public String getDataBlock() {
    return "Block";
  }

  public void serializeClassesJsonArray(BufferedString targetBufferedString) {
    if (classesJsonArray.isEmpty()) {
      return;
    }
    targetBufferedString.appendString(" class=\"");
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
  }

  public void serializeStyleJsonObject(BufferedString targetBufferedString) {
    ArrayList<String> keys = styleJsonObject.keys();
    if (keys.isEmpty()) {
      return;
    }

    targetBufferedString.appendString(" style=\"");

    for (String key : keys) {
      String value = styleJsonObject.getStringValue(key);
      if (value != null) {
        targetBufferedString.appendString(key + ":" + value + ";");
      }
    }
    targetBufferedString.appendString("\"");
  }

  public void serializeFlavouredStyleJsonObject(BufferedString targetBufferedString) {
    ArrayList<String> keys = flavouredStyleJsonObject.keys();
    if (keys.isEmpty()) {
      return;
    }

    targetBufferedString.appendString(" flavoured-style=\"");

    for (String key : keys) {
      String value = flavouredStyleJsonObject.getStringValue(key);
      if (value != null) {
        targetBufferedString.appendString(key + ":" + value + ";");
      }
    }
    targetBufferedString.appendString("\"");
  }

  public void serializeAttributesJsonArray(BufferedString targetBufferedString) {
    if (attributesJsonArray.isEmpty()) {
      return;
    }

    for (int i = 0; i < attributesJsonArray.size(); i++) {
      String string = attributesJsonArray.getStringValue(i);
      if (string != null) {
        targetBufferedString.appendChar(' ');
        targetBufferedString.appendString(string);
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
      targetBufferedString.appendChar(' ');
      targetBufferedString.appendString(string);
      string = jsonObject.getStringValue("value");
      if (string != null) {
        targetBufferedString.appendString("=" + string);
      } else {
        string = jsonObject.getStringValue("quotedValue");
        if (string != null) {
          targetBufferedString.appendString(" = \"" + string + "\"");
        }
      }
    }
  }

  public void serializeContents(BufferedString targetBufferedString, String serializationTag, int spacesBefore) {
    if (DocumentFactory.htmlTagIsSelfClosing(serializationTag)) {
      targetBufferedString.appendString(">");
      if (spacesBefore >= 0) {
        targetBufferedString.finishLine();
      }
      return;
    } else if (getInnerBlocks().isEmpty()) {
      targetBufferedString.appendString("/>");
      if (spacesBefore >= 0) {
        targetBufferedString.finishLine();
      }
      return;
    }
    targetBufferedString.appendString(">");
    if (spacesBefore >= 0) {
      targetBufferedString.finishLine();
    }

    for (Block block : getInnerBlocks()) {
      block.serialize(targetBufferedString, spacesBefore);
    }

    if (spacesBefore >= 0) {
      targetBufferedString.appendChars(' ', spacesBefore);
    }
    targetBufferedString.appendString("</" + serializationTag + ">");
    if (spacesBefore >= 0) {
      targetBufferedString.finishLine();
    }
  }

  public void addQuotedAttribute(String attributeName, String attributeValue) {
    for (int i = 0; i < attributesJsonArray.size(); i++) {
      JsonObject jsonObject = attributesJsonArray.getJsonObject(i);
      if (jsonObject == null) {
        continue;
      }
      String string = jsonObject.getStringValue("name");
      if (string == null) {
        continue;
      }
      if (string.equals(attributeName)) {
        return;
      }
    }
    JsonObject jsonObject = new JsonObject();
    attributesJsonArray.add(jsonObject);
    jsonObject.putStringValue("name", attributeName);
    jsonObject.putStringValue("quotedValue", attributeValue);
  }

  public void addInnerBlock(Block block) {
    if (innerBlocks == null) {
      innerBlocks = new ArrayList<>();
    }
    innerBlocks.add(block);
    block.outerBlock = this;
  }

  public ArrayList<Block> getInnerBlocks() {
    if (innerBlocks != null) {
      return innerBlocks;
    }
    if (Block.emptyChildBlocks == null) {
      Block.emptyChildBlocks = new ArrayList<>();
    }
    return Block.emptyChildBlocks;
  }

  public Block getInnerBlock(int index) {
    if (innerBlocks == null) {
      return null;
    }
    if ((index < 0) || (index >= innerBlocks.size())) {
      return null;
    }
    return innerBlocks.get(index);
  }

  public void clearInnerBlocks() {
    if (innerBlocks != null) {
      innerBlocks.clear();
    }
    innerBlocks = null;
  }

}
