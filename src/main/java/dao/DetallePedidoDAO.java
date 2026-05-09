package dao;

import database.Conexion;
import modelo.DetallePedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDAO {

    private DetallePedido mapear(ResultSet rs) throws SQLException {
        DetallePedido d = new DetallePedido();
        d.setIdDetalle(rs.getInt("id_detalle"));
        d.setIdPedido(rs.getInt("id_pedido"));
        d.setIdProducto(rs.getInt("id_producto"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        d.setSubtotal(rs.getBigDecimal("subtotal"));
        return d;
    }

    public List<DetallePedido> listarPorPedido(int idPedido) {
        List<DetallePedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM DETALLE_PEDIDO WHERE id_pedido = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listarPorPedido DetallePedido: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(DetallePedido d) {
        String sql = "INSERT INTO DETALLE_PEDIDO (id_pedido, id_producto, cantidad, precio_unitario, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, d.getIdPedido());
            ps.setInt(2, d.getIdProducto());
            ps.setInt(3, d.getCantidad());
            ps.setBigDecimal(4, d.getPrecioUnitario());
            ps.setBigDecimal(5, d.getSubtotal());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar DetallePedido: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminarPorPedido(int idPedido) {
        String sql = "DELETE FROM DETALLE_PEDIDO WHERE id_pedido = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminarPorPedido: " + e.getMessage());
        }
        return false;
    }
}