package monkeybytes.quiz.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import monkeybytes.quiz.game.Question;
import monkeybytes.quiz.game.QuestionTimer;
import monkeybytes.quiz.game.Singleplayer;

import java.util.List;

public class QuizSingleController {
    @FXML
    private AnchorPane rootPane;

    @FXML
    private Pane headerPane;

    @FXML
    private Label questionCounterLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Label currentPlayerLabel;

    @FXML
    private Pane questionPane;

    @FXML
    private Label questionLabel;

    @FXML
    private VBox optionsVBox;

    @FXML
    private VBox answersVBox;

    @FXML
    private Button optionAButton;

    @FXML
    private Button optionBButton;

    @FXML
    private Button optionCButton;

    @FXML
    private Button optionDButton;

    private Singleplayer game;
    private QuestionTimer questionTimer;
    private volatile boolean stopTimerThread = false;

    @FXML
    public void initialize() {
        // Test-Daten da noch keine API
        List<Question> questions = List.of(
                new Question("What is the periodic symbol for Iron?", List.of("Fe", "Ir", "In", "Io"), 0),
                new Question("What is 2 + 2?", List.of("3", "4", "5", "6"), 1)
        );
        game = new Singleplayer(questions);

        loadQuestion();

        // Event-Handler für Buttons setzen
        optionAButton.setOnAction(event -> handleAnswer(0));
        optionBButton.setOnAction(event -> handleAnswer(1));
        optionCButton.setOnAction(event -> handleAnswer(2));
        optionDButton.setOnAction(event -> handleAnswer(3));
    }

    // Methode zum Laden der aktuellen Frage und Antworten
    private void loadQuestion() {
        Question currentQuestion = game.getCurrentQuestion();
        if (currentQuestion != null) {

            // Zurücksetzen der Stile aller Buttons
            resetButtonStyles();

            questionLabel.setText(currentQuestion.getQuestionText());
            optionAButton.setText(currentQuestion.getOptions().get(0));
            optionBButton.setText(currentQuestion.getOptions().get(1));
            optionCButton.setText(currentQuestion.getOptions().get(2));
            optionDButton.setText(currentQuestion.getOptions().get(3));

            // Bei update question counter fehlt noch eine variable für die Gesamtanzahl an Fragen statt "10 Questions"
            questionCounterLabel.setText((game.getCurrentQuestionIndex() + 1) + " of " + "10 Questions");

            // Update Player Info später implementieren

            // Temporär: Test
            currentPlayerLabel.setText("Player: Testplayer");

            // Start Timer testing
            startTimer();

            // fehlt noch ein else mit Spiel beenden bzw. Ergebnisse anzeigen
        }
    }

    // Event-Handler für die Antwortauswahl
    private void handleAnswer(int answerIndex) {
        Question currentQuestion = game.getCurrentQuestion();
        if (currentQuestion != null) {

            // Es wird der Index der richtigen Antwort abgerufen
            int correctIndex = currentQuestion.getCorrectOptionIndex();

            // Antworten werden markiert
            markAnswers(correctIndex, answerIndex);
            stopTimer();

            // Antwort wird überprüft
            game.checkAnswer(answerIndex);

            // Kurze Verzögerung, erst dann wird die nächste geladen
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> loadQuestion());
            pause.play();
        }
    }

    private void markAnswers(int correctIndex, int selectedIndex) {
        List<Button> buttons = List.of(optionAButton, optionBButton, optionCButton, optionDButton);

        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            if (i == correctIndex) {
                button.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold");
            } else if (i == selectedIndex && i != correctIndex) {
                button.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
            } else {
                button.setStyle("-fx-background-color: #f34626; -fx-text-fill: white;");
            }
        }
    }

    private void startTimer() {
        questionTimer = new QuestionTimer(30);
        questionTimer.startTimer();
        stopTimerThread = false; // Thread-Stop-Flag

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
                Platform.runLater(() -> handleAnswer(-1)); //automatische Antwort wenn Zeit ausläfut
            }
        }).start();
    }

    private void stopTimer() {
        if (questionTimer != null) {
            questionTimer.stopTimer();
        }
        stopTimerThread = true;
    }

    private void updateTimerLabel() {
        int remainingTime = questionTimer.getRemainingTime();
        timerLabel.setText("⏳" + questionTimer.getRemainingTime());

        if (remainingTime == 0) {
            timerLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // highlight wenn Timer = 0
        } else {
            timerLabel.setStyle(null);
        }
    }

    private void resetButtonStyles() {
        List<Button> buttons = List.of(optionAButton, optionBButton, optionCButton);

        for (Button button : buttons) {
            button.setStyle(null); // reset styles damit die styles.css wieder gelten
        }
    }
}

