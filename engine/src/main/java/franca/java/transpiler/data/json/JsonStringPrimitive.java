package franca.java.transpiler.data.json;

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
  public void serialize(StringBuilder stringBuilder) {
    String string = value;
    if (string == null) {
      string = "";
    }
    string = string.replace("\"", "\\\"");
    stringBuilder.append("\"" + string + "\"");
  }

  @Override
  public String getStringValue() {
    return value;
  }
}
