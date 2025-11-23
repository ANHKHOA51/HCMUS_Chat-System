package chatapp.controllers;

import chatapp.models.User;
import chatapp.test.MockData;
import chatapp.views.FriendOptionView;
import chatapp.views.UserList;
import chatapp.views.cells.UserFriendListCell;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public class FriendController {
    User u;
    FriendOptionView fov;
    UserList userList;
    private BorderPane split = new BorderPane();

    public FriendController() {
        fov = new FriendOptionView();
        userList = new UserList(MockData.mockUsers());
        userList.setToChatListCellFactory();
        split.setLeft(fov);
        split.setCenter(userList);
    }

    public Tab getTab() {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setContent(split);
        tab.setText("Friends");
        return tab;
    }
}
