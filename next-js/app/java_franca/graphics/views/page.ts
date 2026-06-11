import {View} from "./view";
import {Router} from "../device/router";
import {Painter} from "../device/painter";

export class Page {
  router: Router | null = null;
  views: View[] = [];
  nextPage: Page | null = null;

  constructor(router: Router) {
    this.router = router;
  }

  destroy(): void {
    // очистка массива views
    this.views = [];
  }

  paint(painter: Painter): void {
    for (let i = 0; i < this.views.length; i++) {
      this.views[i].paint(this.router!, painter, this);
    }
  }

  handlePointerDown(pointedX: number, pointedY: number, buttonNumber: number): void {
    for (let i = 0; i < this.views.length; i++) {
      this.views[i].handlePointerDown(
        this.router!,
        this,
        0, // painterX
        0, // painterY
        pointedX,
        pointedY,
        buttonNumber
      );
    }
  }
}
