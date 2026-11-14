package franca.java.ui.views.table;

import franca.java.ui.views.View;

public class TableCellView extends View {

  public View innerView;

  public String verticalAlignment = "top";

  public TableCellView(View innerView) {
    this.innerView = innerView;
  }

}
