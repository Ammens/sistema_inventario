package dao;

import database.Conexion;
import modelo.Pedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setIdPedido(rs.getInt("id_pedido"));
        p.setIdCliente(rs.getInt("id_cliente"));
        Timestamp ts = rs.getTimestamp("fecha_pedido");
        if (ts != null) p.setFechaPedido(ts.toLocalDateTime());
        p.setEstadoPedido(rs.getString("estado_pedido"));
        p.setTotal(rs.getBigDecimal("total"));
        return p;
    }

    public List<Pedido> listarTodos() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM PEDIDOS ORDER BY fecha_pedido DESC";
        try (Statement st = Conexion.getInstancia().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listarTodos Pedido: " + e.getMessage());
        }
        return lista;
    }

    public List<Pedido> listarPorCliente(int idCliente) {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM PEDIDOS WHERE id_cliente = ? ORDER BY fecha_pedido DESC";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listarPorCliente: " + e.getMessage());
        }
        return lista;
    }

    public Pedido obtenerPorId(int id) {
        String sql = "SELECT * FROM PEDIDOS WHERE id_pedido = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerPorId Pedido: " + e.getMessage());
        }
        return null;
    }

    public int insertar(Pedido p) {
        String sql = "INSERT INTO PEDIDOS (id_cliente, estado_pedido, total) VALUES (?, ?, ?) RETURNING id_pedido";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, p.getIdCliente());
            ps.setString(2, p.getEstadoPedido());
            ps.setBigDecimal(3, p.getTotal());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_pedido");
        } catch (SQLException e) {
            System.err.println("Error insertar Pedido: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizarEstado(int idPedido, String nuevoEstado) {
        Connection conn = Conexion.getInstancia();
        try {
            conn.setAutoCommit(false); 

            String sqlEstado = "UPDATE PEDIDOS SET estado_pedido = ? WHERE id_pedido = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlEstado)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, idPedido);
                ps.executeUpdate();
            }

            if ("Enviado".equals(nuevoEstado)) {
                String sqlDetalle = "SELECT id_producto, cantidad FROM DETALLE_PEDIDO WHERE id_pedido = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlDetalle)) {
                    ps.setInt(1, idPedido);
                    try (ResultSet rs = ps.executeQuery()) {
                        String sqlStock = "UPDATE PRODUCTOS SET stock_actual = stock_actual - ? WHERE id_producto = ?";
                        while (rs.next()) {
                            try (PreparedStatement psStock = conn.prepareStatement(sqlStock)) {
                                psStock.setInt(1, rs.getInt("cantidad"));
                                psStock.setInt(2, rs.getInt("id_producto"));
                                psStock.executeUpdate();
                            }
                        }
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { System.err.println("Error rollback: " + ex.getMessage()); }
            System.err.println("Error actualizarEstado: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { System.err.println("Error autocommit: " + e.getMessage()); }
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM PEDIDOS WHERE id_pedido = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar Pedido: " + e.getMessage());
        }
        return false;
    }
}
