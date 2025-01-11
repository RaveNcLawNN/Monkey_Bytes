package monkeybytes.quiz.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.stage.StageStyle;


public class MainMenuController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button playGameButton;

    @FXML
    private Button rulesButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button highscoreButton;

    @FXML
    public void initialize() {
        //event handler
        playGameButton.setOnAction(event -> openModeSelectionScreen());
        rulesButton.setOnAction(event -> openRulesScreen());
        settingsButton.setOnAction(event -> openSettingsPopUp());
        highscoreButton.setOnAction(event -> openHighscorePopUp());
    }

    // wird aufgerufen wenn man auf play game drückt
    private void openModeSelectionScreen() {
        try {
            // lädt den mode-selection-screen
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screens/mode-selection-screen.fxml"));
            Parent root = fxmlLoader.load();

            // aktuelles fenster wird dynamisch verändert. kein neues wird geöffnet
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // wird aufgerufen wenn man auf rules drückt
    private void openRulesScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screens/rules-screen.fxml"));
            Parent root = fxmlLoader.load();

            // aktuelles fenster wird dynamisch verändert. kein neues wird geöffnet
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // wird aufgerufen wenn man auf settings drückt
    private void openSettingsPopUp() {
        try {
            Stage settings = new Stage();

            // fehlt noch. die idee ist ein pop up. muss ich mir noch anschauen

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // wird aufgerufen wenn man auf highscore drückt
    private void openHighscorePopUp() {
        try {
            //öffnet ein neues Fenster (== Stage) für highscore.fxml:
            Stage highscoreStage = new Stage();

            //Erstellt einen FXML Loader und lädt die Inhalte aus highscore.fxml:
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/popups/highscores.fxml"));

            //Erstellt eine neue Szene (== Container für Inhalt der Stage) basierend auf der dazugehörige Root Node (mit FXML Loader geladen):
            Scene highscoreScene = new Scene(fxmlLoader.load());
            //Entfernt weißen default BG für runde Ecken:
            highscoreScene.setFill(Color.TRANSPARENT);

            //Verbindet Stage mit Scene:
            highscoreStage.setScene(highscoreScene);

            //Blockiert Interaktionen mit dem Parent Window (== Main Menu):
            highscoreStage.initModality(Modality.WINDOW_MODAL);

            //Verbindet das Highscore Fenster mit dem Parent Window:
            highscoreStage.initOwner(rootPane.getScene().getWindow());

            //Macht das Highscore Fenster transparent damit Runde Ecken funktionieren und entfernt title bar:
            highscoreStage.initStyle(StageStyle.TRANSPARENT);

            //Macht das Fenster am Bildschirm sichtbar:
            highscoreStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}