import {BrowserRouter} from "./browser-router";
import {SkiaPainter} from "./skia-painter";
import {Painter} from "@java-franca/expected/painter";

class SkiaRouter extends BrowserRouter {

  static canvasKit: any = null;
  static fonts: Map<string, any> = new Map();

  getTime(): number {
    return performance.now();
  }

  static async loadResources(): Promise<void> {

    try {

      await new Promise((resolve, reject) => {
        const script = document.createElement("script");
        script.src = "https://unpkg.com/canvaskit-wasm@0.37.0/bin/full/canvaskit.js";
        script.onload = resolve;
        script.onerror = reject;
        document.head.appendChild(script);
      });

      SkiaRouter.canvasKit = await Promise.race([
        (window as any).CanvasKitInit({
          locateFile: (file: string) => `https://unpkg.com/canvaskit-wasm@0.37.0/bin/full/${file}`
        }),
        new Promise((_, reject) => {
          SkiaRouter.canvasKit = undefined;
          setTimeout(() => reject(new Error("CanvasKit initialization timeout")), 30000);
        })
      ]);

      try {
        const response = await fetch(require("~assets/fonts/fira_sans_extra_condensed_bold.ttf"));
        const arrayBuffer = await response.arrayBuffer();
        const typeface = SkiaRouter.canvasKit.Typeface.MakeFreeTypeFaceFromData(arrayBuffer);
        SkiaRouter.fonts.set("primary", typeface);
      } catch (e) {
        console.warn(`Font inter_regular.ttf not loaded`, e);
      }
      try {
        const response = await fetch(require("~assets/fonts/fira_sans_extra_condensed_bold.ttf"));
        const arrayBuffer = await response.arrayBuffer();
        const typeface = SkiaRouter.canvasKit.Typeface.MakeFreeTypeFaceFromData(arrayBuffer);
        SkiaRouter.fonts.set("secondary", typeface);
      } catch (error) {
        console.warn("Font fira_sans_extra_condensed_bold.ttf not loaded", error);
      }
      try {
        const response = await fetch(require("~assets/fonts/remixicon.ttf"));
        const arrayBuffer = await response.arrayBuffer();
        const typeface = SkiaRouter.canvasKit.Typeface.MakeFreeTypeFaceFromData(arrayBuffer);
        SkiaRouter.fonts.set("remixicon", typeface);
      } catch (error) {
        console.warn("Font remixicon.ttf not loaded", error);
      }

    } catch (error) {
      console.error("Failed to load CanvasKit", error);
    }
  }

  mount(parentHTMLElement: HTMLElement): void {
    super.mount(parentHTMLElement);
    if (this.htmlCanvasElement) {
      this.painter = new SkiaPainter(this.htmlCanvasElement);
    }
  }

  preparePainting(painter: Painter): void {
  }

  doPainting(painter: Painter): void {
    if (this.topPage) {
      this.topPage.paint(painter);
    }
  }

  finishPainting(painter: Painter): void {
    (painter as SkiaPainter).flush();
  }

  static getFont(name: string): any {
    return SkiaRouter.fonts.get(name) || null;
  }

}

export default SkiaRouter;
