package franca.java.ws;

import franca.java.common.JavaDesktopRouter;
import franca.java.graphics.GraphicsRouter;
import franca.java.skia.SkiaRouter;

public class WsRouter extends JavaDesktopRouter {

  public WsServer wsServer;
  public GraphicsRouter graphicsRouter;
  public SkiaRouter skiaRouter;

  public WsRouter(GraphicsRouter graphicsRouter, SkiaRouter skiaRouter) {
    super();
    this.graphicsRouter = graphicsRouter;
    this.skiaRouter = skiaRouter;
  }
}
