package franca.java.office.document;

import franca.java.expected.TranspilableClass;

public class BlockStyle extends TranspilableClass {

  public String color = null;
  public String backgroundColor = null;

  public String textAlign = null;

  public String deviceFontName = null;
  public String fontWeight = null;
  public Boolean isItalic = null;
  public Boolean isUnderline = null;
  public Boolean isStrikethrough = null;

  public BlockStyle clone() {
    BlockStyle result = new BlockStyle();
    result.color = color;
    result.backgroundColor = backgroundColor;
    result.textAlign = textAlign;
    result.deviceFontName = deviceFontName;
    result.fontWeight = fontWeight;
    result.isItalic = isItalic;
    result.isUnderline = isUnderline;
    result.isStrikethrough = isStrikethrough;

    return result;
  }

  public BlockStyle mergeWith(BlockStyle overlayBlockStyle) {
    BlockStyle result = new BlockStyle();

    if ((overlayBlockStyle != null) && (overlayBlockStyle.color != null)) {
      result.color = overlayBlockStyle.color;
    } else {
      result.color = color;
    }

    if ((overlayBlockStyle != null) && (overlayBlockStyle.backgroundColor != null)) {
      result.backgroundColor = overlayBlockStyle.backgroundColor;
    } else {
      result.backgroundColor = backgroundColor;
    }

    if ((overlayBlockStyle != null) && (overlayBlockStyle.deviceFontName != null)) {
      result.deviceFontName = overlayBlockStyle.deviceFontName;
    } else {
      result.deviceFontName = deviceFontName;
    }

    if ((overlayBlockStyle != null) && (overlayBlockStyle.fontWeight != null)) {
      result.fontWeight = overlayBlockStyle.fontWeight;
    } else {
      result.fontWeight = fontWeight;
    }

    if ((overlayBlockStyle != null) && (overlayBlockStyle.isItalic != null)) {
      result.isItalic = overlayBlockStyle.isItalic;
    } else {
      result.isItalic = isItalic;
    }

    if ((overlayBlockStyle != null) && (overlayBlockStyle.isUnderline != null)) {
      result.isUnderline = overlayBlockStyle.isUnderline;
    } else {
      result.isUnderline = isUnderline;
    }

    if ((overlayBlockStyle != null) && (overlayBlockStyle.isStrikethrough != null)) {
      result.isStrikethrough = overlayBlockStyle.isStrikethrough;
    } else {
      result.isStrikethrough = isStrikethrough;
    }

    return result;
  }
}
