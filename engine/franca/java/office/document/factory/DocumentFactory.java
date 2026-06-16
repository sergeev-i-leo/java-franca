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

import java.util.ArrayList;

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
    targetBufferedString.appendEndLine();
    targetBufferedString.appendString("<html lang=\"en\">");
    targetBufferedString.appendEndLine();
    targetBufferedString.appendString("<head>");
    targetBufferedString.appendEndLine();
    targetBufferedString.appendString("  <meta charset=\"UTF-8\">");
    targetBufferedString.appendEndLine();
    targetBufferedString.appendString("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
    targetBufferedString.appendEndLine();
    targetBufferedString.appendString("  <title>Java Franca Document</title>");
    targetBufferedString.appendEndLine();
    targetBufferedString.appendString("  <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\">");
    targetBufferedString.appendEndLine();
    targetBufferedString.appendString("  <style>[data-class-name]{position:relative;}[data-class-name]:hover{outline:1px solid #66afe9;}[data-class-name]:hover::before{content:attr(data-class-name)\" : \"attr(data-type);position:absolute;top:-20px;left:0;font:11px monospace;padding:2px 5px;border:1px solid #66afe9;border-radius:3px;background:#fff;white-space:nowrap;z-index:1000;pointer-events:none;}</style>");
    targetBufferedString.appendEndLine();
    targetBufferedString.appendString("</head>");
    targetBufferedString.appendEndLine();
    targetBufferedString.appendString("<body class=\"bg-body text-body\">");
    targetBufferedString.appendEndLine();
    for (Block childBlock : block.getBlocks()) {
      childBlock.serialize(targetBufferedString, 2);
    }
    targetBufferedString.appendString("<body>");
    targetBufferedString.appendEndLine();
    targetBufferedString.appendString("<html>");
    targetBufferedString.appendEndLine();
  }
}
