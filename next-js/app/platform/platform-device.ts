import {Device} from "../java_franca/graphics/device/device";

export class PlatformDevice extends Device {

  getTime(): number {
    return performance.now();
  }
}
