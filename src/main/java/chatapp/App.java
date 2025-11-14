package chatapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

import chatapp.db.DBConnection;
import chatapp.utils.FXMLPaths;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Connection db = DBConnection.getConnection();
        Parent root = FXMLLoader.load(getClass().getResource(FXMLPaths.Dashboard.USER));
        stage.setTitle("TEST");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}