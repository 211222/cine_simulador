package com.example.simuladorcorte3.controller;

import com.example.simuladorcorte3.models.MonitorCine;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements  Initializable{
    @FXML
    private Label welcomeText;


    @FXML
    private ImageView cliente;

    private TranslateTransition translateTransition;
    private MonitorCine monitorCine;


    public void setMonitorCine(MonitorCine monitorCine) {
        this.monitorCine = monitorCine;
    }
//    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        translateTransition = new TranslateTransition (Duration.seconds(8), cliente);
        translateTransition.setFromX(8);
        translateTransition.setFromY(0);


    }
    public void iniciarAnimacionCliente() {
        monitorCine.salaLlenaProperty().addListener((observable,oldValue,newValue ) -> {
            if(newValue){
                translateTransition.play();

            }

        });
    }

    public void actualizarInterfaz() {
    }
}