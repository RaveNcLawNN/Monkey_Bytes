package monkeybytes.quiz.controller.popup;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;

// pause-popup.fxml
public class PauseController {
    private Stage mainStage;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button settingsButton;

    @FXML
    private Button backToMainMenuButton;

    @FXML
    private Button backToGameButton;

    @FXML
    public void initialize() {
        // event handler
        settingsButton.setOnAction(event -> openSettings());
        backToMainMenuButton.setOnAction(event -> backToMainMenu());
        backToGameButton.setOnAction(event -> backToGame());
    }

    private void openSettings() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/popup/settings-popup.fxml"));
            Parent settingsRoot = fxmlLoader.load();

            rootPane.getScene().setRoot(settingsRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backToMainMenu() {
        if (mainStage == null) {
            System.err.println("Main stage is not set!");
            return;
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screen/main-menu.fxml"));
            Parent mainMenu = fxmlLoader.load();

            Stage popupStage = (Stage) rootPane.getScene().getWindow();
            popupStage.close();

            mainStage.getScene().setRoot(mainMenu);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backToGame() {
        try {
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

}
