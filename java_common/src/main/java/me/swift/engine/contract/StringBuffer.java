package me.swift.engine.contract;

public class StringBuffer {

  private StringBuilder stringBuilder = new StringBuilder();

  public boolean isEmpty() {
    return stringBuilder.isEmpty();
  }

  public boolean isNotEmpty() {
    return !stringBuilder.isEmpty();
  }

  public void appendCharacter(char character) {
    stringBuilder.append(character);
  }

  public void appendString(String string) {
    stringBuilder.append(string);
  }

  public void appendLineEnd() {
    appendString("\r\n");
  }

  public String getString() {
    return stringBuilder.toString();
  }

  public String getLowerCaseString() {
    return stringBuilder.toString().toLowerCase();
  }
}
