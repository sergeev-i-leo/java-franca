package franca.java.ui.views.table;

import java.util.ArrayList;

import franca.java.ui.views.panel.PanelView;

public class TableView extends PanelView {

  public ArrayList<TableViewColumn> tableViewColumns = new ArrayList<>();
  public ArrayList<TableViewRow> tableViewRows = new ArrayList<>();

  public TableView() {
    super();
  }
}
