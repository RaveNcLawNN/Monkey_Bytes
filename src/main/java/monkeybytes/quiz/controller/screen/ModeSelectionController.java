package monkeybytes.quiz.controller.screen;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

// mode-selection-screen.fxml
public class ModeSelectionController {
    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button singleButton;

    @FXML
    private Button multiButton;

    @FXML
    public void initialize() {
        // event handler
        singleButton.setOnAction(event -> openProfileSingleScreen());
        multiButton.setOnAction(event -> openProfileMultiScreen());
    }

    private void openProfileSingleScreen() {
        try {
            // lädt den profile single screen (noch nicht fertig - leitet jetzt mal direkt zum quiz single screen weiter)
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screen/profile-single-screen.fxml"));
            Parent root = fxmlLoader.load();

            // aktuelles fenster wird dynamisch verändert. kein neues wird geöffnet
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openProfileMultiScreen() {
        try {
            // lädt den profile multi screen (noch nicht fertig - also jetzt mal leer)
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/monkeybytes/quiz/screen/profile-multi-screen.fxml"));
            Parent root = fxmlLoader.load();

            // aktuelles fenster wird dynamisch verändert. kein neues wird geöffnet
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
