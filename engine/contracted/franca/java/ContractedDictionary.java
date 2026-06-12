package contracted.franca.java;

import java.util.HashMap;

public class ContractedDictionary<K, V> {

  private HashMap<K, V> storage;

  public ContractedDictionary() {
    storage = new HashMap<>();
  }

  public int size() {
    return storage.size();
  }

  public boolean isEmpty() {
    return storage.isEmpty();
  }

  public ContractedArray<K> keys() {
    ContractedArray<K> result = new ContractedArray<>();
    for (K key : storage.keySet()) {
      result.add(key);
    }
    return result;
  }

  public ContractedArray<V> values() {
    ContractedArray<V> result = new ContractedArray<>();
    for (V value : storage.values()) {
      result.add(value);
    }
    return result;
  }

  public V get(K key) {
    return storage.get(key);
  }

  public void put(K key, V value) {
    storage.put(key, value);
  }

  public void remove(K key) {
    storage.remove(key);
  }

  public void clear() {
    storage.clear();
  }

  @Override
  public String toString() {
    return storage.toString();
  }
}
