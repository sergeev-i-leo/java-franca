package franca.java.data.json;

import franca.java.expected.ExpectedRuntime;
import franca.java.expected.StringBuffer;

public class JsonIntPrimitive extends JsonPrimitive {

  private final int value;

  public JsonIntPrimitive(int value) {
    super();
    this.value = value;
  }

  public String getClassName() {
    return "JsonIntPrimitive";
  }

  @Override
  public void serialize(StringBuffer stringBuffer, Integer spacesBefore) {
    stringBuffer.appendString(ExpectedRuntime.intToString(value));
  }

  @Override
  public Integer getIntegerValue() {
    return value;
  }
}
