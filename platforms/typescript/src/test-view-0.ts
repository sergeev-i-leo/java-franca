import {Tween} from "@java-franca/animations/tween";
import {Page} from "@java-franca/graphics/page";
import {Ease} from "@java-franca/animations/ease";
import {Router} from "@java-franca/expected/router";
import {View} from "@java-franca/graphics/view";
import {Painter} from "@java-franca/expected/painter";

class MyTween extends Tween {
  private testView0: TestView0;

  constructor(page: Page, testView0: TestView0) {
    super(page, null, 500, Ease.EASE_LINEAR);
    this.testView0 = testView0;
  }

  needsRepainting(router: Router): boolean {
    const result = super.needsRepainting(router);
    if (!result) {
      this.testView0.removeViewAnimation(router);
      return false;
    }
    return true;
  }
}

export class TestView0 extends View {
  private myTween: MyTween | null = null;

  paint(router: Router, painter: Painter, page: Page): void {
    const x = Math.floor(Math.random() * 50) + 50;
    const y = Math.floor(Math.random() * 50) + 50;

    if (this.myTween !== null) {
      painter.paintText(
        String(this.myTween.getEase()?.currentValue),
        x,
        y,
        '',
        255
      );
    } else {
      painter.paintText('PAINT', x, y, '', 255);
    }
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
    if (buttonNumber === 1) {
      this.removeViewAnimation(router);
      this.myTween = new MyTween(page, this);
      router.registerTween(this.myTween);
    } else {
      router.requestRepainting();
    }
  }

  removeViewAnimation(router: Router): void {
    if (this.myTween !== null) {
      router.removeTween(this.myTween);
      this.myTween = null;
    }
  }
}
