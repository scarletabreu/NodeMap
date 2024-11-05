package org.example.NodeMap;

import Visual.Login;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainProof extends Application {

    @Override
    public void start(Stage primaryStage) {
        Login loginView = new Login();
        //Scene scene = new Scene(loginView.getView(), 400, 200); // Tamaño de la ventana

        primaryStage.setTitle("GPS - Inicio de Sesión");
       // primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
