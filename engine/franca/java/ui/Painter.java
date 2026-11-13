package franca.java.ui;

public class Painter {

  public static final int PRECISION = 100;

  public static final int PRIMARY_BACKGROUND_COLOR = 1;
  public static final int SECONDARY_BACKGROUND_COLOR = 2;
  public static final int TERTIARY_BACKGROUND_COLOR = 3;

  public static final int PRIMARY_TEXT_COLOR = 4;
  public static final int SECONDARY_TEXT_COLOR = 5;
  public static final int TERTIARY_TEXT_COLOR = 6;

  public int currentOpacity = 255;

  public TextStyle currentTextStyle = null;

  public int getWidth() {
    return 0;
  }

  public int getHeight() {
    return 0;
  }

  public int calculateTextWidth(String text, String fontName, String fontWeight, String fontStyle) {
    return 0;
  }

  public int calculateFontHeight(String fontName, String fontWeight, String fontStyle) {
    return 0;
  }

  public int setCurrentOpacity(int opacity) {
    int currentOpacity = this.currentOpacity;
    this.currentOpacity = opacity;
    return currentOpacity;
  }

  public void setCurrentTextStyle(TextStyle textStyle) {
    this.currentTextStyle = textStyle;
  }

  public void fillCircle(int x, int y, int radius, int color) {

  }

  public void paintText(String text, int x, int y, String fontName, String fontWeight, String fontStyle, int color, float scale) {

  }

}
