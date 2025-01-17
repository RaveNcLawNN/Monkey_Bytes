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

// quiz-single-screen.fxml
public class QuizSingleController {
    @FXML
    private AnchorPane rootPane;

    @FXML
    private Pane headerPane, questionPane;

    @FXML
    private Label questionCounterLabel, timerLabel, currentPlayerLabel, questionLabel;

    @FXML
    private VBox optionsVBox;

    @FXML
    private Button optionAButton, optionBButton, optionCButton, optionDButton;

    private Singleplayer game;
    private QuestionTimer questionTimer;
    private volatile boolean stopTimerThread = false;
    private int remainingTime = 30;

    private TriviaAPIService triviaAPIService = new TriviaAPIService();

    @FXML
    public void initialize() {
        // Event-Handler für Antwort-Buttons
        optionAButton.setOnAction(event -> handleAnswerSingle(0));
        optionBButton.setOnAction(event -> handleAnswerSingle(1));
        optionCButton.setOnAction(event -> handleAnswerSingle(2));
        optionDButton.setOnAction(event -> handleAnswerSingle(3));

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

    /**
     * Setzt die API-Parameter für die aktuelle Spielsitzung.
     * @param category Die ausgewählte Kategorie.
     * @param difficulty Der Schwierigkeitsgrad.
     * @param profileName Der Name des aktuellen Spielers.
     */
    public void setApiParameters(String category, String difficulty, String profileName) {
        try {
            // Spielername setzen
            currentPlayerLabel.setText("Player: " + profileName);

            // Kategorie-ID abrufen und Fragen von der API laden
            String categoryId = triviaAPIService.getFixedCategoryId(category);
            List<Question> questions = triviaAPIService.fetchUniqueQuestions(10, categoryId, difficulty);

            // Spielinitialisierung mit geladenen Fragen
            game = new Singleplayer(questions);
            loadQuestion();

        } catch (Exception e) {
            e.printStackTrace();
            questionLabel.setText("Fehler beim Laden der Fragen. Bitte versuche es erneut.");
        }
    }

    /**
     * Lädt die aktuelle Frage und deren Antwortmöglichkeiten.
     */
    private void loadQuestion() {
        Question currentQuestion = game.getCurrentQuestion();
        if (currentQuestion != null) {
            resetButtonStyles();

            // Frage und Antwortmöglichkeiten setzen
            // adjustable font size, falls die Frage zu lang ist
            questionLabel.setText(currentQuestion.getQuestionText());
            adjustFontSize(questionLabel, 125, 30, 24);

            optionAButton.setText(currentQuestion.getOptions().get(0));
            adjustFontSize(optionAButton, 50, 23, 16);
            optionBButton.setText(currentQuestion.getOptions().get(1));
            adjustFontSize(optionAButton, 50, 23, 16);
            optionCButton.setText(currentQuestion.getOptions().get(2));
            adjustFontSize(optionAButton, 50, 23, 16);
            optionDButton.setText(currentQuestion.getOptions().get(3));
            adjustFontSize(optionAButton, 50, 23, 16);

            // Fragezähler aktualisieren
            questionCounterLabel.setText((game.getCurrentQuestionIndex() + 1) + " of " + game.getQuestions().size() + " Questions");

            remainingTime = 30;
            stopTimer();
            startTimer();
        } else {
            // Wenn keine weiteren Fragen vorhanden sind
            showResults();
        }
    }

    /**
     * Zeigt die Ergebnisse des Spiels an.
     */
    private void showResults() {

            PlayerDataManager dataManager = new PlayerDataManager("src/main/resources/data/playerData.json");
            dataManager.updatePlayerInformation(currentPlayerLabel.getText().replace("Player: ", ""), game.getScore());

            questionLabel.setText("Quiz finished! Your score: " + game.getScore());
        }

    /**
     * Handhabt die Auswahl einer Antwort.
     * @param answerIndex Index der ausgewählten Antwort.
     */
    private void handleAnswerSingle(int answerIndex) {
        Question currentQuestion = game.getCurrentQuestion();
        if (currentQuestion != null) {
            int correctIndex = currentQuestion.getCorrectOptionIndex();

            markAnswers(correctIndex, answerIndex);
            stopTimer();

            game.checkAnswer(answerIndex);

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> loadQuestion());
            pause.play();
        }
    }

    /**
     * Markiert die richtige und falsche Antwort auf dem Screen.
     * @param correctIndex Index der richtigen Antwort.
     * @param selectedIndex Index der vom Spieler ausgewählten Antwort.
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

    private void adjustFontSize(Label label, int maxLength, double defaultSize, double minSize) {
        if (label.getText().length() > maxLength) {
            label.setStyle("-fx-font-size: " + minSize + "px;");
        } else {
            label.setStyle("-fx-font-size: " + defaultSize + "px;");
        }
    }

    private void adjustFontSize(Button button, int maxLength, double defaultSize, double minSize) {
        if (button.getText().length() > maxLength) {
            button.setStyle("-fx-font-size: " + minSize + "px;");
        } else {
            button.setStyle("-fx-font-size: " + defaultSize + "px;");
        }
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
                Platform.runLater(() -> handleAnswerSingle(-1));
            }
        }).start();
    }

    /**
     * Stoppt den Timer.
     */
    private void stopTimer() {
        if (questionTimer != null) {
//            remainingTime = questionTimer.getRemainingTime();
            questionTimer.stopTimer();
        }
        stopTimerThread = true;
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
     * Setzt die Stile der Antwort-Buttons zurück.
     */
    private void resetButtonStyles() {
        List<Button> buttons = List.of(optionAButton, optionBButton, optionCButton, optionDButton);
        for (Button button : buttons) {
            button.setStyle(null);
        }
    }

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

