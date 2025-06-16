package com.example.incidentes.models;

public class Resumen {
    private int total;
    private int activos;
    private int resueltos;
    private int solucionados;
    private int aplazados;
    private int revisados;
    private int eliminados;

    // Getters
    public int getTotal() {
        return total;
    }

    public int getActivos() {
        return activos;
    }

    public int getResueltos() {
        return resueltos;
    }

    public int getSolucionados() {
        return solucionados;
    }

    public int getAplazados() {
        return aplazados;
    }

    public int getRevisados() {
        return revisados;
    }

    public int getEliminados() {
        return eliminados;
    }

    // Setters (si es necesario para modificar estos valores)
    public void setTotal(int total) {
        this.total = total;
    }

    public void setActivos(int activos) {
        this.activos = activos;
    }

    public void setResueltos(int resueltos) {
        this.resueltos = resueltos;
    }

    public void setSolucionados(int solucionados) {
        this.solucionados = solucionados;
    }

    public void setAplazados(int aplazados) {
        this.aplazados = aplazados;
    }

    public void setRevisados(int revisados) {
        this.revisados = revisados;
    }

    public void setEliminados(int eliminados) {
        this.eliminados = eliminados;
    }
}
