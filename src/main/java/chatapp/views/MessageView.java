package chatapp.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MessageView extends BorderPane {
    private VBox msgList;
    private ScrollPane scroll;
    private TextField input;
    private Button sendBtn;

    public MessageView() {
        super();
        msgList = new VBox();
        msgList.setFillWidth(true);
        scroll = new ScrollPane(msgList);
        scroll.setFitToWidth(true);
        msgList.heightProperty().addListener((obs, oldVal, newVal) -> scroll.setVvalue(1.0));
        input = new TextField();
        HBox.setHgrow(input, Priority.ALWAYS);
        sendBtn = new Button();
        HBox inpLayout = new HBox(input, sendBtn);
        inpLayout.setAlignment(Pos.CENTER);
        inpLayout.setSpacing(6);
        setBottom(inpLayout);
        setCenter(scroll);
    }

    private HBox createBubble(String msg, boolean isMine) {
        Label chat = new Label(msg);
        chat.setWrapText(true);
        HBox bubble = new HBox(chat);
        chat.setMaxWidth(400);
        chat.setStyle(
                "-fx-background-color: " + (isMine ? "#9292ffff;" : "#E5E5EA") +
                        " -fx-padding: 5;" +
                        " -fx-background-radius: 10;" +
                        " -fx-text-fill: black;");
        bubble.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        bubble.setStyle(" -fx-padding: 3;");
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
