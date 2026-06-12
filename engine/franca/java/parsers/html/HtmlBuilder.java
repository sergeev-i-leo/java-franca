package franca.java.parsers.html;

import franca.java.expected.ExpectedStringBuilder;
import franca.java.expected.TranspilableClass;
import franca.java.parsers.json.JsonArray;
import franca.java.parsers.json.JsonElement;
import franca.java.parsers.json.JsonObject;

public class HtmlBuilder extends TranspilableClass {

  public String build(JsonElement jsonElement) {
    ExpectedStringBuilder expectedStringBuilder = new ExpectedStringBuilder();
    buildFromJsonElement(jsonElement, expectedStringBuilder);
    String string = expectedStringBuilder.getString();
    delete(expectedStringBuilder);
    return string;
  }

  private void buildFromJsonElement(JsonElement jsonElement, ExpectedStringBuilder expectedStringBuilder) {
    JsonArray jsonArray = jsonElement.asJsonArray();
    JsonObject jsonObject = jsonElement.asJsonObject();
    if (jsonArray != null) {
      for (int i = 0; i < jsonArray.size(); i++) {
        jsonElement = jsonArray.get(i);
        buildFromJsonElement(jsonElement, expectedStringBuilder);
      }
    } else if (jsonObject != null) {
      buildFromJsonObject(jsonObject, expectedStringBuilder);
      jsonArray = jsonObject.getJsonArray("views");
      if (jsonArray != null) {
        buildFromJsonElement(jsonArray, expectedStringBuilder);
      }
    }
  }

  private void buildFromJsonObject(JsonObject jsonObject, ExpectedStringBuilder expectedStringBuilder) {
    String className = jsonObject.getStringValue("className");
    if (className != null) {
      switch (className) {
        case "image-view":
          expectedStringBuilder.appendString("<img ");
          buildAttributes(jsonObject, expectedStringBuilder);
          expectedStringBuilder.appendString("/>");
          return;
        case "table-view":
          expectedStringBuilder.appendString("<table ");
          buildAttributes(jsonObject, expectedStringBuilder);
          expectedStringBuilder.appendString("><tbody>");
          buildFromJsonElement(jsonObject.getJsonArray("views"), expectedStringBuilder);
          expectedStringBuilder.appendString("</tbody></table>");
          return;
        case "table-row-view":
          expectedStringBuilder.appendString("<tr ");
          buildAttributes(jsonObject, expectedStringBuilder);
          buildFromJsonElement(jsonObject.getJsonArray("views"), expectedStringBuilder);
          expectedStringBuilder.appendString("</tr>");
          return;
        case "table-header-cell-view":
          expectedStringBuilder.appendString("<th ");
          buildAttributes(jsonObject, expectedStringBuilder);
          buildFromJsonElement(jsonObject.getJsonArray("views"), expectedStringBuilder);
          expectedStringBuilder.appendString("</th>");
          return;
        case "table-cell-view":
          expectedStringBuilder.appendString("<td ");
          buildAttributes(jsonObject, expectedStringBuilder);
          buildFromJsonElement(jsonObject.getJsonArray("views"), expectedStringBuilder);
          expectedStringBuilder.appendString("</td>");
          return;
        case "typography-h1-view":
          buildTextElements(jsonObject, "h1", expectedStringBuilder);
          return;
        case "typography-h2-view":
          buildTextElements(jsonObject, "h2", expectedStringBuilder);
          return;
        case "typography-h3-view":
          buildTextElements(jsonObject, "h3", expectedStringBuilder);
          return;
        case "typography-h4-view":
          buildTextElements(jsonObject, "h4", expectedStringBuilder);
          return;
        case "typography-h5-view":
          buildTextElements(jsonObject, "h5", expectedStringBuilder);
          return;
        case "typography-h6-view":
          buildTextElements(jsonObject, "h6", expectedStringBuilder);
          return;
        case "typography-paragraph-view":
          buildTextElements(jsonObject, "p", expectedStringBuilder);
          return;
      }
    }
    String tagName = jsonObject.getStringValue("tagName");
    if (tagName == null) {
      return;
    }
    expectedStringBuilder.appendString("<");
    expectedStringBuilder.appendString(tagName);
    buildAttributes(jsonObject, expectedStringBuilder);
    expectedStringBuilder.appendString(">");
    buildFromJsonElement(jsonObject.getJsonArray("views"), expectedStringBuilder);
    expectedStringBuilder.appendString("</");
    expectedStringBuilder.appendString(tagName);
    expectedStringBuilder.appendString(">");
  }

  private void buildAttributes(JsonObject jsonObject, ExpectedStringBuilder expectedStringBuilder) {

  }

  private void buildTextElements(JsonObject jsonObject, String tagName, ExpectedStringBuilder expectedStringBuilder) {
    expectedStringBuilder.appendString("<");
    expectedStringBuilder.appendString(tagName);
    expectedStringBuilder.appendString(" ");
    buildAttributes(jsonObject, expectedStringBuilder);
    expectedStringBuilder.appendString(">");
    String text = jsonObject.getStringValue("text");
    if (text != null) {
      expectedStringBuilder.appendString(text);
    }
    expectedStringBuilder.appendString("</");
    expectedStringBuilder.appendString(tagName);
    expectedStringBuilder.appendString(">");
  }

}
