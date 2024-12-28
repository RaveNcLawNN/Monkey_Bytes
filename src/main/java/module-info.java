module at.ac.fhcampuswien.monkey_bytes {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens at.ac.fhcampuswien.monkey_bytes to javafx.fxml;
    exports at.ac.fhcampuswien.monkey_bytes.game;
    exports at.ac.fhcampuswien.monkey_bytes.javafx;
    opens at.ac.fhcampuswien.monkey_bytes.javafx to javafx.fxml;
}