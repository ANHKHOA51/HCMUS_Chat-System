package chatapp.controllers;

import chatapp.views.ContactListView;
import chatapp.views.MessageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

public class MessageController {
    private ContactListView contact;
    private MessageView msg;
    private SplitPane split = new SplitPane();

    public MessageController() {
        contact = new ContactListView(FXCollections.observableArrayList("alice", "bob"));
        msg = null;

        split.getItems().addAll(contact, new Pane());
        split.setDividerPositions(0.2);

        contact.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                msg = new MessageView();
                split.getItems().add(msg);
                split.getItems().remove(1);
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
