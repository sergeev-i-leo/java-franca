package franca.java.data.markdown;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.typography.CharsBlock;

public class FlavouredCharsBlock extends CharsBlock {

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
    if (chars.startsWith("/[")) {
      // transfer to ipa
      BufferedString bufferedString = new BufferedString();
      bufferedString.appendString("/ ");
      for (int i = 2; i < chars.length(); i++) {
        char c = chars.charAt(i);
        if (c == '\\') {
          continue;
        }
        switch (c) {
          case ']':
            bufferedString.appendString(" /");
            continue;
          case '-':
            bufferedString.appendString(" / ");
            continue;
          case '"':
            bufferedString.appendChar((char) 0x2C8);
            continue;
          case '\'':
            bufferedString.appendChar((char) 0x2CC);
            continue;
          case 'O':
            c = 'o';
            break;
          case 'T':
            c = (char) 216;
            break;
          case '&':
            if (i + 1 < chars.length()) {
              if (chars.charAt(i + 1) == 'a') {
                i++;
                c = (char) 230;
              }
            }
            break;
          case 'D':
            c = (char) 240;
            break;
          case 'N':
            c = (char) 331;
            break;
          case 'A':
            c = (char) 593;
            break;
          case '@':
            c = (char) 601;
            break;
          case '3':
            c = (char) 604;
            break;
          case 'I':
            c = (char) 618;
            break;
          case 'S':
            c = (char) 643;
            break;
          case 'U':
            c = (char) 650;
            break;
          case 'V':
            c = (char) 652;
            break;
          case 'Z':
            c = (char) 658;
            break;
          case 'Q':
            c = (char) 596;
            break;
        }
        bufferedString.appendChar(c);
      }
      jsonObject.putStringValue("chars", bufferedString.getString());
    }
  }
}
