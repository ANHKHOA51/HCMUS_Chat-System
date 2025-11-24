package chatapp.views;

import chatapp.models.User;
import chatapp.views.cells.FriendReqCell;
import chatapp.views.cells.UserCanChatCell;
import chatapp.views.cells.UserFriendListCell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.collections.ObservableList;

public class UserListView extends BorderPane {
    TextField filterField;
    ListView<User> userListView;
    
    public TextField getFilterField() {
        return filterField;
    }

    public void setFilterField(TextField filterField) {
        this.filterField = filterField;
    }

    public ListView<User> getUserListView() {
        return userListView;
    }

    public void setUserListView(ListView<User> userListView) {
        this.userListView = userListView;
    }

    public UserListView() {
        userListView = new ListView<>();
        filterField = new TextField();
        filterField.setPromptText("Search users...");
        filterField.setMaxWidth(600);

        HBox topBox = new HBox(filterField);
        topBox.setAlignment(Pos.CENTER_RIGHT);
        topBox.setPrefWidth(400);
        topBox.setPadding(new Insets(10));
        this.setTop(topBox);
        this.setCenter(userListView);
    }

    public UserListView(ObservableList<User> users) {
        userListView = new ListView<>(users);
        filterField = new TextField();
        filterField.setPromptText("Search users...");
        filterField.setMaxWidth(200);

        HBox topBox = new HBox(filterField);
        topBox.setAlignment(Pos.CENTER_RIGHT);
        topBox.setPadding(new Insets(10)); // optional spacing
        this.setTop(topBox);
        this.setCenter(userListView);
    }


    public void setFriendListCellFactory() {
        userListView.setCellFactory(param -> new UserFriendListCell());
    }

    public void setRequestListCellFactory() {
        userListView.setCellFactory(param -> new FriendReqCell());
    }

    public void setToChatListCellFactory() {
        userListView.setCellFactory(param -> new UserCanChatCell());
    }
}
