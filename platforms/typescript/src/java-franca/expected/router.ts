import {Page} from "../views/page";
import {Tween} from "../animations/tween";
import {Painter} from "./painter";

export class Router {

  topPage: Page | null = null;

  // animation orchestration
  private lastTweenId: number = 0;
  private firstTween: Tween | null = null;

  getTime(): number {
    return 0;
  }

  getTopPage(): Page | null {
    return this.topPage;
  }

  pushPage(page: Page): void {
    page.nextPage = this.topPage;
    this.topPage = page;
  }

  popPage(): void {
    if (this.topPage !== null) {
      this.topPage = this.topPage.nextPage;
    }
  }

  paint(painter: Painter): void {
    if (this.topPage !== null) {
      this.topPage.paint(painter);
    }
  }

  requestRepainting(): void {
    // one-shot animation
    const tween = new Tween(null, null, 0, 0);
    this.registerTween(tween);
  }

  registerTween(tween: Tween): void {
    // the new animation becomes the first in the chain because registerTween can be called from Tween.needsRepainting

    this.lastTweenId++;
    tween.tweenId = this.lastTweenId;
    tween.registeredTime = this.getDevice()!.getTime();

    tween.nextTween = this.firstTween;
    if (this.firstTween !== null) {
      this.firstTween.previousTween = tween;
    }
    this.firstTween = tween;

    this.startRepainting();
  }

  startRepainting(): void {
    // platform implementation will override
  }

  removeTween(tween: Tween): void {
    let currentTween = this.firstTween;
    while (currentTween !== null) {
      if (currentTween.tweenId === tween.tweenId) {
        const previousTween = currentTween.previousTween;
        const nextTween = currentTween.nextTween;

        if (previousTween !== null) {
          previousTween.nextTween = nextTween;
        }
        if (nextTween !== null) {
          nextTween.previousTween = previousTween;
        }
        if (currentTween === this.firstTween) {
          this.firstTween = nextTween;
        }
        break;
      }
      currentTween = currentTween.nextTween;
    }
    if (this.firstTween === null) {
      this.lastTweenId = 0;
    }
  }

  needsRepainting(): boolean {
    let result = false;

    let currentTween = this.firstTween;
    while (currentTween !== null) {
      result = currentTween.needsRepainting(this) || result;
      if (currentTween.getEase() === null) {
        // it's one-shot tween
        const previousTween = currentTween.previousTween;
        const nextTween = currentTween.nextTween;
        if (previousTween !== null) {
          previousTween.nextTween = nextTween;
        }
        if (nextTween !== null) {
          nextTween.previousTween = previousTween;
        }
        if (currentTween.tweenId === this.firstTween?.tweenId) {
          this.firstTween = nextTween;
        }
        currentTween = nextTween;
      } else {
        currentTween = currentTween.nextTween;
      }
    }
    return result;
  }

  needsNextRepainting(): boolean {
    let result = false;

    let currentTween = this.firstTween;
    while (currentTween !== null) {
      if (currentTween.getEase() === null) {
        // wow! one-shot tween has been created during paint(), welcome to next needsRepainting
        result = true;
      } else {
        // always run needsNextRepainting
        result = currentTween.needsNextRepainting(this) || result;
      }
      currentTween = currentTween.nextTween;
    }
    return result;
  }

  handlePointerDown(pointedX: number, pointedY: number, buttonNumber: number): void {
    if (this.topPage !== null) {
      this.topPage.handlePointerDown(pointedX, pointedY, buttonNumber);
    }
  }

  handlePointerMove(pointedX: number, pointedY: number): void {
    if (this.topPage !== null) {
      this.topPage.handlePointerMove(pointedX, pointedY);
    }
  }

  handlePointerUp(): void {
    if (this.topPage !== null) {
      this.topPage.handlePointerUp();
    }
  }
}
