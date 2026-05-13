package ui;

import db.DatabaseHelper;
import models.User;
import app.Main;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the Register page.
 */
public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        if (username.length() < 3) {
            showError("Le nom d'utilisateur doit avoir au moins 3 caractères.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Veuillez entrer un email valide.");
            return;
        }

        if (password.length() < 4) {
            showError("Le mot de passe doit avoir au moins 4 caractères.");
            return;
        }

        if (!password.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas.");
            confirmPasswordField.clear();
            return;
        }

        // Register
        User newUser = new User(username, email, password);
        boolean success = DatabaseHelper.registerUser(newUser);

        if (success) {
            messageLabel.setText("Compte créé avec succès ! Redirection...");
            messageLabel.getStyleClass().setAll("success-label");
            // Short delay then go to login
            javafx.application.Platform.runLater(() -> {
                try { Thread.sleep(800); } catch (InterruptedException ignored) {}
                Main.switchScene("ui/login.fxml");
            });
        } else {
            showError("Erreur: nom d'utilisateur ou email déjà utilisé.");
        }
    }

    @FXML
    private void goToLogin() {
        Main.switchScene("ui/login.fxml");
    }

    private void showError(String msg) {
        messageLabel.setText(msg);
        messageLabel.getStyleClass().setAll("error-label");
    }
}
