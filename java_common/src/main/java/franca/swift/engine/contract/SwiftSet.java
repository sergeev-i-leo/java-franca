package franca.swift.engine.contract;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class SwiftSet<T> implements Iterable<T> {

  private HashSet<T> storage;

  // MARK: - Initializers

  public SwiftSet() {
    storage = new HashSet<>();
  }

  @SafeVarargs
  public SwiftSet(T... items) {
    storage = new HashSet<>(Arrays.asList(items));
  }

  public SwiftSet(Collection<? extends T> collection) {
    storage = new HashSet<>(collection);
  }

  // MARK: - Properties

  public int count() {
    return storage.size();
  }

  public boolean isEmpty() {
    return storage.isEmpty();
  }

  public T first() {
    return isEmpty() ? null : storage.iterator().next();
  }

  // MARK: - Insert and Remove

  public boolean insert(T item) {
    return storage.add(item);
  }

  public void remove(T item) {
    storage.remove(item);
  }

  public void removeAll() {
    storage.clear();
  }

  // MARK: - Searching

  public boolean contains(T item) {
    return storage.contains(item);
  }

  public T randomElement() {
    if (isEmpty()) return null;
    int randomIndex = new Random().nextInt(storage.size());
    int i = 0;
    for (T item : storage) {
      if (i++ == randomIndex) return item;
    }
    return null; // never reached
  }

  // MARK: - Set Operations (return new sets)

  public SwiftSet<T> union(SwiftSet<? extends T> other) {
    SwiftSet<T> result = new SwiftSet<>(storage);
    result.storage.addAll(other.storage);
    return result;
  }
/*
  public SwiftSet<T> intersection(SwiftSet<? extends T> other) {
    SwiftSet<T> result = new SwiftSet<>();
    for (T item : storage) {
      if (other.contains(item)) {
        result.insert(item);
      }
    }
    return result;
  }
*/
  public SwiftSet<T> subtracting(SwiftSet<? extends T> other) {
    SwiftSet<T> result = new SwiftSet<>(storage);
    result.storage.removeAll(other.storage);
    return result;
  }
/*
  public SwiftSet<T> symmetricDifference(SwiftSet<? extends T> other) {
    SwiftSet<T> result = new SwiftSet<>();
    for (T item : storage) {
      if (!other.contains(item)) {
        result.insert(item);
      }
    }
    for (T item : other.storage) {
      if (!contains(item)) {
        result.insert(item);
      }
    }
    return result;
  }
*/
  // Set Comparisons

  public boolean isSupersetOf(SwiftSet<? extends T> other) {
    return storage.containsAll(other.storage);
  }

  public boolean isDisjointWidth(SwiftSet<? extends T> other) {
    return Collections.disjoint(storage, other.storage);
  }

  public boolean isEqualTo(SwiftSet<? extends T> other) {
    return storage.equals(other.storage);
  }

  // MARK: - Transformations

  public SwiftSet<T> filter(Predicate<T> predicate) {
    SwiftSet<T> result = new SwiftSet<>();
    for (T item : storage) {
      if (predicate.test(item)) {
        result.insert(item);
      }
    }
    return result;
  }

  public <R> SwiftSet<R> map(Function<T, R> mapper) {
    SwiftSet<R> result = new SwiftSet<>();
    for (T item : storage) {
      result.insert(mapper.apply(item));
    }
    return result;
  }

  public SwiftArray<T> sorted(Comparator<? super T> comparator) {
    SwiftArray<T> result = new SwiftArray<>();
    ArrayList<T> list = new ArrayList<>(storage);
    list.sort(comparator);
    for (T item : list) {
      result.append(item);
    }
    return result;
  }

  // MARK: - Adding elements

  public SwiftSet<T> adding(T item) {
    SwiftSet<T> result = new SwiftSet<>(storage);
    result.insert(item);
    return result;
  }

  // MARK: - Higher order

  public void forEach(java.util.function.Consumer<? super T> action) {
    storage.forEach(action);
  }

  public T reduce(T initial, java.util.function.BinaryOperator<T> accumulator) {
    T result = initial;
    for (T item : storage) {
      result = accumulator.apply(result, item);
    }
    return result;
  }

  // MARK: - Equality

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof SwiftSet)) return false;
    SwiftSet<?> other = (SwiftSet<?>) obj;
    return storage.equals(other.storage);
  }

  @Override
  public int hashCode() {
    return storage.hashCode();
  }

  // MARK: - Printable

  @Override
  public String toString() {
    return storage.toString();
  }

  // MARK: - Iterable

  @Override
  public Iterator<T> iterator() {
    return storage.iterator();
  }
}
