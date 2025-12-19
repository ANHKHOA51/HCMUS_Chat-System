package chatapp.views;

import chatapp.models.GroupMember;
import chatapp.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class GroupInfoView extends VBox {
    private TextField groupNameField;
    private Button renameBtn;
    private ListView<GroupMember> membersListView;
    private Button addMemberBtn;
    private Button leaveBtn; 
    private Label errorLabel;

    public GroupInfoView() {
        this.setPadding(new Insets(20));
        this.setSpacing(10);
        this.setPrefWidth(400);
        this.setPrefHeight(500);

        Label title = new Label("Group Settings");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox nameBox = new HBox(10);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        groupNameField = new TextField();
        groupNameField.setPromptText("Group Name");
        HBox.setHgrow(groupNameField, Priority.ALWAYS);
        renameBtn = new Button("Rename");
        nameBox.getChildren().addAll(groupNameField, renameBtn);

        Label membersLabel = new Label("Members");
        membersLabel.setStyle("-fx-font-weight: bold;");

        membersListView = new ListView<>();
        VBox.setVgrow(membersListView, Priority.ALWAYS);

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        addMemberBtn = new Button("Add Member");
        leaveBtn = new Button("Leave Group");
        leaveBtn.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red;");
        actionBox.getChildren().addAll(leaveBtn, addMemberBtn);

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        this.getChildren().addAll(title, nameBox, membersLabel, membersListView, actionBox, errorLabel);
    }

    public TextField getGroupNameField() {
        return groupNameField;
    }

    public Button getRenameBtn() {
        return renameBtn;
    }

    public ListView<GroupMember> getMembersListView() {
        return membersListView;
    }

    public Button getAddMemberBtn() {
        return addMemberBtn;
    }

    public Button getLeaveBtn() {
        return leaveBtn;
    }

    public void setError(String msg) {
        errorLabel.setText(msg);
    }
}
