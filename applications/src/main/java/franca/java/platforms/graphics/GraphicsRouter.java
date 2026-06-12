package franca.java.platforms.graphics;

import java.franca.graphics.device.Router;

public class GraphicsRouter extends Router {

  public GraphicsApplication graphicsApplication = null;

  public void startRepainting() {
    if (graphicsApplication != null) {
      graphicsApplication.startRepainting();
    }
  }

}
