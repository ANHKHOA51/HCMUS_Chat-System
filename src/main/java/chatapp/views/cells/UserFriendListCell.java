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

    private final Button sendReqBtn = new Button("Send request");
    private final Button blockBtn = new Button("Block");
    private final Button delBtn = new Button("Del");

    private boolean boundWidth = false;

    private Consumer<User> onDeleteConsumer;
    private Consumer<User> onSendRequestConsumer;
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
        String hoverDelBtn = "-fx-cursor: hand; -fx-opacity: 1; -fx-background-color: red;";
        delBtn.setFocusTraversable(false);
        String defaultDelBtn = "-fx-background-color: red; -fx-opacity: 0.6;";
        delBtn.setStyle(defaultDelBtn + buttonStyle);
        delBtn.setOnMouseEntered(e -> delBtn.setStyle(hoverDelBtn + buttonStyle));
        delBtn.setOnMouseExited(e -> delBtn.setStyle(defaultDelBtn + buttonStyle));

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

        buttonsBox.getChildren().addAll(sendReqBtn, blockBtn, delBtn);

        delBtn.setOnAction(e -> {
            User u = getItem();
            if (u != null && onDeleteConsumer != null)
                onDeleteConsumer.accept(u);
        });
        sendReqBtn.setOnAction(e -> {
            User u = getItem();
            if (u != null && onSendRequestConsumer != null)
                onSendRequestConsumer.accept(u);
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
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (empty || user == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(null);
            // adjust name text -- replace with actual getter if available
            String display = (user.toString() == null ? "" : user.toString());
            // if your User has getUsername(), use: user.getUsername()
            nameLabel.setText(display);

            String statusText = user.isOnline() ? " -fx-text-fill: green;" : " -fx-text-fill: gray;";
            statusLabel.setStyle("-fx-font-size: 11px; -fx-opacity: 0.6;" + statusText);

            statusLabel.setText(user.isOnline() ? "Online" : "Offline");

            // bind cell width to listview width once so cell uses full container width
            if (!boundWidth && getListView() != null) {
                boundWidth = true;
                // subtract a small value for padding/scrollbar if needed
                getListView().widthProperty().addListener((obs, oldW, newW) -> root.setPrefWidth(newW.doubleValue()));
                // initial set
                root.setPrefWidth(getListView().getWidth());
            }

            setGraphic(root);
        }
    }

    public Button getDeleteButton() {
        return delBtn;
    }

    public Button getSendRequestButton() {
        return sendReqBtn;
    }

    public Button getBlockButton() {
        return blockBtn;
    }

    // Consumer-based setters: caller gets the User directly
    public void setOnDelete(Consumer<User> c) {
        this.onDeleteConsumer = c;
    }

    public void setOnSendRequest(Consumer<User> c) {
        this.onSendRequestConsumer = c;
    }

    public void setOnBlock(Consumer<User> c) {
        this.onBlockConsumer = c;
    }

    // EventHandler-based setters (if caller prefers raw ActionEvent)
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