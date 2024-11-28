module org.example.mapapp {
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
    requires java.sql;
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires google.cloud.firestore;

    opens org.example.NodeMap to javafx.fxml;
    exports org.example.NodeMap;
    exports Visual;
    opens Visual to javafx.fxml;
}