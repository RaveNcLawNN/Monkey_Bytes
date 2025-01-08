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

    public GameLogic(List<Question> questions, int numberOfPlayers) {
        this.questions = questions;
        this.playerScores = new int[numberOfPlayers]; // initialisiert das playerScores-Array mit der Länge numberOfPlayers.
    }

    // gibt die aktuelle Frage zurück, wenn der aktuelle Index innerhalb der questions-Liste liegt. Wenn es keine Fragen mehr gibt, wird null zurückgegeben.
    public Question getCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }

    // überprüft, ob die Antwort eines Spielers korrekt ist und aktualisiert den Punktestand.
    public void checkAnswer(int selectedOptionIndex, int playerIndex) {
        Question currentQuestion = getCurrentQuestion();
        if (currentQuestion != null && currentQuestion.getCorrectOptionIndex() == selectedOptionIndex) { // falls der Index der korrekten Antwort gleich dem Index der ausgewählten Antwort ist, gibt es Punkte.
            playerScores[playerIndex] += 10;
        }
        currentQuestionIndex++;
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

}

//    private List<Question> questions; // Liste aller Fragen im Quiz
//    private int score; // Aktueller Punktestand des Spielers
//
//    /*
//    Konstruktor, der die Fragen initialisiert und den Punktestand auf 0 setzt.
//     */
//
//    public GameLogic() {
//        questions = new ArrayList<>(); // Initialisiert die Fragenliste
//        score = 0; // Setzt den Punktestand auf 0
//        loadQuestions(); // Lädt die Fragen in die Liste
//    }
//
//    /*
//    Lädt die Fragen in die Liste. Man könnte diese Methode später durch eine Datenbank- oder Datei-Integration ersetzen.
//     */
//
//    private void loadQuestions() {
//        questions.add(new Question("Was ist die Hauptstadt von Österreich?",
//                new String[]{"Salzburg", "Graz", "Wien", "Bregenz"}, 2));
//        questions.add(new Question("Wie viele Planeten hat unser Sonnensystem?",
//                new String[]{"7", "8", "9", "10"}, 1));
//        questions.add(new Question("Wann wurde Amerika entdeckt?",
//                new String[]{"1451", "1576", "1354", "1492"}, 3));
//    }
//
//    /*
//    Startet das Quiz und zeigt die Fragen nacheinander an.
//     */
//
//    public void startGame() {
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("Willkommen zum Quiz!");
//        System.out.println("---------------------");
//
//        // Geht durch die Liste der Fragen
//
//        for (Question question : questions) {
//            System.out.println(question.getQuestionText()); // Zeigt die Frage an
//
//            String[] options = question.getOptions();
//            for (int i = 0; i < options.length; i++) {
//                System.out.println((i + 1) + ": " + options[i]); // Zeigt die Antwortmöglichkeiten an (Antwortnummer 1-basiert)
//            }
//
//            System.out.print("Deine Antwort (1-" + options.length + "): "); // Liest die Antworten des Spielers ein
//            int answer = scanner.nextInt() - 1; // Konvertiert die Eingabe in ein 0-basierten Index
//
//            // Überprüft ob die Antwort richtig ist
//
//            if (answer == question.getCorrectOptionIndex()) {
//                System.out.println("Richtig!\n");
//                score++;
//            } else {
//                System.out.println("Falsch! Die richtige Antwort ist: " +
//                        options[question.getCorrectOptionIndex()] + "\n");
//            }
//
//        }
//
//        // Zeigt den Endpunktestand an
//
//        System.out.println("Quiz beendet!");
//        System.out.println("Dein Punktestand: " + score + "/" + questions.size());
//        scanner.close();
//