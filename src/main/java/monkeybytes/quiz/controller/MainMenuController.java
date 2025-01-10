package monkeybytes.quiz.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
        playGameButton.setOnAction(event -> openQuizSingleScreen());
        rulesButton.setOnAction(event -> openRulesScreen());
        settingsButton.setOnAction(event -> openSettingsPopUp());
        highscoreButton.setOnAction(event -> openHighscorePopUp());
    }

    // wird aufgerufen wenn man auf play game drückt
    private void openQuizSingleScreen() {
        try {
            // lädt die quiz single screen (jetzt mal zum testen, weil noch keine selection screens fertig sind)
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/quiz-single-screen.fxml"));
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/rules-screen.fxml"));
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