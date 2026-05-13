package app;

import models.User;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * ThinkGreen Application Entry Point.
 * Manages scene switching and user session.
 */
public class Main extends Application {

    private static Stage primaryStage;
    private static User currentUser;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("🌿 ThinkGreen — Jardinage Urbain");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);

        // Start with login page
        switchScene("ui/login.fxml");
        primaryStage.show();
    }

    /**
     * Switch the current scene to a new FXML file.
     * @param fxmlPath path relative to the classpath (e.g. "ui/login.fxml")
     */
    public static void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            // Maximize for dashboard pages (not login/register)
            if (!fxmlPath.contains("login") && !fxmlPath.contains("register")) {
                primaryStage.setMaximized(true);
            } else {
                primaryStage.setMaximized(false);
                primaryStage.setWidth(900);
                primaryStage.setHeight(680);
                primaryStage.centerOnScreen();
            }
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Get the primary stage (for dialogs).
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Get the currently logged-in user.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Set the currently logged-in user.
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Logout: clear user and go back to login.
     */
    public static void logout() {
        currentUser = null;
        switchScene("ui/login.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
