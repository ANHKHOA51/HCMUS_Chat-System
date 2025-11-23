package chatapp.controllers.dashboard;

import java.net.URL;
import java.util.ResourceBundle;

import chatapp.models.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class LoginHistoryController extends DashboardController {

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> timeCol;

    @FXML
    private TableColumn<User, String> userNameCol;

    @FXML
    private TableColumn<User, String> nameCol;

    private ObservableList<User> userList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}
