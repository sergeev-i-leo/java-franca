package franca.java.ui.views;

import java.util.ArrayList;

import franca.java.data.blocks.Block;
import franca.java.data.blocks.table.*;
import franca.java.data.blocks.typography.HeadingBlock;
import franca.java.data.blocks.typography.ParagraphBlock;
import franca.java.data.blocks.typography.TextBlock;
import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.table.TableCellView;
import franca.java.ui.views.table.TableView;
import franca.java.ui.views.typography.TextView;

public class GroupView extends View {

  public ArrayList<View> innerViews = new ArrayList<>();

  public int paddingLeft = 0;
  public int paddingTop = 0;
  public int paddingRight = 0;
  public int paddingBottom = 0;

  public int maxInnerLayer = 0;

  public boolean isReady() {
    int state = getReadiness();
    if (state != 1) {
      return false;
    }
    for (int i = 0; i < innerViews.size(); i++) {
      if (!innerViews.get(i).isReady()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void prepare(Device device, Painter painter, int availableWidth, int availableHeight, int zoomPercentage) {

    width = 0;
    height = 0;
    maxInnerLayer = 0;

    int viewTop = paddingTop;
    availableWidth -= paddingLeft + paddingRight;
    availableHeight -= paddingTop + paddingBottom;

    for (int i = 0; i < innerViews.size(); i++) {
      View view = innerViews.get(i);

      maxInnerLayer = Math.max(view.layer, maxInnerLayer);

      if (view.layer == 0) {
        view.left = paddingLeft;
        view.top = viewTop;
      }

      view.getReady(device, painter, availableWidth, availableHeight, zoomPercentage);

      if (view.layer > 0) {
        continue;
      }

      width = Math.max(view.left + view.width + paddingRight, width);
      height = Math.max(view.top + view.height + paddingBottom, height);

      viewTop += view.height;
    }
  }

  @Override
  public void paint(Device device, Painter painter, int painterX, int painterY, int zoomPercentage) {
    for (int i0 = 0; i0 <= maxInnerLayer; i0++) {
      for (int i1 = 0; i1 < innerViews.size(); i1++) {
        View view = innerViews.get(i1);
        if (view.layer == i0) {
          view.paint(device, painter, painterX + left + paddingLeft, painterY + top + paddingTop, zoomPercentage);
        }
      }
    }
  }
}
