import {Router} from "@java-franca/expected/router";
import {Painter} from "@java-franca/expected/painter";
import {Page} from "@java-franca/graphics/page";

export class View {

  destroy(): void {
    // очистка, если нужна
  }

  paint(router: Router, painter: Painter, page: Page): void {

  }

  handlePointerDown(
    router: Router,
    page: Page,
    painterX: number,
    painterY: number,
    pointedX: number,
    pointedY: number,
    buttonNumber: number
  ): void {

  }
}
