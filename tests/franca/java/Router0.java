package franca.java;

import franca.java.ui.Page;
import franca.java.ui.Painter;
import franca.java.ui.Device;
import franca.java.ui.Router;

public class Router0 extends Router {

  TestView0 testView0;
  SpinnerView spinnerView;

  public Router0(Device device) {
    super(device);
    testView0 = new TestView0();
    spinnerView = new SpinnerView(300 * Painter.PRECISION);
    topPage = new Page(device);
    topPage.views.add(testView0);
    topPage.views.add(spinnerView);
  }

  @Override
  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    if (buttonNumber == 1) {
      testView0.startSpinning(device, topPage);
    } else {
      device.requestPainting();
    }
    if (spinnerView.spinnerTween != null) {
      spinnerView.stopSpinning(device);
    } else {
      spinnerView.startSpinning(device);
    }
  }
}
