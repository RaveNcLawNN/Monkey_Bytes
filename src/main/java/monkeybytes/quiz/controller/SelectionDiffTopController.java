package monkeybytes.quiz.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

    private String profileName; // Das Profil, das vom vorherigen Screen übergeben wurde
    private TriviaAPIService triviaAPIService = new TriviaAPIService(); // Instanz des TriviaAPIService

    /**
     * Diese Methode wird vom ProfileSingleController aufgerufen, um das Profil zu setzen.
     */
    public void setProfile(String profileName) {
        this.profileName = profileName;
        System.out.println("Profil für diesen Screen: " + profileName);
    }

    @FXML
    public void initialize() {
        // Schwierigkeit hinzufügen (statisch)
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
            System.out.println("Please choose a category and difficulty level!");
            return;
        }

        try {
            // Lädt die nächste Screen-Layout-Datei (Quiz-Screen)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screens/quiz-single-screen.fxml"));
            Parent root = loader.load();

            // Holt den Controller des nächsten Screens und übergibt die Parameter
            QuizSingleController quizController = loader.getController();
            quizController.setApiParameters(selectedCategory, selectedDifficulty, profileName);

            // Holt das aktuelle Fenster und ersetzt den Inhalt mit dem neuen Screen
            Stage stage = (Stage) categoryComboBox.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

