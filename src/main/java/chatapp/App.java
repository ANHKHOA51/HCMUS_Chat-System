package chatapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;

import chatapp.controllers.AuthController;
import chatapp.controllers.FriendController;
import chatapp.controllers.MessageController;
import chatapp.controllers.ProfileController;
import chatapp.db.DBConnection;
import chatapp.models.LoginHistory;
// import chatapp.db.DBConnection;
import chatapp.models.User;

import chatapp.utils.FXMLPaths;

public class App extends Application {
    private User cur_user;

    @Override
    public void start(Stage stage) throws IOException {
        DBConnection.getConnection();
        AuthController authCtl = new AuthController();

        stage.setScene(authCtl.getScene());
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();

        authCtl.setOnLogin((username, password) -> {
            User user = chatapp.dao.UserDAO.login(username, password);
            if (user != null) {
                cur_user = user;
                cur_user.setOnline(true);
                chatapp.dao.UserDAO.updateFieldUser("is_online", cur_user.isOnline(), "id", cur_user.getId());
                boolean result = LoginHistory.saveLoginHistory(cur_user.getId());
                if (!result) {
                    return;
                }

                if (cur_user.isAdmin()) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPaths.Dashboard.USER));
                        Parent root = loader.load();

                        // Setup logout handler for Dashboard
                        chatapp.controllers.dashboard.DashboardController.onLogout = () -> {
                            authCtl.showLogin();
                            stage.setScene(authCtl.getScene());
                            stage.centerOnScreen();
                        };

                        Scene adminScene = new Scene(root);
                        stage.setScene(adminScene);
                        stage.centerOnScreen();
                        stage.setTitle("Admin Dashboard");
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Failed to load Admin Dashboard");
                    }
                } else {
                    TabPane pane = new TabPane();
                    MessageController msgCtl = new MessageController(cur_user);
                    ProfileController pfCtl = new ProfileController(cur_user);
                    pfCtl.setOnSignOut(() -> {
                        cur_user = null;
                        authCtl.showLogin();
                        stage.setScene(authCtl.getScene());
                        stage.centerOnScreen();
                    });
                    FriendController frCtl = new FriendController(cur_user);

                    frCtl.setOnOpenChat(targetUser -> {
                        pane.getSelectionModel().select(0); // Assuming MessageController is at index 0
                        // TODO: Ideally msgCtl should have a method to select/open chat with this user
                        // Since MessageController uses internal ContactListView, we might need to
                        // expose a method
                        // to select the user if they exist in the list, or add them.
                        msgCtl.openChatWith(targetUser);
                    });

                    pane.getTabs().add(msgCtl.getTab());
                    pane.getTabs().add(frCtl.getTab());
                    pane.getTabs().add(pfCtl.getProfileView());
                    Scene main = new Scene(pane, 1200, 800);

                    stage.setScene(main);
                    stage.centerOnScreen();
                    stage.setTitle("HCMUS Chat App");
                }
            } else {
                System.out.println("Login failed for user=" + username);
                authCtl.showLoginError("Invalid username or password");
            }
        });

        authCtl.setOnSignup((username, password, name, email) -> {
            System.out.println("Signup requested: " + username);
            boolean success = chatapp.dao.UserDAO.register(username, password, name, email);
            if (success) {
                System.out.println("Signup successful");
                authCtl.showLogin();
            } else {
                System.out.println("Signup failed");
                authCtl.showSignupError("Signup failed. User might already exist.");
            }
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