package chatapp.controllers;

import chatapp.dao.FriendShipDAO;
import chatapp.models.User;
import chatapp.views.FriendOptionView;
import chatapp.views.UserListView;
import chatapp.views.cells.FriendReqCell;
import chatapp.utils.DbTask;
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

    private enum Mode {
        FRIENDS, REQUESTS, ONLINE, SEARCH
    }

    private Mode currentMode = Mode.FRIENDS;

    public FriendController(User user) {
        this.currentUser = user;
        fov = new FriendOptionView();
        userList = new UserListView();

        loadFriends();
        fov.setActive(fov.getFriendBtn());

        wireOptions();
        wireSearch();

        split.setLeft(fov);
        split.setCenter(userList);
    }

    private void loadFriends() {
        currentMode = Mode.FRIENDS;
        refreshFriendsList();
    }

    private void refreshFriendsList() {
        DbTask<java.util.List<User>> task = new DbTask<>(() -> {
            return FriendShipDAO.getFriendsList(currentUser.getId());
        });

        task.setOnSucceeded(e -> {
            ObservableList<User> friends = FXCollections.observableArrayList(task.getValue());
            userList.getUserListView().setItems(friends);

            userList.getUserListView().setCellFactory(param -> {
                UserFriendListCell cell = new UserFriendListCell();
                cell.getSendRequestButton().setVisible(false);
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
        fov.getFriendBtn().setOnAction(e -> {
            loadFriends();
            fov.setActive(fov.getFriendBtn());
        });
        fov.getFriendReqBtn().setOnAction(e -> {
            loadRequests();
            fov.setActive(fov.getFriendReqBtn());
        });
        fov.getOnlineBtn().setOnAction(e -> {
            loadOnline();
            fov.setActive(fov.getOnlineBtn());
        });
        fov.getSearchBtn().setOnAction(e -> {
            fov.setActive(fov.getSearchBtn());
            currentMode = Mode.SEARCH;
            userList.getUserListView().setItems(FXCollections.observableArrayList());
            userList.getFilterField().clear();
            userList.setToChatListCellFactory();
            userList.getUserListView().setCellFactory(param -> {
                UserFriendListCell cell = new UserFriendListCell();
                cell.getDeleteButton().setVisible(false);
                cell.getChatButton().setVisible(false);
                cell.setOnSendRequest(u -> handleSendRequest(u));
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
            DbTask<java.util.List<User>> task = new DbTask<>(() -> {
                java.util.List<User> all = new java.util.ArrayList<>();
                if (currentMode == Mode.FRIENDS) {
                    all = FriendShipDAO.getFriendsList(currentUser.getId());
                } else if (currentMode == Mode.REQUESTS) {
                    all = FriendShipDAO.getPendingRequests(currentUser.getId());
                } else if (currentMode == Mode.ONLINE) {
                    all = FriendShipDAO.getFriendsList(currentUser.getId());
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

    private void handleUnblock(User u) {
        boolean success = FriendShipDAO.unblockUser(currentUser.getId(), u.getId());
        if (success) {
            refreshFriendsList();
            if (currentMode == Mode.SEARCH)
                performSearch();
        } else {
            System.out.println("Failed to unblock " + u.getUsername());
        }
    }

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
                performSearch();
        } else {
            System.out.println("Failed to block " + u.getUsername());
        }
    }

    private void handleAccept(User u) {
        boolean success = FriendShipDAO.acceptFriendRequest(currentUser.getId(), u.getId());
        if (success) {
            loadRequests();
            if (currentMode == Mode.SEARCH)
                performSearch();
        } else {
            System.out.println("Failed to accept request from " + u.getUsername());
        }
    }

    private void handleDecline(User u) {
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
                performSearch();
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

        java.util.List<User> friends = FriendShipDAO.getFriendsList(currentUser.getId());
        view.getFriendsListView().getItems().addAll(friends);

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
                if (onOpenChat != null) {
                    onOpenChat.accept(new GroupUser(conv));
                }
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
            if (currentMode == Mode.ONLINE) {
                loadOnline();
            }
        }
    }
}