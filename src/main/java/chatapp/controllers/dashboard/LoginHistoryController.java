package chatapp.controllers.dashboard;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import chatapp.dto.LoginHistoryDTO;
import chatapp.models.LoginHistory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class LoginHistoryController extends DashboardController {

    @FXML
    private TableView<LoginHistoryDTO> tableView;

    @FXML
    private TableColumn<LoginHistoryDTO, String> timeCol;

    @FXML
    private TableColumn<LoginHistoryDTO, String> userNameCol;

    @FXML
    private TableColumn<LoginHistoryDTO, String> nameCol;

    private ObservableList<LoginHistoryDTO> loginHistoryList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<LoginHistoryDTO> list = LoginHistory.getAllLoginHistory();

        loginHistoryList = FXCollections.observableArrayList(list);

        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));

        tableView.setItems(loginHistoryList);
    }

}
