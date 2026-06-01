package me.swift.engine.data.json;

public class JsonStringPrimitive extends JsonPrimitive {

  private String value;

  public JsonStringPrimitive(String value) {
    this.value = value;
  }

  @Override
  public void destroy() {
    delete(value);
    value = null;
    super.destroy();
  }

  @Override
  public String serialize() {
    String string = value;
    if (string == null) {
      string = "";
    }
    string = string.replace("\"", "\\\"");
    return "\"" + string + "\"";
  }

  @Override
  public boolean isJsonStringPrimitive() {
    return true;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getAsString() {
    return value;
  }
}
