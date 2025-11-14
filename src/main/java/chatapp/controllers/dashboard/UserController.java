package chatapp.controllers.dashboard;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import chatapp.models.User;
import chatapp.utils.FXMLPaths;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class UserController extends DashboardController {
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
    @FXML
    private TableColumn<User, String> statusCol;
    @FXML
    private TableColumn<User, String> timeCol;

    @FXML
    private TableView<?> activityTableView;

    @FXML
    private TableView<User> friendTableView;

    @FXML
    private TableColumn<User, String> friendEmailCol;
    @FXML
    private TableColumn<User, String> friendStatusCol;
    @FXML
    private TableColumn<User, String> friendUserAccountCol;
    @FXML
    private TableColumn<User, String> friendUserNameCol;

    @FXML
    private Button filterBtn;

    @FXML
    private ComboBox<String> fieldCombox;

    @FXML
    private ComboBox<String> statusCombox;

    @FXML
    private TextField filterValueField;

    private ObservableList<User> userList;
    private ObservableList<User> friendList;
    private ObservableList<String> fieldList;
    private ObservableList<String> statusList;

    private boolean changeStatus = false;
    private boolean changeField = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userList = FXCollections.observableArrayList(
                new User(1, "helloworld", "Hello World", "HCM", "1/1/2000", false, "helloworld@gmail.com", "online"),
                new User(2, "test", "Test", "VietNam", "2/2/2002", true, "test@gmail.com", "online"),
                new User(3, "alice", "Alice Nguyen", "Ha Noi", "3/3/2001", false, "alice@gmail.com", "offline"),
                new User(4, "bob", "Bob Tran", "Da Nang", "4/4/1999", true, "bob@gmail.com", "online"),
                new User(5, "charlie", "Charlie Le", "Can Tho", "5/5/2000", false, "charlie@gmail.com", "offline"),
                new User(6, "david", "David Pham", "Hue", "6/6/1998", true, "david@gmail.com", "offline"),
                new User(7, "eva", "Eva Vo", "Nha Trang", "7/7/2003", false, "eva@gmail.com", "online"),
                new User(8, "frank", "Frank Ngo", "Binh Duong", "8/8/2001", true, "frank@gmail.com", "offline"),
                new User(9, "grace", "Grace Truong", "Dong Nai", "9/9/2002", false, "grace@gmail.com", "online"),
                new User(10, "henry", "Henry Phan", "HCM", "10/10/2000", true, "henry@gmail.com", "offline"),
                new User(11, "ivy", "Ivy Do", "Ha Noi", "11/11/1999", false, "ivy@gmail.com", "offline"),
                new User(12, "jack", "Jack Dang", "Hue", "12/12/2001", true, "jack@gmail.com", "online"),
                new User(13, "kate", "Kate Vo", "Da Nang", "1/1/2003", false, "kate@gmail.com", "offline"),
                new User(14, "leo", "Leo Nguyen", "Can Tho", "2/2/2000", true, "leo@gmail.com", "online"),
                new User(15, "mary", "Mary Pham", "Vung Tau", "3/3/2002", false, "mary@gmail.com", "offline"),
                new User(16, "nick", "Nick Tran", "Bac Giang", "4/4/1998", true, "nick@gmail.com", "offline"),
                new User(17, "olivia", "Olivia Bui", "Da Lat", "5/5/2001", false, "olivia@gmail.com", "online"),
                new User(18, "peter", "Peter Vo", "HCM", "6/6/2002", true, "peter@gmail.com", "offline"),
                new User(19, "quinn", "Quinn Do", "Ha Noi", "7/7/1999", false, "quinn@gmail.com", "offline"),
                new User(20, "ryan", "Ryan Le", "Can Tho", "8/8/2003", true, "ryan@gmail.com", "online"));
        userAccountCol.setCellValueFactory(new PropertyValueFactory<User, String>("userAccount"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<User, String>("address"));
        birthdayCol.setCellValueFactory(new PropertyValueFactory<User, String>("birthday"));
        genderCol.setCellValueFactory(new PropertyValueFactory<User, Boolean>("gender"));
        emailCol.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        statusCol.setCellValueFactory(new PropertyValueFactory<User, String>("status"));
        timeCol.setCellValueFactory(new PropertyValueFactory<User, String>("createAt"));
        tableView.setItems(userList);

        fieldList = FXCollections.observableArrayList("User account", "User name");
        fieldCombox.setItems(fieldList);

        statusList = FXCollections.observableArrayList("online", "offline", "both");
        statusCombox.setItems(statusList);

    }

    @FXML
    void addUser(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(FXMLPaths.Auth.REGISTER));
        Stage stage = new Stage();
        stage.setTitle("TEST");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    void updateUser(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(FXMLPaths.Auth.REGISTER));
        Stage stage = new Stage();
        stage.setTitle("TEST");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    void deleteUser(ActionEvent event) {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null)
            return;

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete account?");
        alert.setContentText("Do you want to delete account " + selectedUser.getUserAccount() + "?");

        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonData.YES);
        ButtonType buttonTypeCancle = new ButtonType("Cancle", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancle);
        Optional<ButtonType> result = alert.showAndWait();
        System.out.println(result);
    }

    @FXML
    void getActivityHistory(ActionEvent event) {

    }

    @FXML
    void getListFriend(ActionEvent event) {
        friendList = FXCollections.observableArrayList(
                new User(1, "helloworld", "Hello World", "HCM", "1/1/2000", false, "helloworld@gmail.com", "online"),
                new User(2, "test", "Test", "VietNam", "2/2/2002", true, "test@gmail.com", "online"));

        friendUserAccountCol.setCellValueFactory(new PropertyValueFactory<User, String>("userAccount"));
        friendUserNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));
        friendStatusCol.setCellValueFactory(new PropertyValueFactory<User, String>("status"));
        friendEmailCol.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        friendTableView.setItems(friendList);
    }

    @FXML
    void filterUser(ActionEvent event) {
        ObservableList<User> userListFilter = FXCollections.observableArrayList();
        String value = filterValueField.getText();
        boolean checkStatus = statusCombox.getValue().equals("both");
        for (User user : userList) {
            if (fieldCombox.getValue() == "User account") {
                if (user.getUserAccount().contains(value)
                        && (checkStatus || user.getStatus().equals(statusCombox.getValue()))) {
                    userListFilter.add(user);
                }
            } else if (fieldCombox.getValue() == "User name"
                    && (checkStatus || user.getStatus().equals(statusCombox.getValue()))) {
                if (user.getUserName().contains(value)) {
                    userListFilter.add(user);
                }
            }
        }
        tableView.setItems(userListFilter);
    }

    @FXML
    void fieldComboxChanged(ActionEvent event) {
        changeField = true;
        filterBtn.setDisable(!(changeStatus && changeField));
    }

    @FXML
    void statusComboxChanged(ActionEvent event) {
        changeStatus = true;
        filterBtn.setDisable(!(changeStatus && changeField));
    }

}
