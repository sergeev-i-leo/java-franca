package franca.java.ui.views;

public class PanelViewport {

  public static int SCROLLABLE_VERTICALLY = 1;
  public static int SCROLLABLE_HORIZONTALLY = 2;

  public PanelView panelView;
  public int scrollable;

  public int leftMargin = 0;
  public int topMargin = 0;
  public int rightMargin = 0;
  public int bottomMargin = 0;

  public int viewsX = 0;
  public int viewsY = 0;

  public PanelViewport(PanelView panelView, int scrollable) {
    this.panelView = panelView;
    this.scrollable = scrollable;
  }

}

