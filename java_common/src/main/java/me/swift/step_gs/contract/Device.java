package me.swift.step_gs.contract;

import me.swift.step_gs.Page;

public class Device {

  public long getTime() {
    return 0L;
  }

  public void readFile(String path, StringConsumer callback) {
    // Реализация в платформозависимом классе
  }

  /**
   * Асинхронно записывает файл
   * @param path путь к файлу
   * @param content содержимое для записи
   * @param callback возвращает OptionalInt (200 если успех, null если ошибка)
   */
  public void writeFile(String path, String content, OptionalIntConsumer callback) {
    // Реализация в платформозависимом классе
  }
  public void startRepainting(Page page) {
  }

}

