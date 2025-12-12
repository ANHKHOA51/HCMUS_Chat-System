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
        contact = new ContactListView(FXCollections.observableArrayList(friends));
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

        javafx.scene.layout.VBox leftPane = new javafx.scene.layout.VBox(globalSearch, contact);
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
                        // entry.getKey() is conversationalist ID (User ID or Group ID).
                        // We can force refresh all.
                        String key = entry.getKey();
                        try {
                            UUID uuid = UUID.fromString(key);
                            // Need to reconstruct User object slightly better or refactor refreshChat to
                            // take UUID
                            // refreshChat takes User.
                            // Let's refactor refreshChat to be more robust or just call it if we can
                            // resolve user.
                            // For now, only Direct Private Chat refresh implemented reliably.
                        } catch (Exception e) {
                        }
                    }
                });
            });
        }
    }

    // send message for a specific MessageView / contact
    private void sendMessageFor(MessageView mv, User contact) {
        if (mv == null)
            return;
        String text = mv.getTextField().getText();
        if (text == null)
            return;
        text = text.trim();
        if (!text.isEmpty()) {
            if (contact instanceof GroupUser) {
                // It is a group, contact.getId() is the conversation ID
                chatapp.models.Message sentMsg = chatapp.dao.MessageDAO.send(contact.getId(), user.getId(), text);
                if (sentMsg != null) {
                    mv.sendMessage(sentMsg);
                    mv.getTextField().clear();

                    // Notify Group Members
                    if (socketClient != null) {
                        java.util.List<UUID> memberIds = chatapp.dao.ConversationDAO
                                .getConversationMemberIds(contact.getId());
                        for (UUID memberId : memberIds) {
                            if (!memberId.equals(user.getId())) {
                                socketClient.notifyUser(memberId);
                            }
                        }
                    }
                }
            } else {
                UUID targetId = contact.getId();
                Conversation conv = chatapp.dao.ConversationDAO.getPrivateConversation(user.getId(), targetId);
                if (conv == null) {
                    conv = chatapp.dao.ConversationDAO.createPrivateConversation(user.getId(), targetId);
                }
                if (conv != null) {
                    chatapp.models.Message sentMsg = chatapp.dao.MessageDAO.send(conv.getId(), user.getId(), text);
                    if (sentMsg != null) {
                        mv.sendMessage(sentMsg);
                        mv.getTextField().clear();

                        // Notify Receiver
                        if (socketClient != null) {
                            socketClient.notifyUser(targetId);
                        }
                    }
                }
            }
        }
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
                    refreshChat(targetUser, view); // Reset
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
                        view.clearMessages();
                        for (chatapp.models.Message m : task.getValue()) {
                            boolean isMine = m.getSenderId().equals(user.getId());
                            view.addMessage(m, isMine);
                        }
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
                    mv.addMessage(m, isMine);
                }
            });
            new Thread(task).start();
        }
    }

    public Tab getTab() {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setContent(split);
        tab.setText("Message");
        return tab;
    }
}