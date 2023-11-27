package com.example.simuladorcorte3.threads;

import com.example.simuladorcorte3.models.MonitorCine;

import java.util.Observable;

public class HiloCliente extends Observable implements Runnable {
    private static final long DELAY = 4000; // Ajusta la duración de la pausa según sea necesario

    private int id;
    private String nombre;
    private MonitorCine monitorCine;

    public HiloCliente(int id, String nombre, MonitorCine monitorCine) {
        this.id = id;
        this.nombre = nombre;
        this.monitorCine = monitorCine;
    }

    @Override
    public void run() {
        while (true) {
            int asientoAsignado = monitorCine.comprarBoleto(id);

            if (asientoAsignado != -1) {
                notificarObservadores("Cliente " + id + " compró boleto. Asiento asignado: " + asientoAsignado);

                monitorCine.ingresoSala();
                notificarObservadores("Cliente " + id + " ingresó a la sala.");

                // Agregar una pausa opcional entre llegada de clientes
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt(); // Restablecer la bandera de interrupción
                }
            } else {
                // Si no se pudo comprar el boleto, salir del bucle
                break;
            }
        }
    }

    private void notificarObservadores(Object arg) {
        setChanged();
        notifyObservers(arg);
    }
}
