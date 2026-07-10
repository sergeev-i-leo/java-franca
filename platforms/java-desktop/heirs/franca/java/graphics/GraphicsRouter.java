package franca.java.graphics;

import franca.java.graphics.device.Router;

public class GraphicsRouter extends Router {

  public GraphicsApplication graphicsApplication = null;

  public void startRepainting() {
    if (graphicsApplication != null) {
      graphicsApplication.startRepainting();
    }
  }

}
