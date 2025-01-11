package monkeybytes.quiz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/*
Dies ist die eigentliche Main, also mit Einbindung in die
 */

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/monkeybytes/quiz/screen/main-menu.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/monkeybytes/quiz/popups/highscores-popup.fxml"));

        Scene menu = new Scene(fxmlLoader.load(), 800, 600);

        // fenster icon hinzugefügt
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.jpg")));


        stage.setTitle("Monkey Bytes");
        stage.setResizable(false);
        stage.setScene(menu);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}