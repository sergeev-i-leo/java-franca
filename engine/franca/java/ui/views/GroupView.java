package franca.java.ui.views;

import java.util.ArrayList;

import franca.java.office.document.Block;
import franca.java.office.document.table.TableBlock;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.ParagraphBlock;
import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.table.TableView;
import franca.java.ui.views.typography.TextView;

public class GroupView extends View {

  public ArrayList<View> innerViews = new ArrayList<>();

  public int paddingLeft = 0;
  public int paddingTop = 0;
  public int paddingRight = 0;
  public int paddingBottom = 0;

  public Float innerViewsScale = null;

  public int zDeepness = 0;

  public void build(Block block) {
    buildInnerView(this, block);
    setReadiness(-1);
  }

  public void buildInnerView(GroupView groupView, Block block) {
    if (block instanceof HeadingBlock) {
      TextView textView = new TextView();
      groupView.innerViews.add(textView);
      textView.textBlock = (HeadingBlock) block;
      return;
    }
    if (block instanceof ParagraphBlock) {
      TextView textView = new TextView();
      groupView.innerViews.add(textView);
      textView.textBlock = (ParagraphBlock) block;
      return;
    }
    if (block instanceof TableBlock) {
      buildTableView(groupView, (TableBlock) block);
      return;
    }
    if (block.getInnerBlocks().size() > 0) {
      buildInnerViews(groupView, block);
    }
  }

  public void buildTableView(GroupView groupView, TableBlock tableBlock) {

    TableView tableView = new TableView();
    groupView.innerViews.add(tableView);

  }

  public void buildInnerViews(GroupView groupView, Block block) {
    for (int i = 0; i < block.getInnerBlocks().size(); i++) {
      buildInnerView(groupView, block.getInnerBlock(i));
    }
  }

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
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, Float scale) {
    if (isReady()) {
      return;
    }
    if (getReadiness() == 0) {
      return;
    }
    setReadiness(0);

    if (innerViewsScale != null) {
      scale *= innerViewsScale;
    }

    width = 0;
    height = 0;
    zDeepness = 0;

    int innerViewTop = paddingTop;

    for (int i = 0; i < innerViews.size(); i++) {
      View innerView = innerViews.get(i);

      zDeepness = Math.max(innerView.zIndex, zDeepness);

      if (innerView.zIndex == 0) {
        innerView.left = paddingLeft;
        innerView.top = innerViewTop;
      }

      innerView.getReady(device, painter, availableWidth, availableHeight, scale);

      if (innerView.zIndex > 0) {
        continue;
      }

      width = Math.max(innerView.left + innerView.width + paddingRight, width);
      height = Math.max(innerView.top + innerView.height + paddingBottom, height);

      innerViewTop += innerView.height;
    }

    setReadiness(1);
  }

  @Override
  public void paint(Device device, Painter painter, int painterX, int painterY, Float scale) {
    for (int i = 0; i < innerViews.size(); i++) {
      View innerView = innerViews.get(i);
      if (innerView.zIndex == 0) {
        innerViews.get(i).paint(device, painter, painterX, painterY, scale);
      }
    }
    for (int i = 1; i < zDeepness; i++) {
      View innerView = innerViews.get(i);
      if (innerView.zIndex == i) {
        innerViews.get(i).paint(device, painter, painterX, painterY, scale);
      }
    }
  }
}
