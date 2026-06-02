package me.swift.engine.data;

import me.swift.engine.contract.TranspilableClass;

public class Parser extends TranspilableClass {

  public String input = null;
  public int position = 0;

  @Override
  public void destroy() {
    if (input != null) {
      delete(input);
      input = null;
    }
    super.destroy();
  }

  public double parseDouble() {

    while (position < input.length()) {
      char c = input.charAt(position);
      if ((c == ' ') || (c == ',') || (c == '\t') || (c == '\n') || (c == '\r')) {
        position++;
      } else {
        break;
      }
    }

    if (position >= input.length()) {
      return 0.0;
    }

    double sign = 1.0;
    if (input.charAt(position) == '-') {
      sign = -1.0;
      position++;
    } else if (input.charAt(position) == '+') {
      position++;
    }

    double result = 0.0;
    while (position < input.length()) {
      char c = input.charAt(position);
      if ((c < '0') || (c > '9')) {
        break;
      }
      result = result * 10.0 + (c - '0');
      position++;
    }

    if ((position < input.length()) && (input.charAt(position) == '.')) {
      position++;
      double fraction = 0.0;
      double divisor = 1.0;
      while (position < input.length()) {
        char c = input.charAt(position);
        if ((c < '0') || (c > '9')) {
          break;
        }
        fraction = fraction * 10.0 + (c - '0');
        divisor *= 10.0;
        position++;
      }
      result = result + fraction / divisor;
    }

    if (position >= input.length()) {
      return sign * result;
    }
    if ((input.charAt(position) != 'e') && (input.charAt(position) != 'E')) {
      return sign * result;
    }

    position++;
    double exponentSign = 1.0;
    if ((position < input.length()) && (input.charAt(position) == '-')) {
      exponentSign = -1.0;
      position++;
    } else if ((position < input.length()) && (input.charAt(position) == '+')) {
      position++;
    }

    double exponent = 0.0;
    while (position < input.length()) {
      char c = input.charAt(position);
      if ((c < '0') || (c > '9')) {
        break;
      }
      exponent = exponent * 10.0 + (c - '0');
      position++;
    }

    double exponentResult = 1.0;
    for (int i = 0; i < exponent; i++) {
      exponentResult *= 10.0;
    }
    if (exponentSign < 0) {
      result = result / exponentResult;
    } else {
      result = result * exponentResult;
    }

    return sign * result;
  }

  public char peekCharacter() {
    if (position < input.length()) {
      return input.charAt(position);
    }
    return '\u0000';
  }

  public char peekNextCharacter(int offset) {
    if (position + offset < input.length()) {
      return input.charAt(position + offset);
    }
    return '\u0000';
  }

  public boolean peekString(String string) {
    for (int i = 0; i < string.length(); i++) {
      if (position + i >= input.length()) {
        return false;
      }
      if (input.charAt(position + i) != string.charAt(i)) {
        return false;
      }
    }
    return true;
  }

  public char consumeCharacter() {
    if (position < input.length()) {
      return input.charAt(position++);
    }
    return '\u0000';
  }

  public void skipCharacters(int offset) {
    position += offset;
  }

  public void skipWhitespaces() {
    while (position < input.length()) {
      char c = input.charAt(position);
      if (isWhitespace(c)) {
        position++;
      } else {
        break;
      }
    }
  }

  public boolean isWhitespace(char c) {
    return (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r') || (c == '\f');
  }
}
