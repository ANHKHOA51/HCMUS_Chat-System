package chatapp;

import javafx.application.Application;
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
import chatapp.models.User;

public class App extends Application {
    private static App instance;
    public static chatapp.server.ChatClientWrapper socketClient;
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

                TabPane pane = new TabPane();
                MessageController msgCtl = new MessageController(cur_user);

                try {
                    String host = chatapp.db.DBConnection.get("CHAT_SERVER_HOST", "localhost");
                    String port = chatapp.db.DBConnection.get("CHAT_SERVER_PORT", "8887");
                    String wsUrl = "ws://" + host + ":" + port;
                    System.out.println("Connecting to Chat Server at: " + wsUrl);
                    socketClient = new chatapp.server.ChatClientWrapper(new java.net.URI(wsUrl),
                            cur_user.getId());
                    socketClient.connect();
                } catch (Exception e) {
                    System.out.println("Chat Server is offline or unreachable. Continuing in offline mode.");
                }

                Thread reconnectThread = new Thread(() -> {
                    while (cur_user != null && socketClient != null) {
                        try {
                            Thread.sleep(5000);
                            if (socketClient != null
                                    && socketClient.getReadyState() == org.java_websocket.enums.ReadyState.CLOSED) {
                                System.out.println("Reconnecting to Chat Server...");
                                socketClient.reconnect();
                            }
                        } catch (InterruptedException ie) {
                            break;
                        } catch (Exception e) {
                            System.out.println("Reconnection attempt failed: " + e.getMessage());
                        }
                    }
                });
                reconnectThread.setDaemon(true);
                reconnectThread.start();

                msgCtl.setupSocket(socketClient);

                ProfileController pfCtl = new ProfileController(cur_user);
                pfCtl.setOnSignOut(() -> {
                    if (cur_user != null) {
                        chatapp.dao.UserDAO.updateFieldUser("is_online", false, "id", cur_user.getId());
                    }
                    if (socketClient != null) {
                        socketClient.close();
                        socketClient = null;
                    }

                    cur_user = null;
                    authCtl.showLogin();
                    stage.setScene(authCtl.getScene());
                    stage.centerOnScreen();
                });
                FriendController frCtl = new FriendController(cur_user);
                frCtl.setupSocket(socketClient);

                frCtl.setOnOpenChat(targetUser -> {
                    pane.getSelectionModel().select(0); 
                    msgCtl.openChatWith(targetUser);
                });

                pane.getTabs().add(msgCtl.getTab());
                pane.getTabs().add(frCtl.getTab());
                pane.getTabs().add(pfCtl.getProfileView());
                Scene main = new Scene(pane, 1200, 800);

                stage.setScene(main);
                stage.centerOnScreen();
                stage.setTitle("HCMUS Chat App");
            } else {
                System.out.println("Login failed for user=" + username);
                authCtl.showLoginError("Invalid username or password");
            }
        });

        authCtl.setOnSignup((username, password, name, email) -> {
            System.out.println("Signup delegated to AuthController for: " + username);
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
        if (socketClient != null) {
            socketClient.close();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }

}