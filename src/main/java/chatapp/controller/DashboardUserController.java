package chatapp.controller;

import java.net.URL;
import java.util.ResourceBundle;

import chatapp.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardUserController implements Initializable {

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> userAccountCol;
    @FXML
    private TableColumn<User, String> userNameCol;
    @FXML
    private TableColumn<User, String> addressCol;
    @FXML
    private TableColumn<User, String> birthdayCol;
    @FXML
    private TableColumn<User, Boolean> genderCol;
    @FXML
    private TableColumn<User, String> emailCol;

    private ObservableList<User> userList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userList = FXCollections.observableArrayList(
                new User(1, "helloworld", "Hello World", "HCM", "1/1/2000", false, "heloworld@gmail.com"),
                new User(2, "test", "Test", "VietNam", "2/2/2002", true, "test@gmail.com"));
        userAccountCol.setCellValueFactory(new PropertyValueFactory<User, String>("userAccount"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<User, String>("address"));
        birthdayCol.setCellValueFactory(new PropertyValueFactory<User, String>("birthday"));
        genderCol.setCellValueFactory(new PropertyValueFactory<User, Boolean>("gender"));
        emailCol.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        tableView.setItems(userList);
    }

    @FXML
    void addUser(ActionEvent event) {

    }

    @FXML
    void updateUser(ActionEvent event) {

    }

    @FXML
    void deleteUser(ActionEvent event) {

    }

    @FXML
    void logOut(ActionEvent event) {

    }

    @FXML
    void navigateActiveUser(ActionEvent event) {

    }

    @FXML
    void navigateChatGroup(ActionEvent event) {

    }

    @FXML
    void navigateLogInHistory(ActionEvent event) {

    }

    @FXML
    void navigateRegistation(ActionEvent event) {

    }

    @FXML
    void navigateReport(ActionEvent event) {

    }

    @FXML
    void navigateUser(ActionEvent event) {

    }

    @FXML
    void navigateUserFriend(ActionEvent event) {

    }

    @FXML
    void sortUser(ActionEvent event) {

    }

}
