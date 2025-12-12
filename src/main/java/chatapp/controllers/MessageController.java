package chatapp.controllers;

import java.util.HashMap;
import java.util.Map;

import chatapp.models.Conversation;
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

    // send message for a specific MessageView / contactId
    private void sendMessageFor(MessageView mv, String contactId) {
        if (mv == null)
            return;
        String text = mv.getTextField().getText();
        if (text == null)
            return;
        text = text.trim();
        if (!text.isEmpty()) {
            UUID targetId = UUID.fromString(contactId);
            Conversation conv = chatapp.dao.ConversationDAO.getPrivateConversation(user.getId(), targetId);
            if (conv == null) {
                conv = chatapp.dao.ConversationDAO.createPrivateConversation(user.getId(), targetId);
            }
            if (conv != null) {
                chatapp.dao.MessageDAO.send(conv.getId(), user.getId(), text);
                mv.sendMessage(text);
                mv.getTextField().clear();
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
        Conversation conv = chatapp.dao.ConversationDAO.getPrivateConversation(user.getId(), targetUser.getId());
        MessageView mv = views.computeIfAbsent(targetUser.getId().toString(), k -> {
            MessageView view = new MessageView();
            view.getTextField().setOnAction(e -> sendMessageFor(view, targetUser.getId().toString()));
            view.getButton().setOnAction(e -> sendMessageFor(view, targetUser.getId().toString()));
            return view;
        });

        mv.clearMessages();
        if (conv != null) {
            java.util.List<chatapp.models.Message> messages = chatapp.dao.MessageDAO.getMessages(conv.getId());
            for (chatapp.models.Message m : messages) {
                boolean isMine = m.getSenderId().equals(user.getId());
                mv.addMessage(m.getContent(), isMine);
            }
        }
        split.setCenter(mv);
    }

    public Tab getTab() {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setContent(split);
        tab.setText("Message");
        return tab;
    }
}