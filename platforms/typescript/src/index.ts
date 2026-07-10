import "./app.scss";
import Canvas2dRouter from "./java-franca-expected/canvas-2d-router";
import SkiaRouter from "./java-franca-expected/skia-router";
import {TestView0} from "./test-view-0";
import {Page} from "@java-franca/graphics/page";

let canvas2DRouter = new Canvas2dRouter();
let skiaRouter = new SkiaRouter();

const runRouter = async () => {
  await SkiaRouter.loadResources();
  
  let htmlElement = document.getElementById("canvas-2d-router");
  if (htmlElement) {
    const page = new Page(canvas2DRouter);
    page.views.push(new TestView0());
    canvas2DRouter.attach(htmlElement, page);
    canvas2DRouter.requestRepainting();
  }
  htmlElement = document.getElementById("skia-router");
  if (htmlElement) {
    const page = new Page(skiaRouter);
    page.views.push(new TestView0());
    skiaRouter.attach(htmlElement, page);
    skiaRouter.requestRepainting();
  }
};

document.addEventListener("DOMContentLoaded", () => runRouter());
