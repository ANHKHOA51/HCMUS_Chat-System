package chatapp.views;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
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
        setStyle("-fx-background-color: #b9c0c7ff; -fx-padding: 10 0 0 0;"); // Màu nền tối cho sidebar
        setPrefWidth(300);

        // Tạo các nút menu
        friendBtn = createMenuButton("Danh sách bạn bè");
        friendReqBtn = createMenuButton("Yêu cầu kết bạn");
        onlineBtn = createMenuButton("Đang online");
        searchBtn = createMenuButton("Tìm kiếm bạn bè");

        // --- Xử lý sự kiện khi bấm nút ---
        // Logic: Khi bấm nút -> Gọi hàm setCenter để thay đổi nội dung bên phải

        getChildren().addAll(friendBtn, friendReqBtn, onlineBtn, searchBtn);
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE); // Nút giãn full chiều ngang sidebar
        btn.setPrefHeight(60);
        btn.setPrefWidth(300);
        // Style CSS cơ bản cho nút
        btn.setStyle("-fx-background-color: rgba(165, 172, 182, 1); -fx-text-fill: black; -fx-font-size: 14px;");
        
        // Hiệu ứng hover (tùy chọn)
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: rgba(165, 172, 182, 1); -fx-text-fill: black; -fx-font-size: 14px;"));
        
        return btn;
    }
}
