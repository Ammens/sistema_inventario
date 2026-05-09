package dao;

import database.Conexion;
import modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("id_producto"));
        p.setIdTipoProducto(rs.getInt("id_tipo_producto"));
        p.setNombreProducto(rs.getString("nombre_producto"));
        p.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setNivelReorden(rs.getInt("nivel_reorden"));
        p.setCantidadReorden(rs.getInt("cantidad_reorden"));
        p.setStockActual(rs.getInt("stock_actual"));
        p.setUnidadMedida(rs.getString("unidad_medida"));
        p.setEstado(rs.getString("estado"));
        return p;
    }

    public List<Producto> listarTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTOS ORDER BY nombre_producto";
        try (Statement st = Conexion.getInstancia().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listarTodos Producto: " + e.getMessage());
        }
        return lista;
    }

    public List<Producto> listarBajoStock() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTOS WHERE stock_actual <= nivel_reorden AND estado = 'Activo'";
        try (Statement st = Conexion.getInstancia().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listarBajoStock: " + e.getMessage());
        }
        return lista;
    }

    public Producto obtenerPorId(int id) {
        String sql = "SELECT * FROM PRODUCTOS WHERE id_producto = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerPorId Producto: " + e.getMessage());
        }
        return null;
    }

    public boolean insertar(Producto p) {
        String sql = "INSERT INTO PRODUCTOS (id_tipo_producto, nombre_producto, precio_unitario, " +
                     "descripcion, nivel_reorden, cantidad_reorden, stock_actual, unidad_medida, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, p.getIdTipoProducto());
            ps.setString(2, p.getNombreProducto());
            ps.setBigDecimal(3, p.getPrecioUnitario());
            ps.setString(4, p.getDescripcion());
            ps.setInt(5, p.getNivelReorden());
            ps.setInt(6, p.getCantidadReorden());
            ps.setInt(7, p.getStockActual());
            ps.setString(8, p.getUnidadMedida());
            ps.setString(9, p.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar Producto: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizar(Producto p) {
        String sql = "UPDATE PRODUCTOS SET id_tipo_producto = ?, nombre_producto = ?, precio_unitario = ?, " +
                     "descripcion = ?, nivel_reorden = ?, cantidad_reorden = ?, stock_actual = ?, " +
                     "unidad_medida = ?, estado = ? WHERE id_producto = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, p.getIdTipoProducto());
            ps.setString(2, p.getNombreProducto());
            ps.setBigDecimal(3, p.getPrecioUnitario());
            ps.setString(4, p.getDescripcion());
            ps.setInt(5, p.getNivelReorden());
            ps.setInt(6, p.getCantidadReorden());
            ps.setInt(7, p.getStockActual());
            ps.setString(8, p.getUnidadMedida());
            ps.setString(9, p.getEstado());
            ps.setInt(10, p.getIdProducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar Producto: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM PRODUCTOS WHERE id_producto = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar Producto: " + e.getMessage());
        }
        return false;
    }
}
