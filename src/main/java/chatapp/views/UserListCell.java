package chatapp.views;

import chatapp.models.User;
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
public class UserListCell extends ListCell<User> {
    private final HBox root = new HBox(8);
    private final Label nameLabel = new Label();
    private final Label statusLabel = new Label();
    private final HBox buttonsBox = new HBox(6);
    private final HBox leftBox = new HBox(8);

    private boolean boundWidth = false;

    public UserListCell() {
        super();
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.setStyle("-fx-font-size: 16px;");

        statusLabel.setStyle("-fx-font-size: 8px; -fx-opacity: 0.3;");

        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.getChildren().addAll(nameLabel, statusLabel);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(6, 8, 6, 8));
        root.getChildren().addAll(leftBox, buttonsBox);

        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        // buttonsBox keeps its preferred width on the right
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
                getListView().widthProperty().addListener((obs, oldW, newW) ->
                        root.setPrefWidth(newW.doubleValue()));
                // initial set
                root.setPrefWidth(getListView().getWidth());
            }

            setGraphic(root);
        }
    }

    /**
     * Expose buttons box so caller can add buttons and set actions externally.
     * Example: cell.getButtonsBox().getChildren().add(new Button("..."));
     */
    public HBox getButtonsBox() {
        return buttonsBox;
    }

    /**
     * Convenience: create and add a button to the right side, returns it so caller can set action.
     */
    public Button addActionButton(String text) {
        Button b = new Button(text);
        // style to fit height
        b.setFocusTraversable(false);
        buttonsBox.getChildren().add(b);
        return b;
    }
}