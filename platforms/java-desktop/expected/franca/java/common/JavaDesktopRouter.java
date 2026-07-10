package franca.java.common;

import franca.java.expected.IntegerConsumer;
import franca.java.expected.Router;
import franca.java.expected.StringConsumer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JavaDesktopRouter extends Router {

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
