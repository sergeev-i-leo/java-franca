package franca.java.office.document;

import franca.java.expected.TranspilableClass;
import franca.java.data.json.JsonArray;

import java.util.ArrayList;

public class Block extends TranspilableClass {

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

  public void addBlock(Block block) {
    if (blocks == null) {
      blocks = new ArrayList<>();
    }
    blocks.add(block);
    block.parentBlock = this;
  }

  public ArrayList<Block> getBlocks() {
    return blocks;
  }

  public void clearBlocks() {
    if (blocks != null) {
      blocks.clear();
    }
    blocks = null;
  }

}
