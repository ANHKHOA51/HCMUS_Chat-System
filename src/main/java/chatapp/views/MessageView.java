package chatapp.views;

import chatapp.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.application.Platform;

public class MessageView extends BorderPane {
    private VBox msgList = new VBox();;
    private ScrollPane scroll = new ScrollPane(msgList);
    private TextField input = new TextField();
    private Button sendBtn = new Button("Send");;
    private TextField searchMessage = new TextField();
    private Button infoChat = new Button("Info");
    private Label lbl = new Label();

    public MessageView() {
        super();
        searchMessage.setPromptText("Search messages...");
        msgList.setFillWidth(true);
        scroll.setFitToWidth(true);
        msgList.heightProperty().addListener((obs, oldVal, newVal) -> scroll.setVvalue(1.0));
        input = new TextField();
        HBox.setHgrow(input, Priority.ALWAYS);

        HBox inpLayout = new HBox(input, sendBtn);
        inpLayout.setStyle("-fx-padding: 10");
        inpLayout.setAlignment(Pos.CENTER);
        inpLayout.setSpacing(6);
        HBox header = new HBox(6);
        header.setPadding(new Insets(10));
        header.getChildren().addAll(lbl, searchMessage, infoChat);
        setTop(header);
        setBottom(inpLayout);
        setCenter(scroll);
    }

    public MessageView(User u) {
        super();
        searchMessage.setPromptText("Search messages...");
        msgList.setFillWidth(true);
        scroll.setFitToWidth(true);
        msgList.heightProperty().addListener((obs, oldVal, newVal) -> scroll.setVvalue(1.0));
        HBox.setHgrow(input, Priority.ALWAYS);
        HBox inpLayout = new HBox(input, sendBtn);
        inpLayout.setStyle("-fx-padding: 10");
        inpLayout.setAlignment(Pos.CENTER);
        inpLayout.setSpacing(6);

        HBox header = new HBox(6);
        header.setPadding(new Insets(10));
        header.getChildren().addAll(lbl, searchMessage, infoChat);
        setTop(header);
        setBottom(inpLayout);
        setCenter(scroll);
    }

    private HBox createBubble(String msg, boolean isMine) {
        Label chat = new Label(msg);
        Button delMessage = new Button("X");

        // Make delete button small and square
        delMessage.setPrefSize(16, 16);
        delMessage.setMinSize(16, 16);
        delMessage.setMaxSize(16, 16);
        delMessage.setFocusTraversable(false);
        delMessage.setStyle("-fx-opacity: 0;");

        // hover effect for delete button
        delMessage.setOnMouseEntered(e -> delMessage.setStyle(
            "-fx-font-size: 10px; -fx-background-radius: 8px; -fx-padding: 0; -fx-background-color: #e74c3c; -fx-text-fill: white;"
        ));
        delMessage.setOnMouseExited(e -> delMessage.setStyle(
            "-fx-opacity: 0;"
        ));

        chat.setWrapText(true);
        HBox bubble = new HBox(6, delMessage, chat);
        chat.setMaxWidth(400);

        String bg = isMine ? "#9292ff" : "#E5E5EA";
        chat.setStyle(
                "-fx-background-color: " + bg + ";" +
                " -fx-padding: 5;" +
                " -fx-background-radius: 10;" +
                " -fx-text-fill: black;"
        );

        bubble.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        bubble.setStyle(" -fx-padding: 3;");

        delMessage.setOnAction(ev -> {
            Parent parent = bubble.getParent();
            if (parent instanceof Pane) {
                Platform.runLater(() -> {
                    ((Pane) parent).getChildren().remove(bubble);
                });
            }
        });

        return bubble;
    }

    public void sendMessage(String message) {
        HBox bb = createBubble(message, true);

        msgList.getChildren().add(bb);
    }

    public TextField getTextField() {
        return input;
    }

    public Button getButton() {
        return sendBtn;
    }

    public BorderPane getView() {
        return this;
    }
}
