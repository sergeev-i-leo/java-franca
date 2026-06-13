package franca.java;

import franca.java.parsers.json.JsonArray;
import franca.java.parsers.json.JsonElement;
import franca.java.parsers.json.JsonObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;

public class JsonTreePanel extends JPanel {

  private JTree tree;
  private JsonElement rootElement;

  public JsonTreePanel() {
    setLayout(new BorderLayout());
    tree = new JTree();
    tree.setCellRenderer(new JsonTreeCellRenderer());
    add(new JScrollPane(tree), BorderLayout.CENTER);
  }

  public void refresh(JsonElement root) {
    this.rootElement = root;
    DefaultMutableTreeNode rootNode = buildTree(root);
    tree.setModel(new DefaultTreeModel(rootNode));
    expandAll(tree, new TreePath(rootNode));
    repaint();
  }

  private DefaultMutableTreeNode buildTree(JsonElement element) {
    if (element == null) {
      return new DefaultMutableTreeNode("null");
    }

    // JsonArray
    JsonArray array = element.asJsonArray();
    if (array != null) {
      DefaultMutableTreeNode arrayNode = new DefaultMutableTreeNode("Array[" + array.size() + "]");
      for (int i = 0; i < array.size(); i++) {
        DefaultMutableTreeNode childNode = buildTree(array.get(i));
        arrayNode.add(childNode);
      }
      return arrayNode;
    }

    // JsonObject
    JsonObject obj = element.asJsonObject();
    if (obj != null) {
      DefaultMutableTreeNode objNode = new DefaultMutableTreeNode("Object");
      ArrayList<String> keys = obj.keys();

      for (String key : keys) {
        JsonElement value = obj.get(key);
        String displayText = key + ": " + getShortValue(value);
        DefaultMutableTreeNode keyNode = new DefaultMutableTreeNode(displayText);

        // Если значение сложное (объект или массив) — раскрываем рекурсивно
        if (value != null && (value.asJsonObject() != null || value.asJsonArray() != null)) {
          DefaultMutableTreeNode childNode = buildTree(value);
          keyNode.add(childNode);
        }

        objNode.add(keyNode);
      }
      return objNode;
    }

    // Примитивы
    return buildPrimitiveNode(element);
  }

  private String getShortValue(JsonElement element) {
    if (element == null) return "null";

    JsonArray arr = element.asJsonArray();
    if (arr != null) return "Array[" + arr.size() + "]";

    JsonObject obj = element.asJsonObject();
    if (obj != null) return "Object";

    String str = element.getStringValue();
    if (str != null) return str;  // ← без кавычек

    Integer intVal = element.getIntegerValue();
    if (intVal != null) return String.valueOf(intVal);

    Double dblVal = element.getDoubleValue();
    if (dblVal != null) return String.valueOf(dblVal);

    Boolean boolVal = element.getBooleanValue();
    if (boolVal != null) return String.valueOf(boolVal);

    return "null";
  }

  private DefaultMutableTreeNode buildPrimitiveNode(JsonElement element) {
    String str = element.getStringValue();
    if (str != null) {
      return new DefaultMutableTreeNode(str);  // ← без кавычек
    }

    Integer intVal = element.getIntegerValue();
    if (intVal != null) {
      return new DefaultMutableTreeNode(String.valueOf(intVal));
    }

    Double doubleVal = element.getDoubleValue();
    if (doubleVal != null) {
      return new DefaultMutableTreeNode(String.valueOf(doubleVal));
    }

    Boolean boolVal = element.getBooleanValue();
    if (boolVal != null) {
      return new DefaultMutableTreeNode(String.valueOf(boolVal));
    }

    return new DefaultMutableTreeNode("null");
  }

  private void expandAll(JTree tree, TreePath parent) {
    javax.swing.tree.TreeNode node = (javax.swing.tree.TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (int i = 0; i < node.getChildCount(); i++) {
        javax.swing.tree.TreeNode child = node.getChildAt(i);
        TreePath path = parent.pathByAddingChild(child);
        expandAll(tree, path);
      }
    }
    tree.expandPath(parent);
  }

  private static class JsonTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final Color COLOR_TAG = new Color(128, 128, 128);     // серый для тегов
    private static final Color COLOR_ATTR = Color.BLACK;                 // чёрный для атрибутов
    private static final Color COLOR_STRING = new Color(0, 0, 255);      // синий для значений
    private static final Color COLOR_NUMBER = new Color(0, 128, 0);      // зелёный для чисел
    private static final Color COLOR_SELECTION_BG = new Color(220, 220, 220); // светло-серый

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      setIcon(null);
      setClosedIcon(null);
      setOpenIcon(null);
      setLeafIcon(null);

      // Убираем стандартное выделение
      if (sel) {
        setBackground(COLOR_SELECTION_BG);
        setForeground(Color.BLACK);
      } else {
        setBackground(null);
      }

      // Форматируем текст
      if (value instanceof DefaultMutableTreeNode) {
        Object userObj = ((DefaultMutableTreeNode) value).getUserObject();
        if (userObj instanceof String) {
          String text = (String) userObj;

          if (text.contains(": ")) {
            String[] parts = text.split(": ", 2);
            String key = parts[0];
            String val = parts.length > 1 ? parts[1] : "";

            String html = String.format(
              "<html><span style='color:#808080;'>%s:</span> <span style='color:#000000;'>%s</span></html>",
              key, escapeHtml(val)
            );
            setText(html);
          } else if (text.startsWith("Array[") || text.equals("Object")) {
            setText(text);
          } else if (isNumber(text)) {
            setForeground(COLOR_NUMBER);
            setText(text);
          } else {
            setForeground(COLOR_STRING);
            setText(text);
          }
        }
      }

      return this;
    }

    private boolean isNumber(String str) {
      try {
        Double.parseDouble(str);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }

    private String escapeHtml(String str) {
      return str.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;");
    }
  }
}
