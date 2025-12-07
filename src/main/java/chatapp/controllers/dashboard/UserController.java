package chatapp.controllers.dashboard;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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
    private TableView<?> activityTableView;

    @FXML
    private TableColumn<?, ?> activityUserIdCol;
    @FXML
    private TableColumn<?, ?> activityUserNamId;
    @FXML
    private TableColumn<?, ?> activityTimeCol;
    @FXML
    private TableColumn<?, ?> activityCol;

    @FXML
    private TableView<?> friendTableView;

    @FXML
    private TableColumn<?, ?> friendUserIdCol;
    @FXML
    private TableColumn<?, ?> friendUserNameCol;
    @FXML
    private TableColumn<?, ?> friendEmailCol;
    @FXML
    private TableColumn<?, ?> friendStatusCol;

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> userIdCol;
    @FXML
    private TableColumn<User, String> userNameCol;
    @FXML
    private TableColumn<User, String> nameCol;
    @FXML
    private TableColumn<User, String> emailCol;
    @FXML
    private TableColumn<User, String> addressCol;
    @FXML
    private TableColumn<User, String> birthdayCol;
    @FXML
    private TableColumn<User, String> createdAtCol;
    @FXML
    private TableColumn<User, Boolean> genderCol;
    @FXML
    private TableColumn<User, Boolean> isAdminCol;
    @FXML
    private TableColumn<User, String> isOnlineCol;

    @FXML
    private ComboBox<String> fieldCombox;

    @FXML
    private Button filterBtn;

    @FXML
    private TextField filterValueField;

    @FXML
    private ComboBox<String> statusCombox;

    private ObservableList<User> userList;
    private ObservableList<User> friendList;
    private ObservableList<String> fieldList;
    private ObservableList<String> statusList;

    private boolean changeStatus = false;
    private boolean changeField = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<User> list = User.getAllUser();

        userList = FXCollections.observableArrayList(list);

        userIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        birthdayCol.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        isOnlineCol.setCellValueFactory(new PropertyValueFactory<>("online"));
        isAdminCol.setCellValueFactory(new PropertyValueFactory<>("admin"));
        createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        tableView.setItems(userList);

        fieldList = FXCollections.observableArrayList("User name", "Name");
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
        // User selectedUser = tableView.getSelectionModel().getSelectedItem();
        // if (selectedUser == null)
        // return;

        // Alert alert = new Alert(AlertType.CONFIRMATION);
        // alert.setTitle("Confirmation");
        // alert.setHeaderText("Delete account?");
        // alert.setContentText("Do you want to delete account " +
        // selectedUser.getUser_name() + "?");

        // ButtonType buttonTypeYes = new ButtonType("Yes", ButtonData.YES);
        // ButtonType buttonTypeCancle = new ButtonType("Cancle",
        // ButtonData.CANCEL_CLOSE);

        // alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancle);
        // Optional<ButtonType> result = alert.showAndWait();
        // System.out.println(result);
    }

    @FXML
    void getActivityHistory(ActionEvent event) {

    }

    @FXML
    void getListFriend(ActionEvent event) {

    }

    @FXML
    void filterUser(ActionEvent event) {
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
