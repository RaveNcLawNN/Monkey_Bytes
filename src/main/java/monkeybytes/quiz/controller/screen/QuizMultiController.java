package monkeybytes.quiz.controller.screen;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import monkeybytes.quiz.controller.popup.PauseController;
import monkeybytes.quiz.game.*;
import monkeybytes.quiz.service.TriviaAPIService;

import java.util.List;

public class QuizMultiController {

    @FXML
    private VBox optionsVBox, answersVBox, playerSwitchOverlay;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Pane headerPane;
    @FXML
    private Label questionLabel, currentPlayerLabel, player1ScoreLabel, player2ScoreLabel, timerLabel, switchLabel;
    @FXML
    private Button optionAButton, optionBButton, optionCButton, optionDButton, readyButton;

    private Multiplayer game;
    private QuestionTimer questionTimer;
    private volatile boolean stopTimerThread = false;
    private boolean isAnswerSelected = false;
    private List<Button> answerButtons;
    private int remainingTime = 30;

    @FXML
    public void initialize() {
        // Buttons in eine Liste übernehmen
        answerButtons = List.of(optionAButton, optionBButton, optionCButton, optionDButton);

        // Ereignisse für Antwort-Buttons einrichten
        optionAButton.setOnAction(event -> handleAnswerMulti(0));
        optionBButton.setOnAction(event -> handleAnswerMulti(1));
        optionCButton.setOnAction(event -> handleAnswerMulti(2));
        optionDButton.setOnAction(event -> handleAnswerMulti(3));

        // pause popup wird angezeigt wenn man esc drückt
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventHandler(KeyEvent.KEY_PRESSED, escHandler);

                newScene.windowProperty().addListener((windowObservable, oldWindow, newWindow) -> {
                    if (newWindow == null) {
                        newScene.removeEventHandler(KeyEvent.KEY_PRESSED, escHandler);
                    }
                });
            }
        });
    }

    // wenn man auf esc drückt, wird pause popup angezeigt
    private final EventHandler<KeyEvent> escHandler = event -> {
        if (event.getCode() == KeyCode.ESCAPE) {
            showPausePopup();
        }
    };


    public void setApiParameters(String category, String difficulty, String profile1, String profile2) {
        try {
            // Trivia-Daten von der API abrufen
            TriviaAPIService triviaAPIService = new TriviaAPIService();
            String categoryId = triviaAPIService.getFixedCategoryId(category);

            // 10 Fragen holen
            List<Question> questions = triviaAPIService.fetchQuestions(10, categoryId, difficulty);

            // Spielerprofil-Daten laden
            PlayerDataManager dataManager = new PlayerDataManager("src/main/resources/data/playerData.json");
            Player player1 = dataManager.getPlayers().stream()
                    .filter(player -> player.getName().equals(profile1))
                    .findFirst()
                    .orElse(new Player(profile1, 0));
            Player player2 = dataManager.getPlayers().stream()
                    .filter(player -> player.getName().equals(profile2))
                    .findFirst()
                    .orElse(new Player(profile2, 0));

            // Multiplayer-Spiel erstellen
            game = new Multiplayer(questions, List.of(player1, player2));

            // Spiel vorbereiten
            updatePlayerDisplay();
            showPlayerSwitchScreen(); // Spiel startet (Spielerwechsel anzeigen)
        } catch (Exception e) {
            e.printStackTrace();
            questionLabel.setText("Could not fetch questions. Please try again.");
        }
    }

    private void loadQuestion() {
        Question currentQuestion = game.getCurrentQuestion();

        System.out.println("DEBUG: Current Question Index: " + game.getCurrentQuestionIndex());
        System.out.println("DEBUG: Current Question: " + (currentQuestion != null ? currentQuestion.getQuestionText() : "No more questions"));

        if (currentQuestion != null) {
            resetButtonStyles(); // Buttons vollständig zurücksetzen
            isAnswerSelected = false; // Spielzustand resetten
            setAnswerButtonsDisabled(false); // Antworten wieder aktivieren

            // Frage und Antwortoptionen setzen
            questionLabel.setText(currentQuestion.getQuestionText());
            adjustFontSize(questionLabel);

            List<String> options = currentQuestion.getOptions();
            for (int i = 0; i < options.size(); i++) {
                Button button = answerButtons.get(i);
                button.setText(options.get(i));
                button.setStyle(""); // Sicherstellen, dass Styles zurückgesetzt werden
                adjustFontSize(button);
            }

            startTimer(); // Timer neu starten
        } else {
            showResults(); // Alle Fragen beendet
        }
    }


    /**
     * Wird aufgerufen, wenn ein Spieler eine Antwort anklickt.
     */
    private void handleAnswerMulti(int selectedOptionIndex) {
        if (isAnswerSelected || game.getCurrentQuestion() == null) {
            System.out.println("DEBUG: Answer already selected or no question available.");
            return;
        }

        isAnswerSelected = true;
        int currentPlayerIndex = game.getCurrentPlayer();

        // Antwort des Spielers registrieren
        int remainingTimeForBonus = questionTimer.getRemainingTime();
        game.checkAnswer(selectedOptionIndex, currentPlayerIndex, remainingTimeForBonus);
        System.out.println("DEBUG: Player " + currentPlayerIndex + " answered. Question Index: " + game.getCurrentQuestionIndex());

        // Wenn ALLE Spieler geantwortet haben, wird die Runde beendet.
        if (currentPlayerIndex == game.getPlayers().size() - 1) {
            System.out.println("DEBUG: End of round. Processing...");
            processEndOfRound();
        } else {
            // Zum nächsten Spieler wechseln (Zwischenscreen anzeigen)
            System.out.println("DEBUG: Switching to the next player.");
            game.nextPlayer();
            showPlayerSwitchScreen();
        }
    }

    private void processEndOfRound() {
        Question currentQuestion = game.getCurrentQuestion();
        System.out.println("DEBUG: End of Round.");
        System.out.println("DEBUG: Current Question Index BEFORE moveToNextQuestion: " + game.getCurrentQuestionIndex());

        if (currentQuestion == null) {
            System.out.println("DEBUG: No more questions.");
            moveToNextQuestion(); // Sicherstellen, dass fortgefahren wird
            return;
        }

        // Richtige/falsche Antworten markieren
        markAnswers(currentQuestion.getCorrectOptionIndex());

        new Thread(() -> {
            try {
                Thread.sleep(2000); // 2-sekündige Pause
                Platform.runLater(() -> {
                    updatePlayerDisplay();
                    saveScores();// Punkte aktualisieren
                    if (game.moveToNextQuestion()) { // Fortschritt zur nächsten Frage
                        System.out.println("DEBUG: Current Question Index AFTER moveToNextQuestion: " + game.getCurrentQuestionIndex());
                        ((Multiplayer) game).resetToFirstPlayer();
                        showPlayerSwitchScreen();
                    } else {
                        System.out.println("DEBUG: Game Over!");
                        showResults(); // Spiel enden
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void moveToNextQuestion() {
        System.out.println("DEBUG: moveToNextQuestion (Controller) called."); // Debugging

        if (game.moveToNextQuestion()) {
            System.out.println("DEBUG: Moving to next question."); // Debugging
            showPlayerSwitchScreen(); // Vor der nächsten Frage Zwischenscreen anzeigen
        } else {
            System.out.println("DEBUG: No more questions available. Showing results."); // Debugging
            showResults();
        }
    }

    @FXML
    public void onReadyButtonClicked() {
        playerSwitchOverlay.setVisible(false); // Overlay ausblenden
        setAnswerButtonsDisabled(false); // Buttons aktivieren
        resetTimer(); // Timer resetten
        isAnswerSelected = false; // Ermöglicht eine neue Auswahl

        loadQuestion();
        saveScores();
    }

    /**
     * Zeigt den Screen an, der sagt "[Player X]'s turn".
     */
    private void showPlayerSwitchScreen() {
        int currentPlayerIndex = game.getCurrentPlayer();
        Player nextPlayer = game.getPlayers().get(currentPlayerIndex);

        switchLabel.setText(nextPlayer.getName() + "'s turn!");

        updatePlayerDisplay();

        playerSwitchOverlay.setVisible(true);
        playerSwitchOverlay.toFront(); // Überlagert die UI
        setAnswerButtonsDisabled(true);

        isAnswerSelected = false; // Zurücksetzen, um die neue Eingabe zuzulassen
    }

    /**
     * Aktualisiert die Labels der Spieleranzeige (aktueller Spieler und Scores).
     */
    private void updatePlayerDisplay() {
        Player currentPlayer = game.getPlayers().get(game.getCurrentPlayer());

        currentPlayerLabel.setText("Player: " + currentPlayer.getName());

        List<Player> players = game.getPlayers();
        player1ScoreLabel.setText(players.get(0).getName() + ": " + players.get(0).getScore() + " Points");
        player2ScoreLabel.setText(players.get(1).getName() + ": " + players.get(1).getScore() + " Points");
    }


    /**
     * Färbt die korrekte Antwort grün, alle anderen rot.
     */
    private void markAnswers(int correctIndex) {
        for (int i = 0; i < answerButtons.size(); i++) {
            Button button = answerButtons.get(i);
            if (i == correctIndex) {
                button.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            } else {
                button.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            }
        }
    }

    /**
     * Aktiviert/deaktiviert die Buttons (wichtig, damit kein Doppelklick).
     */
    private void setAnswerButtonsDisabled(boolean disabled) {
        answerButtons.forEach(button -> button.setDisable(disabled));
    }

    private void resetButtonStyles() {
        answerButtons.forEach(button -> button.setStyle(null));
    }

    /**
     * Speichert Punktestände in JSON (PlayerDataManager).
     */
    private void saveScores() {
        PlayerDataManager dataManager = new PlayerDataManager("src/main/resources/data/playerData.json");
        game.getPlayers().forEach(player -> dataManager.updatePlayerInformation(player.getName(), player.getScore()));
    }

    /**
     * Wird aufgerufen, wenn keine Fragen mehr vorhanden sind.
     * Zeigt das Ergebnis an.
     */
    private void showResults() {
        questionLabel.setText("🎉 " + game.getWinner());
        setAnswerButtonsDisabled(true);
    }

    /**
     * Startet den Timer für die aktuelle Frage.
     */
    private void startTimer() {
        questionTimer = new QuestionTimer(remainingTime);
        questionTimer.startTimer();
        stopTimerThread = false;

        new Thread(() -> {
            while (!stopTimerThread && !questionTimer.getTimerUp()) {
                try {
                    Thread.sleep(500);
                    Platform.runLater(this::updateTimerLabel);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!stopTimerThread && questionTimer.getTimerUp()) {
                Platform.runLater(() -> handleAnswerMulti(-1));
            }
        }).start();
    }

    /**
     * Stoppt den Timer.
     */
    private void stopTimer() {
        if (questionTimer != null) {
            remainingTime = questionTimer.getRemainingTime();
            questionTimer.stopTimer();
        }
        stopTimerThread = true;
    }

    /**
     * Timer auf 30s zurücksetzen und neu starten.
     */
    private void resetTimer() {
        if (questionTimer != null) {
            stopTimer();
            remainingTime = 30;
            startTimer();
        }
    }

    /**
     * Aktualisiert das Timer-Label im GUI.
     */
    private void updateTimerLabel() {
        int remainingTime = questionTimer.getRemainingTime();
        timerLabel.setText("⏳" + remainingTime);

        if (remainingTime == 0) {
            timerLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            timerLabel.setStyle(null);
        }
    }

    /**
     * Passt die Schriftgröße an.
     */
    private void adjustFontSize(Labeled labeled) {
        // Hol dir den Text
        String txt = labeled.getText();
        int length = (txt != null) ? txt.length() : 0;

        // Beispiel-Schwellwerte
        if (length > 300) {
            labeled.setStyle("-fx-font-size: 10px;");
        } else if (length > 200) {
            labeled.setStyle("-fx-font-size: 14px;");
        } else if (length > 100) {
            labeled.setStyle("-fx-font-size: 18px;");
        } else {
            labeled.setStyle("-fx-font-size: 24px;");
        }

        labeled.setWrapText(true);
    }

    /**
     * ESC -> Pause-Popup
     */
    private void showPausePopup() {
        try {
            if (rootPane.getScene() == null || rootPane.getScene().getWindow() == null) {
                return;
            }
            stopTimer();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/popup/pause-popup.fxml"));
            Parent root = fxmlLoader.load();

            Stage mainStage = (Stage) rootPane.getScene().getWindow();

            PauseController pauseController = fxmlLoader.getController();
            pauseController.setMainStage(mainStage);

            Stage pauseStage = new Stage();
            pauseStage.initStyle(StageStyle.UNDECORATED);
            pauseStage.initStyle(StageStyle.TRANSPARENT);
            pauseStage.initModality(Modality.APPLICATION_MODAL);
            pauseStage.initOwner(mainStage);

            pauseStage.setWidth(250);
            pauseStage.setHeight(300);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            pauseStage.setScene(scene);
            pauseStage.showAndWait();

            startTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
