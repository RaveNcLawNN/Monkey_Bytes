package at.ac.fhcampuswien.monkey_bytes.game;

import java.util.Arrays;

/*
Die Question-Klasse repräsentiert eine einzelne Quizfrage mit ihren Antwortmöglichkeiten.
 */

public class Question {
    private String questionText; // Der Text der Frage
    private String[] options; // Antwortmöglichkeiten
    private int correctOptionIndex; // Index der richtigen Antwort

    // Konstruktor für die Erstellung einer Frage.

    public Question(String questionText, String[] options, int correctOptionIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    // Getter-Methoden, um die Eigenschaften der Frage zu erhalten.

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    // Gibt die Frage und ihre Antwortmöglichkeiten als String zurück.

    @Override
    public String toString() {
        return "Frage: " + questionText + "\nOptionen: " + Arrays.toString(options);
    }

}