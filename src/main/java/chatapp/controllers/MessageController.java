package chatapp.controllers;

import java.util.HashMap;
import java.util.Map;

import chatapp.models.Conversation;
import chatapp.models.GroupUser;
import chatapp.models.User;
import java.util.UUID;
import chatapp.views.ContactListView;
import chatapp.views.MessageView;
import javafx.collections.FXCollections;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import chatapp.views.GroupInfoView;
import chatapp.models.GroupMember;
import chatapp.dao.ConversationDAO;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MessageController {
    User user;
    private ContactListView contact;
    private BorderPane split = new BorderPane();
    private final Map<String, MessageView> views = new HashMap<>();
    private chatapp.server.ChatClientWrapper socketClient;

    public MessageController() {
        this.user = null;
        contact.setPrefWidth(300);
        split.setLeft(contact);
        split.setCenter(null);
    }

    public MessageController(User u) {
        this.user = u;
        // Load friends instead of mock data
        java.util.List<User> friends = chatapp.dao.FriendShipDAO.getFriendsList(u.getId());
        java.util.List<chatapp.models.GroupUser> groups = ConversationDAO.getGroupsForUser(u.getId());
        javafx.collections.ObservableList<User> allContacts = FXCollections.observableArrayList();
        allContacts.addAll(friends);
        allContacts.addAll(groups);
        contact = new ContactListView(allContacts);
        contact.setPrefWidth(300);

        split.setLeft(contact);
        split.setCenter(null);

        // Global Search UI
        javafx.scene.control.TextField globalSearch = new javafx.scene.control.TextField();
        globalSearch.setPromptText("Search all chats...");
        javafx.scene.control.ListView<chatapp.models.Message> searchResults = new javafx.scene.control.ListView<>();
        searchResults.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(chatapp.models.Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getContent() + " (" + item.getCreatedAt() + ")");
                }
            }
        });

        searchResults.setOnMouseClicked(e -> {
            chatapp.models.Message selected = searchResults.getSelectionModel().getSelectedItem();
            if (selected != null) {
                handleGlobalSearchResult(selected);
            }
        });

        // Create Group Button
        javafx.scene.control.Button createGroupBtn = new javafx.scene.control.Button("+");
        createGroupBtn.setTooltip(new javafx.scene.control.Tooltip("Create Group"));
        createGroupBtn.setOnAction(e -> handleCreateGroup());

        javafx.scene.layout.HBox topBar = new javafx.scene.layout.HBox(5, globalSearch, createGroupBtn);
        javafx.scene.layout.HBox.setHgrow(globalSearch, javafx.scene.layout.Priority.ALWAYS);

        javafx.scene.layout.VBox leftPane = new javafx.scene.layout.VBox(topBar, contact);
        javafx.scene.layout.VBox.setVgrow(contact, javafx.scene.layout.Priority.ALWAYS);
        split.setLeft(leftPane);

        globalSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                leftPane.getChildren().set(1, contact);
            } else {
                if (!leftPane.getChildren().contains(searchResults)) {
                    leftPane.getChildren().set(1, searchResults);
                    javafx.scene.layout.VBox.setVgrow(searchResults, javafx.scene.layout.Priority.ALWAYS);
                }

                chatapp.utils.DbTask<java.util.List<chatapp.models.Message>> task = new chatapp.utils.DbTask<>(() -> {
                    return chatapp.dao.MessageDAO.searchAllMessages(u.getId(), newVal.trim());
                });
                task.setOnSucceeded(ev -> {
                    searchResults.setItems(FXCollections.observableArrayList(task.getValue()));
                });
                new Thread(task).start();
            }
        });

        MultipleSelectionModel<User> sel = contact.getSelectionModel();
        if (sel != null) {
            sel.selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    // Load chat for the selected user
                    // reuse per-contact view so state is preserved or updated
                    openChatWith(newVal);
                }
            });
        }

        contact.setOnReportSpam(this::handleReportSpam);
    }

    private void handleReportSpam(User u) {
        if (u == null)
            return;
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Report Spam");
        dialog.setHeaderText("Report " + (u.getDisplayName() != null ? u.getDisplayName() : u.getUsername()));
        dialog.setContentText("Reason:");

        dialog.showAndWait().ifPresent(reason -> {
            if (reason.trim().isEmpty()) {
                // optional: show alert for empty reason
                return;
            }
            // Add report
            boolean success = chatapp.models.Report.addReport(user.getId(), u.getId(), reason);
            if (success) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Report Submitted");
                alert.setHeaderText(null);
                alert.setContentText("Thank you. The user has been reported.");
                alert.showAndWait();
            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to submit report.");
                alert.showAndWait();
            }
        });
    }

    public void setupSocket(chatapp.server.ChatClientWrapper socketClient) {
        this.socketClient = socketClient;
        if (this.socketClient != null) {
            this.socketClient.setOnRefreshChat(senderId -> {
                // If we are currently viewing chat with senderId, refresh
                // Determine which view to refresh
                // Strategy: Refresh ALL active views where the other party is senderId?
                // Or just the currently visible one?
                // Simpler: iterate views. If view key matches senderId (for private), refresh.
                // For group, we need to know if senderId is a member? "REFRESH" from server
                // only sends senderId.
                // Protocol update required for Group Chat Refresh?
                // Currently ChatServer protocol: REFRESH:senderId.
                // If I receive message in GroupID from SenderID.
                // The NOTIFY sent was: NOTIFY:targetId. (TargetID = GroupID or UserID).
                // Server sends REFRESH:senderId TO targetId.
                // Wait.
                // If I send to Group A. I NOTIFY Group A ID.
                // Server looks up Group A ID => connections? NO. Server only maps UserID <->
                // Conn.
                // Group Logic missing in Server.
                // HYBRID MODEL:
                // Client sends NOTIFY:TargetID.
                // If TargetID is a Group (how does server know?). Server doesn't know DB.
                // PROBLEM.

                // ADJUSTMENT:
                // Client must NOTIFY ALL RECIPIENTS manually? Or Server learns groups.
                // Simpler: Client iterates Group Members. Sends NOTIFY:MemberID for each
                // member.
                // Back in sendMessageFor:
                // If Group: Get Members -> Loop -> socket.notifyUser(memberID).
                // If Private: socket.notifyUser(targetID).

                // Handling REFRESH at recipient:
                // Recipient receives REFRESH:senderId.
                // Client checks if currently open chat involves senderId.
                // 1. Private Chat with senderId -> Refresh.
                // 2. Group Chat where senderId is a member -> Refresh?
                // We don't easily know which group senderId is messaging in unless we check DB
                // or payload has GroupID.

                // For now, let's supporting Private Chat Realtime perfectly.
                // For Group Chat, if I receive REFRESH:senderId, I should probably check recent
                // messages or just refresh active view?
                // Let's implement: If we are viewing a chat with senderId, refresh.

                javafx.application.Platform.runLater(() -> {
                    reloadContactList(); // Sync contacts/groups

                    MessageView mv = views.get(senderId.toString());
                    if (mv != null) {
                        // It is a private chat with this user
                        User u = new User();
                        u.setId(senderId); // Dummy user for refresh
                        refreshChat(u, mv);
                    }

                    // Also refresh any group chat? Expensive to check all.
                    // Hack: Just refresh the currently focused chat if possible?
                    // Let's iterate all open views and refresh?
                    // Safe approach for V1.
                    for (Map.Entry<String, MessageView> entry : views.entrySet()) {
                        String key = entry.getKey();
                        try {
                            // If key is ID. Refresh it.
                            MessageView v = entry.getValue();
                            // We need user object.
                            // Hack: Just call refreshChat with dummy User with ID?
                            // refreshChat uses ID.
                            User dummy = new User();
                            dummy.setId(UUID.fromString(key));
                            // Determine if group?
                            // We can check if key is in groups list
                            boolean isGroup = false;
                            for (User c : contact.getItems()) {
                                if (c.getId().toString().equals(key) && c instanceof GroupUser) {
                                    isGroup = true;
                                    dummy = c; // Use real object
                                    break;
                                }
                            }
                            refreshChat(dummy, v);
                        } catch (Exception e) {
                        }
                    }
                });
            });
        }
    }

    // send message for a specific MessageView / contact
    // send message for a specific MessageView / contact
    private void sendMessageFor(MessageView mv, User contact) {
        if (mv == null)
            return;
        String text = mv.getTextField().getText();
        if (text == null)
            return;
        text = text.trim();
        if (text.isEmpty())
            return;

        // Optimistic UI Update
        UUID msgId = UUID.randomUUID();
        chatapp.models.Message tempMsg = new chatapp.models.Message();
        tempMsg.setId(msgId);
        tempMsg.setSenderId(user.getId());
        tempMsg.setContent(text);
        tempMsg.setCreatedAt(java.time.LocalDateTime.now());
        // We set conversationId later or use dummy for UI
        tempMsg.setConversationId(null);

        mv.sendMessage(tempMsg);
        mv.getTextField().clear();

        // Async Database & Notify
        final String content = text;
        new Thread(() -> {
            UUID cid = null;
            if (contact instanceof GroupUser) {
                cid = contact.getId();
            } else {
                Conversation conv = chatapp.dao.ConversationDAO.getPrivateConversation(user.getId(), contact.getId());
                if (conv == null) {
                    conv = chatapp.dao.ConversationDAO.createPrivateConversation(user.getId(), contact.getId());
                }
                cid = (conv != null) ? conv.getId() : null;
            }

            if (cid != null) {
                // Update temp message with real CID if we wanted to sync state, but purely for
                // DB send:
                chatapp.dao.MessageDAO.send(msgId, cid, user.getId(), content);

                // Notify
                if (socketClient != null) {
                    if (contact instanceof GroupUser) {
                        java.util.List<UUID> memberIds = chatapp.dao.ConversationDAO.getConversationMemberIds(cid);
                        for (UUID memberId : memberIds) {
                            if (!memberId.equals(user.getId())) {
                                socketClient.notifyUser(memberId);
                            }
                        }
                    } else {
                        socketClient.notifyUser(contact.getId());
                    }
                }
            } else {
                // Failed to resolve conversation ID
                System.err.println("Failed to resolve conversation ID for message");
            }
        }).start();
    }

    public void openChatWith(User targetUser) {
        // Ensure user is in the list
        if (!contact.getItems().contains(targetUser)) {
            contact.getItems().add(targetUser);
            // Select the newly added user
            contact.getSelectionModel().select(targetUser);
            // The listener will trigger openChatWith again, so we can return?
            // Better to decouple: openChatWith ensures selection, and selection triggers
            // loadChatView.
            // But openChatWith is called by App.java.
            return;
        }

        // If not selected, select it (this triggers listener)
        if (!targetUser.equals(contact.getSelectionModel().getSelectedItem())) {
            contact.getSelectionModel().select(targetUser);
            return;
        }

        // Logic to load messages and set view (called by listener or effectively here)
        loadChatView(targetUser);
    }

    private void loadChatView(User targetUser) {

        MessageView mv = views.computeIfAbsent(targetUser.getId().toString(), k -> {
            MessageView view = new MessageView();
            view.getTextField().setOnAction(e -> sendMessageFor(view, targetUser));
            view.getButton().setOnAction(e -> sendMessageFor(view, targetUser));

            boolean isGroup = targetUser instanceof GroupUser;
            view.getInfoButton().setVisible(isGroup); // Only show for groups for now
            if (isGroup) {
                view.getInfoButton().setOnAction(e -> handleGroupInfo((GroupUser) targetUser));
            }

            // Delete Message Handler
            view.setOnDeleteMessage(msg -> {
                boolean success = chatapp.dao.MessageDAO.deleteMessage(msg.getId());
                if (success)
                    refreshChat(targetUser, view);
            });

            // Clear History Handler
            view.setOnClearHistory(() -> {
                UUID cid;
                if (targetUser instanceof GroupUser)
                    cid = targetUser.getId();
                else {
                    Conversation c = chatapp.dao.ConversationDAO.getPrivateConversation(user.getId(),
                            targetUser.getId());
                    cid = (c != null) ? c.getId() : null;
                }

                if (cid != null) {
                    boolean success = chatapp.dao.MessageDAO.deleteAllMessages(cid);
                    if (success)
                        refreshChat(targetUser, view);
                }
            });

            // Search Context Handler
            view.setOnSearch(query -> {
                if (query == null || query.trim().isEmpty()) {
                    // refreshChat(targetUser, view); // Reset
                    // Don't reset chat, just clear search results
                    view.showSearchResults(null);
                    return;
                }

                UUID cid;
                if (targetUser instanceof GroupUser)
                    cid = targetUser.getId();
                else {
                    Conversation c = chatapp.dao.ConversationDAO.getPrivateConversation(user.getId(),
                            targetUser.getId());
                    cid = (c != null) ? c.getId() : null;
                }

                if (cid != null) {
                    chatapp.utils.DbTask<java.util.List<chatapp.models.Message>> task = new chatapp.utils.DbTask<>(
                            () -> {
                                return chatapp.dao.MessageDAO.searchMessages(cid, query);
                            });
                    task.setOnSucceeded(e -> {
                        view.showSearchResults(task.getValue());
                    });
                    new Thread(task).start();
                }
            });

            return view;
        });

        // Start Polling - REMOVED for WebSocket
        // startPolling(targetUser, mv);

        // We still fetch initial messages
        refreshChat(targetUser, mv);
        split.setCenter(mv);
    }

    private void handleGlobalSearchResult(chatapp.models.Message msg) {
        UUID convId = msg.getConversationId();
        Conversation conv = chatapp.dao.ConversationDAO.getConversation(convId);

        if (conv == null) {
            System.out.println("Conversation not found: " + convId);
            return;
        }

        User target = null;
        if (conv.isGroup()) {
            GroupUser g = new GroupUser(conv);
            target = g;
        } else {
            java.util.List<UUID> memberIds = chatapp.dao.ConversationDAO.getConversationMemberIds(convId);
            for (UUID memberId : memberIds) {
                if (!memberId.equals(user.getId())) {
                    target = chatapp.dao.UserDAO.getUser("id", memberId);
                    break;
                }
            }
        }

        if (target != null) {
            openChatWith(target);
        }
    }

    private void refreshChat(User targetUser, MessageView mv) {
        UUID conversationId;
        if (targetUser instanceof GroupUser) {
            conversationId = targetUser.getId();
        } else {
            Conversation conv = chatapp.dao.ConversationDAO.getPrivateConversation(user.getId(), targetUser.getId());
            conversationId = (conv != null) ? conv.getId() : null;
        }

        // mv.clearMessages(); // REMOVED PREMATURE CLEAR TO FIX BLINK
        if (conversationId != null) {
            chatapp.utils.DbTask<java.util.List<chatapp.models.Message>> task = new chatapp.utils.DbTask<>(() -> {
                return chatapp.dao.MessageDAO.getMessages(conversationId);
            });
            task.setOnSucceeded(e -> {
                mv.clearMessages(); // Clear again just in case
                for (chatapp.models.Message m : task.getValue()) {
                    boolean isMine = m.getSenderId().equals(user.getId());
                    mv.addMessage(m, isMine, resolveSenderName(m.getSenderId(), targetUser));
                }
            });
            new Thread(task).start();
        }
    }

    private void handleGroupInfo(GroupUser group) {
        GroupInfoView view = new GroupInfoView();

        // Setup Stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Group Info");
        stage.setResizable(false);
        stage.setScene(new Scene(view));
        stage.setWidth(500);
        stage.setHeight(600);

        // Initial Load
        loadGroupData(group, view);

        // Actions
        view.getRenameBtn().setOnAction(e -> {
            String newName = view.getGroupNameField().getText().trim();
            if (!newName.isEmpty()) {
                boolean success = ConversationDAO.renameConversation(group.getId(), newName);
                if (success) {
                    group.setDisplayName(newName); // Update local object
                    loadGroupData(group, view); // Refresh
                    contact.refresh(); // Refresh contact list UI
                } else {
                    view.setError("Unsufficient permissions or error.");
                }
            }
        });

        view.getAddMemberBtn().setOnAction(e -> {
            // Pick a friend to add
            // Simple approach: ChoiceDialog with friends NOT in group
            java.util.List<User> friends = chatapp.dao.FriendShipDAO.getFriendsList(user.getId());
            java.util.List<GroupMember> currentMembers = ConversationDAO.getGroupMembers(group.getId());

            java.util.List<User> eligible = new java.util.ArrayList<>();
            for (User f : friends) {
                boolean exists = false;
                for (GroupMember m : currentMembers) {
                    if (m.getId().equals(f.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists)
                    eligible.add(f);
            }

            if (eligible.isEmpty()) {
                view.setError("No eligible friends to add.");
                return;
            }

            javafx.scene.control.Dialog<User> dialog = new javafx.scene.control.Dialog<>();
            dialog.setTitle("Add Member");
            dialog.setHeaderText("Choose a friend:");

            javafx.scene.control.ButtonType addButtonType = new javafx.scene.control.ButtonType("Add",
                    javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, javafx.scene.control.ButtonType.CANCEL);

            javafx.scene.control.ComboBox<User> combo = new javafx.scene.control.ComboBox<>(
                    FXCollections.observableArrayList(eligible));
            combo.setConverter(new javafx.util.StringConverter<User>() {
                @Override
                public String toString(User object) {
                    return object != null ? object.getDisplayName() : "";
                }

                @Override
                public User fromString(String string) {
                    return null;
                }
            });
            combo.getSelectionModel().selectFirst();

            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10,
                    new javafx.scene.control.Label("Friend:"), combo);
            dialog.getDialogPane().setContent(content);

            dialog.setResultConverter(b -> {
                if (b == addButtonType)
                    return combo.getSelectionModel().getSelectedItem();
                return null;
            });
            dialog.showAndWait().ifPresent(selected -> {
                boolean success = ConversationDAO.addMember(group.getId(), selected.getId());
                if (success) {
                    loadGroupData(group, view);
                } else {
                    view.setError("Failed to add member.");
                }
            });
        });

        view.getLeaveBtn().setOnAction(e -> {
            // Confirm?
            boolean success = ConversationDAO.removeMember(group.getId(), user.getId());
            if (success) {
                stage.close();
                contact.getItems().remove(group);
                split.setCenter(null);
            }
        });

        stage.showAndWait();
    }

    private void loadGroupData(GroupUser group, GroupInfoView view) {
        // Name
        view.getGroupNameField().setText(group.getDisplayName());

        // Members
        java.util.List<GroupMember> members = ConversationDAO.getGroupMembers(group.getId());
        view.getMembersListView().setItems(FXCollections.observableArrayList(members));

        // Check if current user is admin
        boolean isAdmin = false;
        for (GroupMember m : members) {
            if (m.getId().equals(user.getId()) && "admin".equals(m.getRole())) {
                isAdmin = true;
                break;
            }
        }

        final boolean finalIsAdmin = isAdmin;

        // Set permissions on view controls
        view.getRenameBtn().setDisable(!isAdmin);
        // view.getAddMemberBtn().setDisable(!isAdmin); // Anyone can add? Let's say yes
        // for now or restrict to admin.
        // Requirement 12.c just says "Thêm thành viên". 12.e says "Xoá: chỉ admin".
        // Let's assume Add is unrestricted or restricted.
        // Usually safer to restrict to admin, but "Create group" allows anyone to add
        // initial members.
        // Let's allow anyone to add.

        // Setup List Cell Factory for Actions (Kick/Promote)
        view.getMembersListView().setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(GroupMember item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(10);
                    javafx.scene.control.Label nameLbl = new javafx.scene.control.Label(
                            item.getDisplayName() + (item.getId().equals(user.getId()) ? " (You)" : ""));
                    javafx.scene.control.Label roleLbl = new javafx.scene.control.Label(item.getRole());
                    roleLbl.setStyle("-fx-text-fill: grey; -fx-font-size: 10px;");

                    javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
                    javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                    hbox.getChildren().addAll(nameLbl, roleLbl, spacer);

                    if (finalIsAdmin && !item.getId().equals(user.getId())) {
                        javafx.scene.control.Button kickBtn = new javafx.scene.control.Button("X");
                        kickBtn.setStyle("-fx-text-fill: red;");
                        kickBtn.setOnAction(ev -> {
                            boolean ok = ConversationDAO.removeMember(group.getId(), item.getId());
                            if (ok)
                                loadGroupData(group, view);
                        });
                        hbox.getChildren().add(kickBtn);

                        if (!"admin".equals(item.getRole())) {
                            javafx.scene.control.Button promoteBtn = new javafx.scene.control.Button("Promote");
                            promoteBtn.setOnAction(ev -> {
                                boolean ok = ConversationDAO.updateMemberRole(group.getId(), item.getId(), "admin");
                                if (ok)
                                    loadGroupData(group, view);
                            });
                            hbox.getChildren().add(promoteBtn);
                        }
                    }

                    setGraphic(hbox);
                }
            }
        });
    }

    public Tab getTab() {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setContent(split);
        tab.setText("Message");
        return tab;
    }

    private void reloadContactList() {
        // Run on UI thread? The caller (setupSocket) runs inside Platform.runLater
        // already.
        // But if called from elsewhere... safest to assume we need to manage thread or
        // caller does.
        // Since setupSocket does runLater, we are okay.

        java.util.List<User> friends = chatapp.dao.FriendShipDAO.getFriendsList(user.getId());
        java.util.List<chatapp.models.GroupUser> groups = ConversationDAO.getGroupsForUser(user.getId());

        User selected = contact.getSelectionModel().getSelectedItem();

        javafx.collections.ObservableList<User> items = contact.getItems();
        java.util.List<User> newList = new java.util.ArrayList<>(friends);
        newList.addAll(groups);
        items.setAll(newList);

        if (selected != null) {
            for (User u : items) {
                if (u.getId().equals(selected.getId())) {
                    contact.getSelectionModel().select(u);
                    break;
                }
            }
        }
        contact.refresh(); // Force refresh to ensure cells redraw
    }

    private void handleCreateGroup() {
        if (user == null)
            return;
        chatapp.views.CreateGroupView view = new chatapp.views.CreateGroupView(); // Need valid imports or fully
                                                                                  // qualified

        // Set CellFactory for display
        view.getFriendsListView().setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName() != null ? item.getDisplayName() : item.getUsername());
                }
            }
        });

        // Load friends
        java.util.List<User> friends = chatapp.dao.FriendShipDAO.getFriendsList(user.getId());
        view.getFriendsListView().getItems().addAll(friends);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Create Group");
        stage.setResizable(false);
        stage.setScene(new Scene(view));
        stage.setWidth(500);
        stage.setHeight(600);

        view.getCancelButton().setOnAction(e -> stage.close());

        view.getCreateButton().setOnAction(e -> {
            String name = view.getGroupNameField().getText().trim();
            javafx.collections.ObservableList<User> selected = view.getFriendsListView().getSelectionModel()
                    .getSelectedItems();

            if (name.isEmpty())
                return;
            if (selected.isEmpty())
                return;

            java.util.List<java.util.UUID> memberIds = new java.util.ArrayList<>();
            for (User member : selected) {
                memberIds.add(member.getId());
            }

            Conversation conv = ConversationDAO.createChatGroup(name, user.getId(), memberIds);
            if (conv != null) {
                stage.close();
                // Notify members
                if (socketClient != null) {
                    for (java.util.UUID mid : memberIds) {
                        if (!mid.equals(user.getId())) {
                            socketClient.notifyUser(mid);
                        }
                    }
                }
                // Reload local contact list
                reloadContactList();
                // Open the new group chat
                openChatWith(new GroupUser(conv));
            }
        });

        stage.showAndWait();
    }

    private String resolveSenderName(UUID senderId, User targetUser) {
        if (senderId.equals(user.getId()))
            return "You";
        // If private chat and sender is target
        if (targetUser != null && !(targetUser instanceof GroupUser) && targetUser.getId().equals(senderId)) {
            return targetUser.getDisplayName();
        }
        // Group chat or generic lookup
        User u = chatapp.dao.UserDAO.getUser("id", senderId);
        return (u != null && u.getDisplayName() != null && !u.getDisplayName().isEmpty()) ? u.getDisplayName()
                : (u != null ? u.getUsername() : "Unknown");
    }
}