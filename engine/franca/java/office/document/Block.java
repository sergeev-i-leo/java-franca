package franca.java.office.document;

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
  public BlockStyle blockStyle = new BlockStyle();
  // single attributes, non-quoted attributes, quoted attributes
  public JsonArray attributesJsonArray = new JsonArray();

  private ArrayList<Block> blocks = null;

  public String getClassName() {
    return "Block";
  }

  public void serialize(BufferedString targetBufferedString, int spacesBefore) {
    targetBufferedString.appendChars(' ', spacesBefore);

    String serializationTag = getSerializationTag();
    targetBufferedString.appendString("<" + serializationTag + " data-class-name=\"" + getClassName() + "\"");
    targetBufferedString.appendEndLine();

    serializeClassesJsonArray(targetBufferedString, spacesBefore + 1 + serializationTag.length() + 1);

    serializeBlockStyle(targetBufferedString, spacesBefore + 1 + serializationTag.length() + 1);

    serializeAttributesJsonArray(targetBufferedString, spacesBefore + 1 + serializationTag.length() + 1);

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

    for (Block block : getBlocks()) {
      block.serialize(targetBufferedString, spacesBefore + 4);
    }
  }

  public String getSerializationTag() {
    return "div";
  }

  public void serializeClassesJsonArray(BufferedString bufferedString, Integer spacesBefore) {
  }

  public void serializeBlockStyle(BufferedString bufferedString, Integer spacesBefore) {
  }

  public void serializeAttributesJsonArray(BufferedString bufferedString, Integer spacesBefore) {
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
