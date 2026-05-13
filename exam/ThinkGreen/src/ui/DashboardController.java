package ui;

import app.Main;
import db.DatabaseHelper;
import models.User;
import models.WeatherAlert;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the Dashboard page.
 */
public class DashboardController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label userInfoLabel;

    // Stat Labels
    @FXML private Label totalPlantsLabel;
    @FXML private Label healthyPlantsLabel;
    @FXML private Label alertsLabel;
    @FXML private Label usersLabel;
    @FXML private Label pendingLabel;

    // Charts
    @FXML private PieChart statusPieChart;
    @FXML private BarChart<String, Number> userBarChart;

    // Table
    @FXML private TableView<WeatherAlert> alertsTable;
    @FXML private TableColumn<WeatherAlert, String> colAlertType;
    @FXML private TableColumn<WeatherAlert, String> colSeverity;
    @FXML private TableColumn<WeatherAlert, String> colMessage;
    @FXML private TableColumn<WeatherAlert, String> colDate;
    @FXML private TableColumn<WeatherAlert, String> colRegion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = Main.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Bienvenue, " + user.getUsername() + " !");
            userInfoLabel.setText(user.getUsername() + " (" + user.getRole() + ")");
        }

        setupTable();
        loadData();
    }

    private void setupTable() {
        colAlertType.setCellValueFactory(new PropertyValueFactory<>("alertType"));
        colSeverity.setCellValueFactory(new PropertyValueFactory<>("severity"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("alertDate"));
        colRegion.setCellValueFactory(new PropertyValueFactory<>("region"));

        // Custom styling for severity column
        colSeverity.setCellFactory(column -> new javafx.scene.control.TableCell<WeatherAlert, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item.toUpperCase());
                    badge.getStyleClass().add("alert-severity-badge");
                    switch (item.toLowerCase()) {
                        case "low":     badge.getStyleClass().add("badge-low"); break;
                        case "medium":  badge.getStyleClass().add("badge-medium"); break;
                        case "high":    badge.getStyleClass().add("badge-high"); break;
                        case "critical":badge.getStyleClass().add("badge-critical"); break;
                    }
                    setGraphic(badge);
                }
            }
        });
    }

    private void loadData() {
        // Load Stats
        Map<String, Integer> stats = DatabaseHelper.getDashboardStats();
        totalPlantsLabel.setText(String.valueOf(stats.getOrDefault("totalPlants", 0)));
        healthyPlantsLabel.setText(String.valueOf(stats.getOrDefault("healthyPlants", 0)));
        alertsLabel.setText(String.valueOf(stats.getOrDefault("totalAlerts", 0)));
        usersLabel.setText(String.valueOf(stats.getOrDefault("totalUsers", 0)));
        pendingLabel.setText(String.valueOf(stats.getOrDefault("pendingSchedules", 0)));

        // Load PieChart
        Map<String, Integer> statusCounts = DatabaseHelper.getPlantStatusCounts();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        statusCounts.forEach((status, count) -> {
            String label = "";
            switch (status) {
                case "healthy":     label = "Saines"; break;
                case "needs_water": label = "Besoin d'eau"; break;
                case "sick":        label = "Malades"; break;
                case "harvested":   label = "Récoltées"; break;
                default:            label = status;
            }
            pieData.add(new PieChart.Data(label + " (" + count + ")", count));
        });
        statusPieChart.setData(pieData);

        // Load BarChart
        Map<String, Integer> userPlants = DatabaseHelper.getPlantsPerUser();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Plantes");
        userPlants.forEach((username, count) -> {
            if (username != null) {
                series.getData().add(new XYChart.Data<>(username, count));
            }
        });
        userBarChart.getData().clear();
        userBarChart.getData().add(series);

        // Load Recent Alerts
        List<WeatherAlert> recentAlerts = DatabaseHelper.getRecentAlerts();
        alertsTable.setItems(FXCollections.observableArrayList(recentAlerts));
    }

    // ── NAVIGATION ──

    @FXML private void goToDashboard() { /* Already here */ }
    @FXML private void goToPlants() { Main.switchScene("ui/plants.fxml"); }
    @FXML private void goToUsers() { Main.switchScene("ui/users.fxml"); }
    @FXML private void goToWeather() { Main.switchScene("ui/weather.fxml"); }
    @FXML private void goToSchedule() { Main.switchScene("ui/schedule.fxml"); }
    
    @FXML
    private void handleLogout() {
        Main.logout();
    }
}
