import {Painter} from "@java-franca/expected/painter";
import SkiaRouter from "./skia-router";

export class SkiaPainter extends Painter {

  private surface: any = null;
  private readonly canvasElement: HTMLCanvasElement;

  constructor(canvasElement: HTMLCanvasElement) {
    super();
    this.canvasElement = canvasElement;
  }

  private ensureSurface(): void {
    if (this.surface) {
      return;
    }

    if (!SkiaRouter.canvasKit) {
      console.warn('CanvasKit not ready');
      return;
    }

    this.surface = SkiaRouter.canvasKit.MakeWebGLCanvasSurface(this.canvasElement);
  }

  paintText(text: string, x: number, y: number, deviceFontKey: string, deviceColor: number): void {
    this.ensureSurface();
    if (!this.surface) {
      return;
    }

    const canvas = this.surface.getCanvas();
    const font = SkiaRouter.getFont("inter_regular");

    const paint = new SkiaRouter.canvasKit.Paint();
    const alpha = deviceColor / 255;
    paint.setColor(SkiaRouter.canvasKit.Color(0, 0, 0, alpha));

    const skFont = new SkiaRouter.canvasKit.Font(font, 16);
    canvas.drawText(text, x, y, paint, skFont);
  }

  drawRect(x: number, y: number, width: number, height: number): void {
    this.ensureSurface();
    if (!this.surface) return;

    const canvas = this.surface.getCanvas();
    const paint = new SkiaRouter.canvasKit.Paint();
    paint.setColor(SkiaRouter.canvasKit.Color(52, 152, 219, 1));
    paint.setStyle(SkiaRouter.canvasKit.PaintStyle.Fill);

    const rect = SkiaRouter.canvasKit.XYWHRect(x, y, width, height);
    canvas.drawRect(rect, paint);
  }

  drawCircle(x: number, y: number, radius: number): void {
    this.ensureSurface();
    if (!this.surface) return;

    const canvas = this.surface.getCanvas();
    const paint = new SkiaRouter.canvasKit.Paint();
    paint.setColor(SkiaRouter.canvasKit.Color(231, 76, 60, 1));
    paint.setStyle(SkiaRouter.canvasKit.PaintStyle.Fill);

    canvas.drawCircle(x, y, radius, paint);
  }

  clear(color?: string): void {
    this.ensureSurface();
    if (!this.surface) return;

    const canvas = this.surface.getCanvas();
    canvas.clear(SkiaRouter.canvasKit.Color(255, 255, 255, 1));
  }

  flush(): void {
    if (this.surface) {
      this.surface.flush();
    }
  }
}
