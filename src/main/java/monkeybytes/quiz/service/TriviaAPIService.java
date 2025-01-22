package monkeybytes.quiz.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import monkeybytes.quiz.game.Question;
import org.apache.commons.text.StringEscapeUtils;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * TriviaAPIService ruft Fragen von der OpenTDB API ab und konvertiert sie in Question-Objekte.
 * Diese Klasse ist zuständig für die Kommunikation mit der API und stellt sicher,
 * dass keine Frage in einem Spieldurchlauf doppelt gestellt wird.
 */
public class TriviaAPIService {

    // Basis-URLs der API für Fragen und Kategorien
    private static final String API_URL = "https://opentdb.com/api.php";

    // Feste Kategorien mit ihren entsprechenden IDs aus der API
    private static final Map<String, String> FIXED_CATEGORY_IDS = Map.of(
            "Geography", "22",
            "History", "23",
            "Science & Nature", "17",
            "Science: Computers", "18",
            "Animals", "27"
    );

    // Session-Token, um eindeutige Fragen zu erhalten
    private String sessionToken;

    /**
     * Konstruktor: Initialisiert den Service und lädt Kategorien.
     */
    public TriviaAPIService() {
        this.sessionToken = fetchSessionToken(); // Session-Token um eindeutige Fragen zu generieren
    }

    public List<Question> fetchQuestions(int amount, String categoryId, String difficulty) throws Exception {
        List<Question> questions = requestQuestions(amount, categoryId, difficulty);

        // Falls leer, einmal Token zurücksetzen und erneut versuchen
        if (questions.isEmpty()) {
            resetSessionToken();
            questions = requestQuestions(amount, categoryId, difficulty);
        }
        return questions;
    }

    /**
     * Baut die Anfrage-URL zusammen, ruft die API auf und parst
     * die JSON-Antwort in Question-Objekte.
     */
    private List<Question> requestQuestions(int amount, String categoryId, String difficulty) throws Exception {
        String urlString = String.format(
                "%s?amount=%d&category=%s&difficulty=%s&type=multiple&token=%s",
                API_URL, amount, categoryId, difficulty, sessionToken
        );

        // Verbindung herstellen
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Prüfen, ob wir StatusCode = 200 bekommen
        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("API-Connection failed: HTTP-Code " + connection.getResponseCode());
        }

        // JSON-Antwort parsen
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonObject jsonResponse = new Gson().fromJson(reader, JsonObject.class);

        JsonArray results = jsonResponse.getAsJsonArray("results");
        List<Question> questions = new ArrayList<>();

        // Jede Frage in ein Question-Objekt überführen
        for (var element : results) {
            JsonObject questionJson = element.getAsJsonObject();
            String questionText = StringEscapeUtils.unescapeHtml4(questionJson.get("question").getAsString());
            String correctAnswer = StringEscapeUtils.unescapeHtml4(questionJson.get("correct_answer").getAsString());

            // Alle Antwortoptionen sammeln
            List<String> options = new ArrayList<>();
            options.add(correctAnswer);

            for (var incorrectAnswer : questionJson.getAsJsonArray("incorrect_answers")) {
                options.add(StringEscapeUtils.unescapeHtml4(incorrectAnswer.getAsString()));
            }

            // Mischen und Index der korrekten Antwort merken
            Collections.shuffle(options);
            int correctIndex = options.indexOf(correctAnswer);

            questions.add(new Question(questionText, options, correctIndex));
        }

        return questions;
    }

    public List<String> getFixedCategories() {
        // Gibt nur die Namen der festen Kategorien zurück
        return new ArrayList<>(FIXED_CATEGORY_IDS.keySet());
    }

    /**
     * Gibt die ID einer festen Kategorie zurück.
     *
     * @param categoryName Name der Kategorie.
     * @return ID der Kategorie oder Standardwert "9" für General Knowledge.
     */
    public String getFixedCategoryId(String categoryName) {
        // Ruft die ID der festen Kategorie ab, wenn nicht gefunden dann wirds auf 9 gesetzt (General Knowledge)
        return FIXED_CATEGORY_IDS.getOrDefault(categoryName, "9");
    }

    /**
     * Ruft einen Session-Token von der API ab.
     *
     * @return Der Session-Token.
     */
    private String fetchSessionToken() {
        try {
            // Token-API aufrufen, um einen neuen Session-Token zu erhalten
            URL url = new URL("https://opentdb.com/api_token.php?command=request");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject response = new Gson().fromJson(reader, JsonObject.class);
                return response.get("token").getAsString(); // Token aus der Antwort extrahieren
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Falls ein Fehler auftritt, null zurückgeben
    }

    /**
     * Setzt den Session-Token zurück.
     */
    private void resetSessionToken() {
        try {
            // Token-Reset-API aufrufen
            URL url = new URL("https://opentdb.com/api_token.php?command=reset&token=" + sessionToken);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                System.out.println("Session Token has been reset.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
