import {Tween} from "@java-franca/animations/tween";
import {Page} from "@java-franca/graphics/page";
import {Ease} from "@java-franca/animations/ease";
import {Router} from "@java-franca/expected/device";
import {View} from "@java-franca/graphics/view";
import {Painter} from "@java-franca/expected/painter";

class MyTween extends Tween {
  private testView0: TestView0;

  constructor(page: Page, testView0: TestView0) {
    super(page, null, 500, Ease.EASE_LINEAR);
    this.testView0 = testView0;
  }

  needsRepainting(device: Router): boolean {
    const result = super.needsRepainting(device);
    if (!result) {
      this.testView0.removeViewAnimation(device);
      return false;
    }
    return true;
  }
}

export class TestView0 extends View {
  private myTween: MyTween | null = null;

  paint(device: Router, painter: Painter, page: Page): void {
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
    device: Router,
    page: Page,
    painterX: number,
    painterY: number,
    pointedX: number,
    pointedY: number,
    buttonNumber: number
  ): void {
    if (buttonNumber === 1) {
      this.removeViewAnimation(device);
      this.myTween = new MyTween(page, this);
      device.registerTween(this.myTween);
    } else {
      device.requestRepainting();
    }
  }

  removeViewAnimation(device: Router): void {
    if (this.myTween !== null) {
      device.removeTween(this.myTween);
      this.myTween = null;
    }
  }
}
