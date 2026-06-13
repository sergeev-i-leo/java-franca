package franca.java;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class DocumentTreePanel extends JPanel {

  private JTree documentTree;

  public DocumentTreePanel() {
    setLayout(new BorderLayout());

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Document");
    DefaultTreeModel treeModel = new DefaultTreeModel(root);

    DefaultMutableTreeNode paragraph = new DefaultMutableTreeNode("Paragraph");

    DefaultMutableTreeNode word1 = new DefaultMutableTreeNode("Word: Hello");
    word1.add(new DefaultMutableTreeNode("attr: bold"));
    paragraph.add(word1);

    paragraph.add(new DefaultMutableTreeNode("Space"));

    DefaultMutableTreeNode word2 = new DefaultMutableTreeNode("Word: World");
    word2.add(new DefaultMutableTreeNode("attr: italic"));
    paragraph.add(word2);

    root.add(paragraph);

    documentTree = new JTree(treeModel);
    add(new JScrollPane(documentTree), BorderLayout.CENTER);
  }
}
