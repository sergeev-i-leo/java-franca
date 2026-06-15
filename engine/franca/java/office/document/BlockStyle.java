package franca.java.office.document;

import franca.java.expected.TranspilableClass;

public class BlockStyle extends TranspilableClass {

  public Integer color = null;
  public Integer backgroundColor = null;

  public String deviceFontName = null;
  public Integer fontWeight = null;
  public Boolean isItalic = null;
  public Boolean isUnderline = null;
  public Boolean isStrikethrough = null;

  @Override
  public String getClassName() {
    return "BlockStyle";
  }

  public void mergeWith(BlockStyle blockStyle) {
    if (blockStyle.color != null) {
      color = blockStyle.color;
    }
    if (blockStyle.backgroundColor != null) {
      backgroundColor = blockStyle.backgroundColor;
    }
    if (blockStyle.deviceFontName != null) {
      deviceFontName = blockStyle.deviceFontName;
    }
    if (blockStyle.fontWeight != null) {
      fontWeight = blockStyle.fontWeight;
    }
    if (blockStyle.isItalic != null) {
      isItalic = blockStyle.isItalic;
    }
    if (blockStyle.isUnderline != null) {
      isUnderline = blockStyle.isUnderline;
    }
    if (blockStyle.isStrikethrough != null) {
      isStrikethrough = blockStyle.isStrikethrough;
    }
  }
}
