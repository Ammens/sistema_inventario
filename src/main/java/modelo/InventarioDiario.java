package modelo;

import java.time.LocalDate;

public class InventarioDiario {
    private LocalDate fechaInventario;
    private int idProducto;
    private int nivelStock;

    public InventarioDiario() {}

    public InventarioDiario(LocalDate fechaInventario, int idProducto, int nivelStock) {
        this.fechaInventario = fechaInventario;
        this.idProducto = idProducto;
        this.nivelStock = nivelStock;
    }

    public LocalDate getFechaInventario() { return fechaInventario; }
    public void setFechaInventario(LocalDate fechaInventario) { this.fechaInventario = fechaInventario; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getNivelStock() { return nivelStock; }
    public void setNivelStock(int nivelStock) { this.nivelStock = nivelStock; }
}