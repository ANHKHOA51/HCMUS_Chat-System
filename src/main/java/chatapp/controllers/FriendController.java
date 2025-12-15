package chatapp.controllers;

import chatapp.dao.FriendShipDAO;
import chatapp.models.User;
import chatapp.views.FriendOptionView;
import chatapp.views.UserListView;
import chatapp.views.cells.FriendReqCell;
import chatapp.utils.DbTask;
import javafx.concurrent.Task;
import chatapp.models.Conversation;
import chatapp.models.GroupUser;
import chatapp.dao.ConversationDAO;
import chatapp.views.CreateGroupView;
import chatapp.views.cells.UserCanChatCell;
import chatapp.views.cells.UserFriendListCell;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public class FriendController {
    User currentUser;
    FriendOptionView fov;
    UserListView userList;
    private BorderPane split = new BorderPane();
    private chatapp.server.ChatClientWrapper socketClient;

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
        // Show loading state?
        // userList.getUserListView().setPlaceholder(new
        // javafx.scene.control.Label("Loading..."));

        DbTask<java.util.List<User>> task = new DbTask<>(() -> {
            return FriendShipDAO.getFriendsList(currentUser.getId());
        });

        task.setOnSucceeded(e -> {
            ObservableList<User> friends = FXCollections.observableArrayList(task.getValue());
            userList.getUserListView().setItems(friends);

            userList.getUserListView().setCellFactory(param -> {
                UserFriendListCell cell = new UserFriendListCell();
                cell.getSendRequestButton().setVisible(false); // Default hide request button in Friends list
                cell.getChatButton().setVisible(true);
                cell.setOnChat(u -> handleChat(u));

                cell.setOnCreateGroup(u -> handleCreateGroup(u));

                cell.getDeleteButton().setText("Unfriend");
                cell.setOnDelete(u -> handleUnfriend(u));

                cell.setOnBlock(u -> handleBlock(u));
                return cell;
            });
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void loadRequests() {
        currentMode = Mode.REQUESTS;

        DbTask<java.util.List<User>> task = new DbTask<>(() -> {
            return FriendShipDAO.getPendingRequests(currentUser.getId());
        });

        task.setOnSucceeded(e -> {
            ObservableList<User> requests = FXCollections.observableArrayList(task.getValue());
            userList.getUserListView().setItems(requests);

            userList.getUserListView().setCellFactory(param -> {
                FriendReqCell cell = new FriendReqCell();
                cell.setOnAccept(u -> handleAccept(u));
                cell.setOnDecline(u -> handleDecline(u));
                return cell;
            });
        });

        new Thread(task).start();
    }

    private void loadOnline() {
        currentMode = Mode.ONLINE;

        DbTask<java.util.List<User>> task = new DbTask<>(() -> {
            return FriendShipDAO.getFriendsList(currentUser.getId());
        });

        task.setOnSucceeded(e -> {
            // Filter locally on UI thread or background? Background is better if list is
            // huge.
            // But User objects need to be clean.
            ObservableList<User> friends = FXCollections.observableArrayList(task.getValue());
            ObservableList<User> onlineFriends = friends.filtered(User::isOnline);
            userList.getUserListView().setItems(onlineFriends);

            userList.getUserListView().setCellFactory(param -> {
                UserCanChatCell cell = new UserCanChatCell();
                cell.setOnChat(u -> handleChat(u));
                cell.setOnCreateGroup(u -> handleCreateGroup(u));
                return cell;
            });
        });

        new Thread(task).start();
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
                cell.getChatButton().setVisible(false); // Hide chat/group here initially or maybe enable?
                // For global search entry point (pre-type):
                // keep it simple until search is done.
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
                DbTask<java.util.List<User>> task = new DbTask<>(() -> {
                    return FriendShipDAO.searchUsers(query, currentUser.getId());
                });

                task.setOnSucceeded(e -> {
                    ObservableList<User> results = FXCollections.observableArrayList(task.getValue());
                    userList.getUserListView().setItems(results);

                    userList.getUserListView().setCellFactory(param -> {
                        return new UserFriendListCell() {
                            @Override
                            protected void updateItem(User user, boolean empty) {
                                super.updateItem(user, empty);
                                if (empty || user == null) {
                                    setGraphic(null);
                                    return;
                                }

                                String rel = FriendShipDAO.getRelationship(currentUser.getId(), user.getId());

                                getDeleteButton().setVisible(false);
                                getBlockButton().setVisible(false);
                                getSendRequestButton().setVisible(false);
                                getCreateGroupButton().setVisible(false);
                                getChatButton().setVisible(false);

                                if ("friends".equals(rel)) {
                                    getChatButton().setVisible(true);
                                    getChatButton().setOnAction(event -> handleChat(user));

                                    getCreateGroupButton().setVisible(true);
                                    getCreateGroupButton().setOnAction(event -> handleCreateGroup(user));

                                    getBlockButton().setVisible(true);
                                    getBlockButton().setText("Block");
                                    getBlockButton().setOnAction(event -> handleBlock(user));

                                    getDeleteButton().setVisible(true);
                                    getDeleteButton().setText("Unfriend");
                                    getDeleteButton().setOnAction(event -> handleUnfriend(user));

                                } else if ("blocked".equals(rel)) {
                                    // Check if I am the one who blocked?
                                    // status is 'blocked' only for the pair.
                                    // requester_id is the one who took action (blocked).
                                    // We need to fetch relationship details if we want to be sure, but
                                    // getRelationship returns string.
                                    // Assumption: searchUsers hides users who blocked ME. So this must be someone I
                                    // blocked.
                                    getBlockButton().setVisible(true);
                                    getBlockButton().setText("Unblock");
                                    getBlockButton().setOnAction(event -> handleUnblock(user));

                                } else if ("pending_sent".equals(rel)) {
                                    getSendRequestButton().setText("Sent");
                                    getSendRequestButton().setDisable(true);
                                    getSendRequestButton().setVisible(true);
                                    getBlockButton().setVisible(true);
                                    getBlockButton().setText("Block");
                                    getBlockButton().setOnAction(event -> handleBlock(user));
                                } else if ("pending_received".equals(rel)) {
                                    getSendRequestButton().setText("Accept");
                                    getSendRequestButton().setVisible(true);
                                    getSendRequestButton().setOnAction(event -> handleAccept(user));
                                    getBlockButton().setVisible(true);
                                    getBlockButton().setText("Block");
                                    getBlockButton().setOnAction(event -> handleBlock(user));
                                } else if ("none".equals(rel)) {
                                    // Chat and Group are now allowed for non-friends too
                                    getChatButton().setVisible(true);
                                    getChatButton().setOnAction(event -> handleChat(user));

                                    getCreateGroupButton().setVisible(true);
                                    getCreateGroupButton().setOnAction(event -> handleCreateGroup(user));

                                    getSendRequestButton().setText("Add");
                                    getSendRequestButton().setVisible(true);
                                    getSendRequestButton().setDisable(false);
                                    getSendRequestButton().setOnAction(event -> handleSendRequest(user));

                                    getBlockButton().setVisible(true);
                                    getBlockButton().setText("Block");
                                    getBlockButton().setOnAction(event -> handleBlock(user));
                                }
                            }
                        };
                    });
                });

                new Thread(task).start();
            }
        } else {
            // For FRIENDS, REQUESTS, ONLINE: Filter currently loaded items logic...
            // Since we switched to loading via DB whenever mode changes, the list is fresh.
            // We can just filter the current items in the ListView.
            // Ideally we should cache the full list when loading.

            // Simplification: Trigger a background reload + filter? Or assume items are
            // already there?
            // If we just filter, we need the original list.
            // Let's reload + filter in background for correctness.

            DbTask<java.util.List<User>> task = new DbTask<>(() -> {
                java.util.List<User> all = new java.util.ArrayList<>();
                if (currentMode == Mode.FRIENDS) {
                    all = FriendShipDAO.getFriendsList(currentUser.getId());
                } else if (currentMode == Mode.REQUESTS) {
                    all = FriendShipDAO.getPendingRequests(currentUser.getId());
                } else if (currentMode == Mode.ONLINE) {
                    all = FriendShipDAO.getFriendsList(currentUser.getId());
                    // Filter online in loop or steam
                    // We can't use FXCollections.filtered in background thread on User object
                    // easily if it binds?
                    // User object is POJO.
                    java.util.List<User> onl = new java.util.ArrayList<>();
                    for (User u : all)
                        if (u.isOnline())
                            onl.add(u);
                    all = onl;
                }

                if (!query.isEmpty()) {
                    java.util.List<User> filtered = new java.util.ArrayList<>();
                    for (User u : all) {
                        if ((u.getUsername() != null && u.getUsername().toLowerCase().contains(query)) ||
                                (u.getDisplayName() != null && u.getDisplayName().toLowerCase().contains(query))) {
                            filtered.add(u);
                        }
                    }
                    return filtered;
                }
                return all;
            });

            task.setOnSucceeded(e -> {
                userList.getUserListView().setItems(FXCollections.observableArrayList(task.getValue()));
            });

            new Thread(task).start();
        }
    }

    // Action Handlers
    private void handleUnblock(User u) {
        boolean success = FriendShipDAO.unblockUser(currentUser.getId(), u.getId());
        if (success) {
            refreshFriendsList();
            if (currentMode == Mode.SEARCH)
                performSearch(); // Refresh search to update status
        } else {
            System.out.println("Failed to unblock " + u.getUsername());
        }
    }

    // Action Handlers
    private void handleUnfriend(User u) {
        boolean success = FriendShipDAO.removeFriend(currentUser.getId(), u.getId());
        if (success) {
            refreshFriendsList();
            if (currentMode == Mode.SEARCH)
                performSearch();
        } else {
            System.out.println("Failed to unfriend " + u.getUsername());
        }
    }

    private void handleBlock(User u) {
        boolean success = FriendShipDAO.blockUser(currentUser.getId(), u.getId());
        if (success) {
            refreshFriendsList();
            if (currentMode == Mode.SEARCH)
                performSearch(); // Refresh search to hide or update status
        } else {
            System.out.println("Failed to block " + u.getUsername());
        }
    }

    private void handleAccept(User u) {
        boolean success = FriendShipDAO.acceptFriendRequest(currentUser.getId(), u.getId());
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
        boolean success = FriendShipDAO.removeFriend(currentUser.getId(), u.getId());
        if (success) {
            loadRequests();
            if (currentMode == Mode.SEARCH)
                performSearch();
        } else {
            System.out.println("Failed to decline request from " + u.getUsername());
        }
    }

    private void handleSendRequest(User u) {
        boolean success = FriendShipDAO.sendFriendRequest(currentUser.getId(), u.getId());
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
        CreateGroupView view = new CreateGroupView();

        // Load friends
        java.util.List<User> friends = FriendShipDAO.getFriendsList(currentUser.getId());
        view.getFriendsListView().getItems().addAll(friends);

        // Pre-select target user
        if (u != null) {
            view.getFriendsListView().getSelectionModel().select(u);
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Create Group");

        stage.setScene(new Scene(view));
        stage.setWidth(500);
        stage.setHeight(600);

        view.getCancelButton().setOnAction(e -> stage.close());

        view.getCreateButton().setOnAction(e -> {
            String name = view.getGroupNameField().getText().trim();
            ObservableList<User> selected = view.getFriendsListView().getSelectionModel().getSelectedItems();

            if (name.isEmpty()) {
                // simple alert or error
                System.out.println("Group name empty");
                return;
            }
            if (selected.isEmpty()) {
                System.out.println("No members selected");
                return;
            }

            java.util.List<java.util.UUID> memberIds = new java.util.ArrayList<>();
            for (User member : selected) {
                memberIds.add(member.getId());
            }

            Conversation conv = ConversationDAO.createChatGroup(name, currentUser.getId(), memberIds);
            if (conv != null) {
                System.out.println("Group created: " + conv.getId());
                stage.close();
                // Open Chat with Group
                if (onOpenChat != null) {
                    onOpenChat.accept(new GroupUser(conv));
                }

                // Notify members
                if (socketClient != null) {
                    for (java.util.UUID mid : memberIds) {
                        if (!mid.equals(currentUser.getId())) {
                            socketClient.notifyUser(mid);
                        }
                    }
                }
            } else {
                System.out.println("Failed to create group");
            }
        });

        stage.showAndWait();
    }

    public Tab getTab() {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setContent(split);
        tab.setText("Friends");
        return tab;
    }

    public void setupSocket(chatapp.server.ChatClientWrapper socketClient) {
        this.socketClient = socketClient;
        if (socketClient != null) {
            socketClient.setOnUserOnline(userId -> {
                javafx.application.Platform.runLater(() -> updateUserStatus(userId, true));
            });
            socketClient.setOnUserOffline(userId -> {
                javafx.application.Platform.runLater(() -> updateUserStatus(userId, false));
            });
        }
    }

    private void updateUserStatus(java.util.UUID userId, boolean isOnline) {
        ObservableList<User> items = userList.getUserListView().getItems();
        if (items == null)
            return;

        boolean found = false;
        for (User u : items) {
            if (u.getId().equals(userId)) {
                u.setOnline(isOnline);
                found = true;
            }
        }

        if (found) {
            userList.getUserListView().refresh();
            // If in ONLINE mode, we might need to remove connection if offline, or add if
            // online?
            if (currentMode == Mode.ONLINE) {
                loadOnline(); // Reload list to correctly filter
            }
        }
    }
}