package franca.java.office.document.table;

import franca.java.data.json.JsonArray;
import franca.java.data.json.JsonObject;
import franca.java.office.document.Block;

import java.util.ArrayList;

public class TableBlock extends Block {

  public ArrayList<TableColumn> tableColumns = new ArrayList<>();

  @Override
  public String getSerializationTag() {
    return "table";
  }

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
    if (!tableColumns.isEmpty()) {
      JsonArray tableColumnsJsonArray = new JsonArray();
      jsonObject.put("tableColumns", tableColumnsJsonArray);
      for (TableColumn tableColumn : tableColumns) {
        tableColumnsJsonArray.add(tableColumn.createJsonObject());
      }
    }
  }

  @Override
  public String getDataBlock() {
    return "TableBlock";
  }
}
