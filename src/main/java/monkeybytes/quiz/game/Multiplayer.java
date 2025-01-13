package monkeybytes.quiz.game;

import java.util.List;

/*
Die Multiplayer-Klasse erweitert die GameLogic-Klasse und passt sie für zwei Spieler an.
 */

public class Multiplayer extends GameLogic {

    public Multiplayer (List<Question> questions) {
        // "super" wird verwendet, um Konstruktoren und Methoden aus der Elternklasse ("Superklasse") aufzurufen.
        // hier wird der GameLogic-Konstruktor aufgerufen, mit numberOfPlayers = 2.
        super (questions, 2, 15);
    }

    public void checkAnswer (int selectedOptionIndex) {
        int currentPlayer = getCurrentPlayer(); // der aktuelle Spieler wird aufgerufen.
        // die checkAnswer-Methode aus GameLogic wird aufgerufen, mit dem aktuellen Spieler als currentPlayer, den wir gerade aufgerufen haben.
        super.checkAnswer(selectedOptionIndex, currentPlayer);
    }

    // bestimmt den Gewinner basierend auf den Punkteständen.
    public String getWinner () {
        if (playerScores[0] > playerScores[1]) {
            return "Player 1 wins with " + playerScores[0] + " points!";
        } else if (playerScores[0] < playerScores[1]) {
            return "Player 2 wins with " + playerScores[1] + " points!";
        } else {
            return "It's a draw! Both players scored " + playerScores[0] + " points!";
        }
    }

    public List<Player> getPlayers() {
        return List.of(
                new Player("Alice", 0),
                new Player("Bob", 0)
        );
    }

    public Player getCurrentPlayerProfile() {
        return getPlayers().get(getCurrentPlayer());
    }

    // gibt die Punktestände der Spieler zurück, z.B. für den Endscreen.
    public int [] getScores() {
        return playerScores;
    }
}
