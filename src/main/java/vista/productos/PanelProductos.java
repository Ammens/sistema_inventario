package vista.productos;

import dao.ProductoDAO;
import dao.TipoProductoDAO;
import modelo.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelProductos extends JPanel {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JLabel lblAlerta;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final TipoProductoDAO tipoDAO = new TipoProductoDAO();

    public PanelProductos() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        cargarTabla();
    }

    private void initComponents() {
        JPanel panelTop = new JPanel(new BorderLayout(10, 0));
        panelTop.setOpaque(false);

        JLabel lblTitulo = new JLabel("Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(30, 30, 46));

        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(220, 32));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar producto...");
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filtrarTabla(txtBuscar.getText());
            }
        });

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBusqueda.setOpaque(false);
        panelBusqueda.add(new JLabel("🔍  "));
        panelBusqueda.add(txtBuscar);

        panelTop.add(lblTitulo, BorderLayout.WEST);
        panelTop.add(panelBusqueda, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);

        lblAlerta = new JLabel();
        lblAlerta.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAlerta.setForeground(new Color(200, 80, 60));
        lblAlerta.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        add(lblAlerta, BorderLayout.SOUTH);

        String[] columnas = {"ID", "Nombre", "Tipo", "Precio", "Stock", "Unidad", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(26);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setGridColor(new Color(220, 220, 230));

        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 220)));
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JButton btnAgregar = crearBoton(" Agregar ", new Color(99, 102, 241));
        JButton btnEditar = crearBoton(" Editar ", new Color(59, 130, 246));
        JButton btnEliminar = crearBoton("️ Eliminar ", new Color(220, 60, 60));
        JButton btnActualizar = crearBoton(" Actualizar ", new Color(80, 160, 100));

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        add(panelBotones, BorderLayout.SOUTH);
        btnAgregar.addActionListener(e -> abrirDialogo(null));

        btnEditar.addActionListener(e -> {
            Producto seleccionado = getProductoSeleccionado();
            if (seleccionado == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un producto para editar.");
                return;
            }
            abrirDialogo(seleccionado);
        });

        btnEliminar.addActionListener(e -> eliminarProducto());

        btnActualizar.addActionListener(e -> cargarTabla());
    }

    public void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Producto> lista = productoDAO.listarTodos();
        for (Producto p : lista) {
            String nombreTipo = "";
            var tipo = tipoDAO.obtenerPorId(p.getIdTipoProducto());
            if (tipo != null) nombreTipo = tipo.getDescripcionTipo();

            modeloTabla.addRow(new Object[]{
                p.getIdProducto(),
                p.getNombreProducto(),
                nombreTipo,
                "$" + p.getPrecioUnitario(),
                p.getStockActual(),
                p.getUnidadMedida(),
                p.getEstado()
            });
        }
        actualizarAlertaStock();
    }

    private void filtrarTabla(String texto) {
        modeloTabla.setRowCount(0);
        List<Producto> lista = productoDAO.listarTodos();
        for (Producto p : lista) {
            if (p.getNombreProducto().toLowerCase().contains(texto.toLowerCase())) {
                var tipo = tipoDAO.obtenerPorId(p.getIdTipoProducto());
                String nombreTipo = tipo != null ? tipo.getDescripcionTipo() : "";
                modeloTabla.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getNombreProducto(),
                    nombreTipo,
                    "$" + p.getPrecioUnitario(),
                    p.getStockActual(),
                    p.getUnidadMedida(),
                    p.getEstado()
                });
            }
        }
    }

    private void actualizarAlertaStock() {
        List<Producto> bajoStock = productoDAO.listarBajoStock();
        if (!bajoStock.isEmpty()) {
            lblAlerta.setText("️Alerta  " + bajoStock.size() + " producto(s) con stock bajo o en nivel de reorden.");
        } else {
            lblAlerta.setText("");
        }
    }

    private void abrirDialogo(Producto producto) {
        DialogoProducto dialogo = new DialogoProducto(
            (JFrame) SwingUtilities.getWindowAncestor(this), producto
        );
        dialogo.setVisible(true);
        if (dialogo.isConfirmado()) cargarTabla();
    }

    private void eliminarProducto() {
        Producto seleccionado = getProductoSeleccionado();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Eliminar \"" + seleccionado.getNombreProducto() + "\"?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = productoDAO.eliminar(seleccionado.getIdProducto());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Producto eliminado.");
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar. Puede tener pedidos asociados.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Producto getProductoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return null;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        return productoDAO.obtenerPorId(id);
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
