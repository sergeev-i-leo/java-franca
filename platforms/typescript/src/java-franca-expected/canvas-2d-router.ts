import {BrowserRouter} from "./browser-router";
import {Canvas2DPainter} from "./canvas-2d-painter";
import {Page} from "@java-franca/graphics/page";

class Canvas2dRouter extends BrowserRouter {

  static fonts: Map<string, FontFace> = new Map();

  getTime(): number {
    return performance.now();
  }

  static async loadResources(): Promise<void> {

    try {
      const font = new FontFace("primary", require("~assets/fonts/inter_regular.ttf"));
      await font.load();
      Canvas2dRouter.fonts.set("primary", font);
    } catch (e) {
      console.warn(`Font inter_regular.ttf not loaded`, e);
    }
    try {
      const font = new FontFace("secondary", require("~assets/fonts/fira_sans_extra_condensed_bold.ttf"));
      await font.load();
      Canvas2dRouter.fonts.set("secondary", font);
    } catch (e) {
      console.warn(`Font fira_sans_extra_condensed_bold.ttf not loaded`, e);
    }
    try {
      const font = new FontFace("icons", require("~assets/fonts/remixicon.ttf"));
      await font.load();
      Canvas2dRouter.fonts.set("icons", font);
    } catch (e) {
      console.warn(`Font remixicon.ttf not loaded`, e);
    }
  }

  attach(parentHTMLElement: HTMLElement, rootPage: Page): void {
    super.attach(parentHTMLElement, rootPage);
    if (this.htmlCanvasElement) {
      this.painter = new Canvas2DPainter(this.htmlCanvasElement);
    }
  }

  startRepainting(): void {
    if (this.animationFrameId !== null) {
      return;
    }
    this.lastTickTime = performance.now();
    this.animationFrameId = requestAnimationFrame(this.tick.bind(this));
  }

  private tick(now: number): void {
    if ((!this.painter) || (!this.htmlCanvasElement)) {
      this.stopRepainting();
      return;
    }

    if (now - this.lastTickTime < 16) {
      this.animationFrameId = requestAnimationFrame(this.tick.bind(this));
      return;
    }
    this.lastTickTime = now;

    const needsRedraw = this.needsRepainting();

    if (needsRedraw) {
      (this.painter as Canvas2DPainter).clear('#f0f0f0');
      if (this.topPage) {
        this.topPage.paint(this.painter);
      }
      (this.painter as Canvas2DPainter).flush();
    }

    if (this.needsNextRepainting()) {
      this.animationFrameId = requestAnimationFrame(this.tick.bind(this));
    } else {
      this.stopRepainting();
    }
  }

  private stopRepainting(): void {
    if (this.animationFrameId !== null) {
      cancelAnimationFrame(this.animationFrameId);
      this.animationFrameId = null;
    }
  }
}

export default Canvas2dRouter;
