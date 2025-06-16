package com.example.incidentes.models;

public class Incidente {
    private int id;
    private String tipo;
    private String descripcion;
    private String fecha;
    private String estatus;
    private String resolucion;
    private int usuario_id;
    private String imagen; // <-- nuevo campo

    // Campos para dashboard (vienen del endpoint)
    private String usuario_nombre;
    private String fecha_reportado;

    // Constructor vacío
    public Incidente() {
    }

    // Constructor con todos los campos (incluido id e imagen)
    public Incidente(int id, String tipo, String descripcion, String fecha, String estatus, String resolucion, int usuario_id, String imagen) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estatus = estatus;
        this.resolucion = resolucion;
        this.usuario_id = usuario_id;
        this.imagen = imagen;
    }

    // Constructor sin id (para crear nuevos registros)
    public Incidente(String tipo, String descripcion, String fecha, String estatus, int usuario_id, String imagen) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estatus = estatus;
        this.usuario_id = usuario_id;
        this.imagen = imagen;
    }

    // Constructor solo con id (por ejemplo, para eliminar)
    public Incidente(int id) {
        this.id = id;
    }

    // Constructor para edición (id, estatus, resolucion, imagen)
    public Incidente(int id, String estatus, String resolucion, String imagen) {
        this.id = id;
        this.estatus = estatus;
        this.resolucion = resolucion;
        this.imagen = imagen;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    // Nuevos getters y setters para dashboard

    public String getUsuario_nombre() {
        return usuario_nombre;
    }

    public void setUsuario_nombre(String usuario_nombre) {
        this.usuario_nombre = usuario_nombre;
    }

    public String getFecha_reportado() {
        return fecha_reportado;
    }

    public void setFecha_reportado(String fecha_reportado) {
        this.fecha_reportado = fecha_reportado;
    }

    @Override
    public String toString() {
        return "Incidente{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fecha='" + fecha + '\'' +
                ", estatus='" + estatus + '\'' +
                ", resolucion='" + resolucion + '\'' +
                ", usuario_id=" + usuario_id +
                ", imagen='" + (imagen != null ? "[base64 string]" : "null") + '\'' +
                ", usuario_nombre='" + usuario_nombre + '\'' +
                ", fecha_reportado='" + fecha_reportado + '\'' +
                '}';
    }
}
