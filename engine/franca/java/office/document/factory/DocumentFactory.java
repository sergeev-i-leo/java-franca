package franca.java.office.document.factory;

import franca.java.expected.BufferedString;
import franca.java.expected.TranspilableClass;
import franca.java.office.document.Block;
import franca.java.office.document.list.ListBlock;
import franca.java.office.document.list.ListItemBlock;
import franca.java.office.document.structure.HorizontalRuleBlock;
import franca.java.office.document.table.*;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.ParagraphBlock;

public class DocumentFactory extends TranspilableClass {

  public static Block createBlockByTagName(String tagName) {
    if (tagName.equals("h1")) {
      return new HeadingBlock(1);
    }
    if (tagName.equals("h2")) {
      return new HeadingBlock(2);
    }
    if (tagName.equals("h3")) {
      return new HeadingBlock(3);
    }
    if (tagName.equals("h4")) {
      return new HeadingBlock(4);
    }
    if (tagName.equals("h5")) {
      return new HeadingBlock(5);
    }
    if (tagName.equals("h6")) {
      return new HeadingBlock(6);
    }
    if (tagName.equals("p")) {
      return new ParagraphBlock();
    }
    if (tagName.equals("hr")) {
      return new HorizontalRuleBlock();
    }
    if (tagName.equals("ul")) {
      return new ListBlock(false);
    }
    if (tagName.equals("ol")) {
      return new ListBlock(true);
    }
    if (tagName.equals("li")) {
      return new ListItemBlock();
    }
    if (tagName.equals("table")) {
      return new TableBlock();
    }
    if (tagName.equals("thead")) {
      return new TableHeaderBlock();
    }
    if (tagName.equals("tbody")) {
      return new TableBodyBlock();
    }
    if (tagName.equals("tr")) {
      return new TableRowBlock();
    }
    if (tagName.equals("th")) {
      return new TableCellBlock(true);
    }
    if (tagName.equals("td")) {
      return new TableCellBlock(false);
    }
    return new Block();
  }

  public static boolean htmlTagIsSelfClosing(String tagName) {
    switch (tagName) {
      case "area":
      case "img":
      case "input":
      case "hr":
      case "link":
      case "meta":
        return true;
      default:
        return false;
    }
  }

  public static void serialize(Block block, BufferedString targetBufferedString) {
    targetBufferedString.appendString("<!DOCTYPE html>");
    targetBufferedString.finishLine();
    targetBufferedString.appendString("<html lang=\"en\">");
    targetBufferedString.finishLine();
    targetBufferedString.appendString("<head>");
    targetBufferedString.finishLine();
    targetBufferedString.appendString("<meta charset=\"UTF-8\">");
    targetBufferedString.finishLine();
    targetBufferedString.appendString("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
    targetBufferedString.finishLine();
    targetBufferedString.appendString("<style>");
    targetBufferedString.appendString("body{margin:0;padding:0;}table,th,td{border:1px solid black;}span[data-block=\"chars-block\"]{white-space: pre-wrap;}");
    targetBufferedString.appendString("</style>");
    targetBufferedString.finishLine();
    targetBufferedString.appendString("<title>http://localhost:8080</title>");
    targetBufferedString.finishLine();
    targetBufferedString.appendString("</head>");
    targetBufferedString.finishLine();
    targetBufferedString.appendString("<body>");
    targetBufferedString.finishLine();
    for (Block childBlock : block.getChildren()) {
      childBlock.serialize(targetBufferedString, 2);
    }
    targetBufferedString.appendString("<body>");
    targetBufferedString.finishLine();
    targetBufferedString.appendString("<html>");
    targetBufferedString.finishLine();
  }
}
