package franca.java;

import franca.java.ui.Painter;
import franca.java.ui.Device;
import franca.java.ui.Router;
import franca.java.ui.animations.Animation;
import franca.java.ui.typography.LabelView;
import franca.java.ui.views.PageView;

public class Router0 extends Router {

  TestView0 testView0;
  SpinnerView spinnerView;

  public Router0(Device device) {
    super(device);
    testView0 = new TestView0();
    spinnerView = new SpinnerView(300 * Painter.PRECISION);
    PageView pageView = new PageView();
    views.add(pageView);
    pageView.views.add(testView0);
    pageView.views.add(spinnerView);
    LabelView labelView = new LabelView();
    pageView.views.add(labelView);
    labelView.setText("HELLO WORLD!!!");
  }

  @Override
  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    if (buttonNumber == 1) {
      testView0.startSpinning(device);
    } else {
      device.requestPainting();
    }
    if (spinnerView.spinnerAnimation != null) {
      spinnerView.stopSpinning(device);
    } else {
      spinnerView.startSpinning(device);
    }
  }
}
