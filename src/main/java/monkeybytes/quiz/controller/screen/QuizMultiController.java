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

/**
 * QuizMultiController steuert den Ablauf eines Multiplayer-Quiz-Spiels.
 * Er ist mit dem FXML "quiz-multi-screen.fxml" verknüpft und verwaltet
 * das UI (zB die Buttons, Labels, den Switch-Screen für Spielerwechsel)
 * und die Kommunikation mit dem "Multiplayer"-Objekt.
 */

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
        // 1) Speichert Antwortbuttons in einer Liste
        answerButtons = List.of(optionAButton, optionBButton, optionCButton, optionDButton);

        // 2) Event Handler für Antwortbuttons
        optionAButton.setOnAction(event -> handleAnswerMulti(0));
        optionBButton.setOnAction(event -> handleAnswerMulti(1));
        optionCButton.setOnAction(event -> handleAnswerMulti(2));
        optionDButton.setOnAction(event -> handleAnswerMulti(3));

        // 3) ESC -> Pause popup
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

    /**
     * Wird von SelectionDiffTopController aufgerufen, nachdem der User
     * die Kategorie und Difficulty gewählt hat und zwei Profile (Player 1, Player 2).
     * - TriviaAPIService wird erzeugt (neuer Session-Token).
     * - 10 Fragen werden geholt (API-Call: fetchQuestions(...)).
     * - Player-Objekte werden aus playerData.json geladen.
     * - Ein Multiplayer-Spiel (game) wird erstellt.
     * - UI wird aktualisiert (Scores, Playername) und der Switch-Screen gezeigt.
     *
     * @param category   Kategorie-Name (z. B. "Geography")
     * @param difficulty Difficulty-Name (z. B. "easy")
     * @param profile1   Name von Player 1
     * @param profile2   Name von Player 2
     */
    public void setApiParameters(String category, String difficulty, String profile1, String profile2) {
        try {
            // 1) API-Objekt erzeugen -> holt Session Token im Konstruktor
            TriviaAPIService triviaAPIService = new TriviaAPIService();
            // 2) Kategorie in ID umwandeln
            String categoryId = triviaAPIService.getFixedCategoryId(category);

            // 3) Liste aus 10 Fragen holen
            List<Question> questions = triviaAPIService.fetchQuestions(10, categoryId, difficulty);

            // 4) Player-Daten aus JSON holen oder erstellen
            PlayerDataManager dataManager = new PlayerDataManager("src/main/resources/data/playerData.json");
            Player player1 = dataManager.getPlayers().stream()
                    .filter(player -> player.getName().equals(profile1))
                    .findFirst()
                    .orElse(new Player(profile1, 0));
            Player player2 = dataManager.getPlayers().stream()
                    .filter(player -> player.getName().equals(profile2))
                    .findFirst()
                    .orElse(new Player(profile2, 0));

            // 5) Multiplayer-Objekt erstellen
            game = new Multiplayer(questions, List.of(player1, player2));

            // 6) UI aktualisieren und ersten SwitchScreen anzeigen
            updatePlayerDisplay();
            showPlayerSwitchScreen(); // Spiel startet (Spielerwechsel anzeigen)

        } catch (Exception e) {
            e.printStackTrace();
            questionLabel.setText("Could not fetch questions. Please try again.");
        }
    }

    /**
     * Lädt die aktuelle Frage aus dem Multiplayer-Game und zeigt sie im UI.
     * Wenn keine weitere Frage mehr existiert (Null), rufen wir showResults() auf.
     */
    private void loadQuestion() {
        Question currentQuestion = game.getCurrentQuestion();

        // 1) Wenn es noch eine Frage gibt
        if (currentQuestion != null) {
            resetButtonStyles(); // Buttons vollständig zurücksetzen (entfernt rot/grüne Markierungen)
            isAnswerSelected = false; // Spielzustand resetten
            setAnswerButtonsDisabled(false); // Spieler kann wieder klicken

            // b) Frage setzen
            questionLabel.setText(currentQuestion.getQuestionText());
            adjustFontSize(questionLabel);

            // c) Antworten in die Buttons setzen
            List<String> options = currentQuestion.getOptions();
            for (int i = 0; i < options.size(); i++) {
                Button button = answerButtons.get(i);
                button.setText(options.get(i));
                resetButtonStyles();
                adjustFontSize(button);
            }

            // d) Timer neu starten
            startTimer();
        } else {
            // 2) Keine Fragen mehr -> zeigt Ergebnis
            showResults();
        }
    }

    /**
     * Wird aufgerufen, wenn ein Spieler eine Antwort anklickt.
     * @param selectedOptionIndex - 0-3
     */
    private void handleAnswerMulti(int selectedOptionIndex) {
        // 1) Prüfen, ob schon geklickt wurde oder gar keine Frage existiert
        if (isAnswerSelected || game.getCurrentQuestion() == null) {
            return;
        }

        // 2) Flag, dass geklickt wurde (verhindert Mehrfachklicks)
        isAnswerSelected = true;
        // 3) Wer ist gerade dran?
        int currentPlayerIndex = game.getCurrentPlayer();

        // 4) Antwort auswerten und ggf. Score erhöhen
        int remainingTimeForBonus = questionTimer.getRemainingTime();
        game.checkAnswer(selectedOptionIndex, currentPlayerIndex, remainingTimeForBonus);

        // 5) Wenn das der zweite Spieler war, wird die Runde beendet.
        if (currentPlayerIndex == game.getPlayers().size() - 1) {
            processEndOfRound();
        } else {
            // 6) Ansonsten wird zum nächsten Spieler gewechselt (Zwischenscreen anzeigen)
            game.nextPlayer();
            showPlayerSwitchScreen();
        }
    }

    /**
     * Wird aufgerufen, sobald beide Spieler geantwortet haben.
     * - Markiere die korrekte Antwort
     * - Warte 2 Sekunden
     * - Zeige den Punktestand
     * - Gehe zur nächsten Frage oder showResults()
     */
    private void processEndOfRound() {
        // 1) Aktuelle Frage holen (falls null -> weiter zur nächsten Frage)
        Question currentQuestion = game.getCurrentQuestion();

        if (currentQuestion == null) {
            moveToNextQuestion(); // nur zur Sicherheit, beendet das Spiel in diesem Fall
            showResults();
            return;
        }

        // 2) Richtige/falsche Antworten markieren
        markAnswers(currentQuestion.getCorrectOptionIndex());

        // 3) 2 Sekunden Pause, dann Scores updaten und nächste Frage
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> {
                    updatePlayerDisplay();
                    saveScores();
                    if (game.moveToNextQuestion()) {
                        ((Multiplayer) game).resetToFirstPlayer();
                        showPlayerSwitchScreen();
                    } else {
                        showResults();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Thread interrupted.");
            }
        }).start();
    }

    /**
     * moveToNextQuestion() kann an manchen Stellen aufgerufen werden,
     * falls wir sofort zur nächsten Frage wollen (z. B. wenn currentQuestion=null).
     */
    private void moveToNextQuestion() {

        if (game.moveToNextQuestion()) {
            showPlayerSwitchScreen(); // Vor der nächsten Frage Zwischenscreen anzeigen
        } else {
            showResults();
        }
    }

    /**
     * Wird aufgerufen, wenn man im Switch-Screen auf "Ready" klickt.
     * - Overlay verschwindet
     * - Timer wird zurückgesetzt
     * - loadQuestion() für den aktuellen Player
     */
    @FXML
    public void onReadyButtonClicked() {
        playerSwitchOverlay.setVisible(false); // Overlay ausblenden
        setAnswerButtonsDisabled(false); // Buttons aktivieren
        resetTimer(); // Timer resetten
        isAnswerSelected = false; // Ermöglicht eine neue Auswahl

        loadQuestion();
    }

    /**
     * Zeigt den Screen an, der sagt "[Player X]'s turn".
     */
    private void showPlayerSwitchScreen() {
        int currentPlayerIndex = game.getCurrentPlayer();
        Player nextPlayer = game.getPlayers().get(currentPlayerIndex);

        switchLabel.setText(nextPlayer.getName() + "'s turn!");

        playerSwitchOverlay.setVisible(true);
        playerSwitchOverlay.toFront();
        setAnswerButtonsDisabled(true);

//        updatePlayerDisplay();
        updateCurrentPlayerLabel();

        isAnswerSelected = false; // Zurücksetzen, um die neue Eingabe zuzulassen
    }

    /**
     * Aktualisiert die Labels der Spieleranzeige (aktueller Spieler und Scores).
     */
    private void updatePlayerDisplay() {
//        Player currentPlayer = game.getPlayers().get(game.getCurrentPlayer());
//
//        currentPlayerLabel.setText("Player: " + currentPlayer.getName());

        List<Player> players = game.getPlayers();
        player1ScoreLabel.setText(players.get(0).getName() + ": " + game.getScoreCurrentPlayer(0) + " Points");
        player2ScoreLabel.setText(players.get(1).getName() + ": " + game.getScoreCurrentPlayer(1) + " Points");
    }

    private void updateCurrentPlayerLabel() {
        Player currentPlayer = game.getPlayers().get(game.getCurrentPlayer());

        currentPlayerLabel.setText("Player: " + currentPlayer.getName());
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

    /**
     * Entfernt alle roten/grünen Styles der Antwortbuttons.
     */
    private void resetButtonStyles() {
        answerButtons.forEach(button -> button.setStyle(null));
    }

    /**
     * Speichert Punktestände in JSON (PlayerDataManager).
     * Aufgerufen nach jeder Runde in processEndOfRound()
     */
    private void saveScores() {
        PlayerDataManager dataManager = new PlayerDataManager("src/main/resources/data/playerData.json");
        List<Player> players = game.getPlayers();

        dataManager.updatePlayerInformation(players.get(0).getName(), game.getScoreCurrentPlayer(0));
        dataManager.updatePlayerInformation(players.get(1).getName(), game.getScoreCurrentPlayer(1));

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
                    System.out.println("Thread interrupted.");
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
     * Gilt sowohl für Label als auch Button, da beide von Labeled erben.
     */
    private void adjustFontSize(Labeled labeled) {
        String txt = labeled.getText();
        int length = (txt != null) ? txt.length() : 0;

        // Beispiel-Schwellwerte
        if (length > 300) {
            labeled.setStyle("-fx-font-size: 10px;");
        } else if (length > 200) {
            labeled.setStyle("-fx-font-size: 12px;");
        } else if (length > 100) {
            labeled.setStyle("-fx-font-size: 16px;");
        } else if (length > 50) {
            labeled.setStyle("-fx-font-size: 20px;");
        } else {
            labeled.setStyle("-fx-font-size: 24px;");
        }

        labeled.setWrapText(true);
    }

    /**
     * ESC -> Pause-Popup
     * Timer wird gestoppt und nach Schließen fortgesetzt
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
            System.out.println("Could not show pause popup.");
        }
    }
}
