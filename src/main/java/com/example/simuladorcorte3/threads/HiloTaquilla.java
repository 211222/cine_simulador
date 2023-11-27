package com.example.simuladorcorte3.threads;

import com.example.simuladorcorte3.models.MonitorCine;

import java.util.Observable;

public class HiloTaquilla extends Observable implements Runnable {
    private MonitorCine monitorCine;
    private boolean running;
    private int idCliente; // Nuevo campo para almacenar el id del cliente

    public HiloTaquilla(MonitorCine monitorCine, int idCliente) {
        this.monitorCine = monitorCine;
        this.running = true;
        this.idCliente = idCliente;
    }

    public void detener() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep((long) (Math.random() * 1000)); // Simular tiempo entre llegada de clientes
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt(); // Restablecer la bandera de interrupción
            }

            // Compra de boleto y asignación de asiento
            int asientoAsignado = monitorCine.comprarBoleto(idCliente);

            if (asientoAsignado != -1) {
                notificarObservadores("Taquilla asignando asiento: " + asientoAsignado);

                // Ingresar a la sala
                monitorCine.ingresoSala();
                notificarObservadores("Cliente " + idCliente + " ingresó a la sala.");

                // Pausa opcional entre llegada de clientes
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt(); // Restablecer la bandera de interrupción
                }
            }
        }
    }

    private void notificarObservadores(Object arg) {
        setChanged();
        notifyObservers(arg);
    }
}
