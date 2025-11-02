package chatapp.controllers.dashboard;

import java.net.URL;
import java.util.ResourceBundle;

import chatapp.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class LoginHistoryController extends DashboardController {

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> timeCol;

    @FXML
    private TableColumn<User, String> userAccountCol;

    @FXML
    private TableColumn<User, String> userNameCol;

    private ObservableList<User> userList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // userList = FXCollections.observableArrayList(
        // new User(1, "helloworld", "Hello World", "HCM", "1/1/2000", false,
        // "heloworld@gmail.com"),
        // new User(2, "test", "Test", "VietNam", "2/2/2002", true, "test@gmail.com"));
        // timeCol.setCellValueFactory(new PropertyValueFactory<User,
        // String>("updateAt"));
        // userAccountCol.setCellValueFactory(new PropertyValueFactory<User,
        // String>("userAccount"));
        // userNameCol.setCellValueFactory(new PropertyValueFactory<User,
        // String>("userName"));
        // tableView.setItems(userList);
    }

}
