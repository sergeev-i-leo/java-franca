package franca.swift.step_gs.test_components;

import franca.swift.engine.contract.StringBuffer;
import franca.swift.step_gs.renderer.Page;
import franca.swift.step_gs.renderer.View;
import franca.swift.engine.data.html.HtmlParser;
import franca.swift.engine.data.json.JsonArray;
import franca.swift.step_gs.contract.Device;
import franca.swift.step_gs.painter.Painter;

public class HtmlParserView extends View {

  private int state = 0;
  private JsonArray jsonArray = null;
  private String htmlOutput = null;

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
      device.readFile("test.html", result -> {
        if (result != null) {
          HtmlParser htmlParser = new HtmlParser();
          // look for debugging level in input
          htmlParser.debuggingLevel = 1;
          jsonArray = htmlParser.parse(result);
          delete(htmlParser);
          StringBuffer stringBuffer = new StringBuffer();
          htmlParser.toStringBuffer(jsonArray,stringBuffer);
          htmlOutput = stringBuffer.getString();
          delete(stringBuffer);
          device.writeFile("test-output.html", htmlOutput, operationResult -> {
            if ((operationResult != null) && (operationResult == 200)) {
              state = 200;
            } else {
              state = 404;
            }
            page.requestRepainting();
          });
        } else {
          state = 404;
        }
        page.requestRepainting();
      });
    }
    if (state == 200) {
      if (jsonArray != null) {
        state = 2;
        page.requestRepainting();
        String string = jsonArray.serialize();
        device.writeFile("html-0.tmp", string, operationResult -> {
          if ((operationResult != null) && (operationResult == 200)) {
            state = 0;
          } else {
            state = 404;
          }
          page.requestRepainting();
        });
      }
    }
  }
}
