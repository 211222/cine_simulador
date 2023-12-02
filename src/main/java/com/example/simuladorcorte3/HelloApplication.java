package com.example.simuladorcorte3;

import com.example.simuladorcorte3.controller.HelloController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar la interfaz de usuario desde el archivo FXML
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/simuladorcorte3/hello-view.fxml"));


        Scene scene = new Scene(root, 800, 600);


        primaryStage.setTitle("Simulador Cine");
        primaryStage.setScene(scene);


        HelloController helloController = new HelloController();
        helloController.setStage(primaryStage);


        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
