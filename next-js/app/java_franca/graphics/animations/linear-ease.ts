import {Ease} from "./ease";
import {Router} from "../device/router";

export class LinearEase extends Ease {
  constructor(initialValue: number, targetValue: number, duration: number) {
    super(initialValue, targetValue, duration);
  }

  destroy(): void {
    super.destroy();
  }

  tick(router: Router, startedTime: number): boolean {
    // returns true when needs repainting

    if (this.currentValue === this.targetValue) {
      return false;
    }

    const currentTime = router.getDevice()!.getTime();

    if (currentTime >= startedTime + this.duration) {
      this.currentValue = this.targetValue;
      return true;
    }

    this.currentValue = Math.floor(
      this.initialValue +
      (this.targetValue - this.initialValue) *
      (currentTime - startedTime) /
      this.duration
    );
    return true;
  }
}
