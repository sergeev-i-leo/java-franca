package franca.java;

import javax.swing.*;
import java.awt.*;

public class SkiaPanel extends JPanel {

  private DocumentTreePanel rightTree;
  private java.util.List<ElementSelectionListener> listeners = new java.util.ArrayList<>();

  public SkiaPanel() {
    setBackground(Color.WHITE);
  }

  public void setRightTree(DocumentTreePanel tree) {
    this.rightTree = tree;
  }

  public void refresh() {
    repaint();
  }

  public void highlightElement(Object element) {
    // TODO: реализовать подсветку
    repaint();
  }

  public void addElementSelectionListener(ElementSelectionListener listener) {
    listeners.add(listener);
  }

  public void removeElementSelectionListener(ElementSelectionListener listener) {
    listeners.remove(listener);
  }

  private void fireElementSelected(Object element) {
    for (ElementSelectionListener listener : listeners) {
      listener.elementSelected(element);
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2d.setColor(Color.BLACK);
    g2d.setFont(new Font("Arial", Font.BOLD, 24));
    g2d.drawString("Hello World", 50, 100);
  }

  public interface ElementSelectionListener {
    void elementSelected(Object element);
  }
}
