package modelo;

import java.time.LocalDate;

public class Cliente {
    private int idCliente;
    private String nombre;
    private String telefono;
    private String correo;
    private String direccion;
    private LocalDate fechaRegistro;
    private String estado;

    public Cliente() {}

    public Cliente(int idCliente, String nombre, String telefono, String correo,
                   String direccion, LocalDate fechaRegistro, String estado) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() { return nombre; }
}