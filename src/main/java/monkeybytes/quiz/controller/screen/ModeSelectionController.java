package monkeybytes.quiz.controller.screen;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ModeSelectionController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button singleButton;
    @FXML
    private Button multiButton;

    /**
     * Initialisiert die Modusauswahl-Oberfläche.
     * Verknüpft die Schaltflächen mit den entsprechenden Methoden.
     */
    @FXML
    public void initialize() {
        singleButton.setOnAction(event -> openProfileSingleScreen());
        multiButton.setOnAction(event -> openProfileMultiScreen());
    }

    /**
     * Öffnet den Bildschirm zur Profilauswahl für den Einzelspielermodus.
     */
    private void openProfileSingleScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screen/profile-single-screen.fxml"));
            Parent root = fxmlLoader.load();

            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Öffnet den Bildschirm zur Profilauswahl für den Mehrspielermodus.
     */
    private void openProfileMultiScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screen/profile-multi-screen.fxml"));
            Parent root = fxmlLoader.load();

            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
