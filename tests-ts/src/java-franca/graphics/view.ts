import {Router} from "@java-franca/expected/device";
import {Painter} from "@java-franca/expected/painter";
import {Page} from "@java-franca/graphics/page";

export class View {

  destroy(): void {
    // очистка, если нужна
  }

  paint(device: Router, painter: Painter, page: Page): void {

  }

  handlePointerDown(
    device: Router,
    page: Page,
    painterX: number,
    painterY: number,
    pointedX: number,
    pointedY: number,
    buttonNumber: number
  ): void {

  }
}
