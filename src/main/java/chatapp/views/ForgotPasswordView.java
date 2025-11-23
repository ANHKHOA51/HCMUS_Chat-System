package chatapp.views;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ForgotPasswordView {
    private final StackPane root;
    private final TextField emailField = new TextField();
    private final Button sendButton = new Button("Send reset link");
    private final Button backButton = new Button("Back");

    public ForgotPasswordView() {
        emailField.setPromptText("Email or username");
        Text title = new Text("Forgot password");
        HBox buttonBox = new HBox(10, sendButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        VBox box = new VBox(8, title, emailField, buttonBox);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(420);

        root = new StackPane(box);
        StackPane.setAlignment(box, Pos.CENTER);
    }

    public Parent getRoot() { return root; }
    public TextField getEmailField() { return emailField; }
    public Button getSendButton() { return sendButton; }
    public Button getBackButton() { return backButton; }
}