package monkeybytes.quiz.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/popups/settings-popup.fxml"));
            Parent root = fxmlLoader.load();

            Stage settings = new Stage();
            settings.initStyle(StageStyle.UNDECORATED); // entfernt die obere Leiste
            settings.initStyle(StageStyle.TRANSPARENT); // fensterrand transparent damit man die runden ecken sieht
            settings.initModality(Modality.APPLICATION_MODAL); // blockiert das hauptfenster
            settings.initOwner(rootPane.getScene().getWindow());

            // setzt die größe auf 250x300
            settings.setWidth(250);
            settings.setHeight(300);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT); // fensterrand transparent damit man die runden ecken sieht
            settings.setScene(scene);

            // verhindert das schließen anders als durch den exit button
            settings.setOnCloseRequest(event -> event.consume());

            // zeigt das fenster
            settings.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // wird aufgerufen wenn man auf settings drückt
    private void openHighscorePopUp() {
        try {
            Stage highscore = new Stage();

            // fehlt noch. same wie bei settings

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}