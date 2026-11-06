package franca.java.ui.views;

import franca.java.ui.View;

public class PanelView extends View {

  public static int SCROLLABLE = 1;
  public static int SCROLLABLE_VERTICALLY = 2;
  public static int SCROLLABLE_HORIZONTALLY = 3;

  public int scrollable = 0;
  public Float contentScale = null;

  public int viewportLeft = 0;
  public int viewportTop = 0;
  public int viewportWidth = 0;
  public int viewportHeight = 0;

  public int contentWidth = 0;
  public int contentHeight = 0;
  public int contentXOffset = 0;
  public int contentYOffset = 0;

  public PanelView() {
    super();
  }
}
