package chatapp.controllers.dashboard;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import chatapp.models.ChatGroup;
import chatapp.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ChatGroupController extends DashboardController {
    @FXML
    private TableView<User> adminTable;

    @FXML
    private TableColumn<User, String> adminEmailCol;
    @FXML
    private TableColumn<User, Boolean> adminGenderCol;
    @FXML
    private TableColumn<User, String> adminNameCol;

    @FXML
    private TableView<ChatGroup> groupTable;

    @FXML
    private TableColumn<ChatGroup, String> groupCreatedAtCol;
    @FXML
    private TableColumn<ChatGroup, Integer> groupMemberCol;
    @FXML
    private TableColumn<ChatGroup, String> groupNameCol;

    @FXML
    private TableView<User> memberTable;

    @FXML
    private TableColumn<User, String> memberEmailCol;
    @FXML
    private TableColumn<User, Boolean> memberGenderCol;
    @FXML
    private TableColumn<User, String> memberNameCol;

    @FXML
    private TextField groupNameField;

    private ObservableList<ChatGroup> groupList;
    private ObservableList<User> memberList;
    private ObservableList<User> adminList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        groupList = FXCollections.observableArrayList(
                new ChatGroup(1, "Developers", 8, LocalDateTime.now().minusHours(1).toString()),
                new ChatGroup(2, "Designers", 5, LocalDateTime.now().minusDays(1).toString()),
                new ChatGroup(3, "Gamers", 12, LocalDateTime.now().minusDays(2).toString()),
                new ChatGroup(4, "Music Lovers", 9, LocalDateTime.now().minusHours(4).toString()),
                new ChatGroup(5, "Movie Fans", 7, LocalDateTime.now().minusDays(3).toString()),
                new ChatGroup(6, "Tech Talk", 15, LocalDateTime.now().minusMinutes(45).toString()),
                new ChatGroup(7, "Travelers", 10, LocalDateTime.now().minusDays(5).toString()),
                new ChatGroup(8, "Foodies", 6, LocalDateTime.now().minusHours(10).toString()),
                new ChatGroup(9, "Study Group", 11, LocalDateTime.now().minusDays(7).toString()),
                new ChatGroup(10, "Random Chat", 4, LocalDateTime.now().toString()));
        groupCreatedAtCol.setCellValueFactory(new PropertyValueFactory<ChatGroup, String>("createdAt"));
        groupNameCol.setCellValueFactory(new PropertyValueFactory<ChatGroup, String>("groupName"));
        groupMemberCol.setCellValueFactory(new PropertyValueFactory<ChatGroup, Integer>("numMember"));
        groupTable.setItems(groupList);
    }

    @FXML
    void filterGroupName(ActionEvent event) {
        ObservableList<ChatGroup> chatGroupFilter = FXCollections.observableArrayList();
        String value = groupNameField.getText();
        for (ChatGroup group : groupList) {
            if (group.getGroupName().contains(value)) {
                chatGroupFilter.add(group);
            }
        }
        groupTable.setItems(chatGroupFilter);
    }

    @FXML
    void getMember(ActionEvent event) {
        if (groupTable.getSelectionModel().getSelectedCells() == null) {
            return;
        }
        memberList = FXCollections.observableArrayList(
                new User("1", "helloworld", "Hello World", "HCM", "1/1/2000", false, "helloworld@gmail.com", "online"),
                new User("2", "test", "Test", "VietNam", "2/2/2002", true, "test@gmail.com", "online"));

        memberNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("user_name"));
        memberGenderCol.setCellValueFactory(new PropertyValueFactory<User, Boolean>("gender"));
        memberEmailCol.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        memberTable.setItems(memberList);
    }

    @FXML
    void getAdmin(ActionEvent event) {
        if (groupTable.getSelectionModel().getSelectedCells() == null) {
            return;
        }
        adminList = FXCollections.observableArrayList(
                new User("1", "helloworld", "Hello World", "HCM", "1/1/2000", false, "helloworld@gmail.com", "online"),
                new User("2", "test", "Test", "VietNam", "2/2/2002", true, "test@gmail.com", "online"));

        adminNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("user_name"));
        adminGenderCol.setCellValueFactory(new PropertyValueFactory<User, Boolean>("gender"));
        adminEmailCol.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        adminTable.setItems(adminList);
    }
}
