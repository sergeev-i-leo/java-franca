package franca.java.swing_applications.platform;

import franca.java.graphics.device.Router;

public class SwingRouter extends Router {

  public SwingApplication swingApplication = null;

  public void startRepainting() {
    if (swingApplication != null) {
      swingApplication.startRepainting();
    }
  }

}
