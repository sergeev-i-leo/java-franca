import {Painter} from "@java-franca/expected/painter";

export class Canvas2DPainter extends Painter {

  private canvas: HTMLCanvasElement;
  private ctx: CanvasRenderingContext2D;

  constructor(canvas: HTMLCanvasElement) {
    super();
    this.canvas = canvas;
    const ctx = canvas.getContext('2d');
    if (!ctx) {
      throw new Error('Cannot get 2D context');
    }
    this.ctx = ctx;
  }

  paintText(text: string, x: number, y: number, deviceFontKey: string, deviceColor: number): void {
    this.ctx.fillStyle = `rgba(0, 0, 0, ${deviceColor / 255})`;
    this.ctx.font = '16px sans-serif';
    this.ctx.fillText(text, x, y);
  }

  drawRect(x: number, y: number, width: number, height: number): void {
    this.ctx.fillStyle = '#3498db';
    this.ctx.fillRect(x, y, width, height);
  }

  drawCircle(x: number, y: number, radius: number): void {
    this.ctx.beginPath();
    this.ctx.arc(x, y, radius, 0, Math.PI * 2);
    this.ctx.fillStyle = '#e74c3c';
    this.ctx.fill();
  }

  clear(color?: string): void {
    this.ctx.fillStyle = color || '#ffffff';
    this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
  }

  flush(): void {
    // для Canvas 2D не требуется
  }
}
