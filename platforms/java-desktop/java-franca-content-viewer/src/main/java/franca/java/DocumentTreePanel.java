package franca.java;

import franca.java.data.json.JsonArray;
import franca.java.office.document.Block;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
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
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Document");

    for (Block block : DocumentModel.blocks) {
      rootNode.add(buildTree(block));
    }

    tree.setModel(new DefaultTreeModel(rootNode));
    expandAll(tree, new TreePath(rootNode));
    repaint();
  }

  private DefaultMutableTreeNode buildTree(Block block) {
    String nodeText = block.getClassName();

    if (block.classes.size() > 0) {
      nodeText += " [classes: " + joinArray(block.classes) + "]";
    }
    if (block.attributes.size() > 0) {
      nodeText += " [attrs: " + joinArray(block.attributes) + "]";
    }
    if (block.style.size() > 0) {
      nodeText += " [style: " + joinArray(block.style) + "]";
    }

    DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeText);

    for (Block child : block.blocks) {
      node.add(buildTree(child));
    }

    return node;
  }

  private String joinArray(JsonArray array) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < array.size(); i++) {
      if (i > 0) sb.append(", ");
      sb.append(array.getStringValue(i));
    }
    return sb.toString();
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
