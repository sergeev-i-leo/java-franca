package franca.java.ui.views;

public class PanelView extends BoxView {

  public Float viewsScale = null;

  public PanelViewport panelViewport = null;

  public PanelView() {
    super();
  }

  public void getReady(int availableWidth, int availableHeight, Float scale) {
    if (viewsScale != null) {
      scale *= viewsScale;
    }
    super.getReady(availableWidth, availableHeight, scale);
    width = availableWidth;
    if (panelViewport != null) {
      height = availableHeight;
    }
  }
}

