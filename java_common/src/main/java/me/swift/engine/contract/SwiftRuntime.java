package me.swift.engine.contract;

public class SwiftRuntime {

  public static OptionalInt parseInt(String input) {
    if (input == null) {
      return null;
    }
    try {
      return new OptionalInt(Integer.parseInt(input));
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public static OptionalDouble parseDouble(String input) {
    if (input == null) {
      return null;
    }
    try {
      return new OptionalDouble(Double.parseDouble(input));
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public static String intToString(int value) {
    return Integer.toString(value);
  }

  public static String doubleToString(double value) {
    return Double.toString(value);
  }
}
