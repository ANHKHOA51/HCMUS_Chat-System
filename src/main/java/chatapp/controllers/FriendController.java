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

    private javafx.animation.PauseTransition searchDebounce;

    private void wireSearch() {
        searchDebounce = new javafx.animation.PauseTransition(javafx.util.Duration.millis(500));
        searchDebounce.setOnFinished(e -> performSearch());

        userList.getFilterField().textProperty().addListener((obs, oldVal, newVal) -> {
            searchDebounce.playFromStart();
        });
    }

    private void performSearch() {
        String rawQuery = userList.getFilterField().getText();
        final String query = (rawQuery != null) ? rawQuery.trim().toLowerCase() : "";

        if (currentMode == Mode.SEARCH) {
            if (query.isEmpty()) {
                userList.getUserListView().setItems(FXCollections.observableArrayList());
            } else {
                ObservableList<User> results = FXCollections
                        .observableArrayList(FriendShip.searchUsers(query, currentUser.getId()));
                userList.getUserListView().setItems(results);

                // Update cell factory for search results based on relationship
                userList.getUserListView().setCellFactory(param -> {
                    // We need a custom cell that checks relationship dynamically
                    // Since we can't easily change cell type per item in a single ListView without
                    // custom Cell implementation
                    // We will use UserFriendListCell but customize buttons.
                    // Ideally we should have a 'UniversalCell' but for now let's reuse
                    // UserFriendListCell and hack visibility

                    UserFriendListCell cell = new UserFriendListCell() {
                        @Override
                        protected void updateItem(User user, boolean empty) {
                            super.updateItem(user, empty);
                            if (empty || user == null) {
                                setGraphic(null);
                                return;
                            }

                            String rel = FriendShip.getRelationship(currentUser.getId(), user.getId());

                            // Reset buttons
                            getDeleteButton().setVisible(false);
                            getBlockButton().setVisible(false);
                            getSendRequestButton().setVisible(false);

                            // We also need a 'Chat' button... UserFriendListCell doesn't have it.
                            // It seems we might need to add a Chat button to UserFriendListCell or switch
                            // to UserCanChatCell
                            // But ListView uses one CellFactory.
                            // If we want mixed types, we need a Cell that can handle all or switch node.

                            // Let's use UserFriendListCell buttons for now.
                            // If friend -> Show 'Block' (Report Spam). Unfriend?
                            // User asked: "Find user... Then can chat, create group"
                            // If not friend -> Show 'Send Request'

                            if ("friends".equals(rel)) {
                                // Make 'Send Request' act as 'Chat'? No, label is fixed.
                                // We should probably just show 'Block' here.
                                // To allow Chat/Group, we really need UserCanChatCell.
                                // Logic: Search result list contains Friends AND Strangers.
                                // If we use UserFriendListCell, we lack Chat button.

                                // Alternative: Only show Strangers in Search?
                                // User said "Find person based on name... Then can chat". Implies finding
                                // friends too?
                                // Or finding strangers and add them, then chat?
                                // "Find people... to Chat" usually implies finding friends.
                                // But "Find people... (not blocked)" implies finding anyone.

                                // Correct approach: Simple Universal Cell logic inside updateItem
                                // But we are stuck with specific Cell classes.

                                // Hack: UserFriendListCell has 3 buttons.
                                // Label 'Send Request' -> 'Chat'?
                                getSendRequestButton().setText("Chat");
                                getSendRequestButton().setVisible(true);
                                getSendRequestButton().setOnAction(e -> handleChat(user));

                                getBlockButton().setVisible(true); // Report Spam
                                getDeleteButton().setVisible(true); // Unfriend?
                                getDeleteButton().setText("Group"); // Reuse Del for Group?
                                getDeleteButton().setStyle("-fx-background-color: orange; -fx-text-fill: white;");
                                getDeleteButton().setOnAction(e -> handleCreateGroup(user));

                            } else if ("pending_sent".equals(rel)) {
                                getSendRequestButton().setText("Sent");
                                getSendRequestButton().setDisable(true);
                                getSendRequestButton().setVisible(true);
                                getBlockButton().setVisible(true);
                            } else if ("pending_received".equals(rel)) {
                                // Show Accept?
                                getSendRequestButton().setText("Accept");
                                getSendRequestButton().setVisible(true);
                                getSendRequestButton().setOnAction(e -> handleAccept(user));
                                getBlockButton().setVisible(true);
                            } else if ("none".equals(rel)) {
                                getSendRequestButton().setText("Add");
                                getSendRequestButton().setVisible(true);
                                getSendRequestButton().setDisable(false);
                                getSendRequestButton().setOnAction(e -> handleSendRequest(user));
                                getBlockButton().setVisible(true);
                            } else {
                                // Blocked? Search shouldn't show them if they blocked me.
                                // If I blocked them, maybe show Unblock?
                                // Current req: "Search ... (not see people who blocked me)"
                            }
                        }
                    };
                    return cell;
                });
            }
        } else {
            // For FRIENDS, REQUESTS, ONLINE: Filter the *currently loaded* list
            // Issue: We are not reloading from DB, just filtering current items.
            // But refreshFriendsList() etc reload from DB.
            // We should store the 'original' list in a variable to filter against?
            // Or just rely on currentItems? If we filter, we lose items.
            // Better: keep a cached copy or reload and filter.

            // Optimized:
            ObservableList<User> allItems = FXCollections.observableArrayList();
            if (currentMode == Mode.FRIENDS) {
                allItems = FXCollections.observableArrayList(FriendShip.getFriendsList(currentUser.getId()));
            } else if (currentMode == Mode.REQUESTS) {
                allItems = FXCollections.observableArrayList(FriendShip.getPendingRequests(currentUser.getId()));
            } else if (currentMode == Mode.ONLINE) {
                allItems = FXCollections.observableArrayList(FriendShip.getFriendsList(currentUser.getId()));
                allItems = FXCollections.observableArrayList(allItems.filtered(User::isOnline));
            }

            if (query.isEmpty()) {
                userList.getUserListView().setItems(allItems);
            } else {
                ObservableList<User> filtered = allItems
                        .filtered(u -> (u.getUsername() != null && u.getUsername().toLowerCase().contains(query)) ||
                                (u.getDisplayName() != null && u.getDisplayName().toLowerCase().contains(query)));
                userList.getUserListView().setItems(filtered);
            }

            // Re-apply cell factory because setting items might rely on view's default?
            // No, cell factory is set on the ListView, it stays.
            // But we need to ensure correct factory is set if we switch properly.
            // Switching modes sets factory. Filtering just updates items.
        }
    }

    // Action Handlers
    private void handleUnfriend(User u) {
        boolean success = FriendShip.removeFriend(currentUser.getId(), u.getId());
        if (success) {
            refreshFriendsList();
            if (currentMode == Mode.SEARCH)
                performSearch();
        } else {
            System.out.println("Failed to unfriend " + u.getUsername());
        }
    }

    private void handleBlock(User u) {
        boolean success = FriendShip.blockUser(currentUser.getId(), u.getId());
        if (success) {
            refreshFriendsList();
            if (currentMode == Mode.SEARCH)
                performSearch(); // Refresh search to hide or update status
        } else {
            System.out.println("Failed to block " + u.getUsername());
        }
    }

    private void handleAccept(User u) {
        boolean success = FriendShip.acceptFriendRequest(currentUser.getId(), u.getId());
        if (success) {
            loadRequests(); // Refresh
            if (currentMode == Mode.SEARCH)
                performSearch();
        } else {
            System.out.println("Failed to accept request from " + u.getUsername());
        }
    }

    private void handleDecline(User u) {
        // Decline is same as remove pending friendship
        boolean success = FriendShip.removeFriend(currentUser.getId(), u.getId());
        if (success) {
            loadRequests();
            if (currentMode == Mode.SEARCH)
                performSearch();
        } else {
            System.out.println("Failed to decline request from " + u.getUsername());
        }
    }

    private void handleSendRequest(User u) {
        boolean success = FriendShip.sendFriendRequest(currentUser.getId(), u.getId());
        if (success) {
            System.out.println("Friend request sent to " + u.getUsername());
            if (currentMode == Mode.SEARCH) {
                performSearch(); // Refresh search list to update button to "Sent"
            }
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
        // TODO: implement group creation dialog
    }

    public Tab getTab() {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setContent(split);
        tab.setText("Friends");
        return tab;
    }
}