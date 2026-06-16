package franca.java;

import franca.java.data.json.JsonElement;
import franca.java.data.json.JsonObject;
import franca.java.office.document.Block;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.ParagraphBlock;
import franca.java.office.document.typography.CharsBlock;
import franca.java.office.document.typography.TextBlock;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

public class DocumentTreePanel extends JPanel {

  private JTree tree;

  public DocumentTreePanel() {
    setLayout(new BorderLayout());
    tree = new JTree();
    tree.setCellRenderer(new BlockTreeCellRenderer());
    add(new JScrollPane(tree), BorderLayout.CENTER);
  }

  public void refresh() {
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Blocks");

    for (Block block : Document.instance.getBlocks()) {
      rootNode.add(buildTree(block));
    }

    tree.setModel(new DefaultTreeModel(rootNode));
    expandAll(tree, new TreePath(rootNode));
    repaint();
  }

  private DefaultMutableTreeNode buildTree(Block block) {
    String nodeText;

    if (block instanceof HeadingBlock) {
      HeadingBlock headingBlock = (HeadingBlock) block;
      nodeText = headingBlock.getClassName() + " [" + headingBlock.level + "] = \"" + headingBlock.getText() + "\"";
    } else if (block instanceof ParagraphBlock) {
      ParagraphBlock paragraphBlock = (ParagraphBlock) block;
      nodeText = paragraphBlock.getClassName() + " = \"" + paragraphBlock.getText() + "\"";
    } else if (block instanceof TextBlock) {
      TextBlock textBlock = (TextBlock) block;
      nodeText = textBlock.getClassName() + " = \"" + textBlock.getText() + "\"";
    } else if (block instanceof CharsBlock) {
      CharsBlock charsBlock = (CharsBlock) block;
      nodeText = charsBlock.getClassName() + " [" + charsBlock.type + "] = \"" + charsBlock.getChars() + "\"";
    } else {
      nodeText = block.getClassName();
    }

    DefaultMutableTreeNode blockNode = new DefaultMutableTreeNode(nodeText);

    // classes
    if (block.classesJsonArray.size() > 0) {
      DefaultMutableTreeNode classesNode = new DefaultMutableTreeNode("classes");
      for (int i = 0; i < block.classesJsonArray.size(); i++) {
        classesNode.add(new DefaultMutableTreeNode(block.classesJsonArray.getStringValue(i)));
      }
      blockNode.add(classesNode);
    }

    // attributes
    if (block.attributesJsonArray.size() > 0) {
      DefaultMutableTreeNode attrsNode = new DefaultMutableTreeNode("attributes");
      for (int i = 0; i < block.attributesJsonArray.size(); i++) {
        JsonElement jsonElement = block.attributesJsonArray.get(i);
        JsonObject jsonObject = jsonElement.asJsonObject();
        if (jsonObject != null) {
          String name = jsonObject.getStringValue("name");
          if (name == null) {
            attrsNode.add(new DefaultMutableTreeNode("ATTRIBUTE ERROR"));
          } else {
            String value = jsonObject.getStringValue("value");
            if (value != null) {
              attrsNode.add(new DefaultMutableTreeNode(name + " = " + value));
            } else {
              value = jsonObject.getStringValue("string-value");
              if (value != null) {
                attrsNode.add(new DefaultMutableTreeNode(name + " = \"" + value + "\""));
              } else {
                attrsNode.add(new DefaultMutableTreeNode(name + " = ATTRIBUTE ERROR"));
              }
            }
          }
        } else if (jsonElement.getStringValue() != null) {
          attrsNode.add(new DefaultMutableTreeNode(jsonElement.getStringValue()));
        } else {
          attrsNode.add(new DefaultMutableTreeNode("ATTRIBUTE ERROR"));
        }
      }
      blockNode.add(attrsNode);
    }

    // style
    if (block.blockStyle.color != null) {
      DefaultMutableTreeNode styleNode = new DefaultMutableTreeNode("style");
      styleNode.add(new DefaultMutableTreeNode("color : " + block.blockStyle.color));
      blockNode.add(styleNode);
    }
    if (block.blockStyle.backgroundColor != null) {
      DefaultMutableTreeNode styleNode = new DefaultMutableTreeNode("style");
      styleNode.add(new DefaultMutableTreeNode("backgroundColor : " + block.blockStyle.backgroundColor));
      blockNode.add(styleNode);
    }
    if (block.blockStyle.textAlign != null) {
      DefaultMutableTreeNode styleNode = new DefaultMutableTreeNode("style");
      styleNode.add(new DefaultMutableTreeNode("textAlign : " + block.blockStyle.textAlign));
      blockNode.add(styleNode);
    }
    if (block.blockStyle.fontWeight != null) {
      DefaultMutableTreeNode styleNode = new DefaultMutableTreeNode("style");
      styleNode.add(new DefaultMutableTreeNode("fontWeight : " + block.blockStyle.fontWeight));
      blockNode.add(styleNode);
    }
    if (block.blockStyle.isItalic != null) {
      DefaultMutableTreeNode styleNode = new DefaultMutableTreeNode("style");
      styleNode.add(new DefaultMutableTreeNode("isItalic : " + block.blockStyle.isItalic));
      blockNode.add(styleNode);
    }
    if (block.blockStyle.isUnderline != null) {
      DefaultMutableTreeNode styleNode = new DefaultMutableTreeNode("style");
      styleNode.add(new DefaultMutableTreeNode("isUnderline : " + block.blockStyle.isUnderline));
      blockNode.add(styleNode);
    }
    if (block.blockStyle.isStrikethrough != null) {
      DefaultMutableTreeNode styleNode = new DefaultMutableTreeNode("style");
      styleNode.add(new DefaultMutableTreeNode("isStrikethrough : " + block.blockStyle.isStrikethrough));
      blockNode.add(styleNode);
    }

    // blocks (children)
    ArrayList<Block> children = block.getBlocks();
    if (children != null && !children.isEmpty()) {
      DefaultMutableTreeNode childrenNode = new DefaultMutableTreeNode("blocks");
      for (Block child : children) {
        childrenNode.add(buildTree(child));
      }
      blockNode.add(childrenNode);
    }

    return blockNode;
  }

  public void addTreeSelectionListener(javax.swing.event.TreeSelectionListener listener) {
    tree.addTreeSelectionListener(listener);
  }

  public void removeTreeSelectionListener(javax.swing.event.TreeSelectionListener listener) {
    tree.removeTreeSelectionListener(listener);
  }

  public Object getSelectedValue() {
    TreePath path = tree.getSelectionPath();
    if (path != null) {
      DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
      return selectedNode;
    }
    return null;
  }

  public void setSelectedValue(DefaultMutableTreeNode node) {
    if (node != null) {
      expandAllParents(node);
      TreePath path = new TreePath(node.getPath());
      tree.setSelectionPath(path);
      tree.scrollPathToVisible(path);
    }
  }

  private void expandAllParents(DefaultMutableTreeNode node) {
    TreeNode[] path = node.getPath();
    for (int i = 1; i < path.length; i++) {
      tree.expandPath(new TreePath(Arrays.copyOf(path, i + 1)));
    }
  }

  public void expandAll() {
    TreeNode root = (TreeNode) tree.getModel().getRoot();
    expandAll(tree, new TreePath(root));
  }

  private void expandAll(JTree tree, TreePath parent) {
    TreeNode node = (TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
        TreeNode n = (TreeNode) e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        expandAll(tree, path);
      }
    }
    tree.expandPath(parent);
  }

  class BlockTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

      String displayText = value.toString();
      super.getTreeCellRendererComponent(tree, displayText, sel, expanded, leaf, row, hasFocus);

      if (sel) {
        setBackground(new Color(51, 153, 255));
        setForeground(Color.WHITE);
        setOpaque(true);
      } else {
        setBackground(null);
        setOpaque(false);
      }
      return this;
    }
  }
}
