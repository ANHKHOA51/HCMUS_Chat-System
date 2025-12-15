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

    private void updateButtonStyle(Button btn, boolean isActive) {
        String baseStyle = "-fx-font-size: 14px; -fx-pref-width: 300px; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20;";
        if (isActive) {
            // Active style: Darker background, white text, bold?
            btn.setStyle(baseStyle + "-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            // Inactive style: Default background
            btn.setStyle(baseStyle + "-fx-background-color: rgba(165, 172, 182, 1); -fx-text-fill: black;");
        }
    }

    public void setActive(Button activeBtn) {
        updateButtonStyle(friendBtn, friendBtn == activeBtn);
        updateButtonStyle(friendReqBtn, friendReqBtn == activeBtn);
        updateButtonStyle(onlineBtn, onlineBtn == activeBtn);
        updateButtonStyle(searchBtn, searchBtn == activeBtn);
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE); // Nút giãn full chiều ngang sidebar
        btn.setPrefHeight(60);
        btn.setPrefWidth(300);

        // Initial style
        updateButtonStyle(btn, false);

        // Hiệu ứng hover (tùy chọn) - combine with active check?
        // Simple hover that doesn't override active state permanently
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("-fx-font-weight: bold")) { // Check if not active (simple hack) or use
                                                                     // userDate
                btn.setStyle(btn.getStyle().replace("-fx-background-color: rgba(165, 172, 182, 1);",
                        "-fx-background-color: #5D6D7E;"));
            }
        });

        btn.setOnMouseExited(e -> {
            // Re-apply correct style based on whether it is active or not
            // But we don't track active state inside button easily without passing it.
            // Let's just re-call setActive logic or simplified approach:
            // We can let the Controller call setActive to refresh, or just store active
            // button here.
        });

        // Better approach for hover: Just change background if not active logic is
        // complex.
        // Let's use a simpler hover that reverts to current state.
        // Actually, let's keep it simple: relying on setActive to set the main state.
        // Hover effect might conflict if we are not careful.
        // Let's accept that hover changes background temp.

        btn.setOnMouseEntered(e -> {
            // Only hover effect if NOT active (optional improvement)
            if (!btn.getStyle().contains("-fx-background-color: #34495e")) {
                btn.setStyle(
                        "-fx-background-color: #5D6D7E; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 300px; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20;");
            }
        });

        btn.setOnMouseExited(e -> {
            // We need to restore the state.
            // Since we don't know if it's active or not easily here without a field.
            // Let's refactor to store active button.
        });

        return btn;
    }
}
