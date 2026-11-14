package franca.java.ui.views.table;

import franca.java.ui.views.View;

public class TableCellView extends View {

  public int column;
  public int columnsNumber;
  public View innerView;

  public String verticalAlignment = "top";

  public TableCellView(int column, int columnsNumber, View innerView) {
    this.column = column;
    this.columnsNumber = columnsNumber;
    this.innerView = innerView;
  }

}
