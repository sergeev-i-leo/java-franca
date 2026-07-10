import {Router} from "../device/router";
import {Painter} from "../device/painter";
import {Page} from "./page";

export abstract class View {

  destroy(): void {
    // очистка, если нужна
  }

  abstract paint(router: Router, painter: Painter, page: Page): void;

  abstract handlePointerDown(
    router: Router,
    page: Page,
    painterX: number,
    painterY: number,
    pointedX: number,
    pointedY: number,
    buttonNumber: number
  ): void;
}
