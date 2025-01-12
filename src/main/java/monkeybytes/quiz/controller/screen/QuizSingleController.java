package monkeybytes.quiz.controller.screen;

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
import monkeybytes.quiz.service.TriviaAPIService;

import java.util.List;

// quiz-single-screen.fxml
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

    private TriviaAPIService triviaAPIService = new TriviaAPIService();

    @FXML
    public void initialize() {
        // Event-Handler für Antwort-Buttons
        optionAButton.setOnAction(event -> handleAnswer(0));
        optionBButton.setOnAction(event -> handleAnswer(1));
        optionCButton.setOnAction(event -> handleAnswer(2));
        optionDButton.setOnAction(event -> handleAnswer(3));
    }

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

            // Timer starten
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
        System.out.println("Spiel beendet! Punktestand: " + game.getScore());
        questionLabel.setText("Quiz beendet! Dein Punktestand: " + game.getScore());
    }

    /**
     * Handhabt die Auswahl einer Antwort.
     * @param answerIndex Index der ausgewählten Antwort.
     */
    private void handleAnswer(int answerIndex) {
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
        questionTimer = new QuestionTimer(30);
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
                Platform.runLater(() -> handleAnswer(-1));
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
}





//package monkeybytes.quiz.controller;
//
//import javafx.animation.PauseTransition;
//import javafx.application.Platform;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.VBox;
//import javafx.util.Duration;
//import monkeybytes.quiz.game.Question;
//import monkeybytes.quiz.game.QuestionTimer;
//import monkeybytes.quiz.game.Singleplayer;
//import monkeybytes.quiz.service.TriviaAPIService;
//
//import java.util.List;
//
//public class QuizSingleController {
//    @FXML
//    private AnchorPane rootPane;
//
//    @FXML
//    private Pane headerPane;
//
//    @FXML
//    private Label questionCounterLabel;
//
//    @FXML
//    private Label timerLabel;
//
//    @FXML
//    private Label currentPlayerLabel;
//
//    @FXML
//    private Pane questionPane;
//
//    @FXML
//    private Label questionLabel;
//
//    @FXML
//    private VBox optionsVBox;
//
//    @FXML
//    private VBox answersVBox;
//
//    @FXML
//    private Button optionAButton;
//
//    @FXML
//    private Button optionBButton;
//
//    @FXML
//    private Button optionCButton;
//
//    @FXML
//    private Button optionDButton;
//
//    private Singleplayer game;
//    private QuestionTimer questionTimer;
//    private volatile boolean stopTimerThread = false;
//
//    private TriviaAPIService triviaAPIService = new TriviaAPIService();
//
//    @FXML
//    public void initialize() {
//        // Test-Daten da noch keine API
//        List<Question> questions = List.of(
//                new Question("What is the periodic symbol for Iron?", List.of("Fe", "Ir", "In", "Io"), 0),
//                new Question("What is 2 + 2?", List.of("3", "4", "5", "6"), 1),
//                new Question("What is the capital of Slovakia?", List.of("Vienna", "London", "Bratislava", "Budapest"), 2),
//                new Question("Never gonna give you up ... ?", List.of("Never gonna hunt you down.", "Never gonna throw you up.", "Never gonna let you down.", "Never gonna put you down."), 2)
//        );
//        game = new Singleplayer(questions);
//
//        loadQuestion();
//
//        // Event-Handler für Buttons setzen
//        optionAButton.setOnAction(event -> handleAnswer(0));
//        optionBButton.setOnAction(event -> handleAnswer(1));
//        optionCButton.setOnAction(event -> handleAnswer(2));
//        optionDButton.setOnAction(event -> handleAnswer(3));
//    }
//
//    // Methode zum Laden der aktuellen Frage und Antworten
//    private void loadQuestion() {
//        Question currentQuestion = game.getCurrentQuestion();
//        if (currentQuestion != null) {
//
//            // Zurücksetzen der Stile aller Buttons
//            resetButtonStyles();
//
//            questionLabel.setText(currentQuestion.getQuestionText());
//            optionAButton.setText(currentQuestion.getOptions().get(0));
//            optionBButton.setText(currentQuestion.getOptions().get(1));
//            optionCButton.setText(currentQuestion.getOptions().get(2));
//            optionDButton.setText(currentQuestion.getOptions().get(3));
//
//            // Bei update question counter fehlt noch eine variable für die Gesamtanzahl an Fragen statt "10 Questions"
//            questionCounterLabel.setText((game.getCurrentQuestionIndex() + 1) + " of " + "10 Questions");
//
//            // Update Player Info später implementieren
//
//            // Temporär: Test
//            currentPlayerLabel.setText("Player: Testplayer");
//
//            // Start Timer testing
//            startTimer();
//
//            // fehlt noch ein else mit Spiel beenden bzw. Ergebnisse anzeigen
//        }
//    }
//
//    public void setApiParameters(String category, String difficulty, String profileName) {
//        System.out.println("Kategorie: " + category);
//        System.out.println("Schwierigkeit: " + difficulty);
//        System.out.println("Profil: " + profileName);
//
//        // API-Logik, um Fragen basierend auf Kategorie und Schwierigkeitsgrad zu laden
//        // Beispiel:
//        // List<Question> questions = triviaAPIService.fetchQuestions(category, difficulty);
//        // game = new Singleplayer(questions);
//    }
//
//
//    // Event-Handler für die Antwortauswahl
//    private void handleAnswer(int answerIndex) {
//        Question currentQuestion = game.getCurrentQuestion();
//        if (currentQuestion != null) {
//
//            // Es wird der Index der richtigen Antwort abgerufen
//            int correctIndex = currentQuestion.getCorrectOptionIndex();
//
//            // Antworten werden markiert
//            markAnswers(correctIndex, answerIndex);
//            stopTimer();
//
//            // Antwort wird überprüft
//            game.checkAnswer(answerIndex);
//
//            // Kurze Verzögerung, erst dann wird die nächste geladen
//            PauseTransition pause = new PauseTransition(Duration.seconds(2));
//            pause.setOnFinished(event -> loadQuestion());
//            pause.play();
//        }
//    }
//
//    private void markAnswers(int correctIndex, int selectedIndex) {
//        List<Button> buttons = List.of(optionAButton, optionBButton, optionCButton, optionDButton);
//
//        for (int i = 0; i < buttons.size(); i++) {
//            Button button = buttons.get(i);
//            if (i == correctIndex) {
//                button.setStyle("-fx-background-color: #16ad09; -fx-text-fill: white; -fx-font-weight: bold");
//            } else if (i == selectedIndex && i != correctIndex) {
//                button.setStyle("-fx-background-color: red; -fx-text-fill: white;");
//            } else {
//                button.setStyle("-fx-background-color: #ffa000; -fx-text-fill: white;");
//            }
//        }
//    }
//
//    private void startTimer() {
//        questionTimer = new QuestionTimer(30);
//        questionTimer.startTimer();
//        stopTimerThread = false; // Thread-Stop-Flag
//
//        new Thread(() -> {
//            while (!stopTimerThread && !questionTimer.getTimerUp()) {
//                try {
//                    Thread.sleep(500);
//                    Platform.runLater(this::updateTimerLabel);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if (!stopTimerThread && questionTimer.getTimerUp()) {
//                Platform.runLater(() -> handleAnswer(-1)); //automatische Antwort wenn Zeit ausläfut
//            }
//        }).start();
//    }
//
//    private void stopTimer() {
//        if (questionTimer != null) {
//            questionTimer.stopTimer();
//        }
//        stopTimerThread = true;
//    }
//
//    private void updateTimerLabel() {
//        int remainingTime = questionTimer.getRemainingTime();
//        timerLabel.setText("⏳" + questionTimer.getRemainingTime());
//
//        if (remainingTime == 0) {
//            timerLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // highlight wenn Timer = 0
//        } else {
//            timerLabel.setStyle(null);
//        }
//    }
//
//    private void resetButtonStyles() {
//        List<Button> buttons = List.of(optionAButton, optionBButton, optionCButton, optionDButton);
//
//        for (Button button : buttons) {
//            button.setStyle(null); // reset styles damit die screen-styles.css wieder gelten
//        }
//    }
//}

