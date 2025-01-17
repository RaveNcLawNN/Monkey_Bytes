package monkeybytes.quiz.game;

import java.util.List;

/*
Die Multiplayer-Klasse erweitert die GameLogic-Klasse und passt sie für zwei Spieler an.
 */

public class Multiplayer extends GameLogic {
    private List<Player> players;
    private int currentPlayerIndex = 0;

    public Multiplayer (List<Question> questions, List<Player> players) {
        // "super" wird verwendet, um Konstruktoren und Methoden aus der Elternklasse ("Superklasse") aufzurufen.
        // hier wird der GameLogic-Konstruktor aufgerufen, mit numberOfPlayers = 2.
        super (questions, 2, 15);
        this.players = players;
    }

    @Override
    public void checkAnswer(int selectedOptionIndex, int playerIndex) {
        Question currentQuestion = getCurrentQuestion();
        if (currentQuestion != null && currentQuestion.getCorrectOptionIndex() == selectedOptionIndex) {
            int scoreToAdd = calculateScore(100);
            playerScores[playerIndex] += scoreToAdd;
//            players.get(playerIndex).addScore(scoreToAdd); // Synchronisation
            Player currentPlayer = getPlayers().get(playerIndex);
            currentPlayer.setScore(currentPlayer.getScore() + scoreToAdd);
        }
//        currentQuestionIndex++;
    }

    //getter-Methode, die den aktuellen Spieler zurückgibt.
    @Override
    public int getCurrentPlayer() {
        return currentPlayerIndex;
    }

    public void resetToFirstPlayer() {
        currentPlayerIndex = 0;
    }


    public void nextPlayer() {
        // Wechselt einfach 0 -> 1 -> 0 -> 1 usw.
        currentPlayerIndex = (currentPlayerIndex + 1) % playerScores.length;
    }

    // bestimmt den Gewinner basierend auf den Punkteständen.
    public String getWinner () {
        Player player1 = getPlayers().get(0);
        Player player2 = getPlayers().get(1);

        if (player1.getScore() > player2.getScore()) {
            return player1.getName() + " wins with " + player1.getScore() + " points!";
        } else if (player1.getScore() < player2.getScore()) {
            return player2.getName() + " wins with " + player2.getScore() + " points!";
        } else {
            return "It's a draw! Both players scored " + player1.getScore() + " points!";
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    // gibt die Punktestände der Spieler zurück, z.B. für den Endscreen.
    public int [] getScores() {
        return playerScores;
    }
}
