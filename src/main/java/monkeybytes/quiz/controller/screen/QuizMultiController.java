package monkeybytes.quiz.controller.screen;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import monkeybytes.quiz.game.Multiplayer;
import monkeybytes.quiz.game.Player;
import monkeybytes.quiz.game.Question;
import monkeybytes.quiz.game.QuestionTimer;
import monkeybytes.quiz.service.TriviaAPIService;

import java.util.List;

public class QuizMultiController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Pane headerPane, questionPane, questionSeparator;
    @FXML
    private Label questionLabel, currentPlayerLabel, player1ScoreLabel, player2ScoreLabel, timerLabel;
    @FXML
    private VBox answersVBox, optionsVBox;
    @FXML
    private Button optionAButton, optionBButton, optionCButton, optionDButton;

    private Multiplayer game;
    private QuestionTimer questionTimer;
    private boolean isSecondPlayerTurn = false; // Wechselt zwischen den Spielern
    private int remainingTime = 30;
    private TriviaAPIService triviaAPIService = new TriviaAPIService();

    @FXML
    public void initialize() {
        // Antwort-Buttons mit Event-Handlern verknüpfen
        optionAButton.setOnAction(event -> handleAnswer(0));
        optionBButton.setOnAction(event -> handleAnswer(1));
        optionCButton.setOnAction(event -> handleAnswer(2));
        optionDButton.setOnAction(event -> handleAnswer(3));
    }

    /**
     * Setzt die API-Parameter und lädt die Fragen.
     */
    public void setApiParameters(String category, String difficulty, String profile1, String profile2) {
        try {
            // Kategorien-ID und Fragen von der API laden
            String categoryId = triviaAPIService.getFixedCategoryId(category);
            List<Question> questions = triviaAPIService.fetchUniqueQuestions(10, categoryId, difficulty);

            // Multiplayer-Spiel initialisieren
            game = new Multiplayer(questions);

            // Spielerprofile setzen
            List<Player> players = game.getPlayers();
            players.get(0).setName(profile1);
            players.get(1).setName(profile2);

            updatePlayerDisplay();
            loadQuestion();
        } catch (Exception e) {
            e.printStackTrace();
            questionLabel.setText("Fehler beim Laden der Fragen. Bitte versuche es erneut.");
        }
    }

    /**
     * Lädt die aktuelle Frage und zeigt sie an.
     */
    private void loadQuestion() {
        // Hol die aktuelle Frage
        Question currentQuestion = game.getCurrentQuestion();
        if (currentQuestion != null) {
            // Stile der Buttons zurücksetzen
            resetButtonStyles();

            // Setze die Frage
            questionLabel.setText(currentQuestion.getQuestionText());

            // Setze die Antwortmöglichkeiten
            List<String> options = currentQuestion.getOptions();
            optionAButton.setText(options.get(0));
            optionBButton.setText(options.get(1));
            optionCButton.setText(options.get(2));
            optionDButton.setText(options.get(3));

            // Starte den Timer
            startTimer();

            // Debug-Ausgabe, um sicherzustellen, dass die richtigen Werte geladen werden
            System.out.println("Frage geladen: " + currentQuestion.getQuestionText());
            System.out.println("Antwortmöglichkeiten: " + options);
        } else {
            // Falls keine Fragen mehr vorhanden sind
            showResults();
        }
    }


    /**
     * Behandelt die Antwort eines Spielers.
     */
    private void handleAnswer(int selectedOptionIndex) {
        Question currentQuestion = game.getCurrentQuestion();
        if (currentQuestion != null) {
            int correctIndex = currentQuestion.getCorrectOptionIndex();
            markAnswers(correctIndex, selectedOptionIndex);

            // Antwort prüfen und Punktestand aktualisieren
            game.checkAnswer(selectedOptionIndex);

            if (!isSecondPlayerTurn) {
                isSecondPlayerTurn = true; // Nächster Spieler ist an der Reihe
                updatePlayerDisplay();
                resetTimer();
            } else {
                isSecondPlayerTurn = false; // Zurück zum ersten Spieler
                moveToNextQuestion();
            }
        }
    }

    /**
     * Bewegt das Spiel zur nächsten Frage.
     */
    private void moveToNextQuestion() {
        game.setCurrentQuestionIndex(game.getCurrentQuestionIndex() + 1); // Frageindex erhöhen
        loadQuestion(); // Neue Frage laden
    }

    /**
     * Zeigt die Ergebnisse an, wenn das Spiel vorbei ist.
     */
    private void showResults() {
        stopTimer();
        String winnerMessage = game.getWinner();
        questionLabel.setText("🎉 " + winnerMessage);

        // Buttons deaktivieren
        optionAButton.setDisable(true);
        optionBButton.setDisable(true);
        optionCButton.setDisable(true);
        optionDButton.setDisable(true);
    }

    /**
     * Zeigt die richtigen und falschen Antworten an.
     */
    private void markAnswers(int correctIndex, int selectedIndex) {
        List<Button> buttons = List.of(optionAButton, optionBButton, optionCButton, optionDButton);
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            if (i == correctIndex) {
                button.setStyle("-fx-background-color: #16ad09; -fx-text-fill: white;"); // Grün
            } else if (i == selectedIndex && i != correctIndex) {
                button.setStyle("-fx-background-color: red; -fx-text-fill: white;"); // Rot
            }
        }
    }

    /**
     * Aktualisiert die Anzeige des aktuellen Spielers.
     */
    private void updatePlayerDisplay() {
        Player currentPlayer = game.getCurrentPlayerProfile();
        currentPlayerLabel.setText("Current Player: " + currentPlayer.getName());

        // Punktestände aktualisieren
        List<Player> players = game.getPlayers();
        player1ScoreLabel.setText(players.get(0).getName() + ": " + players.get(0).getScore() + " Points");
        player2ScoreLabel.setText(players.get(1).getName() + ": " + players.get(1).getScore() + " Points");
    }

    /**
     * Setzt die Stile der Antwort-Buttons zurück.
     */
    private void resetButtonStyles() {
        List<Button> buttons = List.of(optionAButton, optionBButton, optionCButton, optionDButton);
        for (Button button : buttons) {
            button.setStyle(null);
        }
    }

    /**
     * Startet den Timer für die aktuelle Frage.
     */
    private void startTimer() {
        questionTimer = new QuestionTimer(remainingTime);
        questionTimer.startTimer();

        new Thread(() -> {
            while (!questionTimer.getTimerUp()) {
                try {
                    Thread.sleep(500);
                    Platform.runLater(this::updateTimerLabel);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (questionTimer.getTimerUp()) {
                Platform.runLater(() -> handleAnswer(-1)); // Automatische Antwort bei Timeout
            }
        }).start();
    }

    /**
     * Stoppt den Timer.
     */
    private void stopTimer() {
        if (questionTimer != null) {
            questionTimer.stopTimer();
        }
    }

    /**
     * Aktualisiert das Timer-Label im GUI.
     */
    private void updateTimerLabel() {
        int remainingTime = questionTimer.getRemainingTime();
        timerLabel.setText("⏳ " + remainingTime + "s");
        timerLabel.setStyle(remainingTime <= 5 ? "-fx-text-fill: red;" : "-fx-text-fill: black;");
    }

    /**
     * Setzt den Timer zurück.
     */
    private void resetTimer() {
        stopTimer();
        startTimer();
    }
}