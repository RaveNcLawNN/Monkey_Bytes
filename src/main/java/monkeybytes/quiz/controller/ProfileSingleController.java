package monkeybytes.quiz.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import monkeybytes.quiz.game.Player;

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

    private List<Player> players = new ArrayList<>();

    private static final String DATA_FILE = "src/main/resources/data/playerData.json";

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
        try (FileReader reader = new FileReader(DATA_FILE)) {
            // Der Typ der Liste, die aus JSON gelesen wird.
            Type listType = new TypeToken<List<Player>>() {}.getType();
            // Liest die JSON-Datei und wandelt sie in eine Liste von Player-Objekten um.
            players = new Gson().fromJson(reader, listType);

            // Falls Profile existieren, fügt sie dem Dropdown-Menü hinzu.
            if (players != null) {
                for (Player player : players) {
                    profileComboBox.getItems().add(player.getName());
                }
            } else {
                players = new ArrayList<>(); // Initialisiert eine leere Liste, falls keine Profile vorhanden sind.
            }
        } catch (Exception e) {
            e.printStackTrace();
            players = new ArrayList<>();
        }
    }

    /**
     * Speichert die aktuellen Profile in der JSON-Datei.
     */
    private void saveProfiles() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(players, writer);
//            new Gson().toJson(players, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Erstellt ein neues Profil basierend auf dem Benutzereingabewert.
     */
    private void createProfile() {
        // Holt den eingegebenen Profilnamen und entfernt überflüssige Leerzeichen.
        String profileName = newProfileTextField.getText().trim();

        // Überprüft, ob das Eingabefeld leer ist.
        if (profileName.isEmpty()) {
            System.out.println("Please enter your name.");
            return;
        }

        // Überprüft, ob der Profilname bereits existiert.
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(profileName)) {
                System.out.println("Profile " + player.getName() + " already exists.");
                return;
            }
        }

        // Erstellt ein neues Player-Objekt mit dem eingegebenen Profilnamen.
        Player newPlayer = new Player(profileName, 0);
        // Fügt das neue Profil zur Liste und zum Dropdown-Menü hinzu.
        players.add(newPlayer);
        profileComboBox.getItems().add(profileName);
        // Speichert die aktualisierte Profil-Liste in der JSON-Datei.
        saveProfiles();
        System.out.println("Profil created: " + profileName);
    }

    /**
     * Wechselt zur Kategorie- und Schwierigkeitsauswahl.
     */
    private void goToCategorySelection() {
        String selectedProfile = profileComboBox.getValue();

        // Überprüft, ob ein Profil ausgewählt wurde.
        if (selectedProfile == null || selectedProfile.isEmpty()) {
            System.out.println("Please choose a profile.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screens/selection-difficulty-topic-screen.fxml"));
            Parent root = loader.load();

            SelectionDiffTopController controller = loader.getController();
            controller.setProfile(selectedProfile);

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