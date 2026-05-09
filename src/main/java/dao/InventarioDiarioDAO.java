package dao;

import database.Conexion;
import modelo.InventarioDiario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioDiarioDAO {

    public List<InventarioDiario> listarPorFecha(java.time.LocalDate fecha) {
        List<InventarioDiario> lista = new ArrayList<>();
        String sql = "SELECT * FROM INVENTARIO_DIARIO WHERE fecha_inventario = ? ORDER BY id_producto";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InventarioDiario inv = new InventarioDiario();
                    inv.setFechaInventario(rs.getDate("fecha_inventario").toLocalDate());
                    inv.setIdProducto(rs.getInt("id_producto"));
                    inv.setNivelStock(rs.getInt("nivel_stock"));
                    lista.add(inv);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error listarPorFecha InventarioDiario: " + e.getMessage());
        }
        return lista;
    }

    public boolean registrar(InventarioDiario inv) {
        // INSERT ... ON CONFLICT para no duplicar si ya existe el registro del día
        String sql = "INSERT INTO INVENTARIO_DIARIO (fecha_inventario, id_producto, nivel_stock) " +
                     "VALUES (?, ?, ?) ON CONFLICT (fecha_inventario, id_producto) DO UPDATE SET nivel_stock = EXCLUDED.nivel_stock";
        try (PreparedStatement ps = Conexion.getInstancia().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(inv.getFechaInventario()));
            ps.setInt(2, inv.getIdProducto());
            ps.setInt(3, inv.getNivelStock());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error registrar InventarioDiario: " + e.getMessage());
        }
        return false;
    }
}