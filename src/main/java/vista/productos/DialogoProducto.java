package vista.productos;

import dao.ProductoDAO;
import dao.TipoProductoDAO;
import modelo.Producto;
import modelo.TipoProducto;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DialogoProducto extends JDialog {

    private boolean confirmado = false;
    private final Producto productoExistente;

    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JTextField txtUnidad;
    private JTextField txtNivelReorden;
    private JTextField txtCantidadReorden;
    private JTextArea  txtDescripcion;
    private JComboBox<TipoProducto> cmbTipo;
    private JComboBox<String> cmbEstado;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final TipoProductoDAO tipoDAO = new TipoProductoDAO();

    public DialogoProducto(JFrame parent, Producto producto) {
        super(parent, producto == null ? "Agregar Producto" : "Editar Producto", true);
        this.productoExistente = producto;
        initComponents();
        if (producto != null) precargarDatos(producto);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<TipoProducto> tipos = tipoDAO.listarTodos();
        cmbTipo = new JComboBox<>(tipos.toArray(new TipoProducto[0]));

        txtNombre = new JTextField(20);
        txtPrecio = new JTextField(10);
        txtStock  = new JTextField(10);
        txtUnidad = new JTextField(10);
        txtNivelReorden = new JTextField(10);
        txtCantidadReorden = new JTextField(10);
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        cmbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});

        agregarFila(formulario, gbc, 0, "Tipo:", cmbTipo);
        agregarFila(formulario, gbc, 1, "Nombre:", txtNombre);
        agregarFila(formulario, gbc, 2, "Precio unitario:", txtPrecio);
        agregarFila(formulario, gbc, 3, "Stock actual:", txtStock);
        agregarFila(formulario, gbc, 4, "Unidad de medida:", txtUnidad);
        agregarFila(formulario, gbc, 5, "Nivel reorden:", txtNivelReorden);
        agregarFila(formulario, gbc, 6, "Cantidad reorden:", txtCantidadReorden);
        agregarFila(formulario, gbc, 7, "Estado:", cmbEstado);

        gbc.gridx = 0; gbc.gridy = 8;
        formulario.add(new JLabel("Descripcion:"), gbc);
        gbc.gridx = 1;
        formulario.add(new JScrollPane(txtDescripcion), gbc);

        add(formulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar  = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.setBackground(new Color(99, 102, 241));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    private void precargarDatos(Producto p) {
        txtNombre.setText(p.getNombreProducto());
        txtPrecio.setText(p.getPrecioUnitario().toString());
        txtStock.setText(String.valueOf(p.getStockActual()));
        txtUnidad.setText(p.getUnidadMedida());
        txtNivelReorden.setText(String.valueOf(p.getNivelReorden()));
        txtCantidadReorden.setText(String.valueOf(p.getCantidadReorden()));
        txtDescripcion.setText(p.getDescripcion());
        cmbEstado.setSelectedItem(p.getEstado());

        for (int i = 0; i < cmbTipo.getItemCount(); i++) {
            if (cmbTipo.getItemAt(i).getIdTipoProducto() == p.getIdTipoProducto()) {
                cmbTipo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void guardar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            return;
        }
        BigDecimal precio;
        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
            if (precio.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio inválido.");
            return;
        }
        int stock, nivelReorden, cantidadReorden;
        try {
            stock = Integer.parseInt(txtStock.getText().trim());
            nivelReorden = Integer.parseInt(txtNivelReorden.getText().trim());
            cantidadReorden = Integer.parseInt(txtCantidadReorden.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stock, nivel y cantidad de reorden deben ser numeros enteros.");
            return;
        }

        TipoProducto tipoSeleccionado = (TipoProducto) cmbTipo.getSelectedItem();

        Producto p = productoExistente != null ? productoExistente : new Producto();
        p.setNombreProducto(txtNombre.getText().trim());
        p.setIdTipoProducto(tipoSeleccionado.getIdTipoProducto());
        p.setPrecioUnitario(precio);
        p.setStockActual(stock);
        p.setUnidadMedida(txtUnidad.getText().trim());
        p.setNivelReorden(nivelReorden);
        p.setCantidadReorden(cantidadReorden);
        p.setDescripcion(txtDescripcion.getText().trim());
        p.setEstado((String) cmbEstado.getSelectedItem());

        boolean ok = productoExistente == null ? productoDAO.insertar(p): productoDAO.actualizar(p);
        if (ok) {
            confirmado = true;
            JOptionPane.showMessageDialog(this, "Producto guardado correctamente.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmado() { return confirmado; }
}
