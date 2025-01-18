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
    //Der Name des JSON Files, in dem die Player Data gespeichert wird. Wird immer playerData.json enthalten vermutlich.
    private final String fileName;
    private final Gson gson;
    private List<Player> players;

    //Konstruktor
    public PlayerDataManager(String fileName) {
        this.fileName = fileName;
        //Erstellt einen GsonBuilder, der für die Bearbeitung der JSON Datei gebraucht wird.
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        //loadPlayerData übergibt und speichert so alle Player Objekte in players ab.
        this.players = loadPlayerData();
    }

    //Lädt die Player Data aus der JSON file.
    private List<Player> loadPlayerData() {
        //"try" schließt FileReader automatisch, wenn er nicht mehr benötigt wird.
        //Liest die Datei, die in fileName hinterlegt ist.
        try (FileReader reader = new FileReader(fileName)) {
            //Konvertiert die JSON Daten in ein Array von Player Objekten:
            //Input == JSON Quelle (reader) und in was die Daten umgewandelt werden sollen (Array von Player Objekten).
            Player[] playersArray = gson.fromJson(reader, Player[].class);
            //Wandelt um und übergibt das playersArray als ArrayList.
            return new ArrayList<>(List.of(playersArray));
        //Falls die Datei nicht existiert, wir eine leere ArrayList zurückgegeben. Sollte nicht passieren.
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // Speichert Player Data in die JSON-Datei
    private void savePlayerData() {
        //Sortiert Players nach Score (absteigend):
        players.sort(Comparator.comparingInt(Player::getScore).reversed());
        //Öffnet und schreibt in die Datei aus fileName.
        try (FileWriter writer = new FileWriter(fileName)) {
            //Übernimmt die Liste an Player Objekten und den Writer, der die Daten direkt ins JSON File schreibt.
            gson.toJson(players, writer);
        //gibt eine Fehlermeldung + Zusatzinfo aus, falls etwas nicht funktioniert.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Eine extra Methode im Player Objekte zu erstellen. Bei Erstellung wird ein Score von 0 übergeben.
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

    //Aktualisiert Player Information, wenn neuer Score höher ist als der alte.
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

    //Gibt eine Liste mit Player Namen zurück.
    public List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        for (Player player : players) {
            names.add(player.getName());
        }
        return names;
    }
}
