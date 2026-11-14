package franca.java.ui.views;

import java.util.ArrayList;

import franca.java.office.document.Block;
import franca.java.office.document.table.*;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.ParagraphBlock;
import franca.java.office.document.typography.TextBlock;
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

  public Float innerViewsScale = null;

  public int maxInnerLayer = 0;

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
    if (block instanceof TextBlock) {
      TextView textView = new TextView();
      groupView.innerViews.add(textView);
      textView.textBlock = (TextBlock) block;
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

    for (int i0 = 0; i0 < tableBlock.getInnerBlocks().size(); i0++) {
      Block block0 = tableBlock.getInnerBlock(i0);
      if ((block0 instanceof TableHeaderBlock) || (block0 instanceof TableBodyBlock)) {
        for (int i1 = 0; i1 < block0.getInnerBlocks().size(); i1++) {
          Block block1 = block0.getInnerBlock(i1);
          if (block1 instanceof TableRowBlock) {
            for (int i2 = 0; i2 < block1.getInnerBlocks().size(); i2++) {
              Block block2 = block1.getInnerBlock(i2);
              if (block2 instanceof TableCellBlock) {
                TableCellView tableCellView = new TableCellView();
                tableView.innerViews.add(tableCellView);
                buildInnerView(tableCellView, block2);
              }
            }
          }
        }
      }
    }
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
    maxInnerLayer = 0;

    int viewTop = paddingTop;

    for (int i = 0; i < innerViews.size(); i++) {
      View view = innerViews.get(i);

      maxInnerLayer = Math.max(view.layer, maxInnerLayer);

      if (view.layer == 0) {
        view.left = paddingLeft;
        view.top = viewTop;
      }

      view.getReady(device, painter, availableWidth, availableHeight, scale);

      if (view.layer > 0) {
        continue;
      }

      width = Math.max(view.left + view.width + paddingRight, width);
      height = Math.max(view.top + view.height + paddingBottom, height);

      viewTop += view.height;
    }

    setReadiness(1);
  }

  @Override
  public void paint(Device device, Painter painter, int painterX, int painterY, Float scale) {
    for (int i0 = 0; i0 <= maxInnerLayer; i0++) {
      for (int i1 = 0; i1 < innerViews.size(); i1++) {
        View view = innerViews.get(i1);
        if (view.layer == i0) {
          view.paint(device, painter, painterX, painterY, scale);
        }
      }
    }
  }
}
