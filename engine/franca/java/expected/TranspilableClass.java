package franca.java.expected;

public class TranspilableClass {

  private long id = 0L;
  private String dataName = null;

  public TranspilableClass() {
    id = Runtime.getId();
  }

  public long getId() {
    return id;
  }

  public void setDataName(String dataName) {
    this.dataName = dataName;
  }

  public String getDataName() {
    return dataName;
  }
}
