package dao;

import database.Conexion;
import modelo.TipoProducto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoProductoDAO {

    public List<TipoProducto> listarTodos() {
        List<TipoProducto> lista = new ArrayList<>();
        String sql = "SELECT id_tipo_producto, id_tipo_padre, descripcion_tipo, estado FROM TIPOS_PRODUCTO";
        try (Statement st = Conexion.getInstancia().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                TipoProducto t = new TipoProducto();
                t.setIdTipoProducto(rs.getInt("id_tipo_producto"));
                int padre = rs.getInt("id_tipo_padre");
                t.setIdTipoPadre(rs.wasNull() ? null : padre);
                t.setDescripcionTipo(rs.getString("descripcion_tipo"));
                t.setEstado(rs.getString("estado"));
                lista.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Error listarTodos TipoProducto: " + e.getMessage());
        }
        return lista;
    }

    public TipoProducto obtenerPorId(int id) {
        String sql = "SELECT id_tipo_producto, id_tipo_padre, descripcion_tipo, estado FROM TIPOS_PRODUCTO WHERE id_tipo_producto = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TipoProducto t = new TipoProducto();
                    t.setIdTipoProducto(rs.getInt("id_tipo_producto"));
                    int padre = rs.getInt("id_tipo_padre");
                    t.setIdTipoPadre(rs.wasNull() ? null : padre);
                    t.setDescripcionTipo(rs.getString("descripcion_tipo"));
                    t.setEstado(rs.getString("estado"));
                    return t;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerPorId TipoProducto: " + e.getMessage());
        }
        return null;
    }

    public boolean insertar(TipoProducto t) {
        String sql = "INSERT INTO TIPOS_PRODUCTO (id_tipo_padre, descripcion_tipo, estado) VALUES (?, ?, ?)";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            if (t.getIdTipoPadre() == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, t.getIdTipoPadre());
            ps.setString(2, t.getDescripcionTipo());
            ps.setString(3, t.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar TipoProducto: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizar(TipoProducto t) {
        String sql = "UPDATE TIPOS_PRODUCTO SET id_tipo_padre = ?, descripcion_tipo = ?, estado = ? WHERE id_tipo_producto = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            if (t.getIdTipoPadre() == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, t.getIdTipoPadre());
            ps.setString(2, t.getDescripcionTipo());
            ps.setString(3, t.getEstado());
            ps.setInt(4, t.getIdTipoProducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar TipoProducto: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM TIPOS_PRODUCTO WHERE id_tipo_producto = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar TipoProducto: " + e.getMessage());
        }
        return false;
    }
}
