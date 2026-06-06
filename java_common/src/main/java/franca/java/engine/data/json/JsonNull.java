package franca.java.engine.data.json;

public class JsonNull extends JsonElement {

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public void serialize(StringBuilder stringBuilder) {
    stringBuilder.append("null");
  }

  @Override
  public JsonNull asJsonNull() {
    return this;
  }

  @Override
  public String getStringValue() {
    return "null";
  }
}
