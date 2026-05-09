package dao;

import database.Conexion;
import modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setTelefono(rs.getString("telefono"));
        c.setCorreo(rs.getString("correo"));
        c.setDireccion(rs.getString("direccion"));
        Date fecha = rs.getDate("fecha_registro");
        if (fecha != null) c.setFechaRegistro(fecha.toLocalDate());
        c.setEstado(rs.getString("estado"));
        return c;
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM CLIENTES ORDER BY nombre";
        try (Statement st = Conexion.getInstancia().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listarTodos Cliente: " + e.getMessage());
        }
        return lista;
    }

    public Cliente obtenerPorId(int id) {
        String sql = "SELECT * FROM CLIENTES WHERE id_cliente = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerPorId Cliente: " + e.getMessage());
        }
        return null;
    }

    public boolean insertar(Cliente c) {
        String sql = "INSERT INTO CLIENTES (nombre, telefono, correo, direccion, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getCorreo());
            ps.setString(4, c.getDireccion());
            ps.setString(5, c.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar Cliente: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizar(Cliente c) {
        String sql = "UPDATE CLIENTES SET nombre = ?, telefono = ?, correo = ?, direccion = ?, estado = ? WHERE id_cliente = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getCorreo());
            ps.setString(4, c.getDireccion());
            ps.setString(5, c.getEstado());
            ps.setInt(6, c.getIdCliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar Cliente: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM CLIENTES WHERE id_cliente = ?";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar Cliente: " + e.getMessage());
        }
        return false;
    }
} 

