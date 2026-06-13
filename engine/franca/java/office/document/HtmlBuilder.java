package franca.java.office.document;

import franca.java.expected.StringBuffer;
import franca.java.expected.TranspilableClass;
import franca.java.parsers.json.JsonArray;
import franca.java.parsers.json.JsonElement;
import franca.java.parsers.json.JsonObject;

public class HtmlBuilder extends TranspilableClass {

  public String build(JsonElement jsonElement) {
    StringBuffer stringBuffer = new StringBuffer();
    buildFromJsonElement(jsonElement, stringBuffer);
    String string = copyOf(stringBuffer.getString());
    delete(stringBuffer);
    return string;
  }

  private void buildFromJsonElement(JsonElement jsonElement, StringBuffer stringBuffer) {
    JsonArray jsonArray = jsonElement.asJsonArray();
    JsonObject jsonObject = jsonElement.asJsonObject();
    if (jsonArray != null) {
      for (int i = 0; i < jsonArray.size(); i++) {
        jsonElement = jsonArray.get(i);
        buildFromJsonElement(jsonElement, stringBuffer);
      }
    } else if (jsonObject != null) {
      buildFromJsonObject(jsonObject, stringBuffer);
      jsonArray = jsonObject.getJsonArray("views");
      if (jsonArray != null) {
        buildFromJsonElement(jsonArray, stringBuffer);
      }
    }
  }

  private void buildFromJsonObject(JsonObject jsonObject, StringBuffer stringBuffer) {
    String className = jsonObject.getStringValue("className");
    if (className != null) {
      switch (className) {
        case "image-view":
          stringBuffer.appendString("<img ");
          buildAttributes(jsonObject, stringBuffer);
          stringBuffer.appendString("/>");
          return;
        case "table-view":
          stringBuffer.appendString("<table ");
          buildAttributes(jsonObject, stringBuffer);
          stringBuffer.appendString("><tbody>");
          buildFromJsonElement(jsonObject.getJsonArray("views"), stringBuffer);
          stringBuffer.appendString("</tbody></table>");
          return;
        case "table-row-view":
          stringBuffer.appendString("<tr ");
          buildAttributes(jsonObject, stringBuffer);
          buildFromJsonElement(jsonObject.getJsonArray("views"), stringBuffer);
          stringBuffer.appendString("</tr>");
          return;
        case "table-header-cell-view":
          stringBuffer.appendString("<th ");
          buildAttributes(jsonObject, stringBuffer);
          buildFromJsonElement(jsonObject.getJsonArray("views"), stringBuffer);
          stringBuffer.appendString("</th>");
          return;
        case "table-cell-view":
          stringBuffer.appendString("<td ");
          buildAttributes(jsonObject, stringBuffer);
          buildFromJsonElement(jsonObject.getJsonArray("views"), stringBuffer);
          stringBuffer.appendString("</td>");
          return;
        case "typography-h1-view":
          buildTextElements(jsonObject, "h1", stringBuffer);
          return;
        case "typography-h2-view":
          buildTextElements(jsonObject, "h2", stringBuffer);
          return;
        case "typography-h3-view":
          buildTextElements(jsonObject, "h3", stringBuffer);
          return;
        case "typography-h4-view":
          buildTextElements(jsonObject, "h4", stringBuffer);
          return;
        case "typography-h5-view":
          buildTextElements(jsonObject, "h5", stringBuffer);
          return;
        case "typography-h6-view":
          buildTextElements(jsonObject, "h6", stringBuffer);
          return;
        case "typography-paragraph-view":
          buildTextElements(jsonObject, "p", stringBuffer);
          return;
      }
    }
    String tagName = jsonObject.getStringValue("tagName");
    if (tagName == null) {
      return;
    }
    stringBuffer.appendString("<");
    stringBuffer.appendString(tagName);
    buildAttributes(jsonObject, stringBuffer);
    stringBuffer.appendString(">");
    buildFromJsonElement(jsonObject.getJsonArray("views"), stringBuffer);
    stringBuffer.appendString("</");
    stringBuffer.appendString(tagName);
    stringBuffer.appendString(">");
  }

  private void buildAttributes(JsonObject jsonObject, StringBuffer stringBuffer) {

  }

  private void buildTextElements(JsonObject jsonObject, String tagName, StringBuffer stringBuffer) {
    stringBuffer.appendString("<");
    stringBuffer.appendString(tagName);
    stringBuffer.appendString(" ");
    buildAttributes(jsonObject, stringBuffer);
    stringBuffer.appendString(">");
    String text = jsonObject.getStringValue("text");
    if (text != null) {
      stringBuffer.appendString(text);
    }
    stringBuffer.appendString("</");
    stringBuffer.appendString(tagName);
    stringBuffer.appendString(">");
  }

}
