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
    private static final String API_CATEGORY_URL = "https://opentdb.com/api_category.php";

    // Feste Kategorien mit ihren entsprechenden IDs aus der API
    private static final Map<String, String> FIXED_CATEGORY_IDS = Map.of(
            "Geography", "22",
            "History", "23",
            "Science & Nature", "17",
            "Science: Computers", "18",
            "Animals", "27"
    );

    // Map zum Speichern dynamisch geladener Kategorien aus der API
    private Map<String, String> categoryMap = new HashMap<>();

    // Set zur Speicherung von bereits verwendeten Fragen, um Duplikate zu vermeiden
    private Set<String> usedQuestions = new HashSet<>();

    // Session-Token, um eindeutige Fragen zu erhalten
    private String sessionToken;

    /**
     * Konstruktor: Initialisiert den Service und lädt Kategorien.
     */
    public TriviaAPIService() {
        loadCategories(); // Kategorien dynamisch laden
        sessionToken = fetchSessionToken(); // Session-Token um eindeutige Fragen zu generieren
    }

    /**
     * Lädt Kategorien von der API und speichert sie in der Map categoryMap.
     */
    private void loadCategories() {
        try {
            // URL der Kategorie-API aufrufen
            URL url = new URL(API_CATEGORY_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Prüft, ob die Verbindung erfolgreich war (Statuscode 200)
            if (connection.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject response = new Gson().fromJson(reader, JsonObject.class);
                JsonArray categories = response.getAsJsonArray("trivia_categories");

                // Iteriert durch alle Kategorien und fügt sie in die Map ein
                for (var categoryElement : categories) {
                    JsonObject category = categoryElement.getAsJsonObject();
                    String name = category.get("name").getAsString(); // Name der Kategorie
                    String id = category.get("id").getAsString(); // ID der Kategorie
                    categoryMap.put(name, id); // Speichert die Kategorie in der Map
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldn't load categories.");
        }
    }

    /**
     * Ruft eine Liste der festen Kategorienamen ab.
     * @return Eine Liste der festen Kategorien.
     */
    public List<String> getFixedCategories() {
        // Gibt nur die Namen der festen Kategorien zurück
        return new ArrayList<>(FIXED_CATEGORY_IDS.keySet());
    }

    /**
     * Gibt die ID einer festen Kategorie zurück.
     * @param categoryName Name der Kategorie.
     * @return ID der Kategorie oder Standardwert "9" für General Knowledge.
     */
    public String getFixedCategoryId(String categoryName) {
        // Ruft die ID der festen Kategorie ab, wenn nicht gefunden dann wirds auf 9 gesetzt (General Knowledge)
        return FIXED_CATEGORY_IDS.getOrDefault(categoryName, "9");
    }

    /**
     * Ruft einzigartige Fragen von der API ab. Verwendet einen Session-Token.
     * @param amount Anzahl der gewünschten Fragen.
     * @param category Kategorie-ID.
     * @param difficulty Schwierigkeitsgrad.
     * @return Eine Liste von einzigartigen Fragen.
     */
    public List<Question> fetchUniqueQuestions(int amount, String category, String difficulty) throws Exception {
        List<Question> questions = new ArrayList<>();
        // URL mit den API-Parametern, einschließlich Session-Token
        String urlString = String.format("%s?amount=%d&category=%s&difficulty=%s&type=multiple&token=%s",
                API_URL, amount, category, difficulty, sessionToken);

        try {
            // Fragen von der API abrufen
            questions = fetchQuestions(urlString);

            // Falls keine Fragen zurückgegeben werden, Token zurücksetzen und nochmal versuchen
            if (questions.isEmpty()) {
                resetSessionToken();
                questions = fetchQuestions(urlString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error fetching questions.");
        }

        return questions;
    }

    /**
     * Ruft Fragen direkt von der API ab.
     * @param urlString URL mit API-Parametern.
     * @return Eine Liste von Fragen.
     */
    private List<Question> fetchQuestions(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("API-Connection failed: HTTP-Code " + connection.getResponseCode());
        }

        // JSON-Antwort parsen
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonObject jsonResponse = new Gson().fromJson(reader, JsonObject.class);

        JsonArray results = jsonResponse.getAsJsonArray("results");
        List<Question> questions = new ArrayList<>();

        // Jede Frage verarbeiten und in ein Question-Objekt umwandeln
        for (var element : results) {
            JsonObject questionJson = element.getAsJsonObject();
            String questionText = StringEscapeUtils.unescapeHtml4(questionJson.get("question").getAsString());
            String correctAnswer = StringEscapeUtils.unescapeHtml4(questionJson.get("correct_answer").getAsString());

            List<String> options = new ArrayList<>();
            options.add(correctAnswer); // richtige Antwort hinzufügen

            // Falsche Antworten hinzufügen
            for (var incorrectAnswer : questionJson.getAsJsonArray("incorrect_answers")) {
                options.add(StringEscapeUtils.unescapeHtml4(incorrectAnswer.getAsString()));
            }

            // Antwortmöglichkeiten mischen und Frage hinzufügen
            Collections.shuffle(options);
            int correctOptionIndex = options.indexOf(correctAnswer);
            questions.add(new Question(questionText, options, correctOptionIndex));
        }

        return questions;
    }

    /**
     * Ruft einen Session-Token von der API ab.
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

    /**
     * Setzt die Liste der verwendeten Fragen zurück.
     */
    public void resetUsedQuestions() {
        usedQuestions.clear();
    }
}
