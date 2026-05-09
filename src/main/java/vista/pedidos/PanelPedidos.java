package vista.pedidos;

import dao.PedidoDAO;
import dao.ClienteDAO;
import dao.DetallePedidoDAO;
import dao.ProductoDAO;
import modelo.Pedido;
import modelo.Cliente;
import modelo.DetallePedido;
import modelo.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelPedidos extends JPanel {

    private JTable tablaPedidos;
    private JTable tablaDetalle;
    private DefaultTableModel modeloPedidos;
    private DefaultTableModel modeloDetalle;
    private JLabel lblInfoPedido;

    private final PedidoDAO pedidoDAO   = new PedidoDAO();
    private final ClienteDAO clienteDAO  = new ClienteDAO();
    private final DetallePedidoDAO detalleDAO = new DetallePedidoDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    public PanelPedidos() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        cargarTablaPedidos();
    }

    private void initComponents() {
        JLabel lblTitulo = new JLabel("Pedidos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(30, 30, 46));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        splitPane.setDividerSize(8);
        add(splitPane, BorderLayout.CENTER);

        JPanel panelSuperior = new JPanel(new BorderLayout(0, 6));
        panelSuperior.setBackground(new Color(240, 242, 245));

        JLabel lblPedidos = new JLabel("Lista de pedidos");
        lblPedidos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPedidos.setForeground(new Color(80, 80, 100));
        panelSuperior.add(lblPedidos, BorderLayout.NORTH);

        String[] columnasPedidos = {"ID", "Cliente", "Fecha", "Estado", "Total"};
        modeloPedidos = new DefaultTableModel(columnasPedidos, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaPedidos = new JTable(modeloPedidos);
        tablaPedidos.setRowHeight(26);
        tablaPedidos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaPedidos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPedidos.setGridColor(new Color(220, 220, 230));

        tablaPedidos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaPedidos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaPedidos.getColumnModel().getColumn(0).setWidth(0);

        panelSuperior.add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);

        JPanel botonesSuperiores = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botonesSuperiores.setOpaque(false);

        JButton btnNuevoPedido = crearBoton(" Nuevo pedido", new Color(99, 102, 241));
        JButton btnCambiarEstado = crearBoton(" Cambiar estado", new Color(59, 130, 246));
        JButton btnEliminar = crearBoton("️ Eliminar", new Color(220, 60, 60));

        botonesSuperiores.add(btnNuevoPedido);
        botonesSuperiores.add(btnCambiarEstado);
        botonesSuperiores.add(btnEliminar);
        panelSuperior.add(botonesSuperiores, BorderLayout.SOUTH);

        splitPane.setTopComponent(panelSuperior);

        JPanel panelInferior = new JPanel(new BorderLayout(0, 6));
        panelInferior.setBackground(new Color(240, 242, 245));

        lblInfoPedido = new JLabel("Selecciona un pedido para ver su detalle");
        lblInfoPedido.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInfoPedido.setForeground(new Color(80, 80, 100));
        panelInferior.add(lblInfoPedido, BorderLayout.NORTH);

        String[] columnasDetalle = {"ID Detalle", "Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        modeloDetalle = new DefaultTableModel(columnasDetalle, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaDetalle = new JTable(modeloDetalle);
        tablaDetalle.setRowHeight(26);
        tablaDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaDetalle.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaDetalle.setGridColor(new Color(220, 220, 230));

        // Ocultar columna ID detalle
        tablaDetalle.getColumnModel().getColumn(0).setMinWidth(0);
        tablaDetalle.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaDetalle.getColumnModel().getColumn(0).setWidth(0);

        panelInferior.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);

        splitPane.setBottomComponent(panelInferior);

        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarDetalle();
        });

        btnNuevoPedido.addActionListener(e -> abrirDialogoPedido());

        btnCambiarEstado.addActionListener(e -> {
            int idPedido = getIdPedidoSeleccionado();
            if (idPedido < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un pedido.");
                return;
            }
            cambiarEstado(idPedido);
        });

        btnEliminar.addActionListener(e -> eliminarPedido());
    }

    public void cargarTablaPedidos() {
        modeloPedidos.setRowCount(0);
        List<Pedido> lista = pedidoDAO.listarTodos();
        for (Pedido p : lista) {
            Cliente c = clienteDAO.obtenerPorId(p.getIdCliente());
            String nombreCliente = c != null ? c.getNombre() : "ID " + p.getIdCliente();
            modeloPedidos.addRow(new Object[]{
                p.getIdPedido(),
                nombreCliente,
                p.getFechaPedido() != null ? p.getFechaPedido().toLocalDate() : "",
                p.getEstadoPedido(),
                "$" + p.getTotal()
            });
        }
        modeloDetalle.setRowCount(0);
        lblInfoPedido.setText("Selecciona un pedido para ver su detalle");
    }

    private void cargarDetalle() {
        int idPedido = getIdPedidoSeleccionado();
        if (idPedido < 0) return;

        modeloDetalle.setRowCount(0);
        List<DetallePedido> detalles = detalleDAO.listarPorPedido(idPedido);

        for (DetallePedido d : detalles) {
            Producto prod = productoDAO.obtenerPorId(d.getIdProducto());
            String nombreProducto = prod != null ? prod.getNombreProducto() : "ID " + d.getIdProducto();
            modeloDetalle.addRow(new Object[]{
                d.getIdDetalle(),
                nombreProducto,
                d.getCantidad(),
                "$" + d.getPrecioUnitario(),
                "$" + d.getSubtotal()
            });
        }

        Pedido pedido = pedidoDAO.obtenerPorId(idPedido);
        if (pedido != null) {
            Cliente c = clienteDAO.obtenerPorId(pedido.getIdCliente());
            String nombre = c != null ? c.getNombre() : "";
            lblInfoPedido.setText("Detalle — Pedido #" + idPedido 
                    + "  |  " + nombre + "  |  Estado: " + pedido.getEstadoPedido() 
                    + "  |  Total: $" + pedido.getTotal());
        }
    }

    private void abrirDialogoPedido() {
        DialogoPedido dialogo = new DialogoPedido(
            (JFrame) SwingUtilities.getWindowAncestor(this)
        );
        dialogo.setVisible(true);
        if (dialogo.isConfirmado()) cargarTablaPedidos();
    }

    private void cambiarEstado(int idPedido) {
        Pedido pedido = pedidoDAO.obtenerPorId(idPedido);
        if (pedido == null) return;

        if ("Enviado".equals(pedido.getEstadoPedido()) || "Cancelado".equals(pedido.getEstadoPedido())) {
            JOptionPane.showMessageDialog(this,
                "No se puede modificar un pedido ya " + pedido.getEstadoPedido().toLowerCase() + ".",
                "Operación no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = {"Pendiente", "Enviado", "Cancelado"};
        String seleccion = (String) JOptionPane.showInputDialog(
            this,
            "Selecciona el nuevo estado:",
            "Cambiar estado",
            JOptionPane.PLAIN_MESSAGE,
            null,
            opciones,
            opciones[0]
        );
        if (seleccion != null) {
            boolean ok = pedidoDAO.actualizarEstado(idPedido, seleccion);
            if (ok) cargarTablaPedidos();
            else JOptionPane.showMessageDialog(this, "Error al cambiar estado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPedido() {
        int idPedido = getIdPedidoSeleccionado();
        if (idPedido < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido para eliminar.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Eliminar el pedido #" + idPedido + " y todos sus detalles?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            // Primero eliminar detalles, luego el pedido (integridad referencial)
            detalleDAO.eliminarPorPedido(idPedido);
            boolean ok = pedidoDAO.eliminar(idPedido);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Pedido eliminado.");
                cargarTablaPedidos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getIdPedidoSeleccionado() {
        int fila = tablaPedidos.getSelectedRow();
        if (fila < 0) return -1;
        return (int) modeloPedidos.getValueAt(fila, 0);
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