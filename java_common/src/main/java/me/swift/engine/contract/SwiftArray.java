package me.swift.engine.contract;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class SwiftArray<T> implements Iterable<T> {
  private ArrayList<T> storage;

  // MARK: - Initializers

  public SwiftArray() {
    storage = new ArrayList<>();
  }

  public SwiftArray(T... items) {
    storage = new ArrayList<>(Arrays.asList(items));
  }

  public SwiftArray(Collection<? extends T> collection) {
    storage = new ArrayList<>(collection);
  }

  // MARK: - Properties

  public int count() {
    return storage.size();
  }

  public boolean isEmpty() {
    return storage.isEmpty();
  }

  public T first() {
    return isEmpty() ? null : storage.get(0);
  }

  public T last() {
    return isEmpty() ? null : storage.get(storage.size() - 1);
  }

  // MARK: - Subscript (access)

  public T get(int index) {
    checkIndex(index);
    return storage.get(index);
  }

  public void set(int index, T value) {
    checkIndex(index);
    storage.set(index, value);
  }

  // MARK: - Adding elements

  public void append(T item) {
    storage.add(item);
  }

  public void insert(T item, int at) {
    checkIndexForInsert(at);
    storage.add(at, item);
  }

  // MARK: - Removing elements

  @SuppressWarnings("unchecked")
  public void removeAt(int index) {
    checkIndex(index);
    storage.remove(index);
  }

  public T removeLast() {
    if (isEmpty()) throw new NoSuchElementException("Array is empty");
    return storage.remove(storage.size() - 1);
  }

  public T removeFirst() {
    if (isEmpty()) throw new NoSuchElementException("Array is empty");
    return storage.remove(0);
  }

  public void removeAll() {
    storage.clear();
  }

  // MARK: - Searching

  public boolean contains(T item) {
    return storage.contains(item);
  }

  public int firstIndexOf(T item) {
    return storage.indexOf(item);
  }

  public int lastIndexOf(T item) {
    return storage.lastIndexOf(item);
  }

  public T randomElement() {
    if (isEmpty()) return null;
    return storage.get(new Random().nextInt(storage.size()));
  }

  // MARK: - Transformations

  public SwiftArray<T> filter(Predicate<T> predicate) {
    SwiftArray<T> result = new SwiftArray<>();
    for (T item : storage) {
      if (predicate.test(item)) {
        result.append(item);
      }
    }
    return result;
  }

  public <R> SwiftArray<R> map(Function<T, R> mapper) {
    SwiftArray<R> result = new SwiftArray<>();
    for (T item : storage) {
      result.append(mapper.apply(item));
    }
    return result;
  }

  public <R> SwiftArray<R> compactMap(Function<T, R> mapper) {
    SwiftArray<R> result = new SwiftArray<>();
    for (T item : storage) {
      R mapped = mapper.apply(item);
      if (mapped != null) {
        result.append(mapped);
      }
    }
    return result;
  }

  public SwiftArray<T> sorted(Comparator<? super T> comparator) {
    SwiftArray<T> result = new SwiftArray<>(storage);
    result.storage.sort(comparator);
    return result;
  }

  // MARK: - Combining

  public SwiftArray<T> adding(T item) {
    SwiftArray<T> result = new SwiftArray<>(storage);
    result.append(item);
    return result;
  }

  public SwiftArray<T> adding(Collection<? extends T> items) {
    SwiftArray<T> result = new SwiftArray<>(storage);
    result.storage.addAll(items);
    return result;
  }

  // MARK: - Higher order functions

  public T reduce(T initial, java.util.function.BinaryOperator<T> accumulator) {
    T result = initial;
    for (T item : storage) {
      result = accumulator.apply(result, item);
    }
    return result;
  }

  public void forEach(java.util.function.Consumer<? super T> action) {
    storage.forEach(action);
  }

  // Sub-arrays

  public SwiftArray<T> dropFirst(int k) {
    if (k >= count()) return new SwiftArray<>();
    SwiftArray<T> result = new SwiftArray<>();
    for (int i = k; i < count(); i++) {
      result.append(get(i));
    }
    return result;
  }

  public SwiftArray<T> dropLast(int k) {
    if (k >= count()) return new SwiftArray<>();
    SwiftArray<T> result = new SwiftArray<>();
    for (int i = 0; i < count() - k; i++) {
      result.append(get(i));
    }
    return result;
  }

  public SwiftArray<T> prefix(int maxLength) {
    int len = Math.min(maxLength, count());
    SwiftArray<T> result = new SwiftArray<>();
    for (int i = 0; i < len; i++) {
      result.append(get(i));
    }
    return result;
  }

  public SwiftArray<T> suffix(int maxLength) {
    int len = Math.min(maxLength, count());
    SwiftArray<T> result = new SwiftArray<>();
    for (int i = count() - len; i < count(); i++) {
      result.append(get(i));
    }
    return result;
  }

  // Equality

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof SwiftArray)) return false;
    SwiftArray<?> other = (SwiftArray<?>) obj;
    return storage.equals(other.storage);
  }

  @Override
  public int hashCode() {
    return storage.hashCode();
  }

  // Printable

  @Override
  public String toString() {
    return storage.toString();
  }

  // Iterable

  @Override
  public Iterator<T> iterator() {
    return storage.iterator();
  }

  // Private helpers

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
