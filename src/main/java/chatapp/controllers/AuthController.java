package chatapp.controllers;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import chatapp.views.ForgotPasswordView;
import chatapp.views.LoginView;
import chatapp.views.SignupView;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class AuthController {
    private final StackPane root = new StackPane();
    private final Scene scene = new Scene(root, 1200, 800);

    private final LoginView loginView = new LoginView();
    private final SignupView signupView = new SignupView();
    private final ForgotPasswordView forgotView = new ForgotPasswordView();

    public interface SignupHandler {
        void handle(String username, String password, String name, String email);
    }

    private BiConsumer<String, String> onLogin;
    private SignupHandler onSignup;
    private Consumer<String> onRequestReset;

    public AuthController() {
        root.getChildren().add(loginView.getRoot());
        wireLogin();
        wireSignup();
        wireForgot();
    }

    public Scene getScene() {
        return scene;
    }

    public void setOnLogin(BiConsumer<String, String> handler) {
        this.onLogin = handler;
    }

    public void setOnSignup(SignupHandler handler) {
        this.onSignup = handler;
    }

    public void setOnRequestReset(Consumer<String> handler) {
        this.onRequestReset = handler;
    }

    private void wireLogin() {
        loginView.getUsernameField().setOnAction(e -> submitLogin());
        loginView.getPasswordField().setOnAction(e -> submitLogin());
        loginView.getLoginButton().setOnAction(e -> submitLogin());

        loginView.getRegisterButton().setOnAction(e -> showSignup());
        loginView.getForgotButton().setOnAction(e -> showForgot());
    }

    private void wireSignup() {
        signupView.getSignupButton().setOnAction(e -> submitSignup());
        signupView.getBackButton().setOnAction(e -> showLogin());
        signupView.getUsernameField().setOnAction(e -> submitSignup());
        signupView.getPasswordField().setOnAction(e -> submitSignup());
        signupView.getConfirmField().setOnAction(e -> submitSignup());
    }

    private void wireForgot() {
        forgotView.getSendButton().setOnAction(e -> submitForgot());
        forgotView.getBackButton().setOnAction(e -> showLogin());
        forgotView.getEmailField().setOnAction(e -> submitForgot());
    }

    public void showLogin() {
        loginView.clearError();
        root.getChildren().setAll(loginView.getRoot());
    }

    public void showSignup() {
        signupView.clearError();
        root.getChildren().setAll(signupView.getRoot());
    }

    public void showForgot() {
        root.getChildren().setAll(forgotView.getRoot());
    }

    public void showLoginError(String message) {
        loginView.showError(message);
    }

    public void showSignupError(String message) {
        signupView.showError(message);
    }

    private void submitLogin() {
        loginView.clearError();
        String u = loginView.getUsernameField().getText() == null ? "" : loginView.getUsernameField().getText().trim();
        String p = loginView.getPasswordField().getText() == null ? "" : loginView.getPasswordField().getText();
        if (onLogin != null)
            onLogin.accept(u, p);
    }

    private void submitSignup() {
        signupView.clearError();
        String u = signupView.getUsernameField().getText() == null ? ""
                : signupView.getUsernameField().getText().trim();
        String p = signupView.getPasswordField().getText() == null ? "" : signupView.getPasswordField().getText();
        String conf = signupView.getConfirmField().getText() == null ? "" : signupView.getConfirmField().getText();
        String name = signupView.getNameField().getText() == null ? "" : signupView.getNameField().getText().trim();
        String email = signupView.getEmailField().getText() == null ? "" : signupView.getEmailField().getText().trim();
        String address = signupView.getAddressField().getText() == null ? ""
                : signupView.getAddressField().getText().trim();
        String birthday = "";
        if (signupView.getBirthdayPicker().getValue() != null) {
            birthday = signupView.getBirthdayPicker().getValue().toString();
        }
        boolean gender = true;
        if (signupView.getGenderGroup().getSelectedToggle() != null) {
            javafx.scene.control.RadioButton selectedInfo = (javafx.scene.control.RadioButton) signupView
                    .getGenderGroup().getSelectedToggle();
            gender = selectedInfo.getText().equals("Male");
        }

        if (u.isEmpty() || p.isEmpty()) {
            signupView.showError("Username and password are required");
            return;
        }
        if (!p.equals(conf)) {
            signupView.showError("Passwords do not match");
            return;
        }
        if (onSignup != null)
            onSignup.handle(u, p, name, email);

        boolean success = chatapp.dao.UserDAO.register(u, p, name, email, address, birthday, gender);
        if (success) {
            showLogin();
            loginView.showError("Registration successful. Please login.");
        } else {
            signupView.showError("Registration failed. Username might be taken.");
        }
    }

    private void submitForgot() {
        forgotView.clearError();
        String v = forgotView.getEmailField().getText() == null ? "" : forgotView.getEmailField().getText().trim();
        if (v.isEmpty()) {
            forgotView.showError("Please enter email");
            return;
        }

        if (!chatapp.dao.UserDAO.isEmailExists(v)) {
            forgotView.showError("Email not found");
            System.out.println("Email not found: " + v);
            return;
        }

        String newPass = java.util.UUID.randomUUID().toString().substring(0, 8);

        boolean updated = chatapp.dao.UserDAO.updatePassword(v, newPass);
        if (updated) {
            chatapp.utils.EmailService.sendPasswordReset(v, newPass);
            showLogin();
            loginView.showError("Password reset. Check your email.");
        } else {
            forgotView.showError("Failed to reset password.");
        }
    }
}