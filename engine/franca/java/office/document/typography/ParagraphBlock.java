package franca.java.office.document.typography;

public class ParagraphBlock extends TextBlock {

  @Override
  public String getHtmlTag() {
    return "p";
  }

  @Override
  public String getDataBlock() {
    return "ParagraphBlock";
  }
}
