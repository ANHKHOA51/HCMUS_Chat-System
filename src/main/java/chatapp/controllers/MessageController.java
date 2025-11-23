package chatapp.controllers;

import java.util.HashMap;
import java.util.Map;

import chatapp.models.User;
import chatapp.test.MockData;
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
        contact = new ContactListView(MockData.mockUsers());
        contact.setPrefWidth(300);

        split.setLeft(contact);
        split.setCenter(null);

        MultipleSelectionModel<User> sel = (MultipleSelectionModel<User>) contact.getSelectionModel();
        if (sel != null) {
            sel.selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    // reuse per-contact view so state (history, draft) is preserved
                    MessageView view = views.computeIfAbsent(newVal.getId(), k -> {
                        MessageView mv = new MessageView();
                        // handlers capture mv and the contact id (user id string)
                        mv.getTextField().setOnAction(e -> sendMessageFor(mv, newVal.getId()));
                        mv.getButton().setOnAction(e -> sendMessageFor(mv, newVal.getId()));
                        return mv;
                    });
                    split.setCenter(view);
                } else {
                    split.setCenter(null);
                }
            });
        }
    }

    // send message for a specific MessageView / contactId
    private void sendMessageFor(MessageView mv, String contactId) {
        if (mv == null) return;
        String text = mv.getTextField().getText();
        if (text == null) return;
        text = text.trim();
        if (!text.isEmpty()) {
            mv.sendMessage(text);
            mv.getTextField().clear();
            // TODO: persist message to conversation store for contactId if needed
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