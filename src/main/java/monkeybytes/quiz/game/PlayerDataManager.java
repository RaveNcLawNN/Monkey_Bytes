package monkeybytes.quiz.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
        //Öffnet und schreibt in die Datei aus fileName.
        try (FileWriter writer = new FileWriter(fileName)) {
            //Übernimmt die Liste an Player Objekten und den Writer, der die Daten direkt ins JSON File schreibt.
            gson.toJson(players, writer);
        //gibt eine Fehlermeldung + Zusatzinfo aus, falls etwas nicht funktioniert.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Übernimmt Spieler und einen zu aktualisierenden Score. Aktualisiert den Score, oder erstellt ein neues Objekt.
    //Glaub am besten einzusetzen 1x bei der Erstellung des Namens vor dem Spiel mit newScore = 0 und 1x am Ende des Spiels.
    //So ist sichergestellt, dass, auch wenn kein Spiel beendet wird, der Player gespeichert wird und auch gut in der Highscore Tabelle angezeigt werden kann.
    public void updatePlayerInformation(String playerName, int newScore) {

        boolean playerFound = false;

        //Durchläuft jedes Player Objekt in der "players" Liste und prüft, ob Spieler existiert.
        for (Player player : players) {
            //Vergleicht Objekte mit übergebenem playerName (z.B. aus GameLogic?)
            if (player.getName().equals(playerName)) {
                //Wenn ein Spieler gefunden wurde, und der neue Score höher ist, wird der Score aktualisiert.
                if (player.getScore() <= newScore) {
                    player.setScore(newScore);
                }
                playerFound = true;
                break;
            }
        }

        if (!playerFound) {
            players.add(new Player(playerName, newScore));
        }

        savePlayerData();
    }

    public List<Player> getPlayers() {
        return players;
    }
}
