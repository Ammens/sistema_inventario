package vista.clientes;

import dao.ClienteDAO;
import modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelClientes extends JPanel {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public PanelClientes() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        cargarTabla();
    }

    private void initComponents() {
        JPanel panelTop = new JPanel(new BorderLayout(10, 0));
        panelTop.setOpaque(false);

        JLabel lblTitulo = new JLabel("Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(30, 30, 46));

        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(220, 32));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar cliente...");
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

        String[] columnas = {"ID", "Nombre", "Teléfono", "Correo", "Dirección", "Registro", "Estado"};
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

        JButton btnAgregar = crearBoton(" Agregar ",    new Color(99, 102, 241));
        JButton btnEditar = crearBoton("️ Editar ",     new Color(59, 130, 246));
        JButton btnEliminar = crearBoton("️ Eliminar ",   new Color(220, 60, 60));
        JButton btnActualizar = crearBoton(" Actualizar ", new Color(80, 160, 100));

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> abrirDialogo(null));
        btnEditar.addActionListener(e -> {
            Cliente seleccionado = getClienteSeleccionado();
            if (seleccionado == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un cliente para editar.");
                return;
            }
            abrirDialogo(seleccionado);
        });
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnActualizar.addActionListener(e -> cargarTabla());
    }

    public void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Cliente> lista = clienteDAO.listarTodos();
        for (Cliente c : lista) {
            modeloTabla.addRow(new Object[]{
                c.getIdCliente(),
                c.getNombre(),
                c.getTelefono(),
                c.getCorreo(),
                c.getDireccion(),
                c.getFechaRegistro(),
                c.getEstado()
            });
        }
    }

    private void filtrarTabla(String texto) {
        modeloTabla.setRowCount(0);
        List<Cliente> lista = clienteDAO.listarTodos();
        for (Cliente c : lista) {
            if (c.getNombre().toLowerCase().contains(texto.toLowerCase()) ||
                (c.getCorreo() != null && c.getCorreo().toLowerCase().contains(texto.toLowerCase()))) {
                modeloTabla.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNombre(),
                    c.getTelefono(),
                    c.getCorreo(),
                    c.getDireccion(),
                    c.getFechaRegistro(),
                    c.getEstado()
                });
            }
        }
    }

    private void abrirDialogo(Cliente cliente) {
        DialogoCliente dialogo = new DialogoCliente(
            (JFrame) SwingUtilities.getWindowAncestor(this), cliente
        );
        dialogo.setVisible(true);
        if (dialogo.isConfirmado()) cargarTabla();
    }

    private void eliminarCliente() {
        Cliente seleccionado = getClienteSeleccionado();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente para eliminar.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Eliminar a \"" + seleccionado.getNombre() + "\"?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = clienteDAO.eliminar(seleccionado.getIdCliente());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado.");
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar. El cliente tiene pedidos asociados.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Cliente getClienteSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return null;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        return clienteDAO.obtenerPorId(id);
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