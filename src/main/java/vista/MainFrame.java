package vista;

import dao.ProductoDAO;
import modelo.Producto;
import vista.productos.PanelProductos;
import vista.clientes.PanelClientes;
import vista.pedidos.PanelPedidos;
import vista.reportes.PanelReportes;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel panelContenido;
    private CardLayout cardLayout;

    private JButton btnProductos;
    private JButton btnClientes;
    private JButton btnPedidos;
    private JButton btnReportes;

    private PanelProductos panelProductos;
    private PanelClientes panelClientes;
    private PanelPedidos panelPedidos;

    public MainFrame() {
        initComponents();
        mostrarPanel("PRODUCTOS");
    }

    private void initComponents() {
        setTitle("Sistema de Inventario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 46));
        header.setPreferredSize(new Dimension(0, 55));

        JLabel titulo = new JLabel(" Sistema de Inventario");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(titulo, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel menuLateral = new JPanel();
        menuLateral.setLayout(new BoxLayout(menuLateral, BoxLayout.Y_AXIS));
        menuLateral.setBackground(new Color(45, 45, 65));
        menuLateral.setPreferredSize(new Dimension(180, 0));
        menuLateral.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        btnProductos = crearBotonMenu("Productos");
        btnClientes = crearBotonMenu("Clientes");
        btnPedidos = crearBotonMenu("Pedidos");
        btnReportes = crearBotonMenu("Reportes");

        menuLateral.add(btnProductos);
        menuLateral.add(Box.createVerticalStrut(8));
        menuLateral.add(btnClientes);
        menuLateral.add(Box.createVerticalStrut(8));
        menuLateral.add(btnPedidos);
        menuLateral.add(Box.createVerticalStrut(8));
        menuLateral.add(btnReportes);
        menuLateral.add(Box.createVerticalGlue());

        add(menuLateral, BorderLayout.WEST);
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(new Color(240, 242, 245));

        panelProductos = new PanelProductos();
        panelClientes = new PanelClientes();
        panelPedidos = new PanelPedidos();

        panelContenido.add(panelProductos, "PRODUCTOS");
        panelContenido.add(panelClientes, "CLIENTES");
        panelContenido.add(panelPedidos, "PEDIDOS");
        panelContenido.add(new PanelReportes(), "REPORTES");

        add(panelContenido, BorderLayout.CENTER);

        btnProductos.addActionListener(e -> mostrarPanel("PRODUCTOS"));
        btnClientes.addActionListener(e  -> mostrarPanel("CLIENTES"));
        btnPedidos.addActionListener(e   -> mostrarPanel("PEDIDOS"));
        btnReportes.addActionListener(e  -> mostrarPanel("REPORTES"));
    }

    private void mostrarPanel(String nombre) {
        cardLayout.show(panelContenido, nombre);
        for (Component c : ((JPanel) btnProductos.getParent()).getComponents()) {
            if (c instanceof JButton btn) {
                btn.setBackground(new Color(45, 45, 65));
                btn.setForeground(new Color(180, 180, 200));
            }
        }
        JButton activo = switch (nombre) {
            case "PRODUCTOS" -> btnProductos;
            case "CLIENTES"  -> btnClientes;
            case "PEDIDOS"   -> btnPedidos;
            case "REPORTES"  -> btnReportes;
            default          -> btnProductos;
        };
        activo.setBackground(new Color(99, 102, 241));
        activo.setForeground(Color.WHITE);
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(180, 180, 200));
        btn.setBackground(new Color(45, 45, 65));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 8));
        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            System.err.println("No se pudo aplicar L&F: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}