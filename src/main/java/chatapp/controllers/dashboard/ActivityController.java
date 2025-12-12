package chatapp.controllers.dashboard;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import chatapp.dto.ActivityUserDTO;
import chatapp.dto.LoginHistoryDTO;
import chatapp.models.LoginHistory;
import chatapp.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ActivityController extends DashboardController {

    @FXML
    private TableColumn<ActivityUserDTO, Integer> activityCol;
    @FXML
    private TableColumn<ActivityUserDTO, Integer> messageCol;
    @FXML
    private TableColumn<ActivityUserDTO, Integer> numLoginCol;
    @FXML
    private TableColumn<ActivityUserDTO, String> userNameCol;

    @FXML
    private TableView<ActivityUserDTO> activityTable;

    @FXML
    private StackedBarChart<String, Number> loginChart;

    @FXML
    private TextField numActivityField;

    @FXML
    private ComboBox<String> typeCombox;

    @FXML
    private TextField usernameField;

    @FXML
    private ComboBox<Integer> yearCombox;

    @FXML
    private DatePicker toDate;
    @FXML
    private DatePicker fromDate;

    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private ObservableList<ActivityUserDTO> listActivityUser;
    private ObservableList<String> listTypeCombox;
    private ObservableList<Integer> listYearCombox;
    private ObservableList<String> months;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        List<ActivityUserDTO> list = User.getListActiviyUser();

        listActivityUser = FXCollections.observableArrayList(list);

        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        numLoginCol.setCellValueFactory(new PropertyValueFactory<>("numLogins"));
        messageCol.setCellValueFactory(new PropertyValueFactory<>("numMessages"));
        activityCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        activityTable.setItems(listActivityUser);

        listTypeCombox = FXCollections.observableArrayList("Greater than", "Equal", "Less than");
        typeCombox.setItems(listTypeCombox);

        listYearCombox = FXCollections.observableArrayList();
        for (int year = 2020; year <= 2025; year++) {
            listYearCombox.add(year);
        }
        yearCombox.setItems(listYearCombox);
        yearCombox.setValue(2025);

        months = FXCollections.observableArrayList(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        xAxis.setCategories(months);

        onFilterChart(new ActionEvent());
    }

    @FXML
    void onFilterChart(ActionEvent event) {
        int year = yearCombox.getValue();
        XYChart.Series<String, Number> loginSeries = new XYChart.Series<>();
        loginSeries.setName("Unique user logins");

        List<LoginHistoryDTO> list = LoginHistory.getAllLoginHistory();

        for (int i = 0; i < months.size(); i++) {
            int monthValue = i + 1;
            int count = 0;

            // Mỗi tháng lưu danh sách user đã login
            Set<UUID> userIds = new HashSet<>();

            for (LoginHistoryDTO item : list) {
                var time = item.getTimeUnformat();

                if (time.getYear() == year && time.getMonthValue() == monthValue) {
                    userIds.add(item.getUserId());
                }
            }

            count = userIds.size();

            loginSeries.getData().add(
                    new XYChart.Data<>(months.get(i), count));
        }

        loginChart.getData().clear();
        loginChart.getData().add(loginSeries);
    }

    @FXML
    void onFilterTable(ActionEvent event) {
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();

        if (from == null) {
            from = LocalDate.of(2000, 1, 1);
        }
        if (to == null) {
            to = LocalDate.now();
        }

        int numActivity = -1;
        if ((numActivityField.getText() != null || numActivityField.getText().equals("") == false)
                && numActivityField.getText().matches("\\d+")) {
            numActivity = Integer.valueOf(numActivityField.getText());
        }

        String type = "";
        if (typeCombox.getValue() != null) {
            type = typeCombox.getValue();
        }

        String username = usernameField.getText();

        List<ActivityUserDTO> list = User.getListActiviyUserByDate(from, to);
        ObservableList<ActivityUserDTO> filterList = FXCollections.observableArrayList();

        for (ActivityUserDTO user : list) {
            if (user.getUsername().contains(username)) {
                if (numActivity == -1 || type.equals("")) {
                    filterList.add(user);
                }
                if (type.equals("Greater than") && user.getTotal() > numActivity) {
                    filterList.add(user);
                } else if (type.equals("Equal") && user.getTotal() == numActivity) {
                    filterList.add(user);
                } else if (type.equals("Less than") && user.getTotal() < numActivity) {
                    filterList.add(user);
                }
            }
        }

        activityTable.setItems(filterList);
    }

}
