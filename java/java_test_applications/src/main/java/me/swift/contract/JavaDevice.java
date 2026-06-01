package me.swift.contract;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import me.swift.engine.contract.OptionalInt;
import me.swift.step_gs.contract.Device;
import me.swift.step_gs.contract.OptionalIntConsumer;
import me.swift.step_gs.contract.StringConsumer;

public class JavaDevice extends Device {

  @Override
  public void readFile(String path, StringConsumer callback) {
    new Thread(() -> {
      try {
        String content = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
        callback.accept(content);
      } catch (Exception e) {
        callback.accept(null);
      }
    }).start();
  }

  @Override
  public void writeFile(String path, String content, OptionalIntConsumer callback) {
    new Thread(() -> {
      try {
        Files.writeString(Paths.get(path), content, StandardCharsets.UTF_8);
        callback.accept(new OptionalInt(200));
      } catch (Exception exception) {
        callback.accept(null);
      }
    }).start();
  }

}
