package contracted.franca.java;

import java.util.*;

public class ContractedArray<T> implements Iterable<T> {

  private ArrayList<T> storage;

  public ContractedArray() {
    storage = new ArrayList<>();
  }

  public int size() {
    return storage.size();
  }

  public boolean isEmpty() {
    return storage.isEmpty();
  }

  public T get(int index) {
    checkIndex(index);
    return storage.get(index);
  }

  public void set(int index, T value) {
    checkIndex(index);
    storage.set(index, value);
  }

  public void add(T item) {
    storage.add(item);
  }

  public void insert(T item, int at) {
    checkIndexForInsert(at);
    storage.add(at, item);
  }

  public void remove(int index) {
    checkIndex(index);
    storage.remove(index);
  }

  public T removeLast() {
    if (isEmpty()) {
      throw new NoSuchElementException("Array is empty");
    }
    return storage.remove(storage.size() - 1);
  }

  public T removeFirst() {
    if (isEmpty()) {
      throw new NoSuchElementException("Array is empty");
    }
    return storage.remove(0);
  }

  public void clear() {
    storage.clear();
  }

  @Override
  public String toString() {
    return storage.toString();
  }

  @Override
  public Iterator<T> iterator() {
    return storage.iterator();
  }

  private void checkIndex(int index) {
    if ((index < 0) || (index >= storage.size())) {
      throw new IndexOutOfBoundsException(
        "Index " + index + " out of bounds for length " + storage.size()
      );
    }
  }

  private void checkIndexForInsert(int index) {
    if ((index < 0) || (index > storage.size())) {
      throw new IndexOutOfBoundsException(
        "Index " + index + " out of bounds for insertion (0..." + storage.size() + ")"
      );
    }
  }
}
