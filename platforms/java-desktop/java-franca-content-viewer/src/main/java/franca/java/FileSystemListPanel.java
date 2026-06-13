package franca.java;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class FileSystemListPanel extends JPanel {

  private DefaultListModel<String> listModel;
  private JList<String> fileList;
  private File currentDirectory;
  private Runnable onFileSelected;

  public FileSystemListPanel() {
    setLayout(new BorderLayout());

    // Start from user's home directory
    currentDirectory = new File(System.getProperty("user.dir"));

    // List model and JList
    listModel = new DefaultListModel<>();
    fileList = new JList<>(listModel);
    fileList.setCellRenderer(new FileListCellRenderer());

    fileList.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() == 2) {
          int index = fileList.locationToIndex(e.getPoint());
          if (index != -1) {
            String item = listModel.getElementAt(index);

            if (item.equals("..")) {
              // Go to parent directory
              File parent = currentDirectory.getParentFile();
              if (parent != null) {
                currentDirectory = parent;
                refreshList();
              }
            } else {
              File file = new File(item);
              if (file.isDirectory()) {
                currentDirectory = file;
                refreshList();
              } else if (file.isFile() && file.getName().endsWith(".html")) {
                if (onFileSelected != null) {
                  onFileSelected.run();
                }
              } else if (file.isFile() && file.getName().endsWith(".md")) {
                if (onFileSelected != null) {
                  onFileSelected.run();
                }
              }
            }
          }
        }
      }
    });

    JScrollPane scrollPane = new JScrollPane(fileList);
    add(scrollPane, BorderLayout.CENTER);

    refreshList();
  }

  public File getSelectedFile() {
    int index = fileList.getSelectedIndex();
    if (index > 0) { // skip ".." at index 0
      String path = listModel.getElementAt(index);
      return new File(path);
    }
    return null;
  }

  public void setOnFileSelected(Runnable callback) {
    this.onFileSelected = callback;
  }

  // Custom cell renderer
  private static class FileListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      JLabel label = (JLabel) super.getListCellRendererComponent(
        list, value, index, isSelected, cellHasFocus
      );

      if (value instanceof String) {
        String item = (String) value;

        if (item.equals("..")) {
          label.setText("..");
          label.setForeground(Color.BLUE);
          return label;
        }

        File file = new File(item);
        label.setText(file.getName());

        if (file.isDirectory()) {
          label.setForeground(new Color(204, 153, 0));
          label.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        } else if (file.getName().endsWith(".html")) {
          label.setForeground(new Color(0, 127, 255));
        } else if (file.getName().endsWith(".md")) {
          label.setForeground(new Color(0, 191, 0));
        }
      }

      return label;
    }
  }
  public File getCurrentDirectory() {
    return currentDirectory;
  }

  public void refreshList() {
    // Existing refresh logic
    listModel.clear();
    listModel.addElement("..");

    File[] files = currentDirectory.listFiles();
    if (files != null) {
      Arrays.sort(files, (f1, f2) -> {
        if (f1.isDirectory() && !f2.isDirectory()) return -1;
        if (!f1.isDirectory() && f2.isDirectory()) return 1;
        return f1.getName().compareToIgnoreCase(f2.getName());
      });

      for (File file : files) {
        if (file.isDirectory() || file.getName().endsWith(".md") || file.getName().endsWith(".json")) {
          listModel.addElement(file.getAbsolutePath());
        }
      }
    }

    fileList.setModel(listModel);
  }
}
