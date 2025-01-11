package monkeybytes.quiz.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ProfileSingleController {
    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button testButton;

    @FXML
    public void initialize() {
        // event handler
        testButton.setOnAction(event -> openSelectionScreen());
    }

    private void openSelectionScreen() {
        try {
            // lädt den selection difficulty topic screen (noch nicht fertig - leitet jetzt mal direkt zum quiz single screen weiter)
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/quiz-single-screen.fxml"));
            Parent root = fxmlLoader.load();

            // aktuelles fenster wird dynamisch verändert. kein neues wird geöffnet
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}