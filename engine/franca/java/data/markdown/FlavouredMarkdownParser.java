package franca.java.data.markdown;

import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;
import franca.java.office.document.typography.CharsBlock;

import java.util.ArrayList;

public class FlavouredMarkdownParser extends MarkdownParser {

  public String exportContentFolder = "samples/";
  public String exportAssetsFolder = "samples/";

  @Override
  void parseMarkdownBlock(Block parentBlock) {

    if (peekString("@exportContentFolder ")) {
      skipChars(14);
      exportContentFolder = consumeLine();
      return;
    }
    if (peekString("@exportAssetsFolder ")) {
      skipChars(13);
      exportAssetsFolder = consumeLine();
      return;
    }

    super.parseMarkdownBlock(parentBlock);
  }

  @Override
  public boolean parseMarkdownTextContentsStyle(int textInputPosition, ArrayList<JsonObject> styleJsonObjects) {
    if (peekChar() == '`') {
      return false;
    }
    return super.parseMarkdownTextContentsStyle(textInputPosition, styleJsonObjects);
  }

  @Override
  public CharsBlock createCharsBlock() {
    return new FlavouredCharsBlock();
  }
}
