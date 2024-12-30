package monkeybytes.quiz.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import monkeybytes.quiz.game.Question;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * TriviaAPIService ruft Fragen von der OpenTDB API ab und konvertiert sie in Question-Objekte.
 */
public class TriviaAPIService {

    private static final String API_URL = "https://opentdb.com/api.php";

    /**
     * Ruft Fragen von der API ab.
     *
     * @param amount     Anzahl der Fragen
     * @param category   Kategorie der Fragen
     * @param difficulty Schwierigkeitsgrad (easy, medium, hard)
     * @return Liste von Question-Objekten
     * @throws Exception Bei Verbindungsproblemen
     */
    public List<Question> fetchQuestions(int amount, String category, String difficulty) throws Exception {
        // URL zusammenstellen
        String urlString = String.format("%s?amount=%d&category=%s&difficulty=%s&type=multiple",
                API_URL, amount, category, difficulty);

        // HTTP-Verbindung herstellen
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // HTTP-Statuscode prüfen
        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("API-Aufruf fehlgeschlagen: HTTP-Code " + connection.getResponseCode());
        }

        // JSON-Daten lesen
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonObject jsonResponse = new Gson().fromJson(reader, JsonObject.class);

        // Fragen aus dem JSON extrahieren
        JsonArray results = jsonResponse.getAsJsonArray("results");
        List<Question> questions = new ArrayList<>();

        for (var element : results) {
            JsonObject questionJson = element.getAsJsonObject();

            // Frage und Optionen extrahieren
            String questionText = questionJson.get("question").getAsString();
            String correctAnswer = questionJson.get("correct_answer").getAsString();

            List<String> options = new ArrayList<>();
            options.add(correctAnswer); // richtige Antwort hinzufügen

            for (var incorrectAnswer : questionJson.getAsJsonArray("incorrect_answers")) {
                options.add(incorrectAnswer.getAsString());
            }

            // Optionen mischen
            java.util.Collections.shuffle(options);

            // Index der korrekten Antwort
            int correctOptionIndex = options.indexOf(correctAnswer);

            // Question-Objekt erstellen und zur Liste hinzufügen
            questions.add(new Question(questionText, options, correctOptionIndex));
        }

        return questions;
    }
}
