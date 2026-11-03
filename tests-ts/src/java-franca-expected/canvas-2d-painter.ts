import {Painter} from "@java-franca/expected/painter";

export class Canvas2DPainter extends Painter {

  private htmlCanvasElement: HTMLCanvasElement;
  private context2D: CanvasRenderingContext2D;

  constructor(htmlCanvasElement: HTMLCanvasElement) {
    super();
    this.htmlCanvasElement = htmlCanvasElement;
    const context2D = htmlCanvasElement.getContext("2d");
    if (!context2D) {
      throw new Error("Cannot get 2D context");
    }
    this.context2D = context2D;
  }

  paintText(text: string, x: number, y: number, deviceFontKey: string, deviceColor: number): void {
    this.context2D.fillStyle = `rgba(0, 0, 0, ${deviceColor / 255})`;
    this.context2D.font = '16px sans-serif';
    this.context2D.fillText(text, x, y);
  }

  drawRect(x: number, y: number, width: number, height: number): void {
    this.context2D.fillStyle = '#3498db';
    this.context2D.fillRect(x, y, width, height);
  }

  drawCircle(x: number, y: number, radius: number): void {
    this.context2D.beginPath();
    this.context2D.arc(x, y, radius, 0, Math.PI * 2);
    this.context2D.fillStyle = '#e74c3c';
    this.context2D.fill();
  }

  clear(color?: string): void {
    this.context2D.fillStyle = color || '#ffffff';
    this.context2D.fillRect(0, 0, this.htmlCanvasElement.width, this.htmlCanvasElement.height);
  }

  flush(): void {
    // для Canvas 2D не требуется
  }
}
