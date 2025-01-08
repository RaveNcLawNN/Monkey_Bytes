package monkeybytes.quiz.service;

import monkeybytes.quiz.game.Question;

import java.util.List;

public class TriviaAPIServiceTest {

    public static void main(String[] args) {
        TriviaAPIService apiService = new TriviaAPIService();

        try {
            // Beispiel: Ruft 5 leichte Fragen in der Kategorie "Geografie" ab.
            List<Question> questions = apiService.fetchUniqueQuestions(10, "22", "easy");

            // Gibt die abgerufenen Fragen in der Konsole aus
            for (Question question : questions) {
                System.out.println("Question: " + question.getQuestionText());
                for (int i = 0; i < question.getOptions().size(); i++) {
                    System.out.println((i + 1) + ". " + question.getOptions().get(i));
                }
                System.out.println("Right answer: " +
                        question.getOptions().get(question.getCorrectOptionIndex()));
                System.out.println("-------------------------------------------------");
            }

        } catch (Exception e) {
            System.err.println("Error when fetching questions: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
