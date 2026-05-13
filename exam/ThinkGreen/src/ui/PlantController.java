package ui;

import app.Main;
import db.DatabaseHelper;
import models.Plant;
import models.User;

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
import java.util.ResourceBundle;

/**
 * Controller for the Plants CRUD page.
 */
public class PlantController implements Initializable {

    @FXML private Label userInfoLabel;
    @FXML private Label formTitle;
    @FXML private Label formMessage;
    @FXML private Label countLabel;

    @FXML private TextField nameField;
    @FXML private TextField speciesField;
    @FXML private TextField locationField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField searchField;

    @FXML private Button submitBtn;
    @FXML private Button cancelBtn;

    @FXML private TableView<Plant> plantsTable;
    @FXML private TableColumn<Plant, String> colName;
    @FXML private TableColumn<Plant, String> colSpecies;
    @FXML private TableColumn<Plant, String> colLocation;
    @FXML private TableColumn<Plant, String> colDate;
    @FXML private TableColumn<Plant, String> colStatus;
    @FXML private TableColumn<Plant, String> colActions;

    private ObservableList<Plant> plantsList;
    private FilteredList<Plant> filteredPlants;
    private Plant editingPlant = null; // null = add mode, non-null = edit mode

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = Main.getCurrentUser();
        if (user != null) {
            userInfoLabel.setText(user.getUsername() + " (" + user.getRole() + ")");
        }

        // Status combo
        statusCombo.setItems(FXCollections.observableArrayList(
            "healthy", "needs_water", "sick", "harvested"
        ));
        statusCombo.setValue("healthy");
        datePicker.setValue(LocalDate.now());

        setupTable();
        loadData();
        setupSearch();
    }

    private void setupTable() {
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colSpecies.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSpecies()));
        colLocation.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLocation()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getPlantedDate() != null ? c.getValue().getPlantedDate().toString() : ""));
        
        // Status column with colored text
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        colStatus.setCellFactory(col -> new TableCell<Plant, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().removeAll("status-healthy","status-needs-water","status-sick","status-harvested");
                } else {
                    String display = "";
                    switch (item) {
                        case "healthy":     display = "✅ Saine"; break;
                        case "needs_water": display = "💧 Besoin d'eau"; break;
                        case "sick":        display = "🤒 Malade"; break;
                        case "harvested":   display = "🌾 Récoltée"; break;
                        default:            display = item;
                    }
                    Label lbl = new Label(display);
                    switch (item) {
                        case "healthy":     lbl.getStyleClass().add("status-healthy"); break;
                        case "needs_water": lbl.getStyleClass().add("status-needs-water"); break;
                        case "sick":        lbl.getStyleClass().add("status-sick"); break;
                        case "harvested":   lbl.getStyleClass().add("status-harvested"); break;
                    }
                    setGraphic(lbl);
                }
            }
        });

        // Actions column (Edit + Delete)
        colActions.setCellFactory(col -> new TableCell<Plant, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Plant plant = getTableView().getItems().get(getIndex());
                    Button editBtn = new Button("✏ Modifier");
                    editBtn.getStyleClass().addAll("btn-edit", "btn-small");
                    editBtn.setOnAction(e -> beginEdit(plant));

                    Button delBtn = new Button("🗑 Supprimer");
                    delBtn.getStyleClass().addAll("btn-danger", "btn-small");
                    delBtn.setOnAction(e -> deletePlant(plant));

                    HBox box = new HBox(8, editBtn, delBtn);
                    setGraphic(box);
                }
            }
        });
    }

    private void loadData() {
        plantsList = FXCollections.observableArrayList(DatabaseHelper.getAllPlants());
        filteredPlants = new FilteredList<>(plantsList, p -> true);
        plantsTable.setItems(filteredPlants);
        countLabel.setText(plantsList.size() + " plantes");
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            filteredPlants.setPredicate(plant -> {
                if (filter.isEmpty()) return true;
                return plant.getName().toLowerCase().contains(filter)
                    || plant.getSpecies().toLowerCase().contains(filter)
                    || plant.getLocation().toLowerCase().contains(filter);
            });
            countLabel.setText(filteredPlants.size() + " plantes");
        });
    }

    @FXML
    private void handleSubmit() {
        String name = nameField.getText().trim();
        String species = speciesField.getText().trim();
        String loc = locationField.getText().trim();
        LocalDate date = datePicker.getValue();
        String status = statusCombo.getValue();

        if (name.isEmpty() || species.isEmpty() || loc.isEmpty() || date == null || status == null) {
            formMessage.setText("Veuillez remplir tous les champs.");
            formMessage.getStyleClass().setAll("error-label");
            return;
        }

        User user = Main.getCurrentUser();

        if (editingPlant == null) {
            // ADD
            Plant newPlant = new Plant(name, species, loc, date, status, user.getId());
            if (DatabaseHelper.addPlant(newPlant)) {
                formMessage.setText("Plante ajoutée avec succès !");
                formMessage.getStyleClass().setAll("success-label");
                clearForm();
                loadData();
            } else {
                formMessage.setText("Erreur lors de l'ajout.");
                formMessage.getStyleClass().setAll("error-label");
            }
        } else {
            // EDIT
            editingPlant.setName(name);
            editingPlant.setSpecies(species);
            editingPlant.setLocation(loc);
            editingPlant.setPlantedDate(date);
            editingPlant.setStatus(status);
            if (DatabaseHelper.updatePlant(editingPlant)) {
                formMessage.setText("Plante modifiée avec succès !");
                formMessage.getStyleClass().setAll("success-label");
                cancelEdit();
                loadData();
            } else {
                formMessage.setText("Erreur lors de la modification.");
                formMessage.getStyleClass().setAll("error-label");
            }
        }
    }

    private void beginEdit(Plant plant) {
        editingPlant = plant;
        nameField.setText(plant.getName());
        speciesField.setText(plant.getSpecies());
        locationField.setText(plant.getLocation());
        datePicker.setValue(plant.getPlantedDate());
        statusCombo.setValue(plant.getStatus());
        formTitle.setText("✏ Modifier la Plante");
        submitBtn.setText("Enregistrer");
        cancelBtn.setVisible(true);
        formMessage.setText("");
    }

    private void cancelEdit() {
        editingPlant = null;
        formTitle.setText("➕ Ajouter une Plante");
        submitBtn.setText("Ajouter");
        cancelBtn.setVisible(false);
        clearForm();
    }

    @FXML
    private void handleCancel() {
        cancelEdit();
    }

    private void deletePlant(Plant plant) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer \"" + plant.getName() + "\" ?");
        alert.setContentText("Cette action est irréversible.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DatabaseHelper.deletePlant(plant.getId());
                loadData();
            }
        });
    }

    private void clearForm() {
        nameField.clear();
        speciesField.clear();
        locationField.clear();
        datePicker.setValue(LocalDate.now());
        statusCombo.setValue("healthy");
        formMessage.setText("");
    }

    // ── NAVIGATION ──
    @FXML private void goToDashboard() { Main.switchScene("ui/dashboard.fxml"); }
    @FXML private void goToPlants() { /* Already here */ }
    @FXML private void goToUsers() { Main.switchScene("ui/users.fxml"); }
    @FXML private void goToWeather() { Main.switchScene("ui/weather.fxml"); }
    @FXML private void goToSchedule() { Main.switchScene("ui/schedule.fxml"); }
    @FXML private void handleLogout() { Main.logout(); }
}
