import {Painter} from "../java_franca/graphics/device/painter";

export class PlatformPainter extends Painter {
  paintText(
    text: string,
    x: number,
    y: number,
    deviceFontKey: string,
    deviceColor: number
  ): void {
    console.log(`Drawing "${text}" at (${x}, ${y})`);
  }
}
