package monkeybytes.quiz.game;

import java.util.List;

/*
Die Question-Klasse repräsentiert eine einzelne Quizfrage mit ihren Antwortmöglichkeiten.
 */

public class Question {
    private String questionText; // Der Text der Frage
    private List<String> options; // Antwortmöglichkeiten
    private int correctOptionIndex; // Index der richtigen Antwort

    // Konstruktor für die Erstellung einer Frage
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

    // überprüft, ob die ausgewählte Antwort korrekt ist
    public boolean isCorrectAnswer (int selectedOptionIndex) {
        return selectedOptionIndex == correctOptionIndex;
    }

    // gibt die Frage und ihre Antwortmöglichkeiten als String zurück
    @Override
    public String toString() {
        return "Frage: " + questionText + "\nAntwortmöglichkeiten: " + options;
    }

}