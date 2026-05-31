package me.swift.step_gs;

import me.swift.engine.TranspilableClass;
import me.swift.step_gs.painter.Painter;

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
