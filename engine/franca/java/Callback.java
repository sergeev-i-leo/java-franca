package franca.java;

@FunctionalInterface

public interface Callback<T> {
  void onResult(T result);
}
