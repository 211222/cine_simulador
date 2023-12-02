package com.example.simuladorcorte3.threads;

import com.example.simuladorcorte3.models.MonitorCine;

public class HiloTaquilla implements Runnable {
    private final MonitorCine monitorCine;

    public HiloTaquilla(MonitorCine monitorCine) {
        this.monitorCine = monitorCine;
    }


    @Override
    public void run() {

        while (true) {
            int idCliente = monitorCine.obtenerClienteColaEspera();
            if (idCliente != -1) {

                if (monitorCine.tieneAsientoAsignado(idCliente)) {
                    System.out.println("Cliente " + idCliente + " ya tiene un asiento asignado. No se asignará otro asiento.");
                    continue;
                }

                int asientoAsignado = monitorCine.comprarBoleto(idCliente);
                if (asientoAsignado != -1) {
                    monitorCine.ingresoSala(idCliente);
                } else {


                    System.out.println("Cliente " + idCliente + " en espera. No hay asientos disponibles.");
                    // Agrega el cliente a la cola de espera para futuros intentos
                    monitorCine.agregarClienteColaEspera(idCliente);
                }
            }
        }
    }



}
