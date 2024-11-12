package org.example.NodeMap;

import Visual.Login;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainProof extends Application {

    @Override
    public void start(Stage primaryStage) {
        Login login = new Login();
       // Scene scene = new Scene(login.getView(), 800, 600);
        //primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
