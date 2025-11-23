package chatapp.controllers;

import chatapp.models.User;
import chatapp.test.MockData;
import chatapp.views.FriendOptionView;
import chatapp.views.UserListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public class FriendController {
    User u;
    FriendOptionView fov;
    UserListView userList;
    private BorderPane split = new BorderPane();

    public FriendController() {
        fov = new FriendOptionView();

        // lấy dữ liệu mẫu từ MockData
        ObservableList<User> all = MockData.mockUsers();
        // tạo dữ liệu demo cho request/online bằng cách lấy một vài phần tử từ all
        ObservableList<User> requests = FXCollections.observableArrayList();
        ObservableList<User> online = FXCollections.observableArrayList();
        for (int i = 0; i < all.size(); i++) {
            if (i < 3) requests.add(all.get(i));
            if (i % 2 == 0) online.add(all.get(i));
        }

        // mặc định show friend list
        userList = new UserListView(all);
        userList.setFriendListCellFactory();

        // wire các nút để đổi chế độ hiển thị / dữ liệu (demo)
        wireOptions(all, requests, online);

        split.setLeft(fov);
        split.setCenter(userList);
    }

    private void wireOptions(ObservableList<User> all, ObservableList<User> requests, ObservableList<User> online) {
        fov.getFriendBtn().setOnAction(e -> {
            userList.getUserListView().setItems(all);
            userList.setFriendListCellFactory();
        });

        fov.getFriendReqBtn().setOnAction(e -> {
            userList.getUserListView().setItems(requests);
            userList.setRequestListCellFactory();
        });

        fov.getOnlineBtn().setOnAction(e -> {
            userList.getUserListView().setItems(online);
            userList.setToChatListCellFactory();
        });

        fov.getSearchBtn().setOnAction(e -> {
            // demo: show filtered subset (ví dụ users có index %3 == 0)
            ObservableList<User> filtered = FXCollections.observableArrayList();
            for (int i = 0; i < all.size(); i++) {
                if (i % 3 == 0) filtered.add(all.get(i));
            }
            userList.getUserListView().setItems(filtered);
            userList.setFriendListCellFactory();
        });
    }

    public Tab getTab() {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setContent(split);
        tab.setText("Friends");
        return tab;
    }
}