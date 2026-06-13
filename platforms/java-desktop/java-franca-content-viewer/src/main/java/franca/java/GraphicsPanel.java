package franca.java;

import javax.swing.*;
import java.awt.*;

public class GraphicsPanel extends JPanel {

  private JTextArea textArea;

  public GraphicsPanel() {
    setLayout(new BorderLayout());

    textArea = new JTextArea();
    textArea.setText("Hello World\nЭто текстовое представление документа\n(пока заглушка)");
    textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));
    textArea.setEditable(false);

    add(new JScrollPane(textArea), BorderLayout.CENTER);
  }
}
