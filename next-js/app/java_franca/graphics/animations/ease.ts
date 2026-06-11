import {Router} from "../device/router";

export class Ease {
  static readonly EASE_LINEAR: number = 1;

  initialValue: number;
  currentValue: number;
  targetValue: number;
  duration: number;

  constructor(initialValue: number, targetValue: number, duration: number) {
    this.initialValue = initialValue;
    this.currentValue = this.initialValue;
    this.targetValue = targetValue;
    this.duration = duration;
  }

  destroy(): void {
    // очистка, если нужна
  }

  isRunning(): boolean {
    return this.currentValue !== this.targetValue;
  }

  tick(router: Router, startedTime: number): boolean {
    // returns true when needs repainting
    return true;
  }
}
