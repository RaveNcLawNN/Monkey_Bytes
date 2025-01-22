package monkeybytes.quiz.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;


public class PlayerDataManager {
    private final String fileName;
    private final Gson gson;
    private List<Player> players;

    /**
     * Konstruktor:
     * - Initialisiert den Manager mit dem angegebenen Dateinamen für die Spieler-Daten.
     * - Erstellt eine Gson-Instanz.
     * - Lädt vorhandene Spieler-Daten aus der Datei.
     */
    public PlayerDataManager(String fileName) {
        this.fileName = fileName;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.players = loadPlayerData();
    }

    /**
     * Lädt Spieler-Daten aus der JSON-Datei.
     * - Liest die Datei und speichert die JSON-Daten in ein Array von Player-Objekten.
     * - Gibt die Daten als Liste zurück.
     */
    private List<Player> loadPlayerData() {
        try (FileReader reader = new FileReader(fileName)) {
            Player[] playersArray = gson.fromJson(reader, Player[].class);
            return new ArrayList<>(List.of(playersArray));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Speichert die Spieler-Daten in der JSON-Datei.
     * Sortiert die Spieler-Liste absteigend nach Punktestand.
     */
    private void savePlayerData() {
        players.sort(Comparator.comparingInt(Player::getScore).reversed());
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(players, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fügt ein neues Profil hinzu, falls es noch nicht existiert.
     * - Überprüft, ob der Profilname bereits in der Spieler-Liste vorhanden ist.
     * - Fügt ein neues Profil mit einem Startpunktestand von 0 hinzu und speichert die Daten.
     */
    public boolean addProfile(String profileName) {
        for (Player player : players) {
            if (player.getName().equals(profileName)) {
                System.out.println("Profile " + player.getName() + " already exists.");
                return false;
            }
        }
        players.add(new Player(profileName, 0));
        savePlayerData();
        return true;
    }

    /**
     * Aktualisiert die Spielerinformationen mit einem neuen Punktestand.
     * Aktualisiert den Punktestand nur, wenn der neue Wert höher ist als der bestehende.
     */
    public void updatePlayerInformation(String playerName, int newScore) {
        for (Player player : players) {
            if (player.getName().equals(playerName) && player.getScore() < newScore) {
                player.setScore(newScore);
                break;
            }
        }
        savePlayerData();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        for (Player player : players) {
            names.add(player.getName());
        }
        return names;
    }
}
