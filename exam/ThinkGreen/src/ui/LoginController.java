package ui;

import db.DatabaseHelper;
import models.User;
import app.Main;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the Login page.
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            errorLabel.getStyleClass().setAll("error-label");
            return;
        }

        // Authenticate
        User user = DatabaseHelper.authenticateUser(username, password);
        if (user != null) {
            Main.setCurrentUser(user);
            Main.switchScene("ui/dashboard.fxml");
        } else {
            errorLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
            errorLabel.getStyleClass().setAll("error-label");
            passwordField.clear();
        }
    }

    @FXML
    private void goToRegister() {
        Main.switchScene("ui/register.fxml");
    }
}
