package franca.java.data;

import franca.java.expected.Runtime;
import franca.java.expected.BufferedString;
import franca.java.expected.TranspilableClass;

public class Parser extends TranspilableClass {

  public String input = null;
  public int inputPosition = 0;

  public BufferedString literalBufferedString = null;
  public boolean booleanLiteral = false;
  public Integer integerLiteral = null;
  public Double doubleLiteral = null;

  public String parseLiteral() {
    if (input.startsWith("false", inputPosition)) {
      booleanLiteral = false;
      skipChars(5);
      literalBufferedString = new BufferedString();
      literalBufferedString.appendString("false");
      return "boolean-literal";
    }
    if (input.startsWith("true", inputPosition)) {
      booleanLiteral = false;
      skipChars(4);
      literalBufferedString = new BufferedString();
      literalBufferedString.appendString("true");
      return "boolean-literal";
    }
    String result = parseNumberLiteral();
    if (result != null) {
      return result;
    }
    return parseStringLiteral();
  }

  public String parseNumberLiteral() {
    switch (peekChar()) {
      case '-':
      case '+':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        break;
      default:
        return null;
    }
    collectNumberLiteral();
    String literal = literalBufferedString.getString();
    integerLiteral = Runtime.stringToInteger(literal);
    if (integerLiteral != null) {
      return "integer-literal";
    }
    integerLiteral = Runtime.hexStringToInteger(literal);
    if (integerLiteral != null) {
      return "hex-integer-literal";
    }
    doubleLiteral = Runtime.stringToDouble(literal);
    if (doubleLiteral != null) {
      return "double-literal";
    }
    return "error";
  }

  public void collectNumberLiteral() {
    literalBufferedString = new BufferedString();
    while (inputPosition < input.length()) {
      char c = input.charAt(inputPosition);
      switch (c) {
        case '-':
        case '+':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case '.':
        case 'E':
        case 'e':
        case 'X':
        case 'x':
          literalBufferedString.appendChar(c);
          break;
        default:
          return;
      }
      skipChars(1);
    }
  }

  public String parseStringLiteral() {
    if (input.charAt(inputPosition) != '"') {
      return null;
    }
    skipChars(1);
    while (inputPosition < input.length()) {
      char c = consumeChar();
      if (c == '"') {
        skipChars(1);
        return "string";
      }
      if (c == '\\') {
        if (inputPosition >= input.length()) {
          return "error";
        }
        c = consumeChar();
      }
      literalBufferedString.appendChar(c);
    }
    return "error";
  }

  public char peekChar() {
    if (inputPosition < input.length()) {
      return input.charAt(inputPosition);
    }
    return '\u0000';
  }

  public char peekNextChar(int offset) {
    if (inputPosition + offset < input.length()) {
      return input.charAt(inputPosition + offset);
    }
    return '\u0000';
  }

  public boolean peekString(String string) {
    for (int i = 0; i < string.length(); i++) {
      char c0 = peekNextChar(i);
      char c1 = string.charAt(i);
      if (c0 != c1) {
        return false;
      }
    }
    return true;
  }

  public boolean peekLineEnd() {
    return (peekChar() == '\r') || (peekChar() == '\n');
  }

  public char consumeChar() {
    if (inputPosition < input.length()) {
      char c = input.charAt(inputPosition);
      skipChars(1);
      return c;
    }
    return 0;
  }

  public String consumeLine() {
    // consumes line end
    literalBufferedString = new BufferedString();
    while (inputPosition < input.length()) {
      if (peekLineEnd()) {
        skipLineEnd();
        break;
      }
      literalBufferedString.appendChar(consumeChar());
    }
    return literalBufferedString.getString();
  }

  public void skipChars(int offset) {
    inputPosition += offset;
  }

  public void skipWhitespaces() {
    while (inputPosition < input.length()) {
      char c = input.charAt(inputPosition);
      if (isWhitespace(c)) {
        inputPosition++;
      } else {
        break;
      }
    }
  }

  public boolean isWhitespace(char c) {
    return (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r') || (c == '\f');
  }

  public void skipLineEnd() {
    if (peekChar() == '\r') {
      skipChars(1);
    }
    if (peekChar() == '\n') {
      skipChars(1);
    }
  }

  public void skipLine() {
    while (inputPosition < input.length()) {
      if (peekLineEnd()) {
        skipLineEnd();
        break;
      }
      skipChars(1);
    }
  }
}
