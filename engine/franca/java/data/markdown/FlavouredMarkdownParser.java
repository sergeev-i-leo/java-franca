package franca.java.data.markdown;

import franca.java.office.document.Block;

public class FlavouredMarkdownParser extends MarkdownParser {

  public String exportFolder = "samples/";

  void parseMarkdownBlock(Block parentBlock) {

    if (peekString("@exportFolder ")) {
      skipChars(14);
      exportFolder = consumeLine();
      return;
    }

    super.parseMarkdownBlock(parentBlock);
  }

}
