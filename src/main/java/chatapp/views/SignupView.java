package chatapp.views;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javafx.scene.control.Label;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;

public class SignupView {
    private final StackPane root;
    private final TextField usernameField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField emailField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField confirmField = new PasswordField();
    private final TextField addressField = new TextField();
    private final DatePicker birthdayPicker = new DatePicker();
    private final ToggleGroup genderGroup = new ToggleGroup();
    private final RadioButton maleRadio = new RadioButton("Male");
    private final RadioButton femaleRadio = new RadioButton("Female");
    private final Button signupButton = new Button("Sign up");
    private final Button backButton = new Button("Back");
    private final Label errorLabel = new Label();

    public SignupView() {
        usernameField.setPromptText("Username");
        nameField.setPromptText("Full name");
        emailField.setPromptText("Email");
        passwordField.setPromptText("Password");
        confirmField.setPromptText("Confirm password");

        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        Text title = new Text("Sign up");

        addressField.setPromptText("Address");
        birthdayPicker.setPromptText("Birthday");

        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        maleRadio.setSelected(true);

        HBox genderBox = new HBox(10, new Label("Gender:"), maleRadio, femaleRadio);
        genderBox.setAlignment(Pos.CENTER_LEFT);

        HBox buttonBox = new HBox(10, signupButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        VBox box = new VBox(8, title, nameField, usernameField, emailField,
                addressField, birthdayPicker, genderBox,
                passwordField, confirmField, errorLabel,
                buttonBox);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(420);

        root = new StackPane(box);
        StackPane.setAlignment(box, Pos.CENTER);
    }

    public Parent getRoot() {
        return root;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public TextField getNameField() {
        return nameField;
    }

    public TextField getEmailField() {
        return emailField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public PasswordField getConfirmField() {
        return confirmField;
    }

    public Button getSignupButton() {
        return signupButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void clearError() {
        errorLabel.setVisible(false);
        errorLabel.setText("");
    }

    public TextField getAddressField() {
        return addressField;
    }

    public DatePicker getBirthdayPicker() {
        return birthdayPicker;
    }

    public ToggleGroup getGenderGroup() {
        return genderGroup;
    }
}