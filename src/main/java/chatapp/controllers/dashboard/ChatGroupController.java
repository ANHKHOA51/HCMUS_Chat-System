package chatapp.controllers.dashboard;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import chatapp.dto.ChatGroupDTO;
import chatapp.models.ChatGroup;
import chatapp.models.Conversation;
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
    private TableView<ChatGroupDTO> groupTable;

    @FXML
    private TableColumn<ChatGroupDTO, String> groupCreatedAtCol;
    @FXML
    private TableColumn<ChatGroupDTO, Integer> groupMemberCol;
    @FXML
    private TableColumn<ChatGroupDTO, String> groupNameCol;
    @FXML
    private TableColumn<ChatGroupDTO, String> groupCreatorCol;

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

    private ObservableList<ChatGroupDTO> groupList;
    private ObservableList<User> memberList;
    private ObservableList<User> adminList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<ChatGroupDTO> list = Conversation.getListChatGroup();

        groupList = FXCollections.observableArrayList(list);
        groupCreatedAtCol.setCellValueFactory(new PropertyValueFactory<ChatGroupDTO, String>("createdAt"));
        groupNameCol.setCellValueFactory(new PropertyValueFactory<ChatGroupDTO, String>("groupName"));
        groupMemberCol.setCellValueFactory(new PropertyValueFactory<ChatGroupDTO, Integer>("numMember"));
        groupCreatorCol.setCellValueFactory(new PropertyValueFactory<ChatGroupDTO, String>("creator"));
        groupTable.setItems(groupList);
    }

    @FXML
    void filterGroupName(ActionEvent event) {
        ObservableList<ChatGroupDTO> chatGroupFilter = FXCollections.observableArrayList();
        String value = groupNameField.getText();
        for (ChatGroupDTO group : groupList) {
            if (group.getGroupName().contains(value)) {
                chatGroupFilter.add(group);
            }
        }
        groupTable.setItems(chatGroupFilter);
    }

    @FXML
    void getMember(ActionEvent event) {
        ChatGroupDTO selectedGroup = groupTable.getSelectionModel().getSelectedItem();
        if (selectedGroup == null)
            return;

        List<User> listMembers = Conversation.getListMembers(selectedGroup.getId(), "member");

        memberList = FXCollections.observableArrayList(listMembers);

        memberEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        memberGenderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        memberNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        memberTable.setItems(memberList);
    }

    @FXML
    void getAdmin(ActionEvent event) {
        ChatGroupDTO selectedGroup = groupTable.getSelectionModel().getSelectedItem();
        if (selectedGroup == null)
            return;

        List<User> listAdmins = Conversation.getListMembers(selectedGroup.getId(), "admin");

        adminList = FXCollections.observableArrayList(listAdmins);

        adminEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        adminGenderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        adminNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        adminTable.setItems(adminList);
    }
}
