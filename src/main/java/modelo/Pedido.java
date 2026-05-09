package modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pedido {
    private int idPedido;
    private int idCliente;
    private LocalDateTime fechaPedido;
    private String estadoPedido;
    private BigDecimal total;

    public Pedido() {}

    public Pedido(int idPedido, int idCliente, LocalDateTime fechaPedido,
                  String estadoPedido, BigDecimal total) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.fechaPedido = fechaPedido;
        this.estadoPedido = estadoPedido;
        this.total = total;
    }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }

    public String getEstadoPedido() { return estadoPedido; }
    public void setEstadoPedido(String estadoPedido) { this.estadoPedido = estadoPedido; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}