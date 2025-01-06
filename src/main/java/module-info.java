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

    opens monkeybytes.quiz to javafx.fxml;
    exports monkeybytes.quiz.game;
    exports monkeybytes.quiz;
    exports monkeybytes.quiz.controller;
    opens monkeybytes.quiz.controller to javafx.fxml;
    opens monkeybytes.quiz.game to com.google.gson;
}