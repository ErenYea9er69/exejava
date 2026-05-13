package ui;

import app.Main;
import db.DatabaseHelper;
import models.User;
import models.WeatherAlert;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Weather Alerts page.
 */
public class WeatherController implements Initializable {

    @FXML private Label userInfoLabel;
    @FXML private Label formMessage;
    @FXML private Label alertCountLabel;

    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> severityCombo;
    @FXML private TextField regionField;
    @FXML private DatePicker datePicker;
    @FXML private TextArea messageArea;

    @FXML private FlowPane alertsGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = Main.getCurrentUser();
        if (user != null) {
            userInfoLabel.setText(user.getUsername() + " (" + user.getRole() + ")");
        }

        typeCombo.setItems(FXCollections.observableArrayList("frost", "heatwave", "storm", "drought"));
        typeCombo.setValue("storm");
        severityCombo.setItems(FXCollections.observableArrayList("low", "medium", "high", "critical"));
        severityCombo.setValue("medium");
        datePicker.setValue(LocalDate.now());

        loadAlerts();
    }

    private void loadAlerts() {
        alertsGrid.getChildren().clear();
        List<WeatherAlert> alerts = DatabaseHelper.getAllAlerts();
        alertCountLabel.setText(alerts.size() + " alertes actives");

        for (WeatherAlert alert : alerts) {
            alertsGrid.getChildren().add(createAlertCard(alert));
        }
    }

    private VBox createAlertCard(WeatherAlert alert) {
        VBox card = new VBox(8);
        card.getStyleClass().add("alert-card");
        card.setPrefWidth(340);
        card.setPadding(new Insets(18, 20, 18, 20));

        // Severity-based border color
        switch (alert.getSeverity()) {
            case "low":      card.getStyleClass().add("alert-card-low"); break;
            case "medium":   card.getStyleClass().add("alert-card-medium"); break;
            case "high":     card.getStyleClass().add("alert-card-high"); break;
            case "critical": card.getStyleClass().add("alert-card-critical"); break;
        }

        // Header row: icon + type + severity badge
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        String icon = "";
        switch (alert.getAlertType()) {
            case "frost":    icon = "🥶"; break;
            case "heatwave": icon = "🔥"; break;
            case "storm":    icon = "⛈"; break;
            case "drought":  icon = "☀"; break;
        }

        String typeText = "";
        switch (alert.getAlertType()) {
            case "frost":    typeText = "Gel"; break;
            case "heatwave": typeText = "Canicule"; break;
            case "storm":    typeText = "Orage"; break;
            case "drought":  typeText = "Sécheresse"; break;
        }

        Label typeLabel = new Label(icon + " " + typeText);
        typeLabel.getStyleClass().add("alert-type-label");

        Label badge = new Label(alert.getSeverity().toUpperCase());
        badge.getStyleClass().addAll("alert-severity-badge", "badge-" + alert.getSeverity());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(typeLabel, spacer, badge);

        // Message
        Label msg = new Label(alert.getMessage());
        msg.getStyleClass().add("alert-message");
        msg.setWrapText(true);

        // Meta: date + region
        Label meta = new Label("📅 " + alert.getAlertDate() + "  •  📍 " + alert.getRegion());
        meta.getStyleClass().add("alert-meta");

        // Delete button
        Button delBtn = new Button("Supprimer");
        delBtn.getStyleClass().addAll("btn-danger", "btn-small");
        delBtn.setOnAction(e -> {
            DatabaseHelper.deleteAlert(alert.getId());
            loadAlerts();
        });

        HBox footer = new HBox();
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        footer.getChildren().addAll(meta, footerSpacer, delBtn);
        footer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        card.getChildren().addAll(header, msg, footer);
        return card;
    }

    @FXML
    private void handleAddAlert() {
        String type = typeCombo.getValue();
        String severity = severityCombo.getValue();
        String region = regionField.getText().trim();
        LocalDate date = datePicker.getValue();
        String message = messageArea.getText().trim();

        if (type == null || severity == null || region.isEmpty() || date == null || message.isEmpty()) {
            formMessage.setText("Veuillez remplir tous les champs.");
            formMessage.getStyleClass().setAll("error-label");
            return;
        }

        WeatherAlert alert = new WeatherAlert(type, severity, message, date, region);
        if (DatabaseHelper.addAlert(alert)) {
            formMessage.setText("Alerte publiée avec succès !");
            formMessage.getStyleClass().setAll("success-label");
            regionField.clear();
            messageArea.clear();
            datePicker.setValue(LocalDate.now());
            loadAlerts();
        } else {
            formMessage.setText("Erreur lors de la publication.");
            formMessage.getStyleClass().setAll("error-label");
        }
    }

    // ── NAVIGATION ──
    @FXML private void goToDashboard() { Main.switchScene("ui/dashboard.fxml"); }
    @FXML private void goToPlants() { Main.switchScene("ui/plants.fxml"); }
    @FXML private void goToUsers() { Main.switchScene("ui/users.fxml"); }
    @FXML private void goToWeather() { /* Already here */ }
    @FXML private void goToSchedule() { Main.switchScene("ui/schedule.fxml"); }
    @FXML private void handleLogout() { Main.logout(); }
}
