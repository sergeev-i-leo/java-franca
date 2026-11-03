import Canvas2dRouter from "./canvas-2d-router";
import {SkiaPainter} from "./skia-painter";
import {Page} from "@java-franca/graphics/page";
import {Painter} from "@java-franca/expected/painter";

class WsRouter extends Canvas2dRouter {

  skiaParentHTMLElement: HTMLElement | null = null;
  skiaHtmlCanvasElement: HTMLCanvasElement | null = null;
  skiaPainter: SkiaPainter | null = null;

  mountSkia(skiaParentHTMLElement: HTMLElement) {
    this.skiaParentHTMLElement = skiaParentHTMLElement;
    this.skiaHtmlCanvasElement = document.createElement("canvas") as HTMLCanvasElement;
    this.skiaParentHTMLElement.appendChild(this.skiaHtmlCanvasElement);
    if (this.skiaHtmlCanvasElement) {
      this.skiaPainter = new SkiaPainter(this.skiaHtmlCanvasElement);
    }

    this.resize();
  }

  resize() {
    if (this.skiaParentHTMLElement) {
      if (this.skiaHtmlCanvasElement) {
        const rect = this.skiaParentHTMLElement.getBoundingClientRect();
        const width = Math.floor(rect.width);
        const height = Math.floor(rect.height);

        this.skiaHtmlCanvasElement.width = width;
        this.skiaHtmlCanvasElement.height = height;

        this.skiaHtmlCanvasElement.style.width = width + "px";
        this.skiaHtmlCanvasElement.style.height = height + "px";
        this.skiaHtmlCanvasElement.style.display = "block";
      }
    }
    super.resize();
  }

  run(page: Page) {
    super.run(page);
  }

  preparePainting(painter: Painter) {
    super.preparePainting(painter);
  }

  doPainting(painter: Painter): void {
    super.doPainting(painter);
    if ((this.topPage) && (this.skiaPainter)) {
      this.topPage.paint(this.skiaPainter);
    }
  }

  finishPainting(painter: Painter): void {
    super.finishPainting(painter);
    if (this.skiaPainter) {
      (this.skiaPainter as SkiaPainter).flush();
    }
  }
}

export default WsRouter;
