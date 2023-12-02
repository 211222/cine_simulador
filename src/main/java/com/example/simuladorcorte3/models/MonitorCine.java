package com.example.simuladorcorte3.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.example.simuladorcorte3.controller.HelloController;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class MonitorCine {
    private static final int MAX_CLIENTES = 60;

    public int getMaxClientes() {
        return MAX_CLIENTES;
    }

    private int capacidadSala = 30;
    private BooleanProperty salaLlenaProperty = new SimpleBooleanProperty(false);
    private BooleanProperty taquillaOcupadaProperty = new SimpleBooleanProperty(false);
    private HelloController helloController;

    private Set<Integer> clientesEnSala = new HashSet<>();
    private Queue<Integer> colaEsperaClientes = new LinkedList<>();

    private int clientesQueHanSalido = 0;

    public MonitorCine(int capacidadSala) {
        this.capacidadSala = capacidadSala;
    }

    public void setHelloController(HelloController helloController) {
        this.helloController = helloController;
    }

    public synchronized int comprarBoleto(int idCliente) {
        int intentos = 0;

        while ((clientesEnSala.size() >= capacidadSala || taquillaOcupadaProperty.get()) && intentos < 3) {
            try {
                System.out.println("Cliente " + idCliente + " esperando para comprar boleto.");
                wait(2000); // Esperar 2 segundos antes de intentar nuevamente
                intentos++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (intentos == 3) {
            System.out.println("Cliente " + idCliente + " no pudo comprar boleto después de 3 intentos. Saliendo.");
            return -1;
        }

        // Verifica si la sala está llena
        if (clientesEnSala.size() >= capacidadSala) {
            salaLlenaProperty.set(true);
            System.out.println("Sala llena. Informando a los clientes para futuras funciones.");

            // Liberar asientos y notificar
            while (!clientesEnSala.isEmpty()) {
                int clienteEnSala = clientesEnSala.iterator().next();
                clientesEnSala.remove(clienteEnSala);
                notificarSalaVacia();
            }

            salaLlenaProperty.set(false);
            System.out.println("Sala ahora está vacía. Continuando con la venta de boletos.");
        }

        // Verifica si la taquilla está ocupada
        if (taquillaOcupadaProperty.get()) {
            System.out.println("Esperando a que la taquilla esté libre.");
            while (taquillaOcupadaProperty.get()) {
                try {
                    wait(); // Esperar hasta que la taquilla esté libre
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Taquilla vendiendo boleto.");
        taquillaOcupadaProperty.set(true);

        int asientoAsignado = obtenerAsientoDisponible();

        if (asientoAsignado != -1) {
            System.out.println("Asiento asignado: " + asientoAsignado);
            clientesEnSala.add(asientoAsignado);
        } else {
            System.out.println("No hay asientos disponibles. Cliente " + idCliente + " no pudo comprar el boleto.");
            agregarClienteColaEspera(idCliente);
        }

        taquillaOcupadaProperty.set(false);
        notifyAll();
        return asientoAsignado;
    }

    private int obtenerAsientoDisponible() {
        for (int i = 1; i <= capacidadSala; i++) {
            if (!clientesEnSala.contains(i)) {
                return i;
            }
        }


        return -1; // Devuelve -1 si no hay asientos disponibles
    }

    public synchronized void ingresoSala(int idCliente) {
        if (!salaLlenaProperty.get()) {
            System.out.println("Cliente " + idCliente + " ingresando a la sala de cine.");
        } else {
            System.out.println("La sala está llena, no se puede ingresar.");
        }
    }

    public synchronized void agregarClienteColaEspera(int idCliente) {
        colaEsperaClientes.offer(idCliente);
        System.out.println("Cliente " + idCliente + " ha sido añadido a la cola de espera.");
    }

    public synchronized int obtenerClienteColaEspera() {
        return colaEsperaClientes.poll();
    }

    public boolean isSalaLlena() {
        return salaLlenaProperty.get();
    }

    public synchronized void seguridad() {
        while ((taquillaOcupadaProperty.get() || !salaLlenaProperty.get()) && clientesEnSala.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (!salaLlenaProperty.get()) {
            System.out.println("Verificando seguridad para el cliente.");
            System.out.println("Verificación de seguridad exitosa. Ingresando a la sala de cine.");
        } else {
            System.out.println("La sala está llena o no se puede ingresar.");
        }

        if (taquillaOcupadaProperty.get()) {
            System.out.println("Esperando a que la taquilla esté libre.");
        } else {
            System.out.println("Esperando a que la sala esté llena.");
        }

        taquillaOcupadaProperty.set(false);
        notifyAll();
    }

    public synchronized boolean isTaquillaOcupada() {
        return taquillaOcupadaProperty.get();
    }

    public synchronized void notificarSalaLlena() {
        salaLlenaProperty.set(true);
        helloController.salaLlenaNotificada();
        notifyAll();
    }

    public synchronized boolean isSalaVacia() {
        return clientesEnSala.isEmpty();
    }

    public synchronized void notificarSalaVacia() {
        System.out.println("Notificando que la sala está vacía.");
        helloController.salaVaciaNotificada();
        notifyAll();
    }

    public synchronized void salidaSala(int clienteId) {
        System.out.println("Cliente " + clienteId + " ha salido de la sala.");
        clientesEnSala.remove(clienteId); // Remover al cliente de la sala

        clientesQueHanSalido++;
        if (clientesQueHanSalido == MAX_CLIENTES) {
            notificarFinSimulacion();
        }

        if (clientesEnSala.isEmpty()) {
            salaLlenaProperty.set(false); // Actualizar el estado de la sala solo si está vacía

            // Si hay clientes en espera, asigna asientos a los clientes en espera
            while (!colaEsperaClientes.isEmpty()) {
                int clienteEnEspera = colaEsperaClientes.poll();
                int asientoAsignado = comprarBoleto(clienteEnEspera);
                if (asientoAsignado != -1) {
                    ingresoSala(clienteEnEspera);
                }
            }
        }


        notifyAll();
    }

    private synchronized void notificarFinSimulacion() {
        System.out.println("Todos los clientes han salido. Fin de la simulación.");

        while (!colaEsperaClientes.isEmpty()) {
            int clienteEnEspera = colaEsperaClientes.poll();
            int asientoAsignado = comprarBoleto(clienteEnEspera);
            if (asientoAsignado != -1) {
                ingresoSala(clienteEnEspera);
            }
        }
    }
    public boolean tieneAsientoAsignado(int idCliente) {
        synchronized (this) {
            return clientesEnSala.contains(idCliente);
        }
    }
}
