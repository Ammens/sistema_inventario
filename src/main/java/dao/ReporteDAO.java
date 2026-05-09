package dao;

import database.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    public List<Object[]> inventarioDelDia() {
        List<Object[]> lista = new ArrayList<>();
        String sql = """
            SELECT p.nombre_producto,
                   p.unidad_medida,
                   p.stock_actual,
                   p.nivel_reorden,
                   CASE
                       WHEN p.stock_actual = 0 THEN 'Sin stock'
                       WHEN p.stock_actual <= p.nivel_reorden THEN 'Stock bajo'
                       ELSE 'OK'
                   END AS estado_stock
            FROM PRODUCTOS p
            WHERE p.estado = 'Activo'
            ORDER BY p.stock_actual ASC
            """;
        try (Statement st = Conexion.getInstancia().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("nombre_producto"),
                    rs.getString("unidad_medida"),
                    rs.getInt("stock_actual"),
                    rs.getInt("nivel_reorden"),
                    rs.getString("estado_stock")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error inventarioDelDia: " + e.getMessage());
        }
        return lista;
    }


    public List<Object[]> pedidosPorCliente(int idCliente) {
        List<Object[]> lista = new ArrayList<>();
        String sql = """
            SELECT p.id_pedido,
                   p.fecha_pedido,
                   p.estado_pedido,
                   p.total,
                   COUNT(d.id_detalle) AS num_productos
            FROM PEDIDOS p
            LEFT JOIN DETALLE_PEDIDO d ON p.id_pedido = d.id_pedido
            WHERE p.id_cliente = ?
            GROUP BY p.id_pedido, p.fecha_pedido, p.estado_pedido, p.total
            ORDER BY p.fecha_pedido DESC
            """;
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id_pedido"),
                        rs.getTimestamp("fecha_pedido") != null
                            ? rs.getTimestamp("fecha_pedido").toLocalDateTime().toLocalDate()
                            : "",
                        rs.getString("estado_pedido"),
                        rs.getBigDecimal("total"),
                        rs.getInt("num_productos")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error pedidosPorCliente: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> productosMasVendidos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = """
            SELECT p.nombre_producto,
                   SUM(d.cantidad)  AS total_vendido,
                   SUM(d.subtotal)  AS ingresos
            FROM DETALLE_PEDIDO d
            JOIN PRODUCTOS p ON d.id_producto = p.id_producto
            JOIN PEDIDOS pe  ON d.id_pedido   = pe.id_pedido
            WHERE pe.estado_pedido != 'Cancelado'
            GROUP BY p.nombre_producto
            ORDER BY total_vendido DESC
            """;
        try (Statement st = Conexion.getInstancia().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("nombre_producto"),
                    rs.getInt("total_vendido"),
                    rs.getBigDecimal("ingresos")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error productosMasVendidos: " + e.getMessage());
        }
        return lista;
    }
}
