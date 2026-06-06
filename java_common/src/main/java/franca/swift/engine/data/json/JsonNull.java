package franca.swift.engine.data.json;

public class JsonNull extends JsonElement {

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public String serialize() {
    return "null";
  }

  @Override
  public JsonNull asJsonNull() {
    return this;
  }

  @Override
  public String asString() {
    return "null";
  }
}
