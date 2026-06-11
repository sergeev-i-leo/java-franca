package franca.java.platform;

import franca.java.graphics.device.Device;
import franca.java.graphics.device.IntegerConsumer;
import franca.java.graphics.device.StringConsumer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JavaDevice extends Device {

  @Override
  public long getTime() {
    return System.nanoTime() / 1_000_000;
  }

  @Override
  public void readFile(String path, StringConsumer callback) {
    new Thread(() -> {
      try {
        String content = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
        callback.accept(content);
      } catch (Exception exception) {
        exception.printStackTrace();
        callback.accept(null);
      }
    }).start();
  }

  @Override
  public void writeFile(String path, String content, IntegerConsumer callback) {
    new Thread(() -> {
      try {
        Files.writeString(Paths.get(path), content, StandardCharsets.UTF_8);
        callback.accept(200);
      } catch (Exception exception) {
        exception.printStackTrace();
        callback.accept(null);
      }
    }).start();
  }

}
