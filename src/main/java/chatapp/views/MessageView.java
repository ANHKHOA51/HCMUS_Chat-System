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

public class MessageView extends BorderPane {
    private VBox msgList = new VBox();;
    private ScrollPane scroll = new ScrollPane(msgList);
    private TextField input = new TextField();
    private Button sendBtn = new Button("Send");;
    private TextField searchMessage = new TextField();
    private Button infoChat = new Button("Info");
    private Label lbl = new Label();
    private Button clearBtn = new Button("Clear");
    private javafx.scene.control.ListView<chatapp.models.Message> searchResultsList = new javafx.scene.control.ListView<>();
    private VBox searchPanel;

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
        initSearchList();
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
        initSearchList();
    }

    private void initSearchList() {
        searchResultsList.setPrefWidth(200);

        Button closeBtn = new Button("Close");
        closeBtn.setMaxWidth(Double.MAX_VALUE);
        closeBtn.setOnAction(e -> {
            searchPanel.setVisible(false);
            searchPanel.setManaged(false);
        });

        searchResultsList.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(chatapp.models.Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getContent());
                }
            }
        });
        searchResultsList.setOnMouseClicked(e -> {
            chatapp.models.Message selected = searchResultsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                scrollToMessage(selected.getId());
            }
        });

        searchPanel = new VBox(closeBtn, searchResultsList);
        VBox.setVgrow(searchResultsList, Priority.ALWAYS);
        searchPanel.setVisible(false);
        searchPanel.setManaged(false);
        setRight(searchPanel);
    }

    private HBox createBubble(chatapp.models.Message msg, boolean isMine, String senderName) {
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

        if (isMine) {
            javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();
            javafx.scene.control.MenuItem deleteItem = new javafx.scene.control.MenuItem("Delete");
            deleteItem.setOnAction(e -> {
                if (onDeleteMessage != null)
                    onDeleteMessage.accept(msg);
            });
            contextMenu.getItems().add(deleteItem);
            chat.setContextMenu(contextMenu);
        }

        chat.setWrapText(true);
        HBox bubble;
        if (isMine) {
            Label text = new Label("You");
            text.setFont(Font.font(10));
            text.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
            bubble = new HBox(6, chat, text);
            bubble.setAlignment(Pos.CENTER_RIGHT); // Ensure alignment
        } else {
            Label text = new Label(senderName != null ? senderName : "Friend");
            text.setFont(Font.font(10));
            text.setMaxWidth(80);
            text.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
            text.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE); // Allow shrink but prefer content? No.
            // setMinWidth(0) allows shrinking to ellipsis.
            text.setMinWidth(0);
            bubble = new HBox(6, text, chat);
            bubble.setAlignment(Pos.CENTER_LEFT);
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
        bubble.setUserData(msg.getId());

        return bubble;
    }

    public void sendMessage(chatapp.models.Message msg) {
        HBox bb = createBubble(msg, true, "You");
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

    public void addMessage(chatapp.models.Message message, boolean isMine, String senderName) {
        if (message == null)
            return;
        HBox bubble = createBubble(message, isMine, senderName);
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

    public Button getInfoButton() {
        return infoChat;
    }

    public BorderPane getView() {
        return this;
    }

    public void showSearchResults(java.util.List<chatapp.models.Message> results) {
        if (results == null || results.isEmpty()) {
            searchPanel.setVisible(false);
            searchPanel.setManaged(false);
        } else {
            searchResultsList.getItems().setAll(results);
            searchPanel.setVisible(true);
            searchPanel.setManaged(true);
        }
    }

    public void scrollToMessage(java.util.UUID msgId) {
        for (javafx.scene.Node node : msgList.getChildren()) {
            if (msgId.equals(node.getUserData())) {
                // Highlight
                String originalStyle = node.getStyle();
                node.setStyle(originalStyle
                        + "-fx-effect: dropshadow(three-pass-box, yellow, 10, 0, 0, 0); -fx-background-color: yellow;");

                // Reset after delay
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                        javafx.util.Duration.seconds(2));
                pause.setOnFinished(e -> node.setStyle(originalStyle));
                pause.play();

                // Scroll
                // Need bounds relative to VBox.
                double y = node.getBoundsInParent().getMinY();
                double contentHeight = msgList.getHeight();
                double viewportHeight = scroll.getViewportBounds().getHeight();

                // If content is smaller than viewport, scrolling does nothing
                if (contentHeight > viewportHeight) {
                    double vValue = y / (contentHeight - viewportHeight);
                    // Adjust to center the message if possible?
                    // Simple logic: Scroll so message is at top?
                    // vValue 0 = top, 1 = bottom.
                    // y is distance from top.
                    // max scrollable distance = contentHeight - viewportHeight.
                    // So val = y / max_dist.
                    scroll.setVvalue(vValue);
                }
                break;
            }
        }
    }
}
