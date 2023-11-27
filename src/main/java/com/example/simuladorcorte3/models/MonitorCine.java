package com.example.simuladorcorte3.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;



public class MonitorCine {
    private int capacidadSala = 70;
    private int asientosOcupados;
    private BooleanProperty salaLlenaProperty;
    private boolean bloqueoPuerta;

    public MonitorCine(int capacidadSala) {
        this.capacidadSala = capacidadSala;
        this.asientosOcupados = 0;
        this.salaLlenaProperty = new SimpleBooleanProperty(false);
        this.bloqueoPuerta = false;
    }

    public synchronized int comprarBoleto(int idCliente) {
        while (salaLlenaProperty.get() || asientosOcupados >= capacidadSala) {
            // Esperar si la sala está llena o no hay asientos disponibles
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return -1;
            }
        }

        System.out.println("Cliente " + idCliente + " comprando boleto.");
        int asientoAsignado = asientosOcupados + 1;
        System.out.println("Asiento asignado: " + asientoAsignado);
        asientosOcupados++;

        if (asientosOcupados >= capacidadSala) {
            salaLlenaProperty.set(true);
            System.out.println("Sala llena. Informando a los clientes para futuras funciones.");
        }

        // Notificar a los demás hilos que están esperando
        notifyAll();

        return asientoAsignado;
    }

    public synchronized void ingresoSala() {
        if (!salaLlenaProperty.get()) {
            System.out.println("Ingresando a la sala de cine.");

        } else {
            System.out.println("La sala está llena, no se puede ingresar.");
        }
    }

    public synchronized void inicioFuncion() {
        if (salaLlenaProperty.get()) {
            System.out.println("Comienza la función. Disfrute de la película.");
        } else {
            System.out.println("La sala no está llena. La función no puede comenzar.");
        }
    }

    public synchronized void seguridadEntrada() {
        if (!salaLlenaProperty.get()) {
            System.out.println("Verificando seguridad en la entrada para el cliente.");
            // Lógica de verificación de seguridad simulada
            System.out.println("Verificación de seguridad exitosa. Ingresando a la sala de cine.");
        } else {
            System.out.println("La sala está llena, no se puede ingresar.");
        }
    }

    public synchronized void seguridadSalida() {
        if (bloqueoPuerta) {
            System.out.println("Verificando seguridad en la salida.");
            // Lógica de verificación de seguridad simulada
            System.out.println("Verificación de seguridad exitosa. Saliendo de la sala de cine.");
            bloqueoPuerta = false;
        } else {
            System.out.println("La puerta ya está desbloqueada.");
        }
    }

    public synchronized boolean isSalaLlena() {
        return salaLlenaProperty.get();
    }

    public synchronized boolean isBloqueoPuerta() {
        return bloqueoPuerta;
    }

    public synchronized void desbloquearPuerta() {
        bloqueoPuerta = false;
    }

    public synchronized void notificarSalaLlena() {
        salaLlenaProperty.set(true);
    }

    // Métodos getter para la propiedad observable
    public BooleanProperty salaLlenaProperty() {
        return salaLlenaProperty;
    }
}
