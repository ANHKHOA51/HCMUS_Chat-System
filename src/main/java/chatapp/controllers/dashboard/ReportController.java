package chatapp.controllers.dashboard;

import java.net.URL;
import java.util.ResourceBundle;

import chatapp.dto.ReportView;
import chatapp.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

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
        // reportList = FXCollections.observableArrayList(
        // new ReportView(1, 1, "Reporter 1", "Spam messages", 101, "User 101"),
        // new ReportView(2, 2, "Reporter 2", "Offensive language", 102, "User 102"),
        // new ReportView(3, 3, "Reporter 3", "Fake information", 103, "User 103"),
        // new ReportView(4, 4, "Reporter 4", "Harassment in chat", 104, "User 104"),
        // new ReportView(5, 5, "Reporter 5", "Impersonation attempt", 105, "User 105"),
        // new ReportView(6, 6, "Reporter 6", "Scam or fraud activity", 106, "User
        // 106"),
        // new ReportView(7, 7, "Reporter 7", "Hate speech detected", 107, "User 107"),
        // new ReportView(8, 8, "Reporter 8", "Inappropriate images", 108, "User 108"),
        // new ReportView(9, 9, "Reporter 9", "Violation of community rules", 109, "User
        // 109"),
        // new ReportView(10, 10, "Reporter 10", "Suspicious behavior", 110, "User
        // 110"));
        // timeCol.setCellValueFactory(new PropertyValueFactory<ReportView,
        // String>("userAccount"));
        // reportTable.setItems(reportList);
    }

    @FXML
    void filterReport(ActionEvent event) {

    }

    @FXML
    void lockUser(ActionEvent event) {

    }

}
