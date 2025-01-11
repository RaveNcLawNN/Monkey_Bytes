package monkeybytes.quiz.controller.screen;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

// rules-screen.fxml
public class RulesController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button understoodButton;

    @FXML
    public void initialize() {
        // event handler
        understoodButton.setOnAction(event -> returnToMainMenu());
    }

    // wird aufgerufen wenn man auf understood klickt
    private void returnToMainMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screen/main-menu.fxml"));
            Parent mainMenuRoot = fxmlLoader.load();

            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.getScene().setRoot(mainMenuRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
