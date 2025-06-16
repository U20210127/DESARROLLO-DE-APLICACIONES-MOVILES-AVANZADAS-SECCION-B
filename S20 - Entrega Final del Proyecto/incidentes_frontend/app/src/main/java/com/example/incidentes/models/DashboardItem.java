package com.example.incidentes.models;

import androidx.annotation.Nullable;
public class DashboardItem {

    public static final int TIPO_USUARIO = 0;
    public static final int TIPO_INCIDENTE = 1;
    public static final int TIPO_SEPARADOR = 2;


    private final int tipo;
    @Nullable
    private final Object objeto;

    /**
     * Constructor para crear un item del dashboard.
     *
     * @param tipo Tipo del ítem (usuario, incidente o separador)
     * @param objeto Objeto asociado (Usuario, Incidente o null para separador)
     */
    public DashboardItem(int tipo, @Nullable Object objeto) {
        this.tipo = tipo;
        this.objeto = objeto;
    }

    public int getTipo() {
        return tipo;
    }

    @Nullable
    public Object getObjeto() {
        return objeto;
    }
}

