package chatapp.controllers.dashboard;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import chatapp.dto.UserFriendsDTO;
import chatapp.models.Friendship;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FriendController extends DashboardController {
    @FXML
    private TableColumn<UserFriendsDTO, String> emailCol;
    @FXML
    private TableColumn<UserFriendsDTO, Integer> friendCol;
    @FXML
    private TableColumn<UserFriendsDTO, Integer> friendOfFriendCol;
    @FXML
    private TableColumn<UserFriendsDTO, String> nameCol;
    @FXML
    private TableColumn<UserFriendsDTO, String> userNameCol;

    @FXML
    private TableView<UserFriendsDTO> userFriendTable;

    @FXML
    private TextField numFriendsField;

    @FXML
    private ComboBox<String> typeNumFriendFilter;

    @FXML
    private TextField usernameField;

    private ObservableList<UserFriendsDTO> listUsers;
    private ObservableList<String> listTypeNumFriendFilter;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        List<UserFriendsDTO> list = Friendship.getListUserFriends();

        listUsers = FXCollections.observableArrayList(list);

        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        friendCol.setCellValueFactory(new PropertyValueFactory<>("numFriends"));
        friendOfFriendCol.setCellValueFactory(new PropertyValueFactory<>("numFriendsOfFriends"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        userFriendTable.setItems(listUsers);

        listTypeNumFriendFilter = FXCollections.observableArrayList("Greater than", "Equal", "Less than");
        typeNumFriendFilter.setItems(listTypeNumFriendFilter);
    }

    @FXML
    void onFilter(ActionEvent event) {
        String type = "";
        if (typeNumFriendFilter.getValue() != null) {
            type = typeNumFriendFilter.getValue();
        }
        int numFriends = -1;
        if ((numFriendsField.getText() != null || numFriendsField.getText().equals("") == false)
                && numFriendsField.getText().matches("\\d+")) {
            numFriends = Integer.valueOf(numFriendsField.getText());
        }
        String username = usernameField.getText();

        ObservableList<UserFriendsDTO> filterList = FXCollections.observableArrayList();

        for (UserFriendsDTO user : listUsers) {
            if (user.getUsername().contains(username)) {
                if (numFriends == -1 || type.equals("")) {
                    filterList.add(user);
                }
                if (type.equals("Greater than") && user.getNumFriends() > numFriends) {
                    filterList.add(user);
                } else if (type.equals("Equal") && user.getNumFriends() == numFriends) {
                    filterList.add(user);
                } else if (type.equals("Less than") && user.getNumFriends() < numFriends) {
                    filterList.add(user);
                }
            }
        }

        userFriendTable.setItems(filterList);
    }
}