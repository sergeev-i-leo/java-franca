package franca.java.ui;

public class Painter {

  public static int PRECISION = 100;

  public int opacity = 255;

  public int calculateTextWidth(String fontName, String text) {
    return 0;
  }

  public int calculateFontHeight(String fontName) {
    return 0;
  }

  public int setOpacity(int opacity) {
    int currentOpacity = this.opacity;
    this.opacity = opacity;
    return currentOpacity;
  }

  public void fillCircle(int x, int y, int radius, int color) {

  }

  public void paintText(String text, int x, int y, String fontName, int color, float scale) {

  }

}
