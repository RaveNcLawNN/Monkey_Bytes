package monkeybytes.quiz.game;

import java.util.List;

/*
Die Singleplayer-Klasse erweitert die GameLogic-Klasse und passt sie für Singleplayer an.
*/

public class Singleplayer extends GameLogic {

    public Singleplayer (List<Question> questions) {
        // "super" wird verwendet, um Konstruktoren und Methoden aus der Elternklasse ("Superklasse") aufzurufen.
        // hier wird der GameLogic-Konstruktor aufgerufen, mit numberOfPlayers = 1.
        super(questions, 1); // das ist also der angepasste Singleplayer-Konstruktor.
    }

    public void checkAnswer (int selectedOptionIndex) {
        // die checkAnswer-Methode aus GameLogic wird aufgerufen und PlayerIndex auf 0 gesetzt, da es nur einen Spieler gibt.
        super.checkAnswer(selectedOptionIndex, 0);
    }


    public int getScore() {
        return playerScores[0]; // da es nur einen Spieler gibt und unser playerScores-Array also nur ein Element enthält, brauchen wir nur playerScores[0].
    }

}
