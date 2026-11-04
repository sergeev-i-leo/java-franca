import "./app.scss";
import Canvas2dRouter from "./java-franca-expected/canvas-2d-device";
import SkiaRouter from "./java-franca-expected/skia-device";
import {TestView0} from "./test-view-0";
import {Page} from "@java-franca/graphics/page";
import WsRouter from "./java-franca-expected/ws-device";

const runRouter = async () => {
  await SkiaRouter.loadResources();

  let htmlElement = document.getElementById("canvas-2d-device");
  const wsRouter = new WsRouter();
  if (htmlElement) {
    wsRouter.mount(htmlElement);
  }
  htmlElement = document.getElementById("skia-device");
  if (htmlElement) {
    wsRouter.mountSkia(htmlElement);
  }
  const page = new Page(wsRouter);
  page.views.push(new TestView0());
  wsRouter.run(page);
};

document.addEventListener("DOMContentLoaded", () => runRouter());
