package me.swift.step_gs.test_components;

import me.swift.step_gs.Page;
import me.swift.step_gs.View;
import me.swift.step_gs.contract.Device;
import me.swift.step_gs.painter.Painter;

public class HtmlParserView extends View {

  private int state = 0;

  @Override
  public void paint(Device device, Painter painter, Page page) {

    switch (state) {
      case 0:
        painter.paintText("WAITING", 100f, 100f, "", 255);
        break;
      case 1:
        painter.paintText("READING", 100f, 100f, "", 255);
        break;
      case 2:
        painter.paintText("WRITING", 100f, 100f, "", 255);
        break;
      case 200:
        painter.paintText("200", 100f, 100f, "", 255);
        break;
      case 404:
        painter.paintText("404", 100f, 100f, "", 255);
        break;
      default:
        painter.paintText("500", 100f, 100f, "", 255);
        break;
    }
  }

  @Override
  public void handlePointerDown(Device device, Page page, float painterX, float painterY, float pointedX, float pointedY, int buttonNumber) {
    if (state == 0) {
      state = 1;
      page.requestRepainting();
      device.readFile("html-0.html", result -> {
        if (result != null) {
          state = 200;
        } else {
          state = 404;
        }
        page.requestRepainting();
      });
    }
    if (state == 200) {
      state = 2;
      page.requestRepainting();
      device.writeFile("html-0.tmp", "Hello World", optionalInt -> {
        if ((optionalInt != null) && (optionalInt.value == 200)) {
          state = 0;
        } else {
          state = 404;
        }
        page.requestRepainting();
      });
    }
  }
}
