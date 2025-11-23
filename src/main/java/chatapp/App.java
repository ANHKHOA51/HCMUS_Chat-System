package chatapp;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

import chatapp.controllers.AuthController;
import chatapp.controllers.FriendController;
import chatapp.controllers.MessageController;
import chatapp.controllers.ProfileController;
// import chatapp.db.DBConnection;
import chatapp.models.User;

public class App extends Application {
    private User cur_user;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        AuthController authCtl = new AuthController();

        stage.setScene(authCtl.getScene());
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();

        authCtl.setOnLogin((username, password) -> {
            if ("user".equals(username)) {
                cur_user = new User();
                cur_user.setId("1");
                cur_user.setName("Demo User");
                cur_user.setUser_name("user");
                cur_user.setEmail("user@example.com");
                cur_user.setOnline(true);
                // DBConnection.getConnection();
                // try {
                // cur_user = User.login("user");

                // if (cur_user != null) {
                // if (!cur_user.isAdmin()) {
                // System.out.println(cur_user.getId());
                TabPane pane = new TabPane();
                MessageController msgCtl = new MessageController(cur_user);
                ProfileController pfCtl = new ProfileController(cur_user);
                FriendController frCtl = new FriendController();

                pane.getTabs().add(msgCtl.getTab());
                pane.getTabs().add(frCtl.getTab());
                pane.getTabs().add(pfCtl.getProfileView());
                Scene main = new Scene(pane, 1200, 800);

                // đổi scene trên FX thread (callback chạy trên FX thread)
                stage.setScene(main);
                stage.centerOnScreen();
                // }
                // }
                // } catch (NullPointerException e) {
                // System.out.println("Login fail");
                // }

            } else {
                System.out.println("Login failed for user=" + username);
            }
        });

        authCtl.setOnSignup((username, password) -> {
            System.out.println("Signup requested: " + username);
            // TODO: gọi backend tạo user; quay về login sau thành công
            authCtl.showLogin();
        });

        authCtl.setOnRequestReset(email -> {
            System.out.println("Reset requested for: " + email);
            // TODO: gọi backend gửi email; quay về login
            authCtl.showLogin();
        });

    }

    public static void main(String[] args) {
        launch();
    }

}