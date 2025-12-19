package chatapp.controllers.dashboard;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import chatapp.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class RegistrationController extends DashboardController {
    @FXML
    private Button filterChartBtn;
    @FXML
    private Button filterTableBtn;

    @FXML
    private TextField usernameField;

    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;

    @FXML
    private TableView<User> registrationTable;

    @FXML
    private TableColumn<User, String> nameCol;
    @FXML
    private TableColumn<User, String> timeCol;
    @FXML
    private TableColumn<User, String> userNameCol;
    @FXML
    private TableColumn<User, String> emailCol;

    @FXML
    private ComboBox<Integer> yearCombox;

    @FXML
    private StackedBarChart<String, Number> registrationChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private ObservableList<User> ListRegistation;
    private ObservableList<Integer> ListYearCombox;
    private ObservableList<String> months;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        List<User> list = User.getAllUser();

        ListRegistation = FXCollections.observableArrayList(list);

        nameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        registrationTable.setItems(ListRegistation);

        ListYearCombox = FXCollections.observableArrayList();
        for (int year = 2020; year <= 2025; year++) {
            ListYearCombox.add(year);
        }

        int defaultYear = 2025;

        yearCombox.setItems(ListYearCombox);
        yearCombox.setValue(defaultYear);

        months = FXCollections.observableArrayList(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        xAxis.setCategories(months);

        onFilterYearChart(new ActionEvent());

    }

    @FXML
    void onFilterTable(ActionEvent event) {
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();
        String valueFilter = usernameField.getText();

        if (from == null) {
            from = LocalDate.of(2000, 1, 1);

        }
        if (to == null) {
            to = LocalDate.now();
        }

        ObservableList<User> filterList = FXCollections.observableArrayList();

        for (User item : ListRegistation) {
            LocalDate date = item.getCreatedAtUnformat().toLocalDate();
            if ((date.isAfter(from) || date.isEqual(from))
                    && (date.isBefore(to) || date.isEqual(to))) {
                if (item.getUsername().contains(valueFilter)) {
                    filterList.add(item);
                }
                ;
            }
        }

        registrationTable.setItems(filterList);
    }

    @FXML
    void onFilterYearChart(ActionEvent event) {
        int year = yearCombox.getValue();
        XYChart.Series<String, Number> registrationSeries = new XYChart.Series<>();
        registrationSeries.setName("Registration");

        for (int i = 0; i < months.size(); i++) {
            int monthValue = i + 1;
            int count = 0;

            for (User user : ListRegistation) {
                if (user.getCreatedAtUnformat().getYear() == year) {
                    if (user.getCreatedAtUnformat().getMonthValue() == monthValue) {
                        count++;
                    }
                }
            }

            registrationSeries.getData().add(
                    new XYChart.Data<>(months.get(i), count));
        }

        registrationChart.getData().clear();
        registrationChart.getData().add(registrationSeries);
    }
}
