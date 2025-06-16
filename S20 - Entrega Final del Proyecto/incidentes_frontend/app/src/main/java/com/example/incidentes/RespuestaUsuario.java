package com.example.incidentes;

public class RespuestaUsuario {

    private boolean success;
    private String message;
    private int id; // Opcional: solo si el backend lo devuelve

    // Getter para success
    public boolean isSuccess() {
        return success;
    }

    // Getter para message
    public String getMessage() {
        return message;
    }

    // Getter para id
    public int getId() {
        return id;
    }

    // Setter para success (opcional, si lo necesitas)
    public void setSuccess(boolean success) {
        this.success = success;
    }

    // Setter para message (opcional, si lo necesitas)
    public void setMessage(String message) {
        this.message = message;
    }

    // Setter para id (opcional, si lo necesitas)
    public void setId(int id) {
        this.id = id;
    }
}
