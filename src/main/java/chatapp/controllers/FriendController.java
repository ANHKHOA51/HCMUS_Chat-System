package chatapp.controllers;

import chatapp.models.FriendShip;
import chatapp.models.User;
import chatapp.views.FriendOptionView;
import chatapp.views.UserListView;
import chatapp.views.cells.FriendReqCell;
import chatapp.views.cells.UserCanChatCell;
import chatapp.views.cells.UserFriendListCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public class FriendController {
    User currentUser;
    FriendOptionView fov;
    UserListView userList;
    private BorderPane split = new BorderPane();

    // Mode tracking
    private enum Mode {
        FRIENDS, REQUESTS, ONLINE, SEARCH
    }

    private Mode currentMode = Mode.FRIENDS;

    public FriendController(User user) {
        this.currentUser = user;
        fov = new FriendOptionView();
        userList = new UserListView(); // Start empty

        // Initial Load
        loadFriends();

        // Wire buttons
        wireOptions();

        // Wire Search
        wireSearch();

        split.setLeft(fov);
        split.setCenter(userList);
    }

    private void loadFriends() {
        currentMode = Mode.FRIENDS;
        refreshFriendsList();
    }

    private void refreshFriendsList() {
        ObservableList<User> friends = FXCollections
                .observableArrayList(FriendShip.getFriendsList(currentUser.getId()));
        userList.getUserListView().setItems(friends);

        userList.getUserListView().setCellFactory(param -> {
            UserFriendListCell cell = new UserFriendListCell();
            cell.setOnDelete(u -> handleUnfriend(u));
            cell.setOnBlock(u -> handleBlock(u));
            return cell;
        });
    }

    private void loadRequests() {
        currentMode = Mode.REQUESTS;
        ObservableList<User> requests = FXCollections
                .observableArrayList(FriendShip.getPendingRequests(currentUser.getId()));
        userList.getUserListView().setItems(requests);

        userList.getUserListView().setCellFactory(param -> {
            FriendReqCell cell = new FriendReqCell();
            cell.setOnAccept(u -> handleAccept(u));
            cell.setOnDecline(u -> handleDecline(u)); // Deleting request is same as removeFriend/Decline logic
            return cell;
        });
    }

    private void loadOnline() {
        currentMode = Mode.ONLINE;
        // Filter friends list for online only
        ObservableList<User> friends = FXCollections
                .observableArrayList(FriendShip.getFriendsList(currentUser.getId()));
        ObservableList<User> onlineFriends = friends.filtered(User::isOnline);
        userList.getUserListView().setItems(onlineFriends);

        userList.getUserListView().setCellFactory(param -> {
            UserCanChatCell cell = new UserCanChatCell();
            cell.setOnChat(u -> handleChat(u));
            cell.setOnCreateGroup(u -> handleCreateGroup(u));
            return cell;
        });
    }

    private void wireOptions() {
        fov.getFriendBtn().setOnAction(e -> loadFriends());
        fov.getFriendReqBtn().setOnAction(e -> loadRequests());
        fov.getOnlineBtn().setOnAction(e -> loadOnline());
        fov.getSearchBtn().setOnAction(e -> {
            currentMode = Mode.SEARCH;
            userList.getUserListView().setItems(FXCollections.observableArrayList()); // Clear list
            userList.getFilterField().clear();
            userList.setToChatListCellFactory(); // Default cell or specific search cell?
            // For search, maybe we show Add Friend button if not friend?
            // Let's use UserFriendListCell but hide buttons dynamically if needed, or
            // simply UserCanChatCell?
            // Use UserFriendListCell to allow adding?
            // Reuse UserFriendListCell but customize buttons based on relationship?
            // For simplicity, let's use a cell that allows sending requests.
            // UserFriendListCell has a "Send request" button.
            userList.getUserListView().setCellFactory(param -> {
                UserFriendListCell cell = new UserFriendListCell();
                // Check relationship to show correct buttons?
                // The cell doesn't handle logic, controller does.
                cell.getDeleteButton().setVisible(false); // Can't delete/block easily without knowing relation state
                cell.getBlockButton().setVisible(false);
                // We should probably check relation first. But for now enable 'Send Request'
                cell.setOnSendRequest(u -> handleSendRequest(u));
                // Only show Send Request if not friend?
                // That logic is complex for a simple cell factory without checking data for
                // each item.
                // Assuming search returns non-friends.
                return cell;
            });
        });
    }

    private void wireSearch() {
        userList.getFilterField().textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentMode == Mode.SEARCH) {
                if (newVal == null || newVal.isBlank()) {
                    userList.getUserListView().setItems(FXCollections.observableArrayList());
                } else {
                    ObservableList<User> results = FXCollections
                            .observableArrayList(FriendShip.searchUsers(newVal, currentUser.getId()));
                    userList.getUserListView().setItems(results);
                }
            } else if (currentMode == Mode.FRIENDS) {
                // Filter local list
                refreshFriendsList(); // Reload full list
                ObservableList<User> all = userList.getUserListView().getItems();
                ObservableList<User> filtered = all
                        .filtered(u -> u.getUsername().toLowerCase().contains(newVal.toLowerCase())
                                || u.getDisplayName().toLowerCase().contains(newVal.toLowerCase()));
                userList.getUserListView().setItems(filtered);
            }
        });
    }

    // Action Handlers
    private void handleUnfriend(User u) {
        boolean success = FriendShip.removeFriend(currentUser.getId(), u.getId());
        if (success) {
            refreshFriendsList();
        } else {
            System.out.println("Failed to unfriend " + u.getUsername());
        }
    }

    private void handleBlock(User u) {
        boolean success = FriendShip.blockUser(currentUser.getId(), u.getId());
        if (success) {
            refreshFriendsList();
        } else {
            System.out.println("Failed to block " + u.getUsername());
        }
    }

    private void handleAccept(User u) {
        boolean success = FriendShip.acceptFriendRequest(currentUser.getId(), u.getId());
        if (success) {
            loadRequests(); // Refresh
        } else {
            System.out.println("Failed to accept request from " + u.getUsername());
        }
    }

    private void handleDecline(User u) {
        // Decline is same as remove pending friendship
        boolean success = FriendShip.removeFriend(currentUser.getId(), u.getId());
        if (success) {
            loadRequests();
        } else {
            System.out.println("Failed to decline request from " + u.getUsername());
        }
    }

    private void handleSendRequest(User u) {
        boolean success = FriendShip.sendFriendRequest(currentUser.getId(), u.getId());
        if (success) {
            System.out.println("Friend request sent to " + u.getUsername());
            // Optionally disable button or show info
        } else {
            System.out.println("Failed to send request to " + u.getUsername());
        }
    }

    private java.util.function.Consumer<User> onOpenChat;

    public void setOnOpenChat(java.util.function.Consumer<User> onOpenChat) {
        this.onOpenChat = onOpenChat;
    }

    private void handleChat(User u) {
        if (onOpenChat != null) {
            onOpenChat.accept(u);
        } else {
            System.out.println("Chat with " + u.getUsername());
        }
    }

    private void handleCreateGroup(User u) {
        System.out.println("Create group with " + u.getUsername());
    }

    public Tab getTab() {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setContent(split);
        tab.setText("Friends");
        return tab;
    }
}