package franca.java.ui;

import java.util.ArrayList;

public class View {

  public int left = 0;
  public int top = 0;
  public int width = 0;
  public int height = 0;

  public int marginLeft = 0;
  public int marginTop = 0;
  public int marginRight = 0;
  public int marginBottom = 0;

  public ArrayList<View> views = new ArrayList<>();

  public void paint(Device device, Painter painter, Page page) {
  }

  public void didPainted(Device device, Page page) {
  }

  public void handlePointerDown(Device device, Page page, int painterX, int painterY, int pointedX, int pointedY, int buttonNumber) {
  }
}
