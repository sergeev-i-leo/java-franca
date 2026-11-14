package franca.java.ui.views;

import java.util.ArrayList;

import franca.java.office.document.Block;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.ParagraphBlock;
import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.typography.TextView;

public class GroupView extends View {

  public ArrayList<View> innerViews = new ArrayList<>();

  public Float innerViewsScale = null;

  public void build(Block block) {
    buildInnerView(this, block);
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
    if (block.getInnerBlocks().size() > 0) {
      buildInnerViews(groupView, block);
    }
  }

  public void buildInnerViews(GroupView groupView, Block block) {
    for (int i = 0; i < block.getInnerBlocks().size(); i++) {
      buildInnerView(groupView, block.getInnerBlock(i));
    }
  }

  public boolean isReady() {
    int state = getState();
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
    if (getState() == 0) {
      return;
    }
    setState(0);

    if (innerViewsScale != null) {
      scale *= innerViewsScale;
    }

    width = 0;
    height = 0;

    int viewsTop = 0;

    for (int i = 0; i < innerViews.size(); i++) {
      View view = innerViews.get(i);

      if (view.zIndex == 0) {
        view.left = 0;
        view.top = viewsTop;
      }

      view.getReady(device, painter, availableWidth, availableHeight, scale);

      if (view.zIndex > 0) {
        continue;
      }

      width = Math.max(view.left + view.width, width);
      height = Math.max(view.top + view.height, height);

      viewsTop += view.height;
    }

    setState(1);
  }

  @Override
  public void paint(Device device, Painter painter, int painterX, int painterY, Float scale) {
    for (int i = 0; i < innerViews.size(); i++) {
      innerViews.get(i).paint(device, painter, painterX, painterY, scale);
    }
  }
}
