package franca.java.ws;

import franca.java.common.JavaDesktopRouter;
import franca.java.graphics.GraphicsRouter;
import franca.java.skia.SkiaRouter;

public class WsRouter extends JavaDesktopRouter {

  WsServer wsServer;
  GraphicsRouter graphicsRouter;
  SkiaRouter skiaRouter;

  WsRouter(WsServer wsServer, GraphicsRouter graphicsRouter, SkiaRouter skiaRouter) {
    super();
    this.wsServer = wsServer;
    this.graphicsRouter = graphicsRouter;
    this.skiaRouter = skiaRouter;
  }
}
