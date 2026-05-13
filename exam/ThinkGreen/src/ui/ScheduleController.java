package ui;

import app.Main;
import db.DatabaseHelper;
import models.Plant;
import models.User;
import models.WateringSchedule;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Watering Schedule page.
 */
public class ScheduleController implements Initializable {

    @FXML private Label userInfoLabel;
    @FXML private Label formMessage;
    @FXML private Label countLabel;

    @FXML private ComboBox<String> plantCombo;
    @FXML private ComboBox<String> actionCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField notesField;
    @FXML private CheckBox showPendingOnly;

    @FXML private TableView<WateringSchedule> scheduleTable;
    @FXML private TableColumn<WateringSchedule, String> colPlant;
    @FXML private TableColumn<WateringSchedule, String> colAction;
    @FXML private TableColumn<WateringSchedule, String> colDate;
    @FXML private TableColumn<WateringSchedule, String> colCompleted;
    @FXML private TableColumn<WateringSchedule, String> colNotes;
    @FXML private TableColumn<WateringSchedule, String> colActions;

    private List<Plant> allPlants;
    private ObservableList<WateringSchedule> scheduleList;
    private FilteredList<WateringSchedule> filteredSchedules;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = Main.getCurrentUser();
        if (user != null) {
            userInfoLabel.setText(user.getUsername() + " (" + user.getRole() + ")");
        }

        // Load plants into combo
        allPlants = DatabaseHelper.getAllPlants();
        ObservableList<String> plantNames = FXCollections.observableArrayList();
        for (Plant p : allPlants) {
            plantNames.add(p.getId() + " — " + p.getName());
        }
        plantCombo.setItems(plantNames);

        actionCombo.setItems(FXCollections.observableArrayList("water", "fertilize", "prune"));
        actionCombo.setValue("water");
        datePicker.setValue(LocalDate.now());

        setupTable();
        loadData();
    }

    private void setupTable() {
        colPlant.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getPlantName() != null ? c.getValue().getPlantName() : "—"));

        // Action with icon
        colAction.setCellValueFactory(c -> {
            String action = c.getValue().getAction();
            String display = "";
            switch (action) {
                case "water":     display = "💧 Arrosage"; break;
                case "fertilize": display = "🧪 Fertilisation"; break;
                case "prune":     display = "✂ Taille"; break;
                default:          display = action;
            }
            return new SimpleStringProperty(display);
        });

        colDate.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getScheduledDate() != null ? c.getValue().getScheduledDate().toString() : ""));

        // Completed column with toggle button
        colCompleted.setCellFactory(col -> new TableCell<WateringSchedule, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    WateringSchedule sched = getTableView().getItems().get(getIndex());
                    Button toggleBtn = new Button(sched.isCompleted() ? "✅ Fait" : "⏳ En attente");
                    toggleBtn.getStyleClass().addAll(
                        sched.isCompleted() ? "btn-secondary" : "btn-primary", "btn-small"
                    );
                    toggleBtn.setOnAction(e -> {
                        DatabaseHelper.toggleScheduleComplete(sched.getId());
                        loadData();
                    });
                    setGraphic(toggleBtn);
                }
            }
        });

        colNotes.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getNotes() != null ? c.getValue().getNotes() : ""));

        // Delete action
        colActions.setCellFactory(col -> new TableCell<WateringSchedule, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    WateringSchedule sched = getTableView().getItems().get(getIndex());
                    Button delBtn = new Button("🗑");
                    delBtn.getStyleClass().addAll("btn-danger", "btn-small");
                    delBtn.setOnAction(e -> {
                        DatabaseHelper.deleteSchedule(sched.getId());
                        loadData();
                    });
                    setGraphic(new HBox(delBtn));
                }
            }
        });
    }

    private void loadData() {
        scheduleList = FXCollections.observableArrayList(DatabaseHelper.getAllSchedules());
        filteredSchedules = new FilteredList<>(scheduleList, s -> true);

        if (showPendingOnly.isSelected()) {
            filteredSchedules.setPredicate(s -> !s.isCompleted());
        }

        scheduleTable.setItems(filteredSchedules);
        countLabel.setText(filteredSchedules.size() + " tâches");
    }

    @FXML
    private void handleFilterToggle() {
        if (showPendingOnly.isSelected()) {
            filteredSchedules.setPredicate(s -> !s.isCompleted());
        } else {
            filteredSchedules.setPredicate(s -> true);
        }
        countLabel.setText(filteredSchedules.size() + " tâches");
    }

    @FXML
    private void handleAddSchedule() {
        String plantSelection = plantCombo.getValue();
        String action = actionCombo.getValue();
        LocalDate date = datePicker.getValue();
        String notes = notesField.getText().trim();

        if (plantSelection == null || action == null || date == null) {
            formMessage.setText("Veuillez sélectionner une plante, une action et une date.");
            formMessage.getStyleClass().setAll("error-label");
            return;
        }

        // Extract plant ID from combo text "ID — Name"
        int plantId;
        try {
            plantId = Integer.parseInt(plantSelection.split(" — ")[0].trim());
        } catch (NumberFormatException e) {
            formMessage.setText("Erreur: plante invalide.");
            formMessage.getStyleClass().setAll("error-label");
            return;
        }

        WateringSchedule schedule = new WateringSchedule(plantId, action, date, notes);
        if (DatabaseHelper.addSchedule(schedule)) {
            formMessage.setText("Tâche ajoutée avec succès !");
            formMessage.getStyleClass().setAll("success-label");
            notesField.clear();
            datePicker.setValue(LocalDate.now());
            loadData();
        } else {
            formMessage.setText("Erreur lors de l'ajout.");
            formMessage.getStyleClass().setAll("error-label");
        }
    }

    // ── NAVIGATION ──
    @FXML private void goToDashboard() { Main.switchScene("ui/dashboard.fxml"); }
    @FXML private void goToPlants() { Main.switchScene("ui/plants.fxml"); }
    @FXML private void goToUsers() { Main.switchScene("ui/users.fxml"); }
    @FXML private void goToWeather() { Main.switchScene("ui/weather.fxml"); }
    @FXML private void goToSchedule() { /* Already here */ }
    @FXML private void handleLogout() { Main.logout(); }
}
