package chatapp.views.cells;

import java.util.function.Consumer;

import chatapp.models.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Custom ListCell: full width, left = name, right = container for buttons.
 * Caller can add buttons to getButtonsBox() or use addActionButton(...)
 */
public class UserFriendListCell extends ListCell<User> {
    private final HBox root = new HBox(8);
    private final Label nameLabel = new Label();
    private final Label statusLabel = new Label();

    private final Button chatBtn = new Button("Chat");
    private final Button sendReqBtn = new Button("Send request");
    private final Button createGroupBtn = new Button("Create Group");
    private final Button blockBtn = new Button("Block");
    private final Button delBtn = new Button("Del");

    private boolean boundWidth = false;

    private Consumer<User> onDeleteConsumer;
    private Consumer<User> onChatConsumer;
    private Consumer<User> onSendRequestConsumer;
    private Consumer<User> onCreateGroupConsumer;
    private Consumer<User> onBlockConsumer;

    public UserFriendListCell() {
        super();
        setPrefWidth(100);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.setStyle("-fx-font-size: 16px;");

        statusLabel.setStyle("-fx-font-size: 8px; -fx-opacity: 0.3;");

        HBox leftBox = new HBox(8);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.getChildren().addAll(nameLabel, statusLabel);

        HBox buttonsBox = new HBox(6);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        String buttonStyle = " -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 4 8 4 8; -fx-font-size: 12px;";

        delBtn.setFocusTraversable(false);
        String hoverDelBtn = "-fx-cursor: hand; -fx-opacity: 1; -fx-background-color: red;";
        String defaultDelBtn = "-fx-background-color: red; -fx-opacity: 0.6;";
        delBtn.setStyle(defaultDelBtn + buttonStyle);
        delBtn.setOnMouseEntered(e -> delBtn.setStyle(hoverDelBtn + buttonStyle));
        delBtn.setOnMouseExited(e -> delBtn.setStyle(defaultDelBtn + buttonStyle));

        chatBtn.setFocusTraversable(false);
        String hoverChatBtn = "-fx-cursor: hand; -fx-opacity: 1; -fx-background-color: #2196F3;"; // Blue
        String defaultChatBtn = "-fx-background-color: #2196F3; -fx-opacity: 0.8;";
        chatBtn.setStyle(defaultChatBtn + buttonStyle);
        chatBtn.setOnMouseEntered(e -> chatBtn.setStyle(hoverChatBtn + buttonStyle));
        chatBtn.setOnMouseExited(e -> chatBtn.setStyle(defaultChatBtn + buttonStyle));

        sendReqBtn.setFocusTraversable(false);
        String hoverSendReqBtn = "-fx-cursor: hand; -fx-opacity: 1; -fx-background-color: lightblue;";
        String defaultSendReqBtn = "-fx-background-color: lightblue; -fx-opacity: 0.6;";
        sendReqBtn.setStyle(defaultSendReqBtn + buttonStyle);
        sendReqBtn.setOnMouseEntered(e -> sendReqBtn.setStyle(hoverSendReqBtn + buttonStyle));
        sendReqBtn.setOnMouseExited(e -> sendReqBtn.setStyle(defaultSendReqBtn + buttonStyle));

        blockBtn.setFocusTraversable(false);
        String hoverBlockBtn = "-fx-cursor: hand; -fx-opacity: 1; -fx-background-color: orange;";
        String defaultBlockBtn = "-fx-background-color: orange; -fx-opacity: 0.6;";
        blockBtn.setStyle(defaultBlockBtn + buttonStyle);
        blockBtn.setOnMouseEntered(e -> blockBtn.setStyle(hoverBlockBtn + buttonStyle));
        blockBtn.setOnMouseExited(e -> blockBtn.setStyle(defaultBlockBtn + buttonStyle));

        createGroupBtn.setFocusTraversable(false);
        String hoverGroupBtn = "-fx-cursor: hand; -fx-opacity: 1; -fx-background-color: lightgreen;";
        String defaultGroupBtn = "-fx-background-color: lightgreen; -fx-opacity: 0.6;";
        createGroupBtn.setStyle(defaultGroupBtn + buttonStyle);
        createGroupBtn.setOnMouseEntered(e -> createGroupBtn.setStyle(hoverGroupBtn + buttonStyle));
        createGroupBtn.setOnMouseExited(e -> createGroupBtn.setStyle(defaultGroupBtn + buttonStyle));

        buttonsBox.getChildren().addAll(chatBtn, sendReqBtn, createGroupBtn, blockBtn, delBtn);

        delBtn.setOnAction(e -> {
            User u = getItem();
            if (u != null && onDeleteConsumer != null)
                onDeleteConsumer.accept(u);
        });
        delBtn.setOnAction(e -> {
            User u = getItem();
            if (u != null && onDeleteConsumer != null)
                onDeleteConsumer.accept(u);
        });
        chatBtn.setOnAction(e -> {
            User u = getItem();
            if (u != null && onChatConsumer != null)
                onChatConsumer.accept(u);
        });
        sendReqBtn.setOnAction(e -> {
            User u = getItem();
            if (u != null && onSendRequestConsumer != null)
                onSendRequestConsumer.accept(u);
        });
        createGroupBtn.setOnAction(e -> {
            User u = getItem();
            if (u != null && onCreateGroupConsumer != null)
                onCreateGroupConsumer.accept(u);
        });
        blockBtn.setOnAction(e -> {
            User u = getItem();
            if (u != null && onBlockConsumer != null)
                onBlockConsumer.accept(u);
        });

        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(6, 5, 6, 8));
        root.getChildren().addAll(leftBox, buttonsBox);

        HBox.setHgrow(leftBox, Priority.ALWAYS);

        // Bind managed to visible so hidden buttons don't take up space
        chatBtn.managedProperty().bind(chatBtn.visibleProperty());
        sendReqBtn.managedProperty().bind(sendReqBtn.visibleProperty());
        createGroupBtn.managedProperty().bind(createGroupBtn.visibleProperty());
        blockBtn.managedProperty().bind(blockBtn.visibleProperty());
        delBtn.managedProperty().bind(delBtn.visibleProperty());

        selectedProperty().addListener((obs, oldVal, newVal) -> updateStyle(newVal));
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (empty || user == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(null);
            String display = user.getDisplayName() != null && !user.getDisplayName().isEmpty() ? user.getDisplayName()
                    : user.getUsername();
            nameLabel.setText(display);

            String statusText = user.isOnline() ? " -fx-text-fill: green;" : " -fx-text-fill: gray;";
            statusLabel.setStyle("-fx-font-size: 11px; -fx-opacity: 0.6;" + statusText);

            statusLabel.setText(user.isOnline() ? "Online" : "Offline");

            if (!boundWidth && getListView() != null) {
                boundWidth = true;
                getListView().widthProperty().addListener((obs, oldW, newW) -> root.setPrefWidth(newW.doubleValue()));
                root.setPrefWidth(getListView().getWidth());
            }

            setGraphic(root);
            updateStyle(isSelected());
        }
    }

    private void updateStyle(boolean selected) {
        if (selected) {
            nameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
            statusLabel.setStyle("-fx-font-size: 11px; -fx-opacity: 1.0; -fx-text-fill: white;");
        } else {
            nameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
            User u = getItem();
            if (u != null) {
                String statusText = u.isOnline() ? " -fx-text-fill: green;" : " -fx-text-fill: gray;";
                statusLabel.setStyle("-fx-font-size: 11px; -fx-opacity: 0.6;" + statusText);
            }
        }
    }

    public Button getDeleteButton() {
        return delBtn;
    }

    public Button getChatButton() {
        return chatBtn;
    }

    public Button getSendRequestButton() {
        return sendReqBtn;
    }

    public Button getBlockButton() {
        return blockBtn;
    }

    public Button getCreateGroupButton() {
        return createGroupBtn;
    }

    public void setOnDelete(Consumer<User> c) {
        this.onDeleteConsumer = c;
    }

    public void setOnChat(Consumer<User> c) {
        this.onChatConsumer = c;
    }

    public void setOnSendRequest(Consumer<User> c) {
        this.onSendRequestConsumer = c;
    }

    public void setOnCreateGroup(Consumer<User> c) {
        this.onCreateGroupConsumer = c;
    }

    public void setOnBlock(Consumer<User> c) {
        this.onBlockConsumer = c;
    }

    public void setOnDeleteEvent(EventHandler<ActionEvent> h) {
        delBtn.setOnAction(h);
    }

    public void setOnSendRequestEvent(EventHandler<ActionEvent> h) {
        sendReqBtn.setOnAction(h);
    }

    public void setOnBlockEvent(EventHandler<ActionEvent> h) {
        blockBtn.setOnAction(h);
    }
}