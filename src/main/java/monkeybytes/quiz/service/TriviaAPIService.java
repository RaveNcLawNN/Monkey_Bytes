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
     * Konstruktor: Initialisiert den Service, lädt Session-Token und Kategorien.
     */
    public TriviaAPIService() {
        this.sessionToken = fetchSessionToken(); // Session-Token um eindeutige Fragen zu generieren
    }

    /**
     * Lädt Fragen mit einer fixed category und variablem Schwierigkeitsgrad von der API
     * @param amount      Anzahl der gewünschten Fragen (bei uns 10)
     * @param categoryId  z. B. "22" für Geography
     * @param difficulty  easy, medium, hard
     * @return Liste an Question-Objekten, die wir in unserem Quiz verwenden.
     * @throws Exception wenn beim HTTP-Request etwas schiefläuft
     */
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
     * Wird von fetchQuestions aufgerufen, führt den HTTP Request durch.
     * Baut die Anfrage-URL zusammen, ruft die API auf und konvertiert
     * die JSON-Antwort in Question-Objekte.
     * @param amount     Anzahl der gewünschten Fragen
     * @param categoryId zB 22
     * @param difficulty zB easy
     * @return Liste von Question-Objekten
     * @throws Exception wenn die Verbindung fehlschlägt oder HTTP-Code != 200
     */
    private List<Question> requestQuestions(int amount, String categoryId, String difficulty) throws Exception {
        // 1) URL mit Query-Parametern bauen, inkl. Session-Token:
        // zB "https://opentdb.com/api.php?amount=10&category=22&difficulty=easy&type=multiple&token=ABC123"
        String urlString = String.format(
                "%s?amount=%d&category=%s&difficulty=%s&type=multiple&token=%s",
                API_URL, amount, categoryId, difficulty, sessionToken
        );

        // 2) Verbindung herstellen
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // 3) Prüfen ob Verbindung erfolgreich war (muss 200 sein)
        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("API-Connection failed: HTTP-Code " + connection.getResponseCode());
        }

        // 4) JSON-Antwort parsen. Liest InputStream und konvertiert ihn via Gson in ein JSON-Objekt
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonObject jsonResponse = new Gson().fromJson(reader, JsonObject.class);

        // 5) results enthält ein Array mit den Fragen
        JsonArray results = jsonResponse.getAsJsonArray("results");
        List<Question> questions = new ArrayList<>();

        // 6) Jede Frage im Array durchgehen und in ein Question-Objekt umwandeln
        for (var element : results) {
            JsonObject questionJson = element.getAsJsonObject();
            // unescapeHtml14 damit die Sonderzeichen richtig angezeigt werden
            String questionText = StringEscapeUtils.unescapeHtml4(questionJson.get("question").getAsString());
            String correctAnswer = StringEscapeUtils.unescapeHtml4(questionJson.get("correct_answer").getAsString());

            // Liste der Antwortmöglichkeiten inkl. der richtigen Antwort
            List<String> options = new ArrayList<>();
            options.add(correctAnswer);

            // JSON-Array "incorrect_answers" mit 3 falschen Antworten
            for (var incorrectAnswer : questionJson.getAsJsonArray("incorrect_answers")) {
                options.add(StringEscapeUtils.unescapeHtml4(incorrectAnswer.getAsString()));
            }

            // 7) Mischen und Index der korrekten Antwort merken
            Collections.shuffle(options);

            // 8) Index der richtigen Antwort finden
            int correctIndex = options.indexOf(correctAnswer);

            // 9) Question-Objekt erzeugen und zur Liste hinzufügen
            questions.add(new Question(questionText, options, correctIndex));
        }

        // 10) Liste von Question-Ojekten ist fertig
        return questions;
    }

    /**
     * Gibt eine Liste der festen Kategorienamen zurück. Wird in SelectionDiffTop verwendet
     */
    public List<String> getFixedCategories() {
        // Gibt nur die Namen der festen Kategorien zurück
        return new ArrayList<>(FIXED_CATEGORY_IDS.keySet());
    }

    /**
     * Sucht zu einem Kategorie-Namen die passende ID heraus.
     * Falls nicht gefunden, wird "9" (General Knowledge) zurückgegeben.
     */
    public String getFixedCategoryId(String categoryName) {
        // Ruft die ID der festen Kategorie ab, wenn nicht gefunden dann wirds auf 9 gesetzt (General Knowledge)
        return FIXED_CATEGORY_IDS.getOrDefault(categoryName, "9");
    }

    /**
     * Ruft einen Session-Token von der API ab.
     * Passiert im Konstruktor, damit man bei Spielbeginn einen neuen Token hat.
     */
    private String fetchSessionToken() {
        try {
            // Token anfordern
            URL url = new URL("https://opentdb.com/api_token.php?command=request");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject response = new Gson().fromJson(reader, JsonObject.class);
                // Token aus der Antwort extrahieren
                return response.get("token").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not fetch token.");
        }
        // Falls ein Fehler auftritt, null zurückgeben
        return null;
    }

    /**
     * Setzt den Session-Token zurück.
     * Wird in fetchQuestions verwendet, falls es einen Fehler gibt
     */
    private void resetSessionToken() {
        try {
            // Token resetten
            URL url = new URL("https://opentdb.com/api_token.php?command=reset&token=" + sessionToken);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                System.out.println("Session Token has been reset.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not reset token.");
        }
    }
}
