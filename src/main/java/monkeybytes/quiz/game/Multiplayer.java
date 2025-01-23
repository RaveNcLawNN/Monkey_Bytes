package monkeybytes.quiz.game;

import java.util.List;

/**
 * Multiplayer erweitert GameLogic und passt sie für ein 2-Spieler-Quiz an.
 * - Wir haben hier ein "players"-List-Feld, damit wir die Player-Objekte
 *   (Namen, Scores) verwalten können, zusätzlich zum playerScores[] aus der
 *   Superklasse.
 * - Wir überschreiben "getCurrentPlayer()", weil wir einen eigenen Index haben,
 *   damit wir pro Frage zwei Antworten sammeln können.
 */
public class Multiplayer extends GameLogic {
    private List<Player> players;
    private int currentPlayerIndex = 0;

    /**
     * Konstruktor:
     * Ruft den Konstruktor aus GameLogic auf und passt ihn für 2 Spieler an
     */
    public Multiplayer (List<Question> questions, List<Player> players) {
        super (questions, 2, 30);
        this.players = players;
    }

    /**
     * Überprüft die Antwort und aktualisiert die Punkte
     * überschreibt checkAnswer von GameLogic, um 2 Fragen
     * pro Antwort sammeln zu können. KEIN currentQuestionIndex++ hier, weil wir erst warten
     * müssen bis beide Spieler geantwortet haben. Indexerhöhung wird hier im MP in handleAnswerMulti() bzw.
     * processEndOfRound() gemacht.
     */
    @Override
    public void checkAnswer(int selectedOptionIndex, int playerIndex, int remainingTime) {
        Question currentQuestion = getCurrentQuestion();
        if (currentQuestion != null && currentQuestion.getCorrectOptionIndex() == selectedOptionIndex) {
            int scoreToAdd = questionTimer.calculateScore(100, remainingTime);
            playerScores[playerIndex] += scoreToAdd;
        }
    }

    /**
     * Überschreiben von getCurrentPlayer(), damit wir NICHT
     * "currentQuestionIndex % playerScores.length" aus der Superklasse nutzen,
     * sondern stattdessen unser "currentPlayerIndex" zurückgeben.
     */
    @Override
    public int getCurrentPlayer() {
        return currentPlayerIndex;
    }

    /**
     * Wird im Controller aufgerufen, wenn wir nach einer Runde
     * wieder auf Player 1 (Index 0) zurücksetzen wollen.
     */
    public void resetToFirstPlayer() {
        currentPlayerIndex = 0;
    }

    /**
     * Wechselt von Player 0 auf Player 1 oder umgekehrt:
     *  currentPlayerIndex = (currentPlayerIndex + 1) % 2
     */
    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playerScores.length;
    }

    /**
     * getWinner() vergleicht die finalen Scores
     * (playerScores[0], playerScores[1]) und gibt ein String-Resultat zurück.
     */
    public String getWinner () {
        Player player1 = getPlayers().get(0);
        Player player2 = getPlayers().get(1);

        if (getScoreCurrentPlayer(0) > getScoreCurrentPlayer(1)) {
            return player1.getName() + " wins with " + getScoreCurrentPlayer(0) + " points!";
        } else if (getScoreCurrentPlayer(1) > getScoreCurrentPlayer(0)) {
            return player2.getName() + " wins with " + getScoreCurrentPlayer(1) + " points!";
        } else {
            return "It's a draw! Both players scored " + getScoreCurrentPlayer(0) + " points!";
        }
    }

    /**
     * Gibt die Liste der Player-Objekte (mit Name, Score).
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * getScoreCurrentPlayer(index) = playerScores[index].
     * Oft genutzt, um den Score von Player 1 oder Player 2 abzufragen.
     */
    public int getScoreCurrentPlayer(int currentPlayerIndex) {
        return playerScores[currentPlayerIndex];
    }
}
