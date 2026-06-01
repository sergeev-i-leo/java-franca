package me.swift.engine.data.html;

import me.swift.engine.contract.SwiftStringBuilder;
import me.swift.engine.contract.TranspilableClass;
import me.swift.engine.data.json.JsonArray;
import me.swift.engine.data.json.JsonElement;
import me.swift.engine.data.json.JsonObject;

public class HtmlBuilder extends TranspilableClass {

  public String build(JsonElement jsonElement) {
    SwiftStringBuilder swiftStringBuilder = new SwiftStringBuilder();
    buildFromJsonElement(jsonElement, swiftStringBuilder);
    String string = swiftStringBuilder.getString();
    delete(swiftStringBuilder);
    return string;
  }

  private void buildFromJsonElement(JsonElement jsonElement, SwiftStringBuilder swiftStringBuilder) {
    JsonArray jsonArray = jsonElement.getAsJsonArray();
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    if (jsonArray != null) {
      for (int i = 0; i < jsonArray.size(); i++) {
        jsonElement = jsonArray.getElement(i);
        buildFromJsonElement(jsonElement, swiftStringBuilder);
      }
    } else if (jsonObject != null) {
      buildFromJsonObject(jsonObject, swiftStringBuilder);
      jsonArray = jsonObject.getJsonArrayMember("views");
      if (jsonArray != null) {
        buildFromJsonElement(jsonArray, swiftStringBuilder);
      }
    }
  }

  private void buildFromJsonObject(JsonObject jsonObject, SwiftStringBuilder swiftStringBuilder) {
    String className = jsonObject.getStringMember("className");
    if (className != null) {
      switch (className) {
        case "image-view":
          swiftStringBuilder.appendString("<img ");
          buildAttributes(jsonObject, swiftStringBuilder);
          swiftStringBuilder.appendString("/>");
          return;
        case "table-view":
          swiftStringBuilder.appendString("<table ");
          buildAttributes(jsonObject, swiftStringBuilder);
          swiftStringBuilder.appendString("><tbody>");
          buildFromJsonElement(jsonObject.getJsonArrayMember("views"), swiftStringBuilder);
          swiftStringBuilder.appendString("</tbody></table>");
          return;
        case "table-row-view":
          swiftStringBuilder.appendString("<tr ");
          buildAttributes(jsonObject, swiftStringBuilder);
          buildFromJsonElement(jsonObject.getJsonArrayMember("views"), swiftStringBuilder);
          swiftStringBuilder.appendString("</tr>");
          return;
        case "table-header-cell-view":
          swiftStringBuilder.appendString("<th ");
          buildAttributes(jsonObject, swiftStringBuilder);
          buildFromJsonElement(jsonObject.getJsonArrayMember("views"), swiftStringBuilder);
          swiftStringBuilder.appendString("</th>");
          return;
        case "table-cell-view":
          swiftStringBuilder.appendString("<td ");
          buildAttributes(jsonObject, swiftStringBuilder);
          buildFromJsonElement(jsonObject.getJsonArrayMember("views"), swiftStringBuilder);
          swiftStringBuilder.appendString("</td>");
          return;
        case "typography-h1-view":
          buildTextElements(jsonObject, "h1", swiftStringBuilder);
          return;
        case "typography-h2-view":
          buildTextElements(jsonObject, "h2", swiftStringBuilder);
          return;
        case "typography-h3-view":
          buildTextElements(jsonObject, "h3", swiftStringBuilder);
          return;
        case "typography-h4-view":
          buildTextElements(jsonObject, "h4", swiftStringBuilder);
          return;
        case "typography-h5-view":
          buildTextElements(jsonObject, "h5", swiftStringBuilder);
          return;
        case "typography-h6-view":
          buildTextElements(jsonObject, "h6", swiftStringBuilder);
          return;
        case "typography-paragraph-view":
          buildTextElements(jsonObject, "p", swiftStringBuilder);
          return;
      }
    }
    String tagName = jsonObject.getStringMember("tagName");
    if (tagName == null) {
      return;
    }
    swiftStringBuilder.appendString("<");
    swiftStringBuilder.appendString(tagName);
    buildAttributes(jsonObject, swiftStringBuilder);
    swiftStringBuilder.appendString(">");
    buildFromJsonElement(jsonObject.getJsonArrayMember("views"), swiftStringBuilder);
    swiftStringBuilder.appendString("</");
    swiftStringBuilder.appendString(tagName);
    swiftStringBuilder.appendString(">");
  }

  private void buildAttributes(JsonObject jsonObject, SwiftStringBuilder swiftStringBuilder) {

  }

  private void buildTextElements(JsonObject jsonObject, String tagName, SwiftStringBuilder swiftStringBuilder) {
    swiftStringBuilder.appendString("<");
    swiftStringBuilder.appendString(tagName);
    swiftStringBuilder.appendString(" ");
    buildAttributes(jsonObject, swiftStringBuilder);
    swiftStringBuilder.appendString(">");
    String text = jsonObject.getStringMember("text");
    if (text != null) {
      swiftStringBuilder.appendString(text);
    }
    swiftStringBuilder.appendString("</");
    swiftStringBuilder.appendString(tagName);
    swiftStringBuilder.appendString(">");
  }

}
