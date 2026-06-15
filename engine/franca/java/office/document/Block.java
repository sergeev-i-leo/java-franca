package franca.java.office.document;

import franca.java.expected.TranspilableClass;
import franca.java.data.json.JsonArray;
import franca.java.expected.ExpectedRuntime;

import java.util.ArrayList;

public class Block extends TranspilableClass {

  public Block parentBlock = null;

  // array of strings className, className, className ...
  public JsonArray classesJsonArray = new JsonArray();
  // array of strings styleName, styleValue, styleName, styleValue ...
  public JsonArray styleJsonArray = new JsonArray();
  // single attributes, non-quoted attributes, quoted attributes
  public JsonArray attributesJsonArray = new JsonArray();

  public BlockStyle blockStyle = new BlockStyle();

  private ArrayList<Block> blocks = null;

  public String getClassName() {
    return "Block";
  }

  public void createBlockStyle() {

    int i = 0;
    while (i < styleJsonArray.size()) {
      String styleName = styleJsonArray.get(i).getStringValue();
      i++;
      if (i < styleJsonArray.size()) {
        String styleValue = styleJsonArray.get(i).getStringValue();
        if ((styleName != null) && (styleValue != null)) {
          if (styleName.equals("color")) {
            blockStyle.color = ExpectedRuntime.hexStringToInteger(styleValue);
            if (blockStyle.color == null) {
              blockStyle.color = ExpectedRuntime.stringToInteger(styleValue);
            }
          } else if (styleName.equals("background-color")) {
            blockStyle.backgroundColor = ExpectedRuntime.hexStringToInteger(styleValue);
            if (blockStyle.backgroundColor == null) {
              blockStyle.backgroundColor = ExpectedRuntime.stringToInteger(styleValue);
            }
          } else if ((styleName.equals("text-align")) && (styleValue.equals("left"))) {
            blockStyle.textAlign = styleValue;
          } else if ((styleName.equals("text-align")) && (styleValue.equals("center"))) {
            blockStyle.textAlign = styleValue;
          } else if ((styleName.equals("text-align")) && (styleValue.equals("right"))) {
            blockStyle.textAlign = styleValue;
          } else if ((styleName.equals("text-align")) && (styleValue.equals("justify"))) {
            blockStyle.textAlign = styleValue;
          } else if ((styleName.equals("font-weight")) && (styleValue.equals("normal"))) {
            blockStyle.fontWeight = 400;
          } else if ((styleName.equals("font-weight")) && (styleValue.equals("bold"))) {
            blockStyle.fontWeight = 700;
          } else if (styleName.equals("font-weight")) {
            blockStyle.fontWeight = ExpectedRuntime.stringToInteger(styleValue);
          } else if ((styleName.equals("font-style")) && (styleValue.equals("italic"))) {
            blockStyle.isItalic = true;
          } else if ((styleName.equals("font-style")) && (styleValue.equals("normal"))) {
            // reset font
            blockStyle.isItalic = false;
          } else if ((styleName.equals("text-decoration")) && (styleValue.equals("underline"))) {
            blockStyle.isUnderline = true;
          } else if ((styleName.equals("text-decoration")) && (styleValue.equals("strike-through"))) {
            blockStyle.isStrikethrough = true;
          } else if ((styleName.equals("text-decoration")) && (styleValue.equals("none"))) {
            blockStyle.isUnderline = false;
            blockStyle.isStrikethrough = false;
          }
        }
      }
      i++;
    }
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

}
