package monkeybytes.quiz.controller.screen;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import monkeybytes.quiz.game.PlayerDataManager;

public class ProfileSingleController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField newProfileTextField;
    @FXML
    private ComboBox<String> profileComboBox;
    @FXML
    private Button createProfileButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label profileAlerts;

    private final PlayerDataManager playerDataManager = new PlayerDataManager("src/main/resources/data/playerData.json");

    /**
     * Initialisiert die Benutzeroberfläche für das Profilmanagement.
     * - Lädt verfügbare Profile beim Start.
     * - Verknüpft Buttons mit Methoden.
     */
    @FXML
    public void initialize() {
        loadProfiles();

        createProfileButton.setOnAction(event -> createProfile());
        nextButton.setOnAction(event -> goToCategorySelection());
    }

    /**
     * Lädt die gespeicherten Profile aus der JSON-Datei und füllt das Dropdown-Menü.
     */
    private void loadProfiles() {
        profileComboBox.getItems().addAll(playerDataManager.getPlayerNames());
    }

    /**
     * Erstellt ein neues Profil basierend auf der Benutzereingabe.
     * - Überprüft, ob der eingegebene Profilname gültig ist (3-15 alphanumerische Zeichen).
     * - Zeigt Erfolgs- und Fehlermeldung.
     * - Fügt das Profil hinzu, falls es noch nicht existiert, und aktualisiert die Profilanzeige.
     */
    private void createProfile() {
        String profileName = newProfileTextField.getText().trim();

        if (profileName.isEmpty() || !(profileName.matches("[a-zA-Z0-9]{3,15}"))) {
            profileAlerts.setText("Please choose a valid Name.");
            profileAlerts.setStyle("-fx-text-fill: red");
            profileAlerts.setVisible(true);
            return;
        }

        boolean profileCreated = playerDataManager.addProfile(profileName);

        if (profileCreated) {
            profileComboBox.getItems().add(profileName);
            profileAlerts.setText("Profile created: " + profileName);
            profileAlerts.setStyle("-fx-text-fill: green");
            profileAlerts.setVisible(true);
        } else {
            profileAlerts.setText("Profile already exists.");
            profileAlerts.setStyle("-fx-text-fill: red");
            profileAlerts.setVisible(true);
        }
    }

    /**
     * Wechselt zur Kategorie- und Schwierigkeitsauswahl.
     * - Zeigt Alert an, wenn kein Profil ausgewählt ist.
     */
    private void goToCategorySelection() {
        String selectedProfile = profileComboBox.getValue();

        if (selectedProfile == null || selectedProfile.isEmpty()) {
            profileAlerts.setText("Please choose or create a Profile.");
            profileAlerts.setStyle("-fx-text-fill: red");
            profileAlerts.setVisible(true);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screen/selection-difficulty-topic-screen.fxml"));
            Parent root = loader.load();

            SelectionDiffTopController controller = loader.getController();
            controller.setProfileAndMode(selectedProfile, false);

            Stage stage = (Stage) profileComboBox.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not load next screen.");
        }
    }
}