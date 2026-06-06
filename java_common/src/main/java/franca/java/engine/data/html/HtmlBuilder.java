package franca.java.engine.data.html;

import franca.java.engine.contracted.ContractedStringBuffer;
import franca.java.engine.contracted.TranspilableClass;
import franca.java.engine.data.json.JsonArray;
import franca.java.engine.data.json.JsonElement;
import franca.java.engine.data.json.JsonObject;

public class HtmlBuilder extends TranspilableClass {

  public String build(JsonElement jsonElement) {
    ContractedStringBuffer contractedStringBuffer = new ContractedStringBuffer();
    buildFromJsonElement(jsonElement, contractedStringBuffer);
    String string = contractedStringBuffer.getString();
    delete(contractedStringBuffer);
    return string;
  }

  private void buildFromJsonElement(JsonElement jsonElement, ContractedStringBuffer contractedStringBuffer) {
    JsonArray jsonArray = jsonElement.asJsonArray();
    JsonObject jsonObject = jsonElement.asJsonObject();
    if (jsonArray != null) {
      for (int i = 0; i < jsonArray.size(); i++) {
        jsonElement = jsonArray.get(i);
        buildFromJsonElement(jsonElement, contractedStringBuffer);
      }
    } else if (jsonObject != null) {
      buildFromJsonObject(jsonObject, contractedStringBuffer);
      jsonArray = jsonObject.getJsonArray("views");
      if (jsonArray != null) {
        buildFromJsonElement(jsonArray, contractedStringBuffer);
      }
    }
  }

  private void buildFromJsonObject(JsonObject jsonObject, ContractedStringBuffer contractedStringBuffer) {
    String className = jsonObject.getStringValue("className");
    if (className != null) {
      switch (className) {
        case "image-view":
          contractedStringBuffer.appendString("<img ");
          buildAttributes(jsonObject, contractedStringBuffer);
          contractedStringBuffer.appendString("/>");
          return;
        case "table-view":
          contractedStringBuffer.appendString("<table ");
          buildAttributes(jsonObject, contractedStringBuffer);
          contractedStringBuffer.appendString("><tbody>");
          buildFromJsonElement(jsonObject.getJsonArray("views"), contractedStringBuffer);
          contractedStringBuffer.appendString("</tbody></table>");
          return;
        case "table-row-view":
          contractedStringBuffer.appendString("<tr ");
          buildAttributes(jsonObject, contractedStringBuffer);
          buildFromJsonElement(jsonObject.getJsonArray("views"), contractedStringBuffer);
          contractedStringBuffer.appendString("</tr>");
          return;
        case "table-header-cell-view":
          contractedStringBuffer.appendString("<th ");
          buildAttributes(jsonObject, contractedStringBuffer);
          buildFromJsonElement(jsonObject.getJsonArray("views"), contractedStringBuffer);
          contractedStringBuffer.appendString("</th>");
          return;
        case "table-cell-view":
          contractedStringBuffer.appendString("<td ");
          buildAttributes(jsonObject, contractedStringBuffer);
          buildFromJsonElement(jsonObject.getJsonArray("views"), contractedStringBuffer);
          contractedStringBuffer.appendString("</td>");
          return;
        case "typography-h1-view":
          buildTextElements(jsonObject, "h1", contractedStringBuffer);
          return;
        case "typography-h2-view":
          buildTextElements(jsonObject, "h2", contractedStringBuffer);
          return;
        case "typography-h3-view":
          buildTextElements(jsonObject, "h3", contractedStringBuffer);
          return;
        case "typography-h4-view":
          buildTextElements(jsonObject, "h4", contractedStringBuffer);
          return;
        case "typography-h5-view":
          buildTextElements(jsonObject, "h5", contractedStringBuffer);
          return;
        case "typography-h6-view":
          buildTextElements(jsonObject, "h6", contractedStringBuffer);
          return;
        case "typography-paragraph-view":
          buildTextElements(jsonObject, "p", contractedStringBuffer);
          return;
      }
    }
    String tagName = jsonObject.getStringValue("tagName");
    if (tagName == null) {
      return;
    }
    contractedStringBuffer.appendString("<");
    contractedStringBuffer.appendString(tagName);
    buildAttributes(jsonObject, contractedStringBuffer);
    contractedStringBuffer.appendString(">");
    buildFromJsonElement(jsonObject.getJsonArray("views"), contractedStringBuffer);
    contractedStringBuffer.appendString("</");
    contractedStringBuffer.appendString(tagName);
    contractedStringBuffer.appendString(">");
  }

  private void buildAttributes(JsonObject jsonObject, ContractedStringBuffer contractedStringBuffer) {

  }

  private void buildTextElements(JsonObject jsonObject, String tagName, ContractedStringBuffer contractedStringBuffer) {
    contractedStringBuffer.appendString("<");
    contractedStringBuffer.appendString(tagName);
    contractedStringBuffer.appendString(" ");
    buildAttributes(jsonObject, contractedStringBuffer);
    contractedStringBuffer.appendString(">");
    String text = jsonObject.getStringValue("text");
    if (text != null) {
      contractedStringBuffer.appendString(text);
    }
    contractedStringBuffer.appendString("</");
    contractedStringBuffer.appendString(tagName);
    contractedStringBuffer.appendString(">");
  }

}
