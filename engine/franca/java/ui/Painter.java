package franca.java.ui;

public class Painter {

  public static int PRECISION = 100;

  public int currentOpacity = 255;
  public String currentFontName = null;

  public int calculateTextWidth(String fontName, String text) {
    return 0;
  }

  public int calculateFontHeight(String fontName) {
    return 0;
  }

  public int setOpacity(int opacity) {
    int currentOpacity = this.currentOpacity;
    this.currentOpacity = opacity;
    return currentOpacity;
  }

  public String setFontName(String fontName) {
    String currentFontName = this.currentFontName;
    if (fontName != null) {
      this.currentFontName = fontName;
    }
    return currentFontName;
  }

  public void fillCircle(int x, int y, int radius, int color) {

  }

  public void paintText(String text, int x, int y, String fontName, int color, float scale) {

  }

}
