package chatapp.controllers.dashboard;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import chatapp.dto.LoginHistoryDTO;
import chatapp.models.User;
import chatapp.utils.FXMLPaths;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class UserController extends DashboardController {
    @FXML
    private TableView<LoginHistoryDTO> activityTableView;

    @FXML
    private TableColumn<LoginHistoryDTO, String> activityUserIdCol;
    @FXML
    private TableColumn<LoginHistoryDTO, String> activityUserNamId;
    @FXML
    private TableColumn<LoginHistoryDTO, String> activityTimeCol;
    @FXML
    private TableColumn<LoginHistoryDTO, String> activityCol;

    @FXML
    private TableView<User> friendTableView;

    @FXML
    private TableColumn<User, String> friendUserIdCol;
    @FXML
    private TableColumn<User, String> friendUserNameCol;
    @FXML
    private TableColumn<User, String> friendEmailCol;
    @FXML
    private TableColumn<User, Boolean> friendStatusCol;

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

    @FXML
    private Button lockUser;

    @FXML
    private Button unlockUser;

    private ObservableList<User> userList;
    private ObservableList<User> friendList;
    private ObservableList<LoginHistoryDTO> logingHistoryList;
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

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                boolean isLock = newSelection.isLock();
                lockUser.setDisable(isLock);
                unlockUser.setDisable(!isLock);
            }
        });

        if (chatapp.AdminApp.socketClient != null) {
            chatapp.AdminApp.socketClient.setOnActiveUsersUpdate(activeUserIds -> {
                Platform.runLater(() -> {
                    for (User user : userList) {
                        user.setOnline(activeUserIds.contains(user.getId()));
                    }
                    tableView.refresh();
                });
            });
        }
    }

    @FXML
    void addUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPaths.Dashboard.ADD_USER));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void updateUser(ActionEvent event) throws IOException {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null)
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPaths.Dashboard.UPDATE_USER));
        root = loader.load();
        UpdateUserController controller = loader.getController();
        controller.setUpdateUserId(selectedUser.getId());
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void deleteUser(ActionEvent event) {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null)
            return;

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete account?");
        alert.setContentText("Do you want to delete account " +
                selectedUser.getUsername() + "?");

        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonData.YES);
        ButtonType buttonTypeCancle = new ButtonType("Cancle",
                ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancle);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == buttonTypeYes) {
            boolean success = User.deleteUser(selectedUser.getId());
            if (success) {
                userList.remove(selectedUser);
            }
        } else {
            return;
        }
    }

    @FXML
    void getActivityHistory(ActionEvent event) {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null)
            return;

        List<LoginHistoryDTO> loginHistory = User.getLoginHistory(selectedUser.getId());

        logingHistoryList = FXCollections.observableArrayList(loginHistory);

        activityUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        activityUserNamId.setCellValueFactory(new PropertyValueFactory<>("username"));
        activityTimeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        activityCol.setCellValueFactory(new PropertyValueFactory<>("activity"));

        activityTableView.setItems(logingHistoryList);
    }

    @FXML
    void getListFriend(ActionEvent event) {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null)
            return;

        List<User> listFriends = User.getFriends(selectedUser.getId());
        friendList = FXCollections.observableArrayList(listFriends);

        friendUserIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        friendUserNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        friendEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        friendStatusCol.setCellValueFactory(new PropertyValueFactory<>("online"));

        friendTableView.setItems(friendList);
    }

    @FXML
    void filterUser(ActionEvent event) {
        String field = fieldCombox.getValue();
        String status = statusCombox.getValue();
        String filterValue = filterValueField.getText();

        ObservableList<User> filterUsers = FXCollections.observableArrayList();

        for (User user : userList) {
            if (field == "User name" && user.getUsername().contains(filterValue)) {
                if (status == "both") {
                    filterUsers.add(user);
                } else if (status == "offline" && user.isOnline() == false) {
                    filterUsers.add(user);
                } else if (status == "online" && user.isOnline() == true) {
                    filterUsers.add(user);
                }
            } else if (field == "Name" && user.getDisplayName().contains(filterValue)) {
                if (status == "both") {
                    filterUsers.add(user);
                } else if (status == "offline" && user.isOnline() == false) {
                    filterUsers.add(user);
                } else if (status == "online" && user.isOnline() == true) {
                    filterUsers.add(user);
                }
            }
        }

        tableView.setItems(filterUsers);
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

    @FXML
    void onLockUser(ActionEvent event) {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null)
            return;

        boolean result = User.updateFieldUser("lock", true, "id", selectedUser.getId());
        if (result) {
            userList.remove(selectedUser);
            selectedUser.setLock(true);
            userList.add(selectedUser);
        }
    }

    @FXML
    void onUnlockUser(ActionEvent event) {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null)
            return;

        boolean result = User.updateFieldUser("lock", false, "id", selectedUser.getId());
        if (result) {
            userList.remove(selectedUser);
            selectedUser.setLock(false);
            userList.add(selectedUser);
        }
    }

}
