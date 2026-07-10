import {BrowserRouter} from "./browser-router";
import {Page} from "@java-franca/graphics/page";
import {SkiaPainter} from "./skia-painter";

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
        SkiaRouter.fonts.set('secondary', typeface);
      } catch (e) {
        console.warn(`Font fira_sans_extra_condensed_bold.ttf not loaded`, e);
      }
      try {
        const response = await fetch(require("~assets/fonts/remixicon.ttf"));
        const arrayBuffer = await response.arrayBuffer();
        const typeface = SkiaRouter.canvasKit.Typeface.MakeFreeTypeFaceFromData(arrayBuffer);
        SkiaRouter.fonts.set('remixicon', typeface);
      } catch (e) {
        console.warn(`Font remixicon.ttf not loaded`, e);
      }

    } catch (e) {
      console.error('Failed to load CanvasKit', e);
    }
  }

  attach(parentHTMLElement: HTMLElement, rootPage: Page): void {
    super.attach(parentHTMLElement, rootPage);
    if (this.htmlCanvasElement) {
      this.painter = new SkiaPainter(this.htmlCanvasElement, this);
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

    const needsRepainting = this.needsRepainting();

    if (needsRepainting) {
      (this.painter as SkiaPainter).clear('#f0f0f0');
      if (this.topPage) {
        this.topPage.paint(this.painter);
      }
      (this.painter as SkiaPainter).flush();
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

  getFont(name: string): any {
    return SkiaRouter.fonts.get(name) || null;
  }

}

export default SkiaRouter;
