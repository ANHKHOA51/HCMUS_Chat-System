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
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MessageView extends BorderPane {
    private VBox msgList = new VBox();;
    private ScrollPane scroll = new ScrollPane(msgList);
    private TextField input = new TextField();
    private Button sendBtn = new Button("Send");;
    private TextField searchMessage = new TextField();
    private Button infoChat = new Button("Info");
    private Label lbl = new Label();
    private Button clearBtn = new Button("Clear");

    private java.util.function.Consumer<chatapp.models.Message> onDeleteMessage;
    private Runnable onClearHistory;
    private java.util.function.Consumer<String> onSearch;

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
        header.getChildren().addAll(lbl, searchMessage, infoChat, clearBtn);

        searchMessage.setOnAction(e -> {
            if (onSearch != null)
                onSearch.accept(searchMessage.getText());
        });

        clearBtn.setOnAction(e -> {
            if (onClearHistory != null)
                onClearHistory.run();
        });
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
        header.getChildren().addAll(lbl, searchMessage, infoChat, clearBtn);

        searchMessage.setOnAction(e -> {
            if (onSearch != null)
                onSearch.accept(searchMessage.getText());
        });

        clearBtn.setOnAction(e -> {
            if (onClearHistory != null)
                onClearHistory.run();
        });
        setTop(header);
        setBottom(inpLayout);
        setCenter(scroll);
    }

    private HBox createBubble(chatapp.models.Message msg, boolean isMine) {
        if (msg.isDeleted()) {
            // Maybe show "Message Deleted"? Or don't show at all?
            Label deletedLbl = new Label("Message Unsent");
            deletedLbl.setStyle("-fx-font-style: italic; -fx-text-fill: grey;");
            HBox bubble = new HBox(deletedLbl);
            bubble.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            bubble.setPadding(new Insets(5));
            return bubble;
        }

        String content = msg.getContent();
        Label chat = new Label(content);

        javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();
        javafx.scene.control.MenuItem deleteItem = new javafx.scene.control.MenuItem("Delete");
        deleteItem.setOnAction(e -> {
            if (onDeleteMessage != null)
                onDeleteMessage.accept(msg);
        });
        contextMenu.getItems().add(deleteItem);
        chat.setContextMenu(contextMenu);

        chat.setWrapText(true);
        HBox bubble;
        if (isMine) {
            Text text = new Text("You");
            text.setFont(Font.font(10));
            bubble = new HBox(6, chat, text);
        } else {
            Text text = new Text("Friend");
            text.setFont(Font.font(10));
            bubble = new HBox(6, text, chat);
        }
        chat.setMaxWidth(400);
        String bg = isMine ? "#9292ff" : "#E5E5EA";
        chat.setStyle(
                "-fx-background-color: " + bg + ";" +
                        " -fx-padding: 5;" +
                        " -fx-background-radius: 10;" +
                        " -fx-text-fill: black;");

        bubble.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        bubble.setStyle(" -fx-padding: 3;");

        return bubble;
    }

    public void sendMessage(chatapp.models.Message msg) {
        HBox bb = createBubble(msg, true);
        msgList.getChildren().add(bb);
    }

    public TextField getTextField() {
        return input;
    }

    public Button getButton() {
        return sendBtn;
    }

    public void clearMessages() {
        msgList.getChildren().clear();
    }

    public void addMessage(chatapp.models.Message message, boolean isMine) {
        if (message == null)
            return;
        HBox bubble = createBubble(message, isMine);
        msgList.getChildren().add(bubble);
    }

    public void setOnDeleteMessage(java.util.function.Consumer<chatapp.models.Message> onDelete) {
        this.onDeleteMessage = onDelete;
    }

    public void setOnClearHistory(Runnable onClear) {
        this.onClearHistory = onClear;
    }

    public void setOnSearch(java.util.function.Consumer<String> onSearch) {
        this.onSearch = onSearch;
    }

    public BorderPane getView() {
        return this;
    }
}
