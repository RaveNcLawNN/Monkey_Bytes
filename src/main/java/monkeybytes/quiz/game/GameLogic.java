package monkeybytes.quiz.game;

import java.util.List;

/**
 * Abstrakte Basisklasse für die Spiellogik.
 * - Verwaltet die Liste der Fragen und den Index der aktuellen Frage.
 * - Speichert die Punktestände der Spieler in einem Array.
 * - Enthält eine Instanz des QuestionTimer zur Zeitsteuerung.
 */
public abstract class GameLogic {
    protected List<Question> questions;
    protected int currentQuestionIndex = 0;
    protected int[] playerScores;
    public QuestionTimer questionTimer;

    /**
     * Konstruktor für GameLogic.
     * - Initialisiert die Fragenliste und das Punktestand-Array basierend auf der Anzahl der Spieler.
     * - Erstellt einen QuestionTimer mit der angegebenen Zeitbegrenzung in Sekunden.
     */
    public GameLogic(List<Question> questions, int numberOfPlayers, int timeLimitSeconds) {
        this.questions = questions;
        this.playerScores = new int[numberOfPlayers];
        this.questionTimer = new QuestionTimer(timeLimitSeconds);
    }

    /**
     * Gibt die aktuelle Frage zurück.
     * - Überprüft, ob der aktuelle Fragenindex innerhalb der Fragenliste liegt.
     * - Gibt die aktuelle Frage zurück oder null, wenn keine weiteren Fragen vorhanden sind.
     */
    public Question getCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }

    /**
     * Überprüft die Antwort eines Spielers und aktualisiert den Punktestand.
     * - Holt die aktuelle Frage und vergleicht die ausgewählte Antwort mit der richtigen Option.
     * - Berechnet den Punktestand basierend auf den verbleibenden Sekunden und fügt ihn dem Spieler hinzu, wenn die Antwort korrekt ist.
     * - Gibt den aktuellen Punktestand des Spielers aus.
     * - Erhöht den Fragenindex, um zur nächsten Frage zu wechseln.
     */
    public void checkAnswer(int selectedOptionIndex, int playerIndex, int remainingTime) {
        Question currentQuestion = getCurrentQuestion();
        if (currentQuestion != null && currentQuestion.getCorrectOptionIndex() == selectedOptionIndex) {
            int scoreToAdd = questionTimer.calculateScore(100,remainingTime);
            playerScores[playerIndex] += scoreToAdd;
        }
        System.out.println(playerScores[playerIndex]);
        currentQuestionIndex++;
    }

    /**
     * Wechselt zur nächsten Frage, falls verfügbar (nur Multiplayer).
     * - Überprüft, ob der aktuelle Fragenindex kleiner als die Anzahl der Fragen minus eins ist.
     * - Erhöht den Index für die nächste Frage und gibt true zurück, wenn Fragen übrig sind.
     * - Gibt false zurück, wenn keine weiteren Fragen verfügbar sind.
     */
    public boolean moveToNextQuestion() {

        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            return true;
        }
        return false;
    }


    public int getCurrentPlayer() {
        return currentQuestionIndex % playerScores.length;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
