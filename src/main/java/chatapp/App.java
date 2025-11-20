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
        // DBConnection.getConnection();
        // try {
        //     cur_user = User.login("user");

        //     if (cur_user != null) {
        //         if (!cur_user.isAdmin()) {
                    // System.out.println(cur_user.getId());
                    TabPane pane = new TabPane();
                    MessageController msgCtl = new MessageController();
                    // ProfileController pfCtl = new ProfileController(cur_user);
                    ProfileController pfCtl = new ProfileController();
                    FriendController frCtl = new FriendController();

                    pane.getTabs().add(frCtl.getTab());
                    pane.getTabs().add(msgCtl.getTab());
                    pane.getTabs().add(pfCtl.getProfileView());
                    scene = new Scene(pane, 1200, 800);
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                // }
            // }
        // } catch (NullPointerException e) {
        //     System.out.println("Login fail");
        // }

    }

    public static void main(String[] args) {
        launch();
    }

}