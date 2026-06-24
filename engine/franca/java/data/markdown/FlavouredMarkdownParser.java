package franca.java.data.markdown;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;
import franca.java.office.document.typography.CharsBlock;

public class FlavouredMarkdownParser extends MarkdownParser {

  public String exportFolder = "samples/";

  @Override
  void parseMarkdownBlock(Block parentBlock) {

    if (peekString("@exportFolder ")) {
      skipChars(14);
      exportFolder = consumeLine();
      return;
    }

    super.parseMarkdownBlock(parentBlock);
  }

  @Override
  public CharsBlock createCharsBlock() {
    return new FlavouredCharsBlock();
  }
}
