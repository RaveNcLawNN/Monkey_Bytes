module monkeybytes.quiz {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.google.gson;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.apache.commons.text;
    requires java.desktop;

    opens monkeybytes.quiz to javafx.fxml;
    exports monkeybytes.quiz.game;
    exports monkeybytes.quiz;
    opens monkeybytes.quiz.game to com.google.gson;
    exports monkeybytes.quiz.controller.screen;
    opens monkeybytes.quiz.controller.screen to javafx.fxml;
    exports monkeybytes.quiz.controller.popup;
    opens monkeybytes.quiz.controller.popup to javafx.fxml;
}