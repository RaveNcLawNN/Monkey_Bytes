package monkeybytes.quiz.controller.screen;

import javafx.animation.PauseTransition;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import monkeybytes.quiz.controller.popup.PauseController;
import monkeybytes.quiz.game.PlayerDataManager;
import monkeybytes.quiz.game.Question;
import monkeybytes.quiz.game.QuestionTimer;
import monkeybytes.quiz.game.Singleplayer;
import monkeybytes.quiz.service.TriviaAPIService;

import java.util.List;

public class QuizSingleController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Pane headerPane, questionPane;
    @FXML
    private Label questionCounterLabel, timerLabel, currentPlayerLabel, questionLabel, scoreDisplay;
    @FXML
    private VBox optionsVBox;
    @FXML
    private Button optionAButton, optionBButton, optionCButton, optionDButton;

    private Singleplayer game;
    private QuestionTimer questionTimer;
    private volatile boolean stopTimerThread = false;
    private int timeLimitSeconds = 30;
    private boolean isAnswerSelected = false;
    private TriviaAPIService triviaAPIService = new TriviaAPIService();

    /**
     * Initialisiert den Quiz-Bildschirm mit event handlers für Benutzerinteraktionen.
     * - Verknüpft die Antwort-Buttons (A-D) mit der Methode handleAnswerSingle.
     * - Erkennt das Drücken der ESC-Taste, um ein Pause-Popup anzuzeigen.
     * - Entfernt die ESC-Tastensteuerung, wenn das Fenster der Szene geschlossen wird.
     */
    @FXML
    public void initialize() {
        optionAButton.setOnAction(event -> handleAnswerSingle(0));
        optionBButton.setOnAction(event -> handleAnswerSingle(1));
        optionCButton.setOnAction(event -> handleAnswerSingle(2));
        optionDButton.setOnAction(event -> handleAnswerSingle(3));

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

    /**
     * Event handler for detecting the ESC key press.
     * Triggers the display of the pause popup when ESC is pressed.
     */
    private final EventHandler<KeyEvent> escHandler = event -> {
        if (event.getCode() == KeyCode.ESCAPE) {
            showPausePopup();
        }
    };

    /**
     * Setzt die API-Parameter für die aktuelle Spielsitzung.
     * @param category Die ausgewählte Kategorie.
     * @param difficulty Der Schwierigkeitsgrad.
     * @param profileName Der Name des aktuellen Spielers.
     */
    public void setApiParameters(String category, String difficulty, String profileName) {
        try {
            // Spielername ins Label setzen
            currentPlayerLabel.setText("Player: " + profileName);

            // Kategorie-ID abrufen
            String categoryId = triviaAPIService.getFixedCategoryId(category);

            // 10 Fragen abfragen
            List<Question> questions = triviaAPIService.fetchQuestions(10, categoryId, difficulty);

            // Spielinitialisierung mit geladenen Fragen
            game = new Singleplayer(questions);

            // Erste Frage laden
            loadQuestion();

        } catch (Exception e) {
            e.printStackTrace();
            questionLabel.setText("Could not load questions. Please try again.");
        }
    }

    /**
     * Lädt die aktuelle Frage und aktualisiert die Anzeige.
     * - Setzt Texte und Stile für Frage und Antwortmöglichkeiten.
     * - Aktualisiert den Fortschritt und startet den Timer.
     * - Zeigt Ergebnisse an, wenn keine weiteren Fragen vorhanden sind.
     */
    private void loadQuestion() {
        Question currentQuestion = game.getCurrentQuestion();

        if (currentQuestion != null) {
            resetButtonStyles();
            isAnswerSelected = false;

            questionLabel.setText(currentQuestion.getQuestionText());
            adjustFontSize(questionLabel);

            optionAButton.setText(currentQuestion.getOptions().get(0));
            adjustFontSize(optionAButton);
            optionBButton.setText(currentQuestion.getOptions().get(1));
            adjustFontSize(optionAButton);
            optionCButton.setText(currentQuestion.getOptions().get(2));
            adjustFontSize(optionAButton);
            optionDButton.setText(currentQuestion.getOptions().get(3));
            adjustFontSize(optionAButton);

            questionCounterLabel.setText((game.getCurrentQuestionIndex() + 1) + " of " + game.getQuestions().size() + " Questions");

            timeLimitSeconds = 30;
            stopTimer();
            startTimer();
        } else {
            showResults();
        }
    }

    /**
     * Verarbeitet die Antwortauswahl eines Spielers.
     * - Verhindert Mehrfachauswahlen durch Überprüfung, ob bereits eine Antwort ausgewählt wurde.
     * - Überprüft die aktuelle Frage und markiert die Antworten (korrekt und ausgewählt).
     * - Validiert die Antwort, berücksichtigt die verbleibende Zeit und aktualisiert den Punktestand.
     * - Stoppt den Timer und lädt nach einer kurzen Pause die nächste Frage.
     */
    private void handleAnswerSingle(int answerIndex) {
        if (isAnswerSelected) return;
        isAnswerSelected = true;

        Question currentQuestion = game.getCurrentQuestion();

        if (currentQuestion != null) {
            int correctIndex = currentQuestion.getCorrectOptionIndex();

            markAnswers(correctIndex, answerIndex);

            int remainingTime = questionTimer.getRemainingTime();
            game.checkAnswer(answerIndex, remainingTime);

            stopTimer();

            scoreDisplay.setText("Score: " + game.getScore());

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> loadQuestion());
            pause.play();
        }
    }

    /**
     * Zeigt die Ergebnisse des Quiz an und speichert den Punktestand.
     * - Speichert den aktuellen Punktestand des Spielers in playerData.json.
     * - Aktualisiert das Fragefeld, um das Ende des Quiz und den Punktestand anzuzeigen.
     */
    private void showResults() {

        PlayerDataManager dataManager = new PlayerDataManager("src/main/resources/data/playerData.json");
        dataManager.updatePlayerInformation(currentPlayerLabel.getText().replace("Player: ", ""), game.getScore());

        questionLabel.setText("Quiz finished! Your score: " + game.getScore());
    }

    /**
     * Markiert die Antwortoptionen basierend auf der Spielerwahl.
     * - Hebt die richtige Antwort grün hervor.
     * - Markiert eine falsche Auswahl des Spielers rot.
     */
    private void markAnswers(int correctIndex, int selectedIndex) {
        List<Button> buttons = List.of(optionAButton, optionBButton, optionCButton, optionDButton);

        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            if (i == correctIndex) {
                button.setStyle("-fx-background-color: #16ad09; -fx-text-fill: white;");
            } else if (i == selectedIndex && i != correctIndex) {
                button.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            }
        }
    }


    /**
     * Startet den Timer für die aktuelle Frage.
     * - Initialisiert und startet einen neuen QuestionTimer mit der festgelegten Zeitbegrenzung.
     * - Aktualisiert das Timer-Label regelmäßig im UI-Thread.
     * - Verarbeitet automatisch eine unbeantwortete Frage, wenn der Timer abläuft.
     */
    private void startTimer() {
        questionTimer = new QuestionTimer(timeLimitSeconds);
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
                Platform.runLater(() -> handleAnswerSingle(-1));
            }
        }).start();
    }

    /**
     * Stoppt den laufenden Timer.
     * - Beendet den QuestionTimer, falls er aktiv ist.
     * - Signalisiert das Stoppen des zugehörigen Timer-Threads.
     */
    private void stopTimer() {
        if (questionTimer != null) {
            questionTimer.stopTimer();
        }
        stopTimerThread = true;
    }

    /**
     * Aktualisiert das Timer-Label mit der verbleibenden Zeit.
     * - Zeigt die verbleibenden Sekunden an und setzt das Standardstil.
     * - Ändert die Anzeige auf rot und fett, wenn die Zeit abgelaufen ist.
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
     * Setzt die Stile der Antwort-Buttons zurück.
     */
    private void resetButtonStyles() {
        List<Button> buttons = List.of(optionAButton, optionBButton, optionCButton, optionDButton);
        for (Button button : buttons) {
            button.setStyle(null);
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
     * Zeigt ein Pause-Popup an.
     * - Lädt die FXML-Datei für das Pause-Popup und konfiguriert das Popup-Fenster.
     * - Stellt das Hauptfenster als Besitzer des Popup-Fensters ein und zeigt das Popup an.
     * - Startet den Timer erneut, wenn das Popup geschlossen wird.
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