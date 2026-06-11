import {Page} from "../views/page";
import {View} from "../views/view";
import {Ease} from "./ease";
import {LinearEase} from "./linear-ease";
import {Router} from "../device/router";

export class Tween {
  page: Page | null = null;
  view: View | null = null;
  ease: Ease | null = null;

  tweenId: number = 0;
  registeredTime: number = 0;
  previousTween: Tween | null = null;
  nextTween: Tween | null = null;

  constructor(page: Page | null, view: View | null, duration: number, tickerType: number) {
    this.page = page;
    this.view = view;
    switch (tickerType) {
      case Ease.EASE_LINEAR:
        this.ease = new LinearEase(0, 100, duration);
        break;
      default:
        // repaint just once
        this.ease = null;
        break;
    }
  }

  setEase(ease: Ease): void {
    this.ease = ease;
  }

  getEase(): Ease | null {
    return this.ease;
  }

  needsRepainting(router: Router): boolean {
    if (this.ease === null) {
      // one shot animation
      return true;
    }
    return this.ease.tick(router, this.registeredTime);
  }

  needsNextRepainting(router: Router): boolean {
    if (this.ease === null) {
      // one shot animation
      return false;
    }
    return this.ease.isRunning();
  }
}
