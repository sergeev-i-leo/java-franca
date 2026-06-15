package franca.java.data;

import franca.java.expected.ExpectedRuntime;
import franca.java.expected.StringBuffer;
import franca.java.expected.TranspilableClass;

public class Parser extends TranspilableClass {

  public String input = null;
  public int position = 0;

  public StringBuffer literalStringBuffer = null;
  public boolean booleanLiteral = false;
  public Integer integerLiteral = null;
  public Double doubleLiteral = null;

  public String parseLiteral() {
    if (input.startsWith("false", position)) {
      booleanLiteral = false;
      skipChars(5);
      literalStringBuffer = new StringBuffer();
      literalStringBuffer.appendString("false");
      return "boolean-literal";
    }
    if (input.startsWith("true", position)) {
      booleanLiteral = false;
      skipChars(4);
      literalStringBuffer = new StringBuffer();
      literalStringBuffer.appendString("true");
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
    String literal = literalStringBuffer.getString();
    integerLiteral = ExpectedRuntime.stringToInteger(literal);
    if (integerLiteral != null) {
      return "integer-literal";
    }
    integerLiteral = ExpectedRuntime.hexStringToInteger(literal);
    if (integerLiteral != null) {
      return "hex-integer-literal";
    }
    doubleLiteral = ExpectedRuntime.stringToDouble(literal);
    if (doubleLiteral != null) {
      return "double-literal";
    }
    return "error";
  }

  public void collectNumberLiteral() {
    literalStringBuffer = new StringBuffer();
    while (position < input.length()) {
      char c = input.charAt(position);
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
          literalStringBuffer.appendChar(c);
          break;
        default:
          return;
      }
      skipChars(1);
    }
  }

  public String parseStringLiteral() {
    if (input.charAt(position) != '"') {
      return null;
    }
    skipChars(1);
    while (position < input.length()) {
      char c = consumeChar();
      if (c == '"') {
        skipChars(1);
        return "string";
      }
      if (c == '\\') {
        if (position >= input.length()) {
          return "error";
        }
        c = consumeChar();
      }
      literalStringBuffer.appendChar(c);
    }
    return "error";
  }

  public char peekChar() {
    if (position < input.length()) {
      return input.charAt(position);
    }
    return '\u0000';
  }

  public char peekNextChar(int offset) {
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

  public char consumeChar() {
    if (position < input.length()) {
      char c = input.charAt(position);
      skipChars(1);
      return c;
    }
    return 0;
  }

  public void skipChars(int offset) {
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
