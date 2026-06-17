package franca.java.office.document.typography;

public class ParagraphBlock extends TextBlock {

  @Override
  public String getSerializationTag() {
    return "p";
  }

  @Override
  public String getDataBlock() {
    return "paragraph-block";
  }
}
