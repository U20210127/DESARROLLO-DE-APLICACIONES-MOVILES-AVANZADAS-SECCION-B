package com.example.incidentes;

public class RespuestaPermiso {

    private boolean tienePermiso;  // Indica si tiene o no el permiso
    private String mensaje;        // Mensaje adicional (si existe)

    public boolean isTienePermiso() {
        return tienePermiso;
    }

    public void setTienePermiso(boolean tienePermiso) {
        this.tienePermiso = tienePermiso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
