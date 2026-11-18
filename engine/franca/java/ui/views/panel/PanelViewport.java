package franca.java.ui.views.panel;

public class PanelViewport {

  public static int SCROLLABLE_VERTICALLY = 1;
  public static int SCROLLABLE_HORIZONTALLY = 2;

  public PanelView panelView;
  public int scrollable;

  public int paddingLeft = 0;
  public int paddingTop = 0;
  public int paddingRight = 0;
  public int paddingBottom = 0;

  public int innerViewsX = 0;
  public int innerViewsY = 0;

  public PanelViewport(PanelView panelView, int scrollable) {
    this.panelView = panelView;
    this.scrollable = scrollable;
  }

}

