package monkeybytes.quiz.controller.screen;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import monkeybytes.quiz.service.TriviaAPIService;

import java.util.List;

public class SelectionDiffTopController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private ComboBox<String> difficultyComboBox;

    @FXML
    private Button startQuizButton;

    @FXML
    private Label chooseCatDiffAlert;

    private String profileName; // Das Profil, das vom vorherigen Screen übergeben wurde
    private TriviaAPIService triviaAPIService = new TriviaAPIService(); // Instanz des TriviaAPIService
    private boolean isMultiplayer = false; // Flag zur Steuerung des Quiz-Modus

    /**
     * Setzt das Profil und den Spielmodus (Singleplayer/Multiplayer).
     *
     * @param profileName Der kombinierte Profilname (z. B. "Player 1 vs. Player 2").
     * @param isMultiplayer Gibt an, ob Multiplayer gespielt wird.
     */
    public void setProfileAndMode(String profileName, boolean isMultiplayer) {
        this.profileName = profileName;
        this.isMultiplayer = isMultiplayer;
        System.out.println("Profil: " + profileName + ", Multiplayer: " + isMultiplayer);
    }

    @FXML
    public void initialize() {
        // Schwierigkeit (statisch)
        difficultyComboBox.getItems().addAll("easy", "medium", "hard");

        // Kategorien dynamisch von der API laden
        loadCategories();

        // Event-Handler für den Start-Button
        startQuizButton.setOnAction(event -> startQuiz());
    }

    /**
     * Lädt die Kategorien von der API und fügt sie in das Dropdown-Menü ein.
     */
    private void loadCategories() {
        try {
            List<String> categories = triviaAPIService.getFixedCategories(); // Kategorie-Liste aus der API laden
            categoryComboBox.getItems().addAll(categories); // Kategorien in das Dropdown-Menü einfügen
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not load categories.");
        }
    }

    /**
     * Startet das Quiz mit den ausgewählten Optionen.
     */
    private void startQuiz() {
        String selectedCategory = categoryComboBox.getValue();
        String selectedDifficulty = difficultyComboBox.getValue();

        if (selectedCategory == null || selectedDifficulty == null) {
            chooseCatDiffAlert.setText("Choose Category AND Difficulty.");
            chooseCatDiffAlert.setStyle("-fx-text-fill: red");
            chooseCatDiffAlert.setVisible(true);
            return;
        }

        try {
            FXMLLoader loader;

            if (isMultiplayer) {
                // Multiplayer: Lade das Multiplayer-Screen-Layout
                loader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screen/quiz-multi-screen.fxml"));
            } else {
                // Singleplayer: Lade das Singleplayer-Screen-Layout
                loader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screen/quiz-single-screen.fxml"));
            }

            Parent root = loader.load();

            if (isMultiplayer) {
                // Multiplayer: Initialisiere den QuizMultiController
                QuizMultiController quizController = loader.getController();
                String[] profiles = profileName.split(" vs. "); // Annahme: Profile sind durch "vs." getrennt
                quizController.setApiParameters(selectedCategory, selectedDifficulty, profiles[0], profiles[1]); // Setze Parameter für Multiplayer
            } else {
                // Singleplayer: Initialisiere den QuizSingleController
                QuizSingleController quizController = loader.getController();
                quizController.setApiParameters(selectedCategory, selectedDifficulty, profileName);
            }

            // Wechsle zum neuen Screen
            Stage stage = (Stage) categoryComboBox.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not load next screen.");
        }
    }
}
