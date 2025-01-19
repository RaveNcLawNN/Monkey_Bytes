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

    @FXML
    private Label profileAlerts1;

    @FXML
    private Label profileAlerts2;

    private List<Player> players = new ArrayList<>();

    private final PlayerDataManager playerDataManager = new PlayerDataManager("src/main/resources/data/playerData.json");

    @FXML
    public void initialize() {
        loadProfiles();

        createPlayer1ProfileButton.setOnAction(event -> createProfileOne(player1ProfileTextField, player1ProfileComboBox));
        createPlayer2ProfileButton.setOnAction(event -> createProfileTwo(player2ProfileTextField, player2ProfileComboBox));
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
    private void createProfileOne(TextField profileTextField, ComboBox<String> profileComboBox) {
        String profileName = profileTextField.getText().trim();

        if (profileName.isEmpty() || !(profileName.matches("[a-zA-Z0-9]{3,15}"))) {
            profileAlerts1.setText("Please choose a valid Name.");
            profileAlerts1.setStyle("-fx-text-fill: darkred");
            profileAlerts1.setVisible(true);
            return;
        }

        boolean profileCreated = playerDataManager.addProfile(profileName);

        if (profileCreated) {
            profileComboBox.getItems().add(profileName);
            profileAlerts1.setText("Profile created: " + profileName);
            profileAlerts1.setStyle("-fx-text-fill: green");
            profileAlerts1.setVisible(true);
        } else {
            profileAlerts1.setText("Profile already exists.");
            profileAlerts1.setStyle("-fx-text-fill: darkred");
            profileAlerts1.setVisible(true);
        }
    }

    private void createProfileTwo(TextField profileTextField, ComboBox<String> profileComboBox) {
        String profileName = profileTextField.getText().trim();

        if (profileName.isEmpty() || !(profileName.matches("[a-zA-Z0-9]{3,15}"))) {
            profileAlerts2.setText("Please choose a valid Name.");
            profileAlerts2.setStyle("-fx-text-fill: red");
            profileAlerts2.setVisible(true);
            return;
        }

        boolean profileCreated = playerDataManager.addProfile(profileName);

        if (profileCreated) {
            profileComboBox.getItems().add(profileName);
            profileAlerts2.setText("Profile created: " + profileName);
            profileAlerts2.setStyle("-fx-text-fill: green");
            profileAlerts2.setVisible(true);
        } else {
            profileAlerts2.setText("Profile already exists.");
            profileAlerts2.setStyle("-fx-text-fill: red");
            profileAlerts2.setVisible(true);
        }
    }

    /**
     * Wechselt zur Kategorieauswahl.
     */
    private void goToCategorySelection() {
        String player1Profile = player1ProfileComboBox.getValue();
        String player2Profile = player2ProfileComboBox.getValue();

        if (player1Profile == null || player2Profile == null || player1Profile.isEmpty() || player2Profile.isEmpty()) {
            profileAlerts1.setText("Select TWO Players.");
            profileAlerts1.setStyle("-fx-text-fill: darkred");
            profileAlerts1.setVisible(true);
            profileAlerts2.setText("Select TWO Players.");
            profileAlerts2.setStyle("-fx-text-fill: red");
            profileAlerts2.setVisible(true);
            return;
        }

        if (player1Profile.equals(player2Profile)) {
            profileAlerts1.setText("Select two DIFFERENT Players.");
            profileAlerts1.setStyle("-fx-text-fill: darkred");
            profileAlerts1.setVisible(true);
            profileAlerts2.setText("Select two DIFFERENT Players.");
            profileAlerts2.setStyle("-fx-text-fill: red");
            profileAlerts2.setVisible(true);
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