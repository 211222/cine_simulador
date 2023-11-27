package com.example.simuladorcorte3;

import com.example.simuladorcorte3.controller.HelloController;
import com.example.simuladorcorte3.models.MonitorCine;
import com.example.simuladorcorte3.threads.HiloCliente;
import com.example.simuladorcorte3.threads.HiloTaquilla;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Crear monitor de cine
        MonitorCine monitorCine = new MonitorCine(70);

        // Cargar la interfaz gráfica desde el archivo FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        AnchorPane root = loader.load();

        // Configurar el controlador y establecer el monitorCine
        HelloController helloController = loader.getController();
        if (helloController == null) {
            System.out.println("Error: El controlador es nulo.");
        } else {
            helloController.setMonitorCine(monitorCine);
            System.out.println("Controlador configurado correctamente.");
        }

        // Crear hilo de taquilla y clientes
        HiloTaquilla hiloTaquilla = new HiloTaquilla(monitorCine, 0);
        new Thread(hiloTaquilla).start();

        for (int i = 1; i <= 1000; i++) {
            HiloCliente hiloCliente = new HiloCliente(i, "Cliente" + i, monitorCine);
            new Thread(hiloCliente).start();
        }

        // Configurar y mostrar la ventana principal
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        // Iniciar la animación del cliente después de mostrar la interfaz
        helloController.iniciarAnimacionCliente();

        // Sincronizar la interfaz con la ejecución de la lógica usando Platform.runLater
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100); // ajusta según sea necesario
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    // Actualizaciones de la interfaz según sea necesario
                    helloController.actualizarInterfaz();
                });
            }
        }).start();
    }
}
