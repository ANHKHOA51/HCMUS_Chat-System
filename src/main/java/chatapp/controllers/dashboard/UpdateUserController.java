package chatapp.controllers.dashboard;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import chatapp.utils.FXMLPaths;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateUserController extends DashboardController {
    @FXML
    private TextField addressField;

    @FXML
    private DatePicker birthdayField;

    @FXML
    private TextField displayNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private ComboBox<String> roleCombox;

    @FXML
    void onBack(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource(FXMLPaths.Dashboard.USER));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void onUpdate(ActionEvent event) {

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        ObservableList<String> roleList = FXCollections.observableArrayList("User", "Admin");
        roleCombox.setItems(roleList);
    }
}
