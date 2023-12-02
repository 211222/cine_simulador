package com.example.simuladorcorte3.threads;


import com.example.simuladorcorte3.models.MonitorCine;

public class HiloSeguridad implements Runnable {
    private final MonitorCine monitorCine;

    public HiloSeguridad(MonitorCine monitorCine) {
        this.monitorCine = monitorCine;
    }

    @Override
    public void run() {
        while (true) {
            monitorCine.seguridad();
        }
    }
}
