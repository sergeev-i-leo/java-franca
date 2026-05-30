package me.swift.engine.expected;

import me.swift.engine.TranspilableClass;

import java.util.HashMap;
import java.util.Map;

public class ExpectedMap<T> extends TranspilableClass {

  @Override
  public void destroy() {
    super.destroy();
  }

  private final Map<String, T> map = new HashMap<>();

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public boolean isNotEmpty() {
    return !map.isEmpty();
  }

  public void set(String key, T value) {
    map.put(key, value);
  }

  public T get(String key) {
    return map.get(key);
  }

  public void remove(String key) {
    map.remove(key);
  }

  public ExpectedList<String> keys() {
    ExpectedList<String> keys = new ExpectedList<>();
    for (String key : map.keySet()) {
      keys.add(key);
    }
    return keys;
  }

  public void clear() {
    map.clear();
  }

  public void destroyAll() {
    map.clear();
  }
}
