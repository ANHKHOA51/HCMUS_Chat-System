package chatapp.views;

import chatapp.models.User;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CreateGroupView extends VBox {
    private TextField groupNameField;
    private ListView<User> friendsListView;
    private Button createButton;
    private Button cancelButton;

    public CreateGroupView() {
        setSpacing(10);
        setPadding(new Insets(20));
        setPrefSize(400, 500);

        Label nameLabel = new Label("Group Name:");
        groupNameField = new TextField();
        groupNameField.setPromptText("Enter group name...");

        Label friendsLabel = new Label("Select Members:");
        friendsListView = new ListView<>();
        friendsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        VBox.setVgrow(friendsListView, Priority.ALWAYS);

        HBox buttonBox = new HBox(10);
        createButton = new Button("Create");
        createButton.setDefaultButton(true);
        cancelButton = new Button("Cancel");
        buttonBox.getChildren().addAll(cancelButton, createButton);
        buttonBox.setStyle("-fx-alignment: center-right;");

        getChildren().addAll(nameLabel, groupNameField, friendsLabel, friendsListView, buttonBox);
    }

    public TextField getGroupNameField() {
        return groupNameField;
    }

    public ListView<User> getFriendsListView() {
        return friendsListView;
    }

    public Button getCreateButton() {
        return createButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}
