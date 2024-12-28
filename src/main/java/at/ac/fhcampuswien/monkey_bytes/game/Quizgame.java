package at.ac.fhcampuswien.monkey_bytes.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
Die Quizgame-Klasse verwaltet den gesamten Ablauf des Quizspiels.
 */

public class Quizgame {
    private List<Question> questions; // Liste aller Fragen im Quiz
    private int score; // Aktueller Punktestand des Spielers

    /*
    Konstruktor, der die Fragen initialisiert und den Punktestand auf 0 setzt.
     */

    public Quizgame() {
        questions = new ArrayList<>(); // Initialisiert die Fragenliste
        score = 0; // Setzt den Punktestand auf 0
        loadQuestions(); // Lädt die Fragen in die Liste
    }

    /*
    Lädt die Fragen in die Liste. Man könnte diese Methode später durch eine Datenbank- oder Datei-Integration ersetzen.
     */

    private void loadQuestions() {
        questions.add(new Question("Was ist die Hauptstadt von Österreich?",
                new String[]{"Salzburg", "Graz", "Wien", "Bregenz"}, 2));
        questions.add(new Question("Wie viele Planeten hat unser Sonnensystem?",
                new String[]{"7", "8", "9", "10"}, 1));
        questions.add(new Question("Wann wurde Amerika entdeckt?",
                new String[]{"1451", "1576", "1354", "1492"}, 3));
    }

    /*
    Startet das Quiz und zeigt die Fragen nacheinander an.
     */

    public void startGame() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Willkommen zum Quiz!");
        System.out.println("---------------------");

        // Geht durch die Liste der Fragen

        for (Question question : questions) {
            System.out.println(question.getQuestionText()); // Zeigt die Frage an

            String[] options = question.getOptions();
            for (int i = 0; i < options.length; i++) {
                System.out.println((i + 1) + ": " + options[i]); // Zeigt die Antwortmöglichkeiten an (Antwortnummer 1-basiert)
            }

            System.out.print("Deine Antwort (1-" + options.length + "): "); // Liest die Antworten des Spielers ein
            int answer = scanner.nextInt() - 1; // Konvertiert die Eingabe in ein 0-basierten Index

            // Überprüft ob die Antwort richtig ist

            if (answer == question.getCorrectOptionIndex()) {
                System.out.println("Richtig!\n");
                score++;
            } else {
                System.out.println("Falsch! Die richtige Antwort ist: " +
                        options[question.getCorrectOptionIndex()] + "\n");
            }

        }

        // Zeigt den Endpunktestand an

        System.out.println("Quiz beendet!");
        System.out.println("Dein Punktestand: " + score + "/" + questions.size());
        scanner.close();


    }
}