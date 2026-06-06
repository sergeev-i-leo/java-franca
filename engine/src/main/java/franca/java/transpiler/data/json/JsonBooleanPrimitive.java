package franca.java.transpiler.data.json;

public class JsonBooleanPrimitive extends JsonPrimitive {

  private boolean value;

  public JsonBooleanPrimitive(boolean value) {
    this.value = value;
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public void serialize(StringBuilder stringBuilder) {
    stringBuilder.append(value ? "true" : "false");
  }

  @Override
  public Boolean getBooleanValue() {
    return value;
  }
}
