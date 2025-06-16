package com.example.incidentes.models;

import java.util.List;

public class RespuestaDashboard {
    private boolean success;
    private Resumen resumen;
    private List<Incidente> incidentes;

    public boolean isSuccess() {
        return success;
    }

    public Resumen getResumen() {
        return resumen;
    }

    public List<Incidente> getIncidentes() {
        return incidentes;
    }
}

