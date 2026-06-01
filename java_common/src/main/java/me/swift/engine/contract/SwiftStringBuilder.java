package me.swift.engine.contract;

public class SwiftStringBuilder {

  private StringBuilder stringBuilder = new StringBuilder();

  public void appendCharacter(char character) {
    stringBuilder.append(character);
  }

  public void appendString(String string) {
    stringBuilder.append(string);
  }

  public String getString() {
    return stringBuilder.toString();
  }
}
