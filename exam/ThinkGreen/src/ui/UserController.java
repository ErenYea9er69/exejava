package ui;

import app.Main;
import db.DatabaseHelper;
import models.User;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Users management page.
 */
public class UserController implements Initializable {

    @FXML private Label userInfoLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label adminCountLabel;
    @FXML private Label userCountLabel;

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colCreated;
    @FXML private TableColumn<User, String> colActions;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User current = Main.getCurrentUser();
        if (current != null) {
            userInfoLabel.setText(current.getUsername() + " (" + current.getRole() + ")");
        }

        setupTable();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colUsername.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        // Role with badge styling
        colRole.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));
        colRole.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item.equals("admin") ? "🛡 Admin" : "🌱 Jardinier");
                    badge.getStyleClass().add("alert-severity-badge");
                    badge.getStyleClass().add(item.equals("admin") ? "badge-high" : "badge-low");
                    setGraphic(badge);
                }
            }
        });

        colCreated.setCellValueFactory(c -> {
            if (c.getValue().getCreatedAt() != null) {
                return new SimpleStringProperty(c.getValue().getCreatedAt().format(DATE_FMT));
            }
            return new SimpleStringProperty("");
        });

        // Actions column — delete button (admin only, can't delete self)
        colActions.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User target = getTableView().getItems().get(getIndex());
                    User current = Main.getCurrentUser();

                    // Only admin can delete, and can't delete yourself
                    if (current != null && current.isAdmin() && target.getId() != current.getId()) {
                        Button delBtn = new Button("🗑 Supprimer");
                        delBtn.getStyleClass().addAll("btn-danger", "btn-small");
                        delBtn.setOnAction(e -> deleteUser(target));
                        setGraphic(new HBox(delBtn));
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void loadData() {
        List<User> users = DatabaseHelper.getAllUsers();
        ObservableList<User> data = FXCollections.observableArrayList(users);
        usersTable.setItems(data);

        long admins = users.stream().filter(User::isAdmin).count();
        totalUsersLabel.setText(String.valueOf(users.size()));
        adminCountLabel.setText(String.valueOf(admins));
        userCountLabel.setText(String.valueOf(users.size() - admins));
    }

    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer l'utilisateur \"" + user.getUsername() + "\" ?");
        alert.setContentText("Toutes ses plantes seront aussi supprimées.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DatabaseHelper.deleteUser(user.getId());
                loadData();
            }
        });
    }

    // ── NAVIGATION ──
    @FXML private void goToDashboard() { Main.switchScene("ui/dashboard.fxml"); }
    @FXML private void goToPlants() { Main.switchScene("ui/plants.fxml"); }
    @FXML private void goToUsers() { /* Already here */ }
    @FXML private void goToWeather() { Main.switchScene("ui/weather.fxml"); }
    @FXML private void goToSchedule() { Main.switchScene("ui/schedule.fxml"); }
    @FXML private void handleLogout() { Main.logout(); }
}
