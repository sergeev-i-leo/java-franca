package franca.java.graphics.device;

import franca.java.graphics.renderer.Page;

public class Device {

  public long getTime() {
    return 0L;
  }

  public void readFile(String path, StringConsumer callback) {
  }

  public void writeFile(String path, String content, IntegerConsumer callback) {
  }

  public void startRepainting(Page page) {
  }

}

