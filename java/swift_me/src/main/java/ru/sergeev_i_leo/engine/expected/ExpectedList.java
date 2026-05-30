package ru.sergeev_i_leo.engine.expected;

import ru.sergeev_i_leo.engine.TranspilableClass;

import java.util.ArrayList;
import java.util.List;

public class ExpectedList<T> extends TranspilableClass {

  @Override
  public void destroy() {
    super.destroy();
  }

  private final List<T> list = new ArrayList<>();

  public int size() {
    return list.size();
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public boolean isNotEmpty() {
    return !list.isEmpty();
  }

  public boolean add(T item) {
    return list.add(item);
  }

  public T get(int index) {
    return list.get(index);
  }

  public void set(int index, T item) {
    list.set(index, item);
  }

  public T removeAt(int index) {
    return list.remove(index);
  }

  public void insert(int index, T item) {
    list.add(index, item);
  }

  public void clear() {
    list.clear();
  }

  public void destroyAll() {
    list.clear();
  }
}
