package franca.java.office.document;

import franca.java.expected.TranspilableClass;
import franca.java.data.json.JsonArray;

import java.util.ArrayList;

public class Block extends TranspilableClass {

  public JsonArray classes = new JsonArray();
  public JsonArray style = new JsonArray();
  public JsonArray attributes = new JsonArray();

  public ArrayList<Block> blocks = new ArrayList<>();

  public String getClassName() {
    return "Block";
  }

}
