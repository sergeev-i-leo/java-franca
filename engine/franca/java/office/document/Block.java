package franca.java.office.document;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.expected.TranspilableClass;
import franca.java.data.json.JsonArray;
import franca.java.office.document.factory.DocumentFactory;

import java.util.ArrayList;

public class Block extends TranspilableClass {

  private static ArrayList<Block> emptyChildBlocks = null;

  public Block parentBlock = null;

  // array of strings className, className, className ...
  public JsonArray classesJsonArray = new JsonArray();
  // style
  public JsonObject styleJsonObject = new JsonObject();
  // single attributes, non-quoted attributes, quoted attributes
  public JsonArray attributesJsonArray = new JsonArray();

  private ArrayList<Block> childBlocks = null;

  public void addJsonElements(JsonArray jsonArray) {

    JsonObject jsonObject = new JsonObject();
    jsonArray.add(jsonObject);
    fillJsonObject(jsonObject);

    if (childBlocks != null) {
      jsonArray = new JsonArray();
      jsonObject.put("childBlocks", jsonArray);
      for (int i = 0; i < childBlocks.size(); i++) {
        childBlocks.get(i).addJsonElements(jsonArray);
      }
    }
  }

  public void fillJsonObject(JsonObject jsonObject) {
    jsonObject.putStringValue("data-block", getDataBlock());
    JsonArray classesJsonArray = this.classesJsonArray.createCopy().asJsonArray();
    if ((classesJsonArray != null) && (classesJsonArray.isNotEmpty())) {
      jsonObject.put("classesJsonArray", classesJsonArray);
    }
    JsonObject styleJsonObject = this.styleJsonObject.createCopy().asJsonObject();
    if ((styleJsonObject != null) && (styleJsonObject.isNotEmpty())) {
      jsonObject.put("styleJsonObject", styleJsonObject);
    }
    JsonArray attributesJsonArray = this.attributesJsonArray.createCopy().asJsonArray();
    if ((attributesJsonArray != null) && (attributesJsonArray.isNotEmpty())) {
      jsonObject.put("attributesJsonArray", attributesJsonArray);
    }
  }

  public void serialize(BufferedString targetBufferedString, int spacesBefore) {
    if (spacesBefore >= 0) {
      targetBufferedString.appendChars(' ', spacesBefore);
    }

    String serializationTag = getSerializationTag();
    targetBufferedString.appendString("<" + serializationTag + " data-block=\"" + getDataBlock() + "\"");

    serializeClassesJsonArray(targetBufferedString);

    serializeStyleJsonObject(targetBufferedString);

    serializeAttributesJsonArray(targetBufferedString);

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
    return "block";
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
        string = jsonObject.getStringValue("quoted-value");
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
    } else if (getChildBlocks().isEmpty()) {
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

    for (Block block : getChildBlocks()) {
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
    jsonObject.putStringValue("quoted-value", attributeValue);
  }

  public void addChildBlock(Block block) {
    if (childBlocks == null) {
      childBlocks = new ArrayList<>();
    }
    childBlocks.add(block);
    block.parentBlock = this;
  }

  public ArrayList<Block> getChildBlocks() {
    if (childBlocks != null) {
      return childBlocks;
    }
    if (Block.emptyChildBlocks == null) {
      Block.emptyChildBlocks = new ArrayList<>();
    }
    return Block.emptyChildBlocks;
  }

  public Block getChildBlock(int index) {
    if (childBlocks == null) {
      return null;
    }
    if ((index < 0) || (index >= childBlocks.size())) {
      return null;
    }
    return childBlocks.get(index);
  }

  public void clearChildrenBlocks() {
    if (childBlocks != null) {
      childBlocks.clear();
    }
    childBlocks = null;
  }

}
