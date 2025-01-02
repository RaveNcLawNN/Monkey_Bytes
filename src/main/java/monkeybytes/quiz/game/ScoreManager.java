package monkeybytes.quiz.game;

public class ScoreManager {
    private int totalScore;
    private QuestionTimer questionTimer;

    public ScoreManager(QuestionTimer questionTimer) {
        this.totalScore = 0;
        this.questionTimer = questionTimer;

    }

    //Berechnet den Score pro Frage und addiert ihn zum totalScore hinzu. Coolere Berechnung wär vielleicht cool.
    public int calculateScore(int basePoints) {
        int bonus = questionTimer.getRemainingTime() * 2;
        int questionScore = basePoints + bonus;
        totalScore += questionScore;

        return questionScore;
    }

    //Gibt den totalScore aus.
    public int getTotalScore() {
        return totalScore;
    }
}
