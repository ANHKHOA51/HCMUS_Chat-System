package chatapp.controllers;

import chatapp.views.ContactListView;
import chatapp.views.MessageView;
import javafx.collections.FXCollections;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public class MessageController {
    private ContactListView contact;
    private MessageView msg;
    private BorderPane split = new BorderPane();

    public MessageController() {
        contact = new ContactListView(FXCollections.observableArrayList());
        contact.setPrefWidth(240);
        msg = null;
        split.setCenter(msg);
        split.setLeft(contact);

        contact.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                msg = new MessageView();
                split.setCenter(msg);
                msg.getTextField().setOnAction(event -> sendMessage());
                msg.getButton().setOnAction(event -> sendMessage());
            }
        });
    }

    private void sendMessage() {
        String text = msg.getTextField().getText().trim();
        if (!text.isEmpty()) {
            msg.sendMessage(text);
            msg.getTextField().clear();
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
