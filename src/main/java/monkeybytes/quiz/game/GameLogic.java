package monkeybytes.quiz.game;

import java.util.List;

/*
Die GameLogic-Klasse enthält die Kernlogik des Spiels. Sie wird von den Singleplayer- und Multiplayer-Klassen erweitert.
 */

public abstract class GameLogic { // "abstract" wird benutzt, da diese Klasse nicht eigenständig verwendet wird, also nirgends selbst instanziiert wird.
    // die folgenden Attribute sind protected (nicht private), damit die Unterklassen (Singleplayer & Multiplayer) auch darauf Zugriff haben.
    protected List<Question> questions; // Liste der Fragen, die später dem Konstruktor von der API übergeben wird.
    protected int currentQuestionIndex = 0;
    protected int[] playerScores; // speichert die Punktestände/den Punktestand des/der Spieler/s
    public QuestionTimer questionTimer;

    public GameLogic(List<Question> questions, int numberOfPlayers, int timeLimitSeconds) {
        this.questions = questions;
        this.playerScores = new int[numberOfPlayers]; // initialisiert das playerScores-Array mit der Länge numberOfPlayers.
        this.questionTimer = new QuestionTimer(timeLimitSeconds);
    }

    // gibt die aktuelle Frage zurück, wenn der aktuelle Index innerhalb der questions-Liste liegt. Wenn es keine Fragen mehr gibt, wird null zurückgegeben.
    public Question getCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }

    //Berechnet den Score pro Frage und addiert ihn zum totalScore hinzu. Coolere Berechnung wär vielleicht cool.
    public int calculateScore(int basePoints) {
        int bonus = questionTimer.getRemainingTime() * 2;
        return basePoints + bonus;
    }

    // überprüft, ob die Antwort eines Spielers korrekt ist und aktualisiert den Punktestand.
    public void checkAnswer(int selectedOptionIndex, int playerIndex) {
        Question currentQuestion = getCurrentQuestion();
        if (currentQuestion != null && currentQuestion.getCorrectOptionIndex() == selectedOptionIndex) { // falls der Index der korrekten Antwort gleich dem Index der ausgewählten Antwort ist, gibt es Punkte.
            playerScores[playerIndex] += calculateScore(100);
        }
        currentQuestionIndex++;
    }

    // Geht zur nächsten Frage, falls vorhanden (für Multiplayer)
    public boolean moveToNextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            return true;
        }
        return false; // Keine weiteren Fragen verfügbar
    }

    // wenn der Index der aktuellen Frage größer/gleich der Anzahl der Fragen ist, gibt es keine weiteren Fragen und isGameOver() gibt true zurück.
    public boolean isGameOver() {
        return currentQuestionIndex >= questions.size();
    }

    // getter-Methode für den Punktestand. Wird von der GUI aufgerufen, um die Ergebnisse anzuzeigen.
    public int[] getPlayerScores() {
        return playerScores;
    }

    //getter-Methode, die den aktuellen Spieler zurückgibt.
    public int getCurrentPlayer() {
        return currentQuestionIndex % playerScores.length; // durch Modulo wird sichergestellt, dass sich die Spieler im Multiplayer immer abwechseln.
    }

    //getter-Methode, die den aktuellen Question Index zurückgibt.
    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int index) {
        currentQuestionIndex = index;
    }

    //gibt die Gesamtanzahl der Fragen zurück.
    public int getTotalQuestions() {
        return questions.size();
    }

    //gibt die gesamte Fragenliste zurück.
    public List<Question> getQuestions() {
        return questions;
    }
}
