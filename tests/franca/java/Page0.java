package franca.java;

import franca.java.ui.Page;
import franca.java.ui.Painter;
import franca.java.ui.Router;

public class Page0 extends Page {

  TestView0 testView0;
  SpinnerView spinnerView;

  public Page0(Router router) {
    super(router);
    testView0 = new TestView0();
    spinnerView = new SpinnerView(300 * Painter.PRECISION);
    views.add(testView0);
    views.add(spinnerView);
  }

  @Override
  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    if (buttonNumber == 1) {
      testView0.startSpinning(router, this);
    } else {
      router.requestPainting();
    }
    if (spinnerView.spinnerTween != null) {
      spinnerView.stopSpinning(router);
    } else {
      spinnerView.startSpinning(router);
    }
  }
}
