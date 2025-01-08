package monkeybytes.quiz.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import monkeybytes.quiz.game.Question;
import org.apache.commons.text.StringEscapeUtils;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TriviaAPIService ruft Fragen von der OpenTDB API ab und konvertiert sie in Question-Objekte.
 * Diese Klasse ist zuständig für die Kommunikation mit der API und stellt sicher,
 * dass keine Frage in einem Spieldurchlauf doppelt gestellt wird.
 */
public class TriviaAPIService {

    private static final String API_URL = "https://opentdb.com/api.php"; // Basis-URL der API
    private Set<String> usedQuestions = new HashSet<>(); // Speichert bereits verwendete Fragen in einem Set, um Duplikate zu verhindern

    /**
     * Ruft einzigartige Fragen von der API ab. Verwendet die `fetchQuestions`-Methode
     * und filtert doppelte Fragen heraus.
     *
     * @param amount     Anzahl der gewünschten Fragen
     * @param category   Kategorie der Fragen (z. B. "22" für Geografie)
     * @param difficulty Schwierigkeitsgrad ("easy", "medium", "hard")
     * @return Eine Liste von einzigartigen Question-Objekten
     * @throws Exception Bei Verbindungsproblemen oder wenn nicht genug Fragen verfügbar sind
     */
    public List<Question> fetchUniqueQuestions(int amount, String category, String difficulty) throws Exception {
        List<Question> questions = new ArrayList<>(); // Liste für die endgültigen Fragen
        int fetchedAmount = 0; // Zählt, wie viele Fragen bereits abgerufen wurden

        while (questions.size() < amount) {
            // Bestimmt, wie viele Fragen in diesem Durchlauf abgerufen werden sollen
            int batchSize = Math.min(10, amount - fetchedAmount); // Nur so viele Fragen anfordern, wie noch benötigt werden
            List<Question> fetchedQuestions = fetchQuestions(batchSize, category, difficulty); // Ruft eine Batch von Fragen von der API ab, der batchSize entsprechend

            // Filtert Duplikate heraus
            for (Question question : fetchedQuestions) {
                // Wenn die Frage noch nicht verwendet wurde, wird sie der Liste hinzugefügt
                if (!usedQuestions.contains(question.getQuestionText())) {
                    questions.add(question);
                    usedQuestions.add(question.getQuestionText());
                }
                // Beende, wenn die gewünschte Anzahl erreicht ist
                if (questions.size() == amount) break;
            }

            fetchedAmount += batchSize;

            // Sicherheitscheck: Beende, wenn keine neuen Fragen mehr gefunden werden
            if (fetchedQuestions.isEmpty()) {
                throw new RuntimeException("No more unique questions found.");
            }
        }

        return questions; // Gibt die gefilterten (also unique) Questions zurück
    }

    /**
     * Ruft Fragen direkt von der API ab, ohne auf Duplikate zu prüfen.
     * Diese Methode stellt die Verbindung zur API her und konvertiert die Antworten in Question-Objekte.
     *
     * @param amount     Anzahl der gewünschten Fragen
     * @param category   Kategorie der Fragen
     * @param difficulty Schwierigkeitsgrad ("easy", "medium", "hard")
     * @return Eine Liste von Question-Objekten
     * @throws Exception Bei Verbindungsproblemen oder fehlerhafter API-Antwort
     */
    public List<Question> fetchQuestions(int amount, String category, String difficulty) throws Exception {
        // URL wird zusammengestellt basierend auf den Parametern
        String urlString = String.format("%s?amount=%d&category=%s&difficulty=%s&type=multiple",
                API_URL, amount, category, difficulty);

        // HTTP-Verbindung zur API herstellen
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET"); // Setze HTTP-Methode auf GET

        // HTTP-Statuscode prüfen
        if (connection.getResponseCode() != 200) { // Response code 200 bedeutet Erfolg, alles andere ist ein Fehler
            throw new RuntimeException("API-connection failed: HTTP-Code " + connection.getResponseCode());
        }

        // Liest die JSON-Antwort von der API
        InputStreamReader reader = new InputStreamReader(connection.getInputStream()); // Liest JSON-Daten aus der API-Antwort
        JsonObject jsonResponse = new Gson().fromJson(reader, JsonObject.class); // Konvertiert JSON-Daten in ein JsonObject

        // Extrahiert das "results"-Array aus der JSON-Antwort
        JsonArray results = jsonResponse.getAsJsonArray("results");
        List<Question> questions = new ArrayList<>();

        // Konvertiert jede Frage im Array in ein Question-Objekt
        for (var element : results) {
            JsonObject questionJson = element.getAsJsonObject();

            // Frage und Antwortmöglichkeiten extrahieren
            String questionText = StringEscapeUtils.unescapeHtml4(questionJson.get("question").getAsString());
            String correctAnswer = StringEscapeUtils.unescapeHtml4(questionJson.get("correct_answer").getAsString());

            List<String> options = new ArrayList<>();
            options.add(correctAnswer); // richtige Antwort hinzufügen

            for (var incorrectAnswer : questionJson.getAsJsonArray("incorrect_answers")) {
                options.add(StringEscapeUtils.unescapeHtml4(incorrectAnswer.getAsString()));
            }

            // Antwortmöglichkeiten mischen
            java.util.Collections.shuffle(options);

            // Findet den Index der korrekten Antwort
            int correctOptionIndex = options.indexOf(correctAnswer);

            // Question-Objekt erstellen und zur Liste hinzufügen
            questions.add(new Question(questionText, options, correctOptionIndex));
        }

        return questions; // Rückgabe der abgerufenen Fragen
    }

    /**
     * Setzt die Liste der verwendeten Fragen zurück.
     * Diese Methode wird verwendet, um eine neue Runde zu starten, in der alle Fragen wieder verfügbar sind.
     */
    public void resetUsedQuestions() {
        usedQuestions.clear();
    }
}
