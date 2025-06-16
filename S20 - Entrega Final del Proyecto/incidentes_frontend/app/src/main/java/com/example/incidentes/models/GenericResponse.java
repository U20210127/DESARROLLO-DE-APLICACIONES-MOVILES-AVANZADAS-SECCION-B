package com.example.incidentes.models;

public class GenericResponse {
    private boolean success;
    private String message;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // Setters (si necesitas modificar estos valores desde el código)
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
