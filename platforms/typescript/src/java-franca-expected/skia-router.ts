import {BrowserRouter} from "./browser-router";

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
        const response = await fetch("/fonts/inter_regular.ttf");
        const arrayBuffer = await response.arrayBuffer();
        const typeface = SkiaRouter.canvasKit.Typeface.MakeFreeTypeFaceFromData(arrayBuffer);
        SkiaRouter.fonts.set("primary", typeface);
      } catch (e) {
        console.warn(`Font inter_regular.ttf not loaded`, e);
      }
      try {
        const response = await fetch("/fonts/fira_sans_extra_condensed_bold.ttf");
        const arrayBuffer = await response.arrayBuffer();
        const typeface = SkiaRouter.canvasKit.Typeface.MakeFreeTypeFaceFromData(arrayBuffer);
        SkiaRouter.fonts.set('secondary', typeface);
      } catch (e) {
        console.warn(`Font fira_sans_extra_condensed_bold.ttf not loaded`, e);
      }
      try {
        const response = await fetch(`assets/fonts/remixicon.ttf`);
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

  getFont(name: string): any {
    return SkiaRouter.fonts.get(name) || null;
  }

}

export default SkiaRouter;
