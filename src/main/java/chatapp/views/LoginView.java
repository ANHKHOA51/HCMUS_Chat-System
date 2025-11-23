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

public class LoginView {
    private final StackPane root;
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Button loginButton = new Button("Login");
    private final Button registerButton = new Button("Register");
    private final Button forgotButton = new Button("Forgot Password");

    public LoginView() {
        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");

        Text title = new Text("Login to ChatApp");
        HBox buttonBox = new HBox(10, loginButton, registerButton, forgotButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(10, title, usernameField, passwordField, buttonBox);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(420);

        root = new StackPane(box);
        StackPane.setAlignment(box, Pos.CENTER);
    }

    public Parent getRoot() { return root; }
    public TextField getUsernameField() { return usernameField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getLoginButton() { return loginButton; }
    public Button getRegisterButton() { return registerButton; }
    public Button getForgotButton() { return forgotButton; }
}