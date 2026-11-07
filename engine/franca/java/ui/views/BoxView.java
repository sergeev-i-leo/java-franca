package franca.java.ui.views;

import franca.java.ui.View;

public class BoxView extends View {

  public int marginLeft = 0;
  public int marginTop = 0;
  public int marginRight = 0;
  public int marginBottom = 0;

  public int paddingLeft = 0;
  public int paddingTop = 0;
  public int paddingRight = 0;
  public int paddingBottom = 0;

  public int viewsWidth = 0;
  public int viewsHeight = 0;

  public void getReady(int availableWidth, int availableHeight, Float contentScale) {
    left = 0;
    top = 0;
    width = availableWidth;
    height = availableHeight;

    int viewsLeft = marginLeft + paddingLeft;
    int viewsTop = marginTop + paddingTop;
    viewsWidth = 0;
    viewsHeight = 0;

    for (int i = 0; i < views.size(); i++) {
      View view = views.get(i);
      view.getReady(availableWidth, availableHeight, contentScale);

      if (view.positionedAbsolutely) {
        continue;
      }

      view.left = viewsLeft;
      view.top = viewsTop;

      width = Math.max(view.left + view.width + paddingRight + marginRight, width);
      height = Math.max(view.top + view.height + paddingBottom + marginBottom, height);

      viewsWidth = Math.max(view.left + view.width, viewsWidth);
      viewsHeight = Math.max(view.top + view.height, viewsHeight);

      viewsTop += view.height;
    }

    viewsWidth -= marginLeft + paddingLeft;
    viewsHeight -= marginTop + paddingTop;
  }

}
