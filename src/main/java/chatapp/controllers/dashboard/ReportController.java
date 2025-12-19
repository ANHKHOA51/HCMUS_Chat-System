package chatapp.controllers.dashboard;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import chatapp.dto.ReportDTO;
import chatapp.models.Report;
import chatapp.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReportController extends DashboardController {
    @FXML
    private TextField reporterUsername;

    @FXML
    private TextField contentField;

    @FXML
    private TextField reportedUsername;

    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;

    @FXML
    private Button lockUser;

    @FXML
    private TableView<ReportDTO> reportTable;

    @FXML
    private TableColumn<ReportDTO, String> contentCol;
    @FXML
    private TableColumn<ReportDTO, String> reportedUserCol;
    @FXML
    private TableColumn<ReportDTO, String> reporterCol;
    @FXML
    private TableColumn<ReportDTO, String> timeCol;

    private ObservableList<ReportDTO> reportList;

    @FXML
    private Label informLable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<ReportDTO> list = Report.getListReports();

        reportList = FXCollections.observableArrayList(list);

        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
        reportedUserCol.setCellValueFactory(new PropertyValueFactory<>("reportedUserName"));
        reporterCol.setCellValueFactory(new PropertyValueFactory<>("reporterUsername"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        reportTable.setItems(reportList);
    }

    @FXML
    void filterReport(ActionEvent event) {
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();

        if (from == null) {
            from = LocalDate.of(2000, 1, 1);
        }
        if (to == null) {
            to = LocalDate.now();
        }

        ObservableList<ReportDTO> filterList = FXCollections.observableArrayList();

        String contentFilter = contentField.getText();
        String reporterFilter = reporterUsername.getText();
        String reportedFilter = reportedUsername.getText();

        for (ReportDTO item : reportList) {
            LocalDate date = item.getTimeUnformat().toLocalDate();
            if ((date.isAfter(from) || date.isEqual(from))
                    && (date.isBefore(to) || date.isEqual(to))) {
                if (item.getContent().contains(contentFilter) &&
                        item.getReporterUsername().contains(reporterFilter) &&
                        item.getReportedUserName().contains(reportedFilter)) {
                    filterList.add(item);
                }
            }
        }

        reportTable.setItems(filterList);
    }

    @FXML
    void onLockUser(ActionEvent event) {
        ReportDTO selectedReport = reportTable.getSelectionModel().getSelectedItem();
        if (selectedReport == null)
            return;

        boolean result = User.updateFieldUser("lock", true, "id", selectedReport.getReportedUserId());
        if (result) {
            informLable.setStyle("-fx-text-fill: green;");
            informLable.setText("Successfully");
            informLable.setVisible(true);
        } else {
            informLable.setStyle("-fx-text-fill: red;");
            informLable.setText("Failed to lock");
            informLable.setVisible(true);
        }
    }

    @FXML
    void deleteReport(ActionEvent event) {
        ReportDTO selectedReport = reportTable.getSelectionModel().getSelectedItem();
        if (selectedReport == null)
            return;

        boolean result = Report.deleteReport(selectedReport.getId());
        if (result) {
            informLable.setStyle("-fx-text-fill: green;");
            informLable.setText("Successfully");
            informLable.setVisible(true);

            ObservableList<ReportDTO> curList = reportTable.getItems();
            curList.remove(selectedReport);
            reportList.remove(selectedReport);
        } else {
            informLable.setStyle("-fx-text-fill: red;");
            informLable.setText("Failed to delete");
            informLable.setVisible(true);
        }
    }

}
