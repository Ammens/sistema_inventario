package vista.reportes;

import dao.ClienteDAO;
import dao.ReporteDAO;
import modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PanelReportes extends JPanel {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblTituloReporte;

    private JComboBox<Cliente> cmbCliente;

    private final ReporteDAO  reporteDAO = new ReporteDAO();
    private final ClienteDAO  clienteDAO = new ClienteDAO();

    private String[] columnasActuales = {};
    private List<Object[]> datosActuales  = null;

    public PanelReportes() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
    }

    private void initComponents() {
        JLabel lblTitulo = new JLabel("Reportes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(30, 30, 46));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panelControles.setOpaque(false);
        panelControles.setBorder(BorderFactory.createTitledBorder("Seleccionar reporte"));

        JButton btnInventario  = crearBoton(" Inventario del día",    new Color(80, 160, 100));
        JButton btnMasVendidos = crearBoton(" Productos más vendidos", new Color(59, 130, 246));
        JButton btnRefrescarClientes = crearBoton("*", new Color(80, 80, 100));
        btnRefrescarClientes.setToolTipText("Refrescar lista de clientes");

        List<Cliente> clientes = clienteDAO.listarTodos();
        cmbCliente = new JComboBox<>(clientes.toArray(new Cliente[0]));
        cmbCliente.setPreferredSize(new Dimension(200, 30));
        JButton btnPedidosCliente = crearBoton(" Pedidos por cliente", new Color(99, 102, 241));

        panelControles.add(btnInventario);
        panelControles.add(btnMasVendidos);
        panelControles.add(new JSeparator(SwingConstants.VERTICAL));
        panelControles.add(new JLabel("Cliente:"));
        panelControles.add(cmbCliente);
        panelControles.add(btnPedidosCliente);
        panelControles.add(btnRefrescarClientes);

        add(panelControles, BorderLayout.SOUTH);

        JPanel panelTabla = new JPanel(new BorderLayout(0, 6));
        panelTabla.setOpaque(false);

        lblTituloReporte = new JLabel("Selecciona un reporte para comenzar");
        lblTituloReporte.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloReporte.setForeground(new Color(80, 80, 100));
        panelTabla.add(lblTituloReporte, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(26);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.setGridColor(new Color(220, 220, 230));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 220)));
        panelTabla.add(scroll, BorderLayout.CENTER);

        JPanel panelExportar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelExportar.setOpaque(false);
        JButton btnExportar = crearBoton(" Exportar CSV", new Color(60, 60, 80));
        panelExportar.add(btnExportar);
        panelTabla.add(panelExportar, BorderLayout.SOUTH);

        add(panelTabla, BorderLayout.CENTER);

        btnInventario.addActionListener(e  -> mostrarInventario());
        btnMasVendidos.addActionListener(e -> mostrarMasVendidos());
        btnPedidosCliente.addActionListener(e -> {
            Cliente clienteAnterior = (Cliente) cmbCliente.getSelectedItem();
            cmbCliente.setEnabled(false);
            cmbCliente.removeAllItems();
            clienteDAO.listarTodos().forEach(cmbCliente::addItem);

            if (clienteAnterior != null) {
                for (int i = 0; i < cmbCliente.getItemCount(); i++) {
                    if (cmbCliente.getItemAt(i).getIdCliente() == clienteAnterior.getIdCliente()) {
                        cmbCliente.setSelectedIndex(i);
                        break;
                    }
                }
            }
            cmbCliente.setEnabled(true);
            mostrarPedidosCliente();
        });
        btnRefrescarClientes.addActionListener(e -> {
            cmbCliente.removeAllItems();
            clienteDAO.listarTodos().forEach(cmbCliente::addItem);
        });
        btnExportar.addActionListener(e -> exportarCSV());
    }

    private void mostrarInventario() {
        String[] columnas = {"Producto", "Unidad", "Stock actual", "Nivel reorden", "Estado"};
        List<Object[]> datos = reporteDAO.inventarioDelDia();
        cargarEnTabla(columnas, datos);
        lblTituloReporte.setText(" Inventario del día — " + java.time.LocalDate.now());
    }

    private void mostrarMasVendidos() {
        String[] columnas = {"Producto", "Unidades vendidas", "Ingresos generados ($)"};
        List<Object[]> datos = reporteDAO.productosMasVendidos();
        cargarEnTabla(columnas, datos);
        lblTituloReporte.setText(" Productos más vendidos");
    }

    private void mostrarPedidosCliente() {
        Cliente clienteSeleccionado = (Cliente) cmbCliente.getSelectedItem();
        if (clienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente.");
            return;
        }
        String[] columnas = {"# Pedido", "Fecha", "Estado", "Total ($)", "Productos"};
        List<Object[]> datos = reporteDAO.pedidosPorCliente(clienteSeleccionado.getIdCliente());
        cargarEnTabla(columnas, datos);
        lblTituloReporte.setText(" Pedidos de: " + clienteSeleccionado.getNombre());
    }

    private void cargarEnTabla(String[] columnas, List<Object[]> datos) {
        modeloTabla.setColumnCount(0);
        modeloTabla.setRowCount(0);
        for (String col : columnas) modeloTabla.addColumn(col);
        for (Object[] fila : datos) modeloTabla.addRow(fila);

        columnasActuales = columnas;
        datosActuales = datos;
    }

    private void exportarCSV() {
        if (datosActuales == null || datosActuales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte CSV");
        fileChooser.setSelectedFile(new java.io.File("reporte.csv"));

        int resultado = fileChooser.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        java.io.File archivo = fileChooser.getSelectedFile();
        if (!archivo.getName().endsWith(".csv")) {
            archivo = new java.io.File(archivo.getAbsolutePath() + ".csv");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write(String.join(",", columnasActuales));
            bw.newLine();

            for (Object[] fila : datosActuales) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < fila.length; i++) {
                    String valor = fila[i] != null ? fila[i].toString() : "";
                    if (valor.contains(",") || valor.contains("\"")) {
                        valor = "\"" + valor.replace("\"", "\"\"") + "\"";
                    }
                    sb.append(valor);
                    if (i < fila.length - 1) sb.append(",");
                }
                bw.write(sb.toString());
                bw.newLine();
            }

            JOptionPane.showMessageDialog(this,
                "Reporte exportado correctamente:\n" + archivo.getAbsolutePath());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error al exportar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return btn;
    }
}
