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

public class FriendReqCell extends ListCell<User> {
    private final HBox root = new HBox(8);
    private final Label nameLabel = new Label();

    private final Button acptBtn = new Button("Accept");
    private final Button declineBtn = new Button("Decline");

    private boolean boundWidth = false;

    private Consumer<User> onDeclineConsumer;
    private Consumer<User> onAcceptConsumer;

    public FriendReqCell() {
        super();
        setPrefWidth(100);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.setStyle("-fx-font-size: 16px;");

        HBox buttonsBox = new HBox(6);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        String buttonStyle = " -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 4 8 4 8; -fx-font-size: 12px;";
        String hoverDelBtn = "-fx-cursor: hand; -fx-opacity: 1; -fx-background-color: red;";
        declineBtn.setFocusTraversable(false);
        String defaultDelBtn = "-fx-background-color: red; -fx-opacity: 0.6;";
        declineBtn.setStyle(defaultDelBtn + buttonStyle);
        declineBtn.setOnMouseEntered(e -> declineBtn.setStyle(hoverDelBtn + buttonStyle));
        declineBtn.setOnMouseExited(e -> declineBtn.setStyle(defaultDelBtn + buttonStyle));
        
        acptBtn.setFocusTraversable(false);
        String hoverAcptBtn = "-fx-cursor: hand; -fx-opacity: 1; -fx-background-color: lightgreen;";
        String defaultAcptBtn = "-fx-background-color: lightgreen; -fx-opacity: 0.6;";
        acptBtn.setStyle(defaultAcptBtn + buttonStyle);
        acptBtn.setOnMouseEntered(e -> acptBtn.setStyle(hoverAcptBtn + buttonStyle));
        acptBtn.setOnMouseExited(e -> acptBtn.setStyle(defaultAcptBtn + buttonStyle));
        

        buttonsBox.getChildren().addAll(acptBtn, declineBtn);

        declineBtn.setOnAction(e -> {
            User u = getItem();
            if (u != null && onDeclineConsumer != null)
                onDeclineConsumer.accept(u);
        });
        acptBtn.setOnAction(e -> {
            User u = getItem();
            if (u != null && onAcceptConsumer != null)
                onAcceptConsumer.accept(u);
        });

        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(6, 5, 6, 8));
        root.getChildren().addAll(nameLabel, buttonsBox);

        HBox.setHgrow(nameLabel, Priority.ALWAYS);
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

    public Button getDeclineButton() {
        return declineBtn;
    }

    public Button getAcceptBtn() {
        return acptBtn;
    }


    // Consumer-based setters: caller gets the User directly
    public void setOnDecline(Consumer<User> c) {
        this.onDeclineConsumer = c;
    }

    public void setOnAccept(Consumer<User> c) {
        this.onAcceptConsumer = c;
    }

    // EventHandler-based setters (if caller prefers raw ActionEvent)
    public void setOnDeclineEvent(EventHandler<ActionEvent> h) {
        declineBtn.setOnAction(h);
    }

    public void setOnAcceptEvent(EventHandler<ActionEvent> h) {
        acptBtn.setOnAction(h);
    }
}
