package me.swift.engine.contract;

import java.util.HashMap;
import java.util.Map;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class SwiftDictionary<K, V> {

  private HashMap<K, V> storage;

  // Initializers

  public SwiftDictionary() {
    storage = new HashMap<>();
  }

  public SwiftDictionary(java.util.Map<? extends K, ? extends V> map) {
    storage = new HashMap<>(map);
  }

  // Properties

  public int count() {
    return storage.size();
  }

  public boolean isEmpty() {
    return storage.isEmpty();
  }

  public SwiftArray<K> keys() {
    SwiftArray<K> result = new SwiftArray<>();
    for (K key : storage.keySet()) {
      result.append(key);
    }
    return result;
  }

  public SwiftArray<V> values() {
    SwiftArray<V> result = new SwiftArray<>();
    for (V value : storage.values()) {
      result.append(value);
    }
    return result;
  }

  // Subscript

  public V get(K key) {
    return storage.get(key);
  }

  public void put(K key, V value) {
    storage.put(key, value);
  }

  public void remove(K key) {
    storage.remove(key);
  }

  public V getOrDefault(K key, V defaultValue) {
    return storage.getOrDefault(key, defaultValue);
  }

  // MARK: - Update
/*
  public V updateValue(V value, forKey K key) {
    return storage.put(key, value);
  }

  public V removeValue(forKey K key) {
    return storage.remove(key);
  }
*/
  public void removeAll() {
    storage.clear();
  }

  // Checking

  public boolean containsKey(K key) {
    return storage.containsKey(key);
  }

  public boolean containsValue(V value) {
    return storage.containsValue(value);
  }

  // Transformations

  public SwiftDictionary<K, V> filter(Predicate<? super Map.Entry<K, V>> predicate) {
    SwiftDictionary<K, V> result = new SwiftDictionary<>();
    for (Map.Entry<K, V> entry : storage.entrySet()) {
      if (predicate.test(entry)) {
        result.put(entry.getKey(), entry.getValue());
      }
    }
    return result;
  }

  public <R> SwiftDictionary<K, R> mapValues(Function<V, R> mapper) {
    SwiftDictionary<K, R> result = new SwiftDictionary<>();
    for (Map.Entry<K, V> entry : storage.entrySet()) {
      result.put(entry.getKey(), mapper.apply(entry.getValue()));
    }
    return result;
  }

  public SwiftDictionary<K, V> merging(SwiftDictionary<? extends K, ? extends V> other) {
    SwiftDictionary<K, V> result = new SwiftDictionary<>(storage);
    for (Map.Entry<? extends K, ? extends V> entry : other.storage.entrySet()) {
      result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }

  // Higher order

  public void forEach(BiConsumer<? super K, ? super V> action) {
    storage.forEach(action);
  }

  // MARK: - Default values
/*
  public V value(forKey K key, V defaultValue) {
    return storage.getOrDefault(key, defaultValue);
  }
*/
  // MARK: - Equality

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof SwiftDictionary)) return false;
    SwiftDictionary<?, ?> other = (SwiftDictionary<?, ?>) obj;
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

  // MARK: - Helper to get internal map (for advanced use)

  public java.util.Map<K, V> toJavaMap() {
    return new HashMap<>(storage);
  }
}
