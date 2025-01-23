package monkeybytes.quiz.game;

public class Player {
    private String name;
    private int score;

    public Player (String name, int score) {
        if (!validName(name)) {
            throw new IllegalArgumentException("Name not allowed");
        }
        this.name = name;
        this.score = score;
    }

    private boolean validName (String name) {
        return name != null && name.matches("[a-zA-Z0-9]{3,15}");
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return name + ": " + score + " points";
    }
}
