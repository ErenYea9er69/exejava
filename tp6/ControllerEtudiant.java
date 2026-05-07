import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerEtudiant implements Initializable {

    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfPrenom;
    @FXML
    private RadioButton rbFemme;
    @FXML
    private RadioButton rbHomme;
    @FXML
    private ToggleGroup sexeGroup;
    @FXML
    private ComboBox<String> cbFiliere;
    @FXML
    private TableView<Etudiant> tableEtudiants;
    @FXML
    private TableColumn<Etudiant, Integer> colId;
    @FXML
    private TableColumn<Etudiant, String> colNom;
    @FXML
    private TableColumn<Etudiant, String> colPrenom;
    @FXML
    private TableColumn<Etudiant, String> colSexe;
    @FXML
    private TableColumn<Etudiant, String> colFiliere;

    private EtudiantM manager = new EtudiantM();
    private ObservableList<Etudiant> etudiantsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colSexe.setCellValueFactory(new PropertyValueFactory<>("sexe"));
        colFiliere.setCellValueFactory(new PropertyValueFactory<>("filiere"));

        refreshTable();

        tableEtudiants.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        tfNom.setText(newSelection.getNom());
                        tfPrenom.setText(newSelection.getPrenom());
                        cbFiliere.setValue(newSelection.getFiliere());
                        if ("Femme".equals(newSelection.getSexe())) {
                            rbFemme.setSelected(true);
                        } else {
                            rbHomme.setSelected(true);
                        }
                    }
                }
        );
    }

    private void refreshTable() {
        etudiantsList.clear();
        etudiantsList.addAll(manager.findAll());
        tableEtudiants.setItems(etudiantsList);
    }

    private void clearFields() {
        tfNom.clear();
        tfPrenom.clear();
        cbFiliere.setValue(null);
        if (sexeGroup.getSelectedToggle() != null) {
            sexeGroup.getSelectedToggle().setSelected(false);
        }
        tableEtudiants.getSelectionModel().clearSelection();
    }

    private String getSelectedSexe() {
        if (rbFemme.isSelected()) return "Femme";
        if (rbHomme.isSelected()) return "Homme";
        return null;
    }

    private boolean validateFields() {
        if (tfNom.getText().isEmpty() || tfPrenom.getText().isEmpty()
                || getSelectedSexe() == null || cbFiliere.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Veuillez remplir tous les champs !");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void handleAjouter(ActionEvent event) {
        if (!validateFields()) return;

        Etudiant e = new Etudiant(
                tfNom.getText(),
                tfPrenom.getText(),
                getSelectedSexe(),
                cbFiliere.getValue()
        );

        if (manager.create(e)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Étudiant ajouté avec succès !");
            refreshTable();
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de l'ajout de l'étudiant.");
        }
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        Etudiant selected = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner un étudiant à supprimer !");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment supprimer cet étudiant ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (manager.delete(selected)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Étudiant supprimé avec succès !");
                refreshTable();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Erreur lors de la suppression.");
            }
        }
    }

    @FXML
    private void handleModifier(ActionEvent event) {
        Etudiant selected = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner un étudiant à modifier !");
            return;
        }

        if (!validateFields()) return;

        selected.setNom(tfNom.getText());
        selected.setPrenom(tfPrenom.getText());
        selected.setSexe(getSelectedSexe());
        selected.setFiliere(cbFiliere.getValue());

        if (manager.update(selected)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Étudiant modifié avec succès !");
            refreshTable();
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de la modification.");
        }
    }
}
