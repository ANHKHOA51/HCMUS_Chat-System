package chatapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import chatapp.controllers.AuthController;
import chatapp.db.DBConnection;
import chatapp.models.LoginHistory;
import chatapp.models.User;
import chatapp.utils.FXMLPaths;

public class AdminApp extends Application {
    private User cur_user;
    public static chatapp.server.AdminSocketClient socketClient;

    @Override
    public void start(Stage stage) throws IOException {
        DBConnection.getConnection();
        AuthController authCtl = new AuthController();

        stage.setScene(authCtl.getScene());
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
        stage.setTitle("HCMUS Chat App - Admin");

        authCtl.setOnLogin((username, password) -> {
            User user = chatapp.dao.UserDAO.login(username, password);
            if (user != null) {
                // STRICT ADMIN CHECK
                if (!user.isAdmin()) {
                    authCtl.showLoginError("Access Denied: You do not have Admin privileges.");
                    return;
                }

                cur_user = user;
                cur_user.setOnline(true);
                chatapp.dao.UserDAO.updateFieldUser("is_online", cur_user.isOnline(), "id", cur_user.getId());
                boolean result = LoginHistory.saveLoginHistory(cur_user.getId());
                if (!result) {
                    return;
                }

                try {
                    // Initialize Admin Socket
                    socketClient = new chatapp.server.AdminSocketClient(new java.net.URI("ws://localhost:8887"));
                    socketClient.connect();
                } catch (Exception e) {
                    System.out.println("Could not connect to Chat Server as Admin: " + e.getMessage());
                }

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPaths.Dashboard.USER));
                    Parent root = loader.load();

                    // Setup logout handler for Dashboard
                    chatapp.controllers.dashboard.DashboardController.onLogout = () -> {
                        if (socketClient != null) {
                            socketClient.close();
                            socketClient = null;
                        }
                        authCtl.showLogin();
                        stage.setScene(authCtl.getScene());
                        stage.centerOnScreen();
                    };

                    Scene adminScene = new Scene(root);
                    stage.setScene(adminScene);
                    stage.centerOnScreen();
                    stage.setTitle("Admin Dashboard - " + cur_user.getDisplayName());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to load Admin Dashboard");
                    authCtl.showLoginError("System Error: Could not load Dashboard.");
                }

            } else {
                System.out.println("Login failed for user=" + username);
                authCtl.showLoginError("Invalid username or password");
            }
        });

        authCtl.setOnSignup((username, password, name, email) -> {
            authCtl.showLoginError("Admin registration is restricted. Contact system administrator.");
        });

        authCtl.setOnRequestReset(email -> {
            System.out.println("Reset delegated to AuthController for: " + email);
        });
    }

    @Override
    public void stop() throws Exception {
        if (cur_user != null) {
            chatapp.dao.UserDAO.updateFieldUser("is_online", false, "id", cur_user.getId());
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}
