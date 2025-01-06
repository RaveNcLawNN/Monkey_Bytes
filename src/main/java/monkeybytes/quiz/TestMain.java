package monkeybytes.quiz;
import monkeybytes.quiz.game.*;

import java.util.List;

/*
Diese Main ist zum Testen der Spiellogik in der Konsole.
 */


public class TestMain {
    public static void main(String[] args) {
        String fileName = "src/main/resources/data/playerData.json";

        // Initialize the PlayerDataManager
        PlayerDataManager manager = new PlayerDataManager(fileName);

        // Test 1: Add a new player
        System.out.println("Adding new player...");
        manager.updatePlayerInformation("Alice", 100);
        System.out.println("Player added.");

        // Test 2: Update the same player's score (higher score)
        System.out.println("Updating player score (higher)... ");
        manager.updatePlayerInformation("Alice", 150);
        System.out.println("Score updated.");

        // Test 3: Try to update with a lower score (should not update)
        System.out.println("Updating player score (lower)... ");
        manager.updatePlayerInformation("Alice", 50);
        System.out.println("Score should not update.");

        // Test 4: Add another player
        System.out.println("Adding another player...");
        manager.updatePlayerInformation("Bob", 600);
        System.out.println("Another player added.");

        // Test 5: Add another player
        System.out.println("Adding another player...");
        manager.updatePlayerInformation("Player5", 0);
        System.out.println("Another player added.");

        // Test 5: Retrieve and print all players
        System.out.println("Retrieving all players...");
        List<Player> players = manager.getPlayers();
        for (Player player : players) {
            System.out.println("Player: " + player.getName() + ", Score: " + player.getScore());
        }

        System.out.println("All tests completed.");
    }

    /*
    private static void testSingleplayer() {
        System.out.println("=== Singleplayer Test ===");

        // Fragen erstellen
        List<Question> questions = List.of(
                new Question("Was ist die Hauptstadt von Österreich?",
                        List.of("Berlin", "Wien", "Paris", "Rom"), 1),
                new Question("Wie viele Planeten hat unser Sonnensystem?",
                        List.of("7", "8", "9", "10"), 1)
        );

        // Singleplayer-Spiel erstellen
        Singleplayer singleplayer = new Singleplayer(questions);

        // Frage 1 beantworten (korrekt)
        singleplayer.checkAnswer(1);
        System.out.println("Aktueller Punktestand: " + singleplayer.getScore()); // Erwartet: 10

        // Frage 2 beantworten (falsch)
        singleplayer.checkAnswer(2);
        System.out.println("Aktueller Punktestand: " + singleplayer.getScore()); // Erwartet: 10

        // Spielstatus prüfen
        System.out.println("Spiel beendet? " + singleplayer.isGameOver()); // Erwartet: true
    }

    private static void testMultiplayer() {
        System.out.println("\n=== Multiplayer Test ===");

        // Fragen erstellen
        List<Question> questions = List.of(
                new Question("Wer hat die Relativitätstheorie entwickelt?",
                        List.of("Newton", "Einstein", "Galileo", "Tesla"), 1),
                new Question("Wie viele Kontinente gibt es auf der Erde?",
                        List.of("5", "6", "7", "8"), 2)
        );

        // Multiplayer-Spiel erstellen
        Multiplayer multiplayer = new Multiplayer(questions);

        // Spieler 1 beantwortet Frage 1 (falsch)
        multiplayer.checkAnswer(0);
        System.out.println("Punktestand Spieler 1: " + multiplayer.getPlayerScores()[0]); // Erwartet: 0
        System.out.println("Punktestand Spieler 2: " + multiplayer.getPlayerScores()[1]); // Erwartet: 0

        // Spieler 2 beantwortet Frage 2 (korrekt)
        multiplayer.checkAnswer(2);
        System.out.println("Punktestand Spieler 1: " + multiplayer.getPlayerScores()[0]); // Erwartet: 0
        System.out.println("Punktestand Spieler 2: " + multiplayer.getPlayerScores()[1]); // Erwartet: 10

        // Gewinner ermitteln
        System.out.println(multiplayer.getWinner()); // Erwartet: Player 2 wins with 10 points!
    }
     */
}


/*
Die Main-Klasse ist der Einstiegspunkt der App.
 */

//public class TestMain {
//    public static void main(String[] args) {
//        GameLogic gameLogic = new GameLogic();
//        gameLogic.startGame();
//    }
//}