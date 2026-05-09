package modelo;

import java.math.BigDecimal;

public class Producto {
    private int idProducto;
    private int idTipoProducto;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private String descripcion;
    private int nivelReorden;
    private int cantidadReorden;
    private int stockActual;
    private String unidadMedida;
    private String estado;

    public Producto() {}

    public Producto(int idProducto, int idTipoProducto, String nombreProducto,
                    BigDecimal precioUnitario, String descripcion, int nivelReorden,
                    int cantidadReorden, int stockActual, String unidadMedida, String estado) {
        this.idProducto = idProducto;
        this.idTipoProducto = idTipoProducto;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.descripcion = descripcion;
        this.nivelReorden = nivelReorden;
        this.cantidadReorden = cantidadReorden;
        this.stockActual = stockActual;
        this.unidadMedida = unidadMedida;
        this.estado = estado;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getIdTipoProducto() { return idTipoProducto; }
    public void setIdTipoProducto(int idTipoProducto) { this.idTipoProducto = idTipoProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getNivelReorden() { return nivelReorden; }
    public void setNivelReorden(int nivelReorden) { this.nivelReorden = nivelReorden; }

    public int getCantidadReorden() { return cantidadReorden; }
    public void setCantidadReorden(int cantidadReorden) { this.cantidadReorden = cantidadReorden; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() { return nombreProducto; }
}