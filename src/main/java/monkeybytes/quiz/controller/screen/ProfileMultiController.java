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
import monkeybytes.quiz.game.PlayerDataManager;

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

    private final PlayerDataManager playerDataManager = new PlayerDataManager("src/main/resources/data/playerData.json");

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
        player1ProfileComboBox.getItems().addAll(playerDataManager.getPlayerNames());
        player2ProfileComboBox.getItems().addAll(playerDataManager.getPlayerNames());
    }

    /**
     * Erstellt ein neues Profil basierend auf dem übergebenen Textfeld und fügt es in das Dropdown-Menü ein.
     */
    private void createProfile(TextField profileTextField, ComboBox<String> profileComboBox) {
        String profileName = profileTextField.getText().trim();

        if (profileName.isEmpty()) {
            System.out.println("Input Error. Please enter a name.");
            return;
        }

        boolean profileCreated = playerDataManager.addProfile(profileName);

        if (profileCreated) {
            profileComboBox.getItems().add(profileName);
            System.out.println("Profile created: " + profileName);
        } else {
            System.out.println("Profile Error Profile already exists: " + profileName);
        }
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

        if (player1Profile.equals(player2Profile)) {
            System.out.println("Selection Error Players must select different profiles.");
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