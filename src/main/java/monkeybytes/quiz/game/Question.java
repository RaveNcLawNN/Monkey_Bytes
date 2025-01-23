package monkeybytes.quiz.game;

import java.util.List;

/**
 * Die Klasse Question repräsentiert eine einzelne Quizfrage mit vier Antwortoptionen.
 * - questionText: der eigentliche Fragentext
 * - options: eine Liste aller möglichen Antworten
 * - correctOptionIndex: der Index in 'options' der richtigen Antwort
 * Question-Objekte werden in TriviaAPIService.requestQuestions(...)
 * erzeugt, wenn wir die JSON-Daten des API-Aufrufs in Java-Objekte umwandeln.
 */
public class Question {
    private String questionText; // Der Text der Frage
    private List<String> options; // Antwortmöglichkeiten
    private int correctOptionIndex; // Index der richtigen Antwort

    /**
     * Konstruktor:
     * Wir übergeben den Fragetext, die 4 Antwortmöglichkeiten (options),
     * und den Index der richtigen Antwort (correctOptionIndex).
     */
    public Question(String questionText, List <String> options, int correctOptionIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    // getter für den Fragentext
    public String getQuestionText() {
        return questionText;
    }

    // getter für die Antwortmöglichkeiten
    public List<String> getOptions() {
        return options;
    }

    // getter für den Index der richtigen Antwort
    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

}