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

import chatapp.controllers.MessageController;



public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        TabPane pane = new TabPane();
        MessageController controller = new MessageController();
        pane.getTabs().add(controller.getTab());
        scene = new Scene(pane, 1200, 800);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}