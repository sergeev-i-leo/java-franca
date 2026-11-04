import {Ease} from "./ease";
import {Router} from "@java-franca/expected/device";

export class LinearEase extends Ease {
  constructor(initialValue: number, targetValue: number, duration: number) {
    super(initialValue, targetValue, duration);
  }

  destroy(): void {
    super.destroy();
  }

  tick(device: Router, startedTime: number): boolean {
    // returns true when needs repainting

    if (this.currentValue === this.targetValue) {
      return false;
    }

    const currentTime = device.getTime();

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
