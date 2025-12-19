package chatapp.controllers.dashboard;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.UUID;

import chatapp.models.User;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateUserController extends DashboardController {
    private UUID updateUserId;
    private User updateUser;

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
    private ComboBox<String> genderCombox;

    @FXML
    private Label errorLable;
    @FXML
    private Label successLabel;

    public void setUpdateUserId(UUID updateUserId) {
        this.updateUserId = updateUserId;
        loadUser();
    }

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
        if (usernameField.getText() == null || usernameField.getText().trim().isEmpty()) {
            errorLable.setText("Username and password is require");
            errorLable.setVisible(true);
            return;
        }
        if (passwordField.getText() == null || passwordField.getText().trim().isEmpty()) {
            errorLable.setText("Username and password is require");
            errorLable.setVisible(true);
            return;
        }

        if (emailField.getText().equals(updateUser.getEmail()) == false) {
            User userCheckk = User.getUser("email", emailField.getText());
            if (userCheckk != null) {
                errorLable.setText("email already exist");
                errorLable.setVisible(true);
                return;
            }
        }

        if (usernameField.getText().equals(updateUser.getUsername()) == false) {
            User userCheckk = User.getUser("username", usernameField.getText());
            if (userCheckk != null) {
                errorLable.setText("username already exist");
                errorLable.setVisible(true);
                return;
            }
        }

        if (roleCombox.getValue() == null) {
            errorLable.setText("Please chose role");
            errorLable.setVisible(true);
            return;
        }

        if (genderCombox.getValue() == null) {
            errorLable.setText("Please chose gender");
            errorLable.setVisible(true);
            return;
        }

        String username = usernameField.getText();
        String displayName = displayNameField.getText();
        String email = emailField.getText();
        String address = addressField.getText();
        boolean admin = roleCombox.getValue() == "Admin" ? true : false;
        boolean gender = genderCombox.getValue() == "Male" ? true : false;
        LocalDate birthday = birthdayField.getValue();
        String password = passwordField.getText();

        boolean result = User.updateUser(updateUserId, username, displayName, email, address, admin, gender, birthday,
                password);
        if (result == false) {
            errorLable.setText("Failed to add user");
            errorLable.setVisible(true);
            return;
        } else {
            errorLable.setVisible(false);
            successLabel.setVisible(true);
        }

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        ObservableList<String> roleList = FXCollections.observableArrayList("User", "Admin");
        roleCombox.setItems(roleList);

        ObservableList<String> genderList = FXCollections.observableArrayList("Male", "Female");
        genderCombox.setItems(genderList);
    }

    public void loadUser() {
        updateUser = User.getUser("id", updateUserId);

        if (updateUser == null)
            return;

        addressField.setText(updateUser.getAddress());
        birthdayField.setValue(updateUser.getBirthdayUnformat());
        displayNameField.setText(updateUser.getDisplayName());
        emailField.setText(updateUser.getEmail());
        passwordField.setText(updateUser.getPassword());
        usernameField.setText(updateUser.getUsername());
        roleCombox.setValue(updateUser.isAdmin() == true ? "Admin" : "User");
        genderCombox.setValue(updateUser.isGender() == true ? "Male" : "Female");
    }
}
