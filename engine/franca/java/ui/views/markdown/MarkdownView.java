package franca.java.ui.views.markdown;

import franca.java.data.blocks.Block;
import franca.java.data.blocks.table.*;
import franca.java.data.blocks.typography.HeadingBlock;
import franca.java.data.blocks.typography.ParagraphBlock;
import franca.java.data.blocks.typography.TextBlock;
import franca.java.ui.views.GroupView;
import franca.java.ui.views.panels.ZoomablePanelView;
import franca.java.ui.views.table.TableCellView;
import franca.java.ui.views.table.TableView;
import franca.java.ui.views.typography.TextView;

public class MarkdownView extends ZoomablePanelView {

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
}
