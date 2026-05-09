package vista.clientes;

import dao.ClienteDAO;
import modelo.Cliente;

import javax.swing.*;
import java.awt.*;

public class DialogoCliente extends JDialog {

    private boolean confirmado = false;
    private Cliente clienteExistente;

    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtDireccion;
    private JComboBox<String> cmbEstado;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public DialogoCliente(JFrame parent, Cliente cliente) {
        super(parent, cliente == null ? "Agregar Cliente" : "Editar Cliente", true);
        this.clienteExistente = cliente;
        initComponents();
        if (cliente != null) precargarDatos(cliente);
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

        txtNombre = new JTextField(22);
        txtTelefono = new JTextField(22);
        txtCorreo = new JTextField(22);
        txtDireccion = new JTextField(22);
        cmbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});

        agregarFila(formulario, gbc, 0, "Nombre *:", txtNombre);
        agregarFila(formulario, gbc, 1, "Teléfono:", txtTelefono);
        agregarFila(formulario, gbc, 2, "Correo:", txtCorreo);
        agregarFila(formulario, gbc, 3, "Dirección:", txtDireccion);
        agregarFila(formulario, gbc, 4, "Estado:", cmbEstado);

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

    private void precargarDatos(Cliente c) {
        txtNombre.setText(c.getNombre());
        txtTelefono.setText(c.getTelefono());
        txtCorreo.setText(c.getCorreo());
        txtDireccion.setText(c.getDireccion());
        cmbEstado.setSelectedItem(c.getEstado());
    }

    private void guardar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            return;
        }

        String correo = txtCorreo.getText().trim();
        if (!correo.isEmpty() && !correo.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Formato de correo inválido.");
            return;
        }

        Cliente c = clienteExistente != null ? clienteExistente : new Cliente();
        c.setNombre(txtNombre.getText().trim());
        c.setTelefono(txtTelefono.getText().trim());
        c.setCorreo(correo.isEmpty() ? null : correo);
        c.setDireccion(txtDireccion.getText().trim());
        c.setEstado((String) cmbEstado.getSelectedItem());

        boolean ok = clienteExistente == null
            ? clienteDAO.insertar(c)
            : clienteDAO.actualizar(c);

        if (ok) {
            confirmado = true;
            JOptionPane.showMessageDialog(this, "Cliente guardado correctamente.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar. El correo puede estar duplicado.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmado() { return confirmado; }
}
