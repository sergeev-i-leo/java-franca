package franca.java.ui.views.markdown;

import franca.java.office.document.Block;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.ParagraphBlock;
import franca.java.ui.typography.TextView;
import franca.java.ui.views.GroupView;
import franca.java.ui.views.panel.PanelView;

public class MarkdownView extends PanelView {

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
      return;
    }
  }

  public void buildInnerViews(GroupView groupView, Block block) {
    for (int i = 0; i < block.getInnerBlocks().size(); i++) {
      buildInnerView(groupView, block.getInnerBlock(i));
    }
  }
}
