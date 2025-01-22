package monkeybytes.quiz.game;

import java.util.List;

/**
 * Implementiert die Spiellogik für den Singleplayer-Modus.
 */
public class Singleplayer extends GameLogic {

    /**
     * Konstruktor für die Singleplayer-Klasse.
     * Ruft den Konstruktor der Elternklasse auf.
     */
    public Singleplayer (List<Question> questions) {
        super(questions, 1, 30);
    }

    public void checkAnswer (int selectedOptionIndex, int remainingTime) {
        super.checkAnswer(selectedOptionIndex, 0, remainingTime);
    }


    public int getScore() {
        return playerScores[0]; // da es nur einen Spieler gibt und unser playerScores-Array also nur ein Element enthält, brauchen wir nur playerScores[0].
    }

}
