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
        // initial
        root.getChildren().add(loginView.getRoot());
        wireLogin();
        wireSignup();
        wireForgot();
    }

    // scene getter used by App
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
        // Enter or button triggers submit
        loginView.getUsernameField().setOnAction(e -> submitLogin());
        loginView.getPasswordField().setOnAction(e -> submitLogin());
        loginView.getLoginButton().setOnAction(e -> submitLogin());

        // navigation
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

    // navigation helpers
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

    // submit handlers call caller-provided callbacks
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
    }

    private void submitForgot() {
        String v = forgotView.getEmailField().getText() == null ? "" : forgotView.getEmailField().getText().trim();
        if (v.isEmpty())
            return;
        if (onRequestReset != null)
            onRequestReset.accept(v);
    }
}