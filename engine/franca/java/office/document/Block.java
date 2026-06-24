package franca.java.office.document;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.expected.TranspilableClass;
import franca.java.data.json.JsonArray;
import franca.java.office.document.factory.DocumentFactory;

import java.util.ArrayList;

public class Block extends TranspilableClass {

  private static ArrayList<Block> emptyChildren = null;

  public Block parent = null;

  // array of strings className, className, className ...
  public JsonArray classes = new JsonArray();
  // style
  public JsonObject style = new JsonObject();
  // single attributes, non-quoted attributes, quoted attributes
  public JsonArray attributes = new JsonArray();

  private ArrayList<Block> children = null;

  public JsonObject createJsonObject() {

    JsonObject resultJsonObject = new JsonObject();
    fillJsonObject(resultJsonObject);
    return resultJsonObject;
  }

  public void fillJsonObject(JsonObject jsonObject) {
    jsonObject.putStringValue("data-block", getDataBlock());
    JsonArray classesJsonArray = this.classes.createCopy().asJsonArray();
    if ((classesJsonArray != null) && (classesJsonArray.isNotEmpty())) {
      jsonObject.put("classes", classesJsonArray);
    }
    JsonObject styleJsonObject = this.style.createCopy().asJsonObject();
    if ((styleJsonObject != null) && (styleJsonObject.isNotEmpty())) {
      jsonObject.put("style", styleJsonObject);
    }
    JsonArray attributesJsonArray = this.attributes.createCopy().asJsonArray();
    if ((attributesJsonArray != null) && (attributesJsonArray.isNotEmpty())) {
      jsonObject.put("attributes", attributesJsonArray);
    }

    if (children != null) {
      JsonArray blocksJsonArray = new JsonArray();
      jsonObject.put("blocks", blocksJsonArray);
      for (int i = 0; i < children.size(); i++) {
        blocksJsonArray.add(children.get(i).createJsonObject());
      }
    }
  }

  public void serialize(BufferedString targetBufferedString, int spacesBefore) {
    targetBufferedString.appendChars(' ', spacesBefore);

    String serializationTag = getSerializationTag();
    targetBufferedString.appendString("<" + serializationTag + " data-block=\"" + getDataBlock() + "\"");

    serializeClassesJsonArray(targetBufferedString);

    serializeStyleJsonObject(targetBufferedString);

    serializeAttributesJsonArray(targetBufferedString);

    if (DocumentFactory.htmlTagIsSelfClosing(serializationTag)) {
      targetBufferedString.appendString(">");
      if (spacesBefore >= 0) {
        targetBufferedString.finishLine();
      }
      return;
    } else if (getChildren().isEmpty()) {
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

    serializeContents(targetBufferedString, serializationTag, spacesBefore + 2);
  }

  public String getSerializationTag() {
    return "div";
  }

  public String getDataBlock() {
    return "block";
  }

  public void serializeClassesJsonArray(BufferedString targetBufferedString) {
    if (classes.isEmpty()) {
      return;
    }
    targetBufferedString.appendString(" class=\"");
    for (int i = 0; i < classes.size(); i++) {
      String string = classes.get(i).asStringValue();
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
    ArrayList<String> keys = style.keys();
    if (keys.isEmpty()) {
      return;
    }

    targetBufferedString.appendString(" style=\"");

    for (String key : keys) {
      String value = style.getStringValue(key);
      if (value != null) {
        targetBufferedString.appendString(key + ":" + value + ";");
      }
    }
    targetBufferedString.appendString("\"");
  }

  public void serializeAttributesJsonArray(BufferedString targetBufferedString) {
    if (attributes.isEmpty()) {
      return;
    }

    for (int i = 0; i < attributes.size(); i++) {
      String string = attributes.getStringValue(i);
      if (string != null) {
        targetBufferedString.appendChar(' ');
        targetBufferedString.appendString(string);
        continue;
      }
      JsonObject jsonObject = attributes.getJsonObject(i);
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
        targetBufferedString.appendString(" = " + string);
      } else {
        string = jsonObject.getStringValue("quoted-value");
        if (string != null) {
          targetBufferedString.appendString(" = \"" + string + "\"");
        }
      }
    }
  }

  public void serializeContents(BufferedString targetBufferedString, String serializationTag, int spacesBefore) {

    for (Block block : getChildren()) {
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

  public void addChild(Block block) {
    if (children == null) {
      children = new ArrayList<>();
    }
    children.add(block);
    block.parent = this;
  }

  public ArrayList<Block> getChildren() {
    if (children != null) {
      return children;
    }
    if (Block.emptyChildren == null) {
      Block.emptyChildren = new ArrayList<>();
    }
    return Block.emptyChildren;
  }

  public Block getBlock(int index) {
    if (children == null) {
      return null;
    }
    if ((index < 0) || (index >= children.size())) {
      return null;
    }
    return children.get(index);
  }

  public void clearBlocks() {
    if (children != null) {
      children.clear();
    }
    children = null;
  }

  public void addQuotedAttribute(String attributeName, String attributeValue) {
    for (int i = 0; i < attributes.size(); i++) {
      JsonObject jsonObject = attributes.getJsonObject(i);
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
    attributes.add(jsonObject);
    jsonObject.putStringValue("name", attributeName);
    jsonObject.putStringValue("quoted-value", attributeValue);
  }

}
