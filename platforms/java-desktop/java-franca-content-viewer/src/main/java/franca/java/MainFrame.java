package franca.java;

import franca.java.data.json.JsonObject;
import franca.java.data.markdown.FlavouredMarkdownParser;
import franca.java.expected.BufferedString;
import franca.java.data.html.HtmlParser;
import franca.java.office.document.Block;
import franca.java.office.document.factory.DocumentFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainFrame extends JFrame {

  private static final int WEB_SERVER_PORT = 8080;

  private WebServer webServer;
  private DocumentTreePanel documentTreePanel;
  private DocumentTreePanel skiaTreePanel;
  private SkiaPanel skiaPanel;

  public MainFrame() {
    startWebServer();
    initUI();
    initTestData();
  }

  private void startWebServer() {
    new Thread(() -> {
      try {
        webServer = new WebServer();
        webServer.start(WEB_SERVER_PORT);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }

  private void initUI() {
    setTitle("JavaFranca Content Viewer");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 800);
    setLocationRelativeTo(null);
    setJMenuBar(createMenuBar());

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Document Parser", createParserPanel());
    tabbedPane.addTab("Graphics View", new GraphicsPanel());
    tabbedPane.addTab("Skia View", createSkiaPanel());

    add(tabbedPane);
  }

  private void initTestData() {
    Block root = new Block();
    root.classes.addStringValue("container");
    root.classes.addStringValue("main-container");

    Block inner = new Block();
    inner.classes.addStringValue("row");
    inner.attributes.addStringValue("data-name=inner-row");

    root.addChild(inner);
    Document.instance.clearBlocks();
    Document.instance.addChild(root);

    if (documentTreePanel != null) {
      documentTreePanel.refresh();
    }
    if (skiaTreePanel != null) {
      skiaTreePanel.refresh();
    }
  }

  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");
    JMenuItem openItem = new JMenuItem("Open");
    JMenuItem saveItem = new JMenuItem("Save");
    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener(e -> System.exit(0));

    fileMenu.add(openItem);
    fileMenu.add(saveItem);
    fileMenu.addSeparator();
    fileMenu.add(exitItem);

    JMenu editMenu = new JMenu("Edit");
    editMenu.add(new JMenuItem("Copy"));
    editMenu.add(new JMenuItem("Paste"));

    menuBar.add(fileMenu);
    menuBar.add(editMenu);

    return menuBar;
  }

  private JSplitPane createParserPanel() {
    FileSystemListPanel fileSystemPanel = new FileSystemListPanel();
    JsonTextPanel jsonTextPanel = new JsonTextPanel();
    documentTreePanel = new DocumentTreePanel();

    fileSystemPanel.setOnFileSelected(() -> {
      File selectedFile = fileSystemPanel.getSelectedFile();
      if (selectedFile != null && selectedFile.getName().endsWith(".html")) {
        try {
          String content = new String(Files.readAllBytes(selectedFile.toPath()), StandardCharsets.UTF_8);
          HtmlParser parser = new HtmlParser();
          Document.instance = parser.parse(content);
          BufferedString targetBufferedString = new BufferedString();
          DocumentFactory.serialize(Document.instance, targetBufferedString);
          jsonTextPanel.setJsonText(targetBufferedString.getString());
          documentTreePanel.refresh();

          // TODO: конвертация raw → Block
          // DocumentModel.blocks.clear();
          // DocumentModel.blocks.add(convertedBlock);
          // skiaTreePanel.refresh();
          // skiaPanel.refresh();

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (selectedFile != null && selectedFile.getName().endsWith(".md")) {
        try {
          String content = new String(Files.readAllBytes(selectedFile.toPath()), StandardCharsets.UTF_8);
          FlavouredMarkdownParser parser = new FlavouredMarkdownParser();
          Document.instance = parser.parse(content);
          BufferedString targetBufferedString = new BufferedString();
          DocumentFactory.serialize(Document.instance, targetBufferedString);
          jsonTextPanel.setJsonText(targetBufferedString.getString());
          jsonTextPanel.setJsonText(targetBufferedString.getString());
          documentTreePanel.refresh();

          Path targetPath = Paths.get(System.getProperty("user.dir"), parser.exportFolder).normalize();
          String absolutePath = targetPath.toAbsolutePath().toString();
          System.out.println(absolutePath);

          JsonObject json = Document.instance.createJsonObject();
          BufferedString bufferedString = new BufferedString();
          json.serialize(bufferedString, 0);
          Files.writeString(Paths.get(absolutePath), bufferedString.getString(), StandardCharsets.UTF_8);

          // TODO: конвертация raw → Block
          // DocumentModel.blocks.clear();
          // DocumentModel.blocks.add(convertedBlock);
          // skiaTreePanel.refresh();
          // skiaPanel.refresh();

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    leftSplit.setLeftComponent(fileSystemPanel);

    JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    rightSplit.setLeftComponent(jsonTextPanel);
    rightSplit.setRightComponent(documentTreePanel);
    rightSplit.setDividerLocation(400);

    leftSplit.setRightComponent(rightSplit);
    leftSplit.setDividerLocation(250);

    return leftSplit;
  }

  private JSplitPane createSkiaPanel() {
    JSplitPane splitPane = new JSplitPane();

    skiaPanel = new SkiaPanel();
    splitPane.setLeftComponent(skiaPanel);

    skiaTreePanel = new DocumentTreePanel();
    skiaPanel.setRightTree(skiaTreePanel);
    splitPane.setRightComponent(skiaTreePanel);
    splitPane.setDividerLocation(600);

    skiaTreePanel.addTreeSelectionListener(e -> {
      Object selected = skiaTreePanel.getSelectedValue();
      if (selected != null) {
        skiaPanel.highlightElement(selected);
        if (documentTreePanel != null) {
          documentTreePanel.setSelectedValue((DefaultMutableTreeNode) selected);
        }
      }
    });

    skiaPanel.addElementSelectionListener(selected -> {
      if (selected != null) {
        skiaTreePanel.setSelectedValue((DefaultMutableTreeNode) selected);
        if (documentTreePanel != null) {
          documentTreePanel.setSelectedValue((DefaultMutableTreeNode) selected);
        }
      }
    });

    return splitPane;
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
  }
}
