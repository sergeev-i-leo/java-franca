package franca.java;

import javax.swing.*;

public class MainFrame extends JFrame {

  public MainFrame() {
    setTitle("JavaFrancaContentViewer");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 800);
    setLocationRelativeTo(null);

    JTabbedPane tabbedPane = new JTabbedPane();

    tabbedPane.addTab("Document Parser", createParserPanel());

    tabbedPane.addTab("Graphics View", new GraphicsPanel());

    tabbedPane.addTab("Skia View", new SkiaPanel());

    add(tabbedPane);
  }

  private JSplitPane createParserPanel() {
    JSplitPane splitPane = new JSplitPane();
    splitPane.setLeftComponent(new FileSystemListPanel());
    splitPane.setRightComponent(new DocumentTreePanel());
    splitPane.setDividerLocation(300);
    return splitPane;
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      new MainFrame().setVisible(true);
    });
  }
}
