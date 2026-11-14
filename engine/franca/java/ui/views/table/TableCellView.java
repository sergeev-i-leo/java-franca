package franca.java.ui.views.table;

import franca.java.ui.views.GroupView;

public class TableCellView extends GroupView {

  public int column;
  public int columnsNumber;

  public TableCellView(int column, int columnsNumber) {
    super();
    this.column = column;
    this.columnsNumber = columnsNumber;
  }

}
