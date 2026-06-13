package franca.java;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.*;

public class DocumentModel {

  private static DocumentModel instance;
  private DefaultMutableTreeNode documentRoot;
  private Map<Object, Rectangle> elementBounds; // для подсветки

  private DocumentModel() {
    documentRoot = createSampleDocument();
    elementBounds = new HashMap<>();
  }

  public static DocumentModel getInstance() {
    if (instance == null) {
      instance = new DocumentModel();
    }
    return instance;
  }

  private DefaultMutableTreeNode createSampleDocument() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Document");
    DefaultMutableTreeNode paragraph = new DefaultMutableTreeNode("Paragraph");

    DefaultMutableTreeNode word1 = new DefaultMutableTreeNode("Word: Hello");
    word1.add(new DefaultMutableTreeNode("attr: bold"));
    paragraph.add(word1);

    paragraph.add(new DefaultMutableTreeNode("Space"));

    DefaultMutableTreeNode word2 = new DefaultMutableTreeNode("Word: World");
    word2.add(new DefaultMutableTreeNode("attr: italic"));
    paragraph.add(word2);

    root.add(paragraph);
    return root;
  }

  public DefaultMutableTreeNode getDocumentRoot() {
    return documentRoot;
  }

  public void setElementBounds(Object element, Rectangle bounds) {
    elementBounds.put(element, bounds);
  }

  public Rectangle getElementBounds(Object element) {
    return elementBounds.get(element);
  }
}
