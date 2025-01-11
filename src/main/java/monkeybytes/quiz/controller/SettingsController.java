package monkeybytes.quiz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class SettingsController {
    @FXML
    private Button exitButton;

    @FXML
    private Slider volumeSlider;

    @FXML
    private CheckBox muteBox;

    @FXML
    public void initialize() {
        // event handler
        exitButton.setOnAction(event -> closeSettings());
        muteBox.setOnAction(event -> handleMute());
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> handleVolumeChange(newValue.doubleValue()));
    }

    // schließt das settings-pop up und kehrt zurück zum hauptmenü
    private void closeSettings() {
        Stage currentStage = (Stage) exitButton.getScene().getWindow();
        currentStage.close();
    }

    // handhabung der mute-funktion (noch nicht implementiert)
    private void handleMute() {
        if (muteBox.isSelected()) {

            // background music wird gemutet (noch nicht fertig)

        } else {

            // background music wird unmutet (noch nicht fertig)

        }
    }

    private void handleVolumeChange(double volume) {

        // lautstärke der background music wird angepasst (noch nicht fertig)

    }
}
