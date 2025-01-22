package monkeybytes.quiz.controller.screen;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.scene.paint.Color;
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

    /**
     * Initialisiert die Hauptmenü-Oberfläche.
     * Verknüpft die Schaltflächen mit den entsprechenden Methoden.
     */
    @FXML
    public void initialize() {
        playGameButton.setOnAction(event -> openModeSelectionScreen());
        rulesButton.setOnAction(event -> openRulesScreen());
        settingsButton.setOnAction(event -> openSettingsPopUp());
        highscoreButton.setOnAction(event -> openHighscorePopUp());
    }

    /**
     * Öffnet den Bildschirm zur Modusauswahl.
     */
    private void openModeSelectionScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screen/mode-selection-screen.fxml"));
            Parent root = fxmlLoader.load();

            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Öffnet den Bildschirm mit den Spielregeln.
     */
    private void openRulesScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screen/rules-screen.fxml"));
            Parent root = fxmlLoader.load();

            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Öffnet ein Pop-up für die Einstellungen.
     */
    private void openSettingsPopUp() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/popup/settings-popup.fxml"));
            Parent root = fxmlLoader.load();

            Stage settings = new Stage();
            settings.initStyle(StageStyle.UNDECORATED);
            settings.initStyle(StageStyle.TRANSPARENT);
            settings.initModality(Modality.APPLICATION_MODAL);
            settings.initOwner(rootPane.getScene().getWindow());
            settings.setWidth(250);
            settings.setHeight(300);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            settings.setScene(scene);
            settings.setOnCloseRequest(event -> event.consume());
            settings.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Öffnet ein Pop-up für die Highscores.
     */
    private void openHighscorePopUp() {
        try {
            Stage highscoreStage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/popup/highscores-popup.fxml"));

            Scene highscoreScene = new Scene(fxmlLoader.load());
            highscoreScene.setFill(Color.TRANSPARENT);
            highscoreStage.setScene(highscoreScene);
            highscoreStage.initModality(Modality.WINDOW_MODAL);
            highscoreStage.initOwner(rootPane.getScene().getWindow());
            highscoreStage.initStyle(StageStyle.TRANSPARENT);
            highscoreStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}