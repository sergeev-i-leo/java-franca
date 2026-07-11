import {BrowserRouter} from "./browser-router";
import {Canvas2DPainter} from "./canvas-2d-painter";

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
    } catch (error) {
      console.warn("Font inter_regular.ttf not loaded", error);
    }
    try {
      const font = new FontFace("secondary", require("~assets/fonts/fira_sans_extra_condensed_bold.ttf"));
      await font.load();
      Canvas2dRouter.fonts.set("secondary", font);
    } catch (error) {
      console.warn("Font fira_sans_extra_condensed_bold.ttf not loaded", error);
    }
    try {
      const font = new FontFace("icons", require("~assets/fonts/remixicon.ttf"));
      await font.load();
      Canvas2dRouter.fonts.set("icons", font);
    } catch (error) {
      console.warn("Font remixicon.ttf not loaded", error);
    }
  }

  attach(parentHTMLElement: HTMLElement): void {
    super.attach(parentHTMLElement);
    if (this.htmlCanvasElement) {
      this.painter = new Canvas2DPainter(this.htmlCanvasElement);
    }
  }

  startPainting() {
    if (this.painter) {
      (this.painter as Canvas2DPainter).clear('#f0f0f0');
    }
  }

  performPainting() {
    if ((this.painter) && (this.topPage)) {
      this.topPage.paint(this.painter);
    }
  }

  finishPainting() {
    if (this.painter) {
      (this.painter as Canvas2DPainter).flush();
    }
  }
}

export default Canvas2dRouter;
