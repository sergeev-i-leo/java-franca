package franca.swift.engine.data.json;

public class JsonBooleanPrimitive extends JsonPrimitive {

  private boolean value;

  public JsonBooleanPrimitive(boolean value) {
    this.value = value;
  }

  @Override
  public String serialize() {
    return value ? "true" : "false";
  }

  @Override
  public JsonBooleanPrimitive asJsonBooleanPrimitive() {
    return this;
  }

  public Boolean asBoolean() {
    return value;
  }

  @Override
  public String asString() {
    return value ? "true" : "false";
  }
}
