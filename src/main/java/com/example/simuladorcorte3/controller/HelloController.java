package com.example.simuladorcorte3.controller;

import com.example.simuladorcorte3.models.MonitorCine;
import com.example.simuladorcorte3.threads.HiloCliente;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;



public class HelloController implements Initializable {

    @FXML
    private ImageView taquilla;

    @FXML
    private Pane containerPane;

    private MonitorCine monitorCine;
    private List<HiloCliente> clientes = new ArrayList<>();
    private ImageView[][] asientos = new ImageView[5][6];
    private int cantidadClientes = 60;
    private int clientesQueHanLlegado = 0;
    private int totalClientes = 30;

    private final ReentrantLock lock = new ReentrantLock();

    private boolean simulacionFinalizada = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monitorCine = new MonitorCine(60);

        Image imageCliente = new Image(getClass().getResourceAsStream("/com/example/simuladorcorte3/images/cliente.png"));
        Image imageAsiento = new Image(getClass().getResourceAsStream("/com/example/simuladorcorte3/images/asiento.png"));

        inicializarAsientos(imageAsiento);
        iniciarSimulacion(imageCliente);
    }

    private void inicializarAsientos(Image imageAsiento) {
        for (int fila = 0; fila < 5; fila++) {
            for (int columna = 0; columna < 6; columna++) {
                ImageView asientoImageView = new ImageView(imageAsiento);
                asientoImageView.setFitHeight(120.0);
                asientoImageView.setFitWidth(90.0);

                double x = 21.0 + columna * 60.0;
                double y = 25.0 + fila * 90.0;
                asientoImageView.setLayoutX(x);
                asientoImageView.setLayoutY(y);

                containerPane.getChildren().add(asientoImageView);
                asientos[fila][columna] = asientoImageView;
            }
        }
    }

    private void iniciarSimulacion(Image imageCliente) {
        Timeline timeline = new Timeline();

        for (int i = 1; i <= cantidadClientes; i++) {
            ImageView clienteImageView = new ImageView(imageCliente);
            clienteImageView.setFitHeight(120.0);
            clienteImageView.setFitWidth(90.0);
            clienteImageView.setLayoutX(51.0);
            clienteImageView.setLayoutY(495.0);

            containerPane.getChildren().add(clienteImageView);

            HiloCliente hiloCliente = new HiloCliente(monitorCine, i, clienteImageView);
            clientes.add(hiloCliente);

            KeyFrame keyFrame = new KeyFrame(
                    Duration.seconds(5 * i),
                    event -> simularLlegadaCliente(hiloCliente)
            );

            timeline.getKeyFrames().add(keyFrame);
        }

        // Agrega un evento al final de la simulación de llegada
        timeline.setOnFinished(event -> iniciarEsperaDespuesDeLlegada());

        timeline.play();
    }

    private void iniciarEsperaDespuesDeLlegada() {
        // Verificamos si todos los clientes han llegado a sus asientos
        if (clientesQueHanLlegado == totalClientes) {
            // Agregamos una espera de 3 segundos antes de iniciar la simulación de salida
            Timeline timelineEspera = new Timeline(new KeyFrame(
                    Duration.seconds(3),
                    event -> Platform.runLater(() -> iniciarSimulacionSalida())
            ));
            timelineEspera.play();
        }
    }

    private void simularLlegadaCliente(HiloCliente cliente) {
        cliente.getImageView().setTranslateX(0);

        double taquillaX = taquilla.getLayoutX() - cliente.getImageView().getFitWidth();

        TranslateTransition transitionInicial = new TranslateTransition(Duration.seconds(5), cliente.getImageView());
        transitionInicial.setToX(taquillaX);

        transitionInicial.setOnFinished(event -> {
            System.out.println("Cliente " + cliente.getId() + " ha llegado a la taquilla.");

            int asientoAsignado = monitorCine.comprarBoleto(cliente.getId());

            if (asientoAsignado != -1) {
                System.out.println("Cliente " + cliente.getId() + " ha comprado el boleto. Asiento asignado: " + asientoAsignado);
                seguridadEntrada(cliente, asientoAsignado);
            } else {
                System.out.println("Cliente " + cliente.getId() + " no ha podido comprar el boleto.");
                if (!simulacionFinalizada) {
                    simularLlegadaCliente(cliente);
                }
            }
        });

        transitionInicial.play();
    }

    private void seguridadEntrada(HiloCliente cliente, int asientoAsignado) {
        if (!monitorCine.isSalaLlena()) {
            System.out.println("Verificando seguridad en la entrada para el cliente.");
            System.out.println("Verificación de seguridad exitosa. Ingresando a la sala de cine.");

            moverseAlGuardia(cliente, asientoAsignado);
        } else {
            System.out.println("La sala está llena, esperando a que haya espacio disponible.");
            Platform.runLater(() -> {
                iniciarEsperaDespuesDeLlegada(); // Reinicia la espera después de la llegada
            });
        }
    }

    private void moverseAlGuardia(HiloCliente cliente, int asientoAsignado) {
        double guardiaX = 372.0;
        double guardiaY = -110;

        cliente.getImageView().setTranslateX(guardiaX);

        TranslateTransition transitionGuardia = new TranslateTransition(Duration.seconds(2), cliente.getImageView());
        transitionGuardia.setToY(guardiaY);

        transitionGuardia.setOnFinished(event -> {
            System.out.println("Cliente " + cliente.getId() + " se ha movido cerca del guardia.");
            verificarGuardia(cliente, asientoAsignado);
        });

        transitionGuardia.play();
    }

    private void verificarGuardia(HiloCliente cliente, int asientoAsignado) {
        System.out.println("Guardia verificando a Cliente " + cliente.getId() + " antes de ingresar.");



        ubicarClienteEnAsiento(cliente, asientoAsignado);
    }

    private void ubicarClienteEnAsiento(HiloCliente cliente, int asientoAsignado) {
        lock.lock();
        try {
            int asiento = asientoAsignado;

            // Verifica si el asiento es válido
            if (asiento <= 0 || asiento > monitorCine.getMaxClientes()) {
                System.out.println("Número de asiento no válido: " + asiento);
                // Informa al cliente que no hay asientos disponibles y ponlo en la cola de espera
                monitorCine.agregarClienteColaEspera(cliente.getId());
                return;
            }

            ImageView asientoImageView = obtenerAsientoImageView(asiento);

            if (asientoImageView != null) {
                TranslateTransition transitionAsiento = new TranslateTransition(Duration.seconds(2), cliente.getImageView());
                transitionAsiento.setToX(asientoImageView.getLayoutX() - 51.0);
                transitionAsiento.setToY(asientoImageView.getLayoutY() - 495.0);

                transitionAsiento.setOnFinished(event -> {
                    System.out.println("Cliente " + cliente.getId() + " ha llegado a su asiento.");



                    clienteHaLlegado();
                });

                transitionAsiento.play();
            } else {
                System.out.println("El ImageView del asiento es nulo o no válido para el cliente " + cliente.getId());

                monitorCine.agregarClienteColaEspera(cliente.getId());
            }
        } finally {
            lock.unlock();
        }
    }


    private ImageView obtenerAsientoImageView(int numeroAsiento) {
        if (numeroAsiento <= 0) {
            System.out.println("Número de asiento no válido: " + numeroAsiento);
            return null;
        }

        int fila = (numeroAsiento - 1) / 6;
        int columna = (numeroAsiento - 1) % 6;

        if (fila >= 0 && fila < 5 && columna >= 0 && columna < 6) {
            return asientos[fila][columna];
        } else {
            System.out.println("Índices de fila o columna no válidos: Fila=" + fila + ", Columna=" + columna);
            return null;
        }
    }

    private void iniciarSimulacionSalida() {
        if (!clientes.isEmpty()) {
            HiloCliente primerCliente = clientes.get(0);
            salirDeLaSala(primerCliente);
        } else {

            simulacionFinalizada = true;
        }
    }

    private void salirDeLaSala(HiloCliente cliente) {
        TranslateTransition transitionSalida = new TranslateTransition(Duration.seconds(5), cliente.getImageView());
        transitionSalida.setToX(-300.0); // Mover fuera de la pantalla

        transitionSalida.setOnFinished(e -> {
            System.out.println("Cliente " + cliente.getId() + " ha salido de la sala.");
            monitorCine.salidaSala(cliente.getId());

            Platform.runLater(() -> {
                clientes.remove(cliente);

                // Verifica si hay más clientes en la lista
                if (!clientes.isEmpty()) {
                    // Obtiene el siguiente cliente y llama a salirDeLaSala para ese cliente
                    HiloCliente siguienteCliente = clientes.get(0);
                    salirDeLaSala(siguienteCliente);
                } else {

                }
            });
        });

        transitionSalida.play();
    }

    private synchronized void clienteHaLlegado() {
        clientesQueHanLlegado++;

        // Verifica si todos los clientes han llegado a sus asientos
        if (clientesQueHanLlegado == totalClientes) {
            // Si todos los clientes han llegado, inicia la simulación de salida
            Platform.runLater(this::iniciarSimulacionSalida);
        }
    }

    public void setStage(Stage primaryStage) {
    }

    public void salaLlenaNotificada() {
    }

    public void salaVaciaNotificada() {
    }
}
