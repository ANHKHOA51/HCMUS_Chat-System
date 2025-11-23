package chatapp.controllers.user;

import java.io.IOException;

import chatapp.utils.FXMLPaths;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class UserController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    void logOut(ActionEvent event) {
    }

    @FXML
    void navigateChat(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource(FXMLPaths.User.CHAT));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void navigateFriends(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource(FXMLPaths.User.FRIENDS_LIST));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void navigateGroups(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource(FXMLPaths.User.GROUPS_LIST));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void navigateSetting(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource(FXMLPaths.Auth.UPDATE_ACCOUNT));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
