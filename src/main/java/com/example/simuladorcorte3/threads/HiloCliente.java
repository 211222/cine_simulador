package com.example.simuladorcorte3.threads;

import com.example.simuladorcorte3.models.MonitorCine;
import javafx.scene.image.ImageView;

import java.util.Observable;

public class HiloCliente extends Observable implements Runnable {
    private final MonitorCine monitorCine;
    private final int id;
    private final ImageView imageView;

    private boolean llego = false;


    public HiloCliente(MonitorCine monitorCine, int id, ImageView imageView) {
        this.monitorCine = monitorCine;
        this.id = id;
        this.imageView = imageView;
    }

    @Override
    public void run() {
        // Lógica del cliente
        monitorCine.ingresoSala(id);
        setChanged();
        notifyObservers(id);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getId() {
        return id;
    }

    public void getAsiento() {
        setChanged();
        notifyObservers(id);
    }

    public boolean haLlegado() {
        return llego;
    }

    public void setLlego(boolean llego) {
        this.llego = llego;
        setChanged();
        notifyObservers();
    }
}
