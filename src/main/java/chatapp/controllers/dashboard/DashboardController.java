package chatapp.controllers.dashboard;

import java.io.IOException;

import chatapp.utils.FXMLPaths;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class DashboardController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    void navigateActiveUser(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource(FXMLPaths.Dashboard.ACTIVITY));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void navigateChatGroup(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource(FXMLPaths.Dashboard.CHAT_GROUP));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void navigateLogInHistory(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource(FXMLPaths.Dashboard.LOGIN_HISTORY));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    void navigateRegistration(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource(FXMLPaths.Dashboard.REGISTRATION));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    void navigateReport(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource(FXMLPaths.Dashboard.REPORT));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    void navigateUser(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource(FXMLPaths.Dashboard.USER));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    void navigateUserFriend(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource(FXMLPaths.Dashboard.FRIEND));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
}
