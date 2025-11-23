package chatapp.controllers.dashboard;

import java.net.URL;
import java.util.ResourceBundle;

import chatapp.dto.ReportView;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ReportController extends DashboardController {
    @FXML
    private TableView<ReportView> reportTable;

    @FXML
    private TableColumn<ReportView, String> contentCol;
    @FXML
    private TableColumn<ReportView, String> reporterCol;
    @FXML
    private TableColumn<ReportView, String> timeCol;
    @FXML
    private TableColumn<ReportView, String> reportedUserCol;

    @FXML
    private TextField filterField;
    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;

    private ObservableList<ReportView> reportList;
    private ObservableList<ReportView> reportFilterList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    void filterReport(ActionEvent event) {

    }

    @FXML
    void lockUser(ActionEvent event) {

    }

}
