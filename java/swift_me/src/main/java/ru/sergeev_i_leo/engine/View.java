package ru.sergeev_i_leo.engine;

import ru.sergeev_i_leo.engine.painter.Painter;

public class View extends TranspilableClass {

  @Override
  public void destroy() {
    super.destroy();
  }

  public void paint(Device device, Painter painter, Page page) {
  }

  public void handlePointerDown(Device device, Page page, float painterX, float painterY, float pointedX, float pointedY, int buttonNumber) {
  }
}
