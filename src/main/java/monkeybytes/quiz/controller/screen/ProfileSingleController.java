package monkeybytes.quiz.controller.screen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import monkeybytes.quiz.controller.screen.SelectionDiffTopController;
import monkeybytes.quiz.game.Player;
import monkeybytes.quiz.game.PlayerDataManager;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
     * Initialisiert den Controller.
     * Diese Methode wird automatisch nach dem Laden der FXML-Datei aufgerufen.
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
     * Erstellt ein neues Profil basierend auf dem Benutzereingabewert.
     */
    private void createProfile() {
        // Holt den eingegebenen Profilnamen und entfernt überflüssige Leerzeichen.
        String profileName = newProfileTextField.getText().trim();

        // Überprüft, ob das Eingabefeld leer ist.
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
     */
    private void goToCategorySelection() {
        String selectedProfile = profileComboBox.getValue();

        // Überprüft, ob ein Profil ausgewählt wurde.
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
        }
    }
}






//package monkeybytes.quiz.controller;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import javafx.fxml.FXML;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.TextField;
//
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.control.Button;
//import javafx.scene.layout.AnchorPane;
//import javafx.stage.Stage;
//
//public class ProfileSingleController {
//    @FXML
//    private AnchorPane rootPane;
//
//    @FXML
//    private Button testButton;
//
//    @FXML
//    public void initialize() {
//        // event handler
//        testButton.setOnAction(event -> openSelectionScreen());
//    }
//
//    private void openSelectionScreen() {
//        try {
//            // lädt den selection difficulty topic screen (noch nicht fertig - leitet jetzt mal direkt zum quiz single screen weiter)
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screens/quiz-single-screen.fxml"));
//            Parent root = fxmlLoader.load();
//
//            // aktuelles fenster wird dynamisch verändert. kein neues wird geöffnet
//            Stage currentStage = (Stage) rootPane.getScene().getWindow();
//            currentStage.getScene().setRoot(root);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}