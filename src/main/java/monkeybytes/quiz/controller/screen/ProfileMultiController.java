package monkeybytes.quiz.controller.screen;

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
import monkeybytes.quiz.controller.screen.SelectionDiffTopController;
import monkeybytes.quiz.game.Player;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProfileMultiController {
    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField player1ProfileTextField;

    @FXML
    private TextField player2ProfileTextField;

    @FXML
    private ComboBox<String> player1ProfileComboBox;

    @FXML
    private ComboBox<String> player2ProfileComboBox;

    @FXML
    private Button createPlayer1ProfileButton;

    @FXML
    private Button createPlayer2ProfileButton;

    @FXML
    private Button nextButton;

    private List<Player> players = new ArrayList<>();

    private static final String DATA_FILE = "src/main/resources/data/playerData.json";

    @FXML
    public void initialize() {
        loadProfiles();

        createPlayer1ProfileButton.setOnAction(event -> createProfile(player1ProfileTextField, player1ProfileComboBox));
        createPlayer2ProfileButton.setOnAction(event -> createProfile(player2ProfileTextField, player2ProfileComboBox));
        nextButton.setOnAction(event -> goToCategorySelection());
    }

    /**
     * Lädt die gespeicherten Profile aus der JSON-Datei und füllt die Dropdown-Menüs.
     */
    private void loadProfiles() {
        try (FileReader reader = new FileReader(DATA_FILE)) {
            Type listType = new TypeToken<List<Player>>() {
            }.getType();
            players = new Gson().fromJson(reader, listType);

            if (players != null) {
                for (Player player : players) {
                    player1ProfileComboBox.getItems().add(player.getName());
                    player2ProfileComboBox.getItems().add(player.getName());
                }
            } else {
                players = new ArrayList<>();
            }
        } catch (Exception e) {
            players = new ArrayList<>();
            e.printStackTrace();
        }
    }

    /**
     * Speichert die aktuellen Profile in der JSON-Datei.
     */
    private void saveProfiles() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(players, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Erstellt ein neues Profil basierend auf dem übergebenen Textfeld und fügt es in das Dropdown-Menü ein.
     */
    private void createProfile(TextField profileTextField, ComboBox<String> profileComboBox) {
        String profileName = profileTextField.getText().trim();

        if (profileName.isEmpty()) {
            System.out.println("Please enter a name.");
            return;
        }

        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(profileName)) {
                System.out.println("Profile " + player.getName() + " already exists.");
                return;
            }
        }

        Player newPlayer = new Player(profileName, 0);
        players.add(newPlayer);
        profileComboBox.getItems().add(profileName);
        saveProfiles();
        System.out.println("Profile created: " + profileName);
    }

    /**
     * Wechselt zur Kategorieauswahl.
     */
    private void goToCategorySelection() {
        String player1Profile = player1ProfileComboBox.getValue();
        String player2Profile = player2ProfileComboBox.getValue();

        if (player1Profile == null || player2Profile == null || player1Profile.isEmpty() || player2Profile.isEmpty()) {
            System.out.println("Please select profiles for both players.");
            return;
        }

        // Übergebe die Profildaten an den nächsten Controller (z. B. Multiplayer-Kategorie- und Schwierigkeitsauswahl)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screen/selection-difficulty-topic-screen.fxml"));
            Parent root = loader.load();

            // Hole den Controller der Kategorieauswahl
            SelectionDiffTopController controller = loader.getController();

            // Profil und Modus anpassen
            controller.setProfileAndMode(player1Profile + " vs. " + player2Profile, true);

            // Wechsel zum nächsten Screen
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}