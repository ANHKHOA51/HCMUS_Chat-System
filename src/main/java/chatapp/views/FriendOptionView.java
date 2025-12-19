package chatapp.views;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class FriendOptionView extends VBox {
    Button friendBtn;
    Button friendReqBtn;
    Button onlineBtn;
    Button searchBtn;

    public Button getFriendBtn() {
        return friendBtn;
    }

    public void setFriendBtn(Button friendBtn) {
        this.friendBtn = friendBtn;
    }

    public Button getFriendReqBtn() {
        return friendReqBtn;
    }

    public void setFriendReqBtn(Button friendReqBtn) {
        this.friendReqBtn = friendReqBtn;
    }

    public Button getOnlineBtn() {
        return onlineBtn;
    }

    public void setOnlineBtn(Button onlineBtn) {
        this.onlineBtn = onlineBtn;
    }

    public Button getSearchBtn() {
        return searchBtn;
    }

    public void setSearchBtn(Button searchBtn) {
        this.searchBtn = searchBtn;
    }

    public FriendOptionView() {
        super(30);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #b9c0c7ff; -fx-padding: 10 0 0 0;"); 
        setPrefWidth(300);

        friendBtn = createMenuButton("Danh sách bạn bè");
        friendReqBtn = createMenuButton("Yêu cầu kết bạn");
        onlineBtn = createMenuButton("Đang online");
        searchBtn = createMenuButton("Tìm kiếm bạn bè");

        getChildren().addAll(friendBtn, friendReqBtn, onlineBtn, searchBtn);
    }

    private void updateButtonStyle(Button btn, boolean isActive) {
        String baseStyle = "-fx-font-size: 14px; -fx-pref-width: 300px; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20;";
        if (isActive) {
            btn.setStyle(baseStyle + "-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            btn.setStyle(baseStyle + "-fx-background-color: rgba(165, 172, 182, 1); -fx-text-fill: black;");
        }
    }

    private Button activeBtn;

    public void setActive(Button btn) {
        this.activeBtn = btn;
        updateButtonStyle(friendBtn, friendBtn == activeBtn);
        updateButtonStyle(friendReqBtn, friendReqBtn == activeBtn);
        updateButtonStyle(onlineBtn, onlineBtn == activeBtn);
        updateButtonStyle(searchBtn, searchBtn == activeBtn);
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE); 
        btn.setPrefHeight(60);
        btn.setPrefWidth(300);

        updateButtonStyle(btn, false);

        btn.setOnMouseEntered(e -> {
            if (btn != activeBtn) {
                btn.setStyle(
                        "-fx-background-color: #5D6D7E; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 300px; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20;");
            }
        });

        btn.setOnMouseExited(e -> {
            updateButtonStyle(btn, btn == activeBtn);
        });

        return btn;
    }
}
