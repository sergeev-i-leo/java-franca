package franca.java.office.document.table;

import franca.java.office.document.Block;

import java.util.ArrayList;

public class TableBlock extends Block {

  public ArrayList<TableColumn> tableColumns = new ArrayList<>();

  @Override
  public String getSerializationTag() {
    return "table";
  }

  @Override
  public String getDataBlock() {
    return "table-block";
  }
}
