package monkeybytes.quiz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import monkeybytes.quiz.game.Player;
import monkeybytes.quiz.game.PlayerDataManager;

import java.util.List;

public class Highscores {

    @FXML
    private TableView<Player> highscoreTable;

    @FXML
    private TableColumn<Player, String> playerNameColumn;

    @FXML
    private TableColumn<Player, Integer> scoreColumn;

    @FXML
    private Button highscoreExit;

    private PlayerDataManager playerDataManager;

    @FXML
    public void initialize() {
        playerDataManager = new PlayerDataManager("src/main/resources/data/playerData.json");
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        loadHighscores();
    }

    private void loadHighscores() {
        List<Player> players = playerDataManager.getPlayers();

        highscoreTable.getItems().addAll(players);
    }

    @FXML
    private void onExit() {
        Stage stage = (Stage) highscoreExit.getScene().getWindow();
        stage.close();
    }
}
