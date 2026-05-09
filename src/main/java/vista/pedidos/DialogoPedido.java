package vista.pedidos;

import dao.ClienteDAO;
import dao.ProductoDAO;
import dao.PedidoDAO;
import dao.DetallePedidoDAO;
import modelo.Cliente;
import modelo.Producto;
import modelo.Pedido;
import modelo.DetallePedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DialogoPedido extends JDialog {

    private boolean confirmado = false;

    private JComboBox<Cliente> cmbCliente;
    private JComboBox<Producto> cmbProducto;
    private JTextField txtCantidad;
    private JTable tablaItems;
    private DefaultTableModel modeloItems;
    private JLabel lblTotal;

    private final List<DetallePedido> items = new ArrayList<>();

    private final ClienteDAO clienteDAO  = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final PedidoDAO pedidoDAO   = new PedidoDAO();
    private final DetallePedidoDAO detalleDAO = new DetallePedidoDAO();

    public DialogoPedido(JFrame parent) {
        super(parent, "Nuevo Pedido", true);
        initComponents();
        pack();
        setSize(620, 500);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel panelCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelCliente.setOpaque(false);

        List<Cliente> clientes = clienteDAO.listarTodos();
        cmbCliente = new JComboBox<>(clientes.toArray(new Cliente[0]));
        cmbCliente.setPreferredSize(new Dimension(250, 30));

        panelCliente.add(new JLabel("Cliente:"));
        panelCliente.add(cmbCliente);
        add(panelCliente, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(0, 8));
        panelCentro.setOpaque(false);

        JPanel panelAgregar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelAgregar.setOpaque(false);
        panelAgregar.setBorder(BorderFactory.createTitledBorder("Agregar producto"));

        List<Producto> productos = productoDAO.listarTodos();
        cmbProducto = new JComboBox<>(productos.toArray(new Producto[0]));
        cmbProducto.setPreferredSize(new Dimension(220, 30));

        txtCantidad = new JTextField("1", 5);

        JButton btnAgregar = crearBoton(" Agregar", new Color(99, 102, 241));
        JButton btnQuitar  = crearBoton(" Quitar",  new Color(220, 60, 60));

        panelAgregar.add(new JLabel("Producto:"));
        panelAgregar.add(cmbProducto);
        panelAgregar.add(new JLabel("Cantidad:"));
        panelAgregar.add(txtCantidad);
        panelAgregar.add(btnAgregar);
        panelAgregar.add(btnQuitar);

        panelCentro.add(panelAgregar, BorderLayout.NORTH);

        String[] cols = {"#", "Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        modeloItems = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaItems = new JTable(modeloItems);
        tablaItems.setRowHeight(24);
        tablaItems.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaItems.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaItems.setGridColor(new Color(220, 220, 230));

        panelCentro.add(new JScrollPane(tablaItems), BorderLayout.CENTER);

        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotal.setForeground(new Color(30, 30, 46));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        panelCentro.add(lblTotal, BorderLayout.SOUTH);

        add(panelCentro, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        JButton btnGuardar  = crearBoton("Guardar pedido", new Color(80, 160, 100));
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFocusPainted(false);

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarItem());
        btnQuitar.addActionListener(e  -> quitarItem());
        btnGuardar.addActionListener(e -> guardarPedido());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void agregarItem() {
        Producto prod = (Producto) cmbProducto.getSelectedItem();
        if (prod == null) return;

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida.");
            return;
        }

        if (cantidad > prod.getStockActual()) {
            JOptionPane.showMessageDialog(this,
                "Stock insuficiente. Disponible: " + prod.getStockActual());
            return;
        }

        for (DetallePedido d : items) {
            if (d.getIdProducto() == prod.getIdProducto()) {
                int nuevaCantidad = d.getCantidad() + cantidad;
                if (nuevaCantidad > prod.getStockActual()) {
                    JOptionPane.showMessageDialog(this, "Stock insuficiente para esa cantidad total.");
                    return;
                }
                d.setCantidad(nuevaCantidad);
                d.setSubtotal(prod.getPrecioUnitario().multiply(BigDecimal.valueOf(nuevaCantidad)));
                refrescarTablaItems();
                return;
            }
        }

        DetallePedido detalle = new DetallePedido();
        detalle.setIdProducto(prod.getIdProducto());
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(prod.getPrecioUnitario());
        detalle.setSubtotal(prod.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad)));
        items.add(detalle);
        refrescarTablaItems();
    }

    private void quitarItem() {
        int fila = tablaItems.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para quitar.");
            return;
        }
        items.remove(fila);
        refrescarTablaItems();
    }

    private void refrescarTablaItems() {
        modeloItems.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        int num = 1;
        for (DetallePedido d : items) {
            Producto p = productoDAO.obtenerPorId(d.getIdProducto());
            String nombre = p != null ? p.getNombreProducto() : "ID " + d.getIdProducto();
            modeloItems.addRow(new Object[]{
                num++,
                nombre,
                d.getCantidad(),
                "$" + d.getPrecioUnitario(),
                "$" + d.getSubtotal()
            });
            total = total.add(d.getSubtotal());
        }
        lblTotal.setText("Total: $" + total);
    }

    private void guardarPedido() {
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agrega al menos un producto al pedido.");
            return;
        }
        Cliente clienteSeleccionado = (Cliente) cmbCliente.getSelectedItem();
        if (clienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente.");
            return;
        }

        BigDecimal total = items.stream()
            .map(DetallePedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pedido pedido = new Pedido();
        pedido.setIdCliente(clienteSeleccionado.getIdCliente());
        pedido.setEstadoPedido("Pendiente");
        pedido.setTotal(total);

        int idNuevoPedido = pedidoDAO.insertar(pedido);
        if (idNuevoPedido < 0) {
            JOptionPane.showMessageDialog(this, "Error al crear el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (DetallePedido d : items) {
            d.setIdPedido(idNuevoPedido);
            detalleDAO.insertar(d);
        }
        confirmado = true;
        JOptionPane.showMessageDialog(this, "Pedido #" + idNuevoPedido + " creado correctamente.");
        dispose();
    }

    public boolean isConfirmado() { return confirmado; }

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
