package monkeybytes.quiz.game;

public class Player {
    private String name;
    private int score;

    public Player (String name, int score) {
        this.name = name;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore (int points) {
        this.score += points;
    }

    public void resetScore() {
        this.score = 0;
    }

    @Override
    public String toString() {
        return name + ": " + score + " points";
    }
}
