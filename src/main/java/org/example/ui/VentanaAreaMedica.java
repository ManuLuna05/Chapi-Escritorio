// VentanaAreaMedica.java
package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import org.example.model.Recordatorios;
import org.example.service.ControladorRecordatorios;

public class VentanaAreaMedica extends JFrame {
    private JList<String> listaRecordatorios;
    private DefaultListModel<String> modeloLista;
    private ControladorRecordatorios controladorRecordatorios;
    private int usuarioID;
    private int usuarioCuidadorID;
    private String tipoUsuario;

    public VentanaAreaMedica(int usuarioID, int usuarioCuidadorID) {
        this.usuarioID = usuarioID;
        this.usuarioCuidadorID = usuarioCuidadorID;
        setTitle("Área Médica");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        controladorRecordatorios = new ControladorRecordatorios();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(cabeceraVentana(), BorderLayout.NORTH);
        mainPanel.add(footerVentana(), BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setPreferredSize(new Dimension(1450, 950));
        contentPanel.setMaximumSize(new Dimension(1450, 950));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel tituloLabel = new JLabel("MEDICACIÓN", JLabel.CENTER);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
        tituloLabel.setForeground(new Color(113, 183, 188));
        titlePanel.add(tituloLabel);
        contentPanel.add(titlePanel);

        contentPanel.add(Box.createVerticalStrut(70));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        searchPanel.setOpaque(false);
        searchPanel.setMaximumSize(new Dimension(1000, 50));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(600, 38));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(113, 183, 188), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton backButton = new JButton("Volver");
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(113, 183, 188));
        backButton.setForeground(Color.WHITE);

        searchPanel.add(searchField);
        searchPanel.add(backButton);

        contentPanel.add(searchPanel);

        contentPanel.add(Box.createVerticalStrut(40));

        // Lista de recordatorios
        modeloLista = new DefaultListModel<>();
        listaRecordatorios = new JList<>(modeloLista);
        listaRecordatorios.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        listaRecordatorios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaRecordatorios.setBorder(BorderFactory.createLineBorder(new Color(113, 183, 188), 2));
        listaRecordatorios.setFixedCellHeight(30);
        JScrollPane scrollPane = new JScrollPane(listaRecordatorios);
        scrollPane.setPreferredSize(new Dimension(900, 600));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(113, 183, 188), 2),
                "Recordatorios de Medicación ",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 18),
                new Color(113, 183, 188)
        ));
        contentPanel.add(scrollPane);

        cargarRecordatorios();

        contentPanel.add(Box.createVerticalStrut(20));

        // Botonera
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton addButton = new JButton("Añadir");
        addButton.setPreferredSize(new Dimension(140, 45));
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addButton.setBackground(new Color(113, 183, 188));
        addButton.setFocusPainted(false);
        addButton.setForeground(Color.WHITE);

        JButton deleteButton = new JButton("Eliminar");
        deleteButton.setPreferredSize(new Dimension(140, 45));
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        deleteButton.setBackground(new Color(113, 183, 188));
        deleteButton.setFocusPainted(false);
        deleteButton.setForeground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(Box.createHorizontalStrut(30));
        buttonPanel.add(deleteButton);

        contentPanel.add(buttonPanel);

        centerPanel.add(contentPanel, new GridBagConstraints());

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Listeners
        backButton.addActionListener(e -> {
            new VentanaPrincipal(usuarioID, tipoUsuario).setVisible(true);
            dispose();
        });

        addButton.addActionListener(e -> {
            VentanaAgregarMedicacion ventana = new VentanaAgregarMedicacion(usuarioID, usuarioCuidadorID, this);
            ventana.setVisible(true);
        });

        deleteButton.addActionListener(e -> {
            VentanaEliminarMedicación ventanaEliminar = new VentanaEliminarMedicación(usuarioID, usuarioCuidadorID, this);
            ventanaEliminar.setVisible(true);
        });
    }

    void cargarRecordatorios() {
        modeloLista.clear();
        List<Recordatorios> recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID);
        if (recordatorios != null) {
            for (Recordatorios recordatorio : recordatorios) {
                if ("Medicacion".equals(recordatorio.getTipoEvento())) {
                    modeloLista.addElement(recordatorio.toString());
                }
            }
        }
    }

    private JPanel cabeceraVentana() {
        JPanel cabecera = new JPanel(null);
        cabecera.setBackground(new Color(113, 183, 188));
        cabecera.setPreferredSize(new Dimension(getWidth(), 150));

        JButton perfilUsuarioBoton = new JButton();
        try {
            ImageIcon usuarioIcono = new ImageIcon(getClass().getResource("/images/user2.png"));
            Image imagenUsuario = usuarioIcono.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            perfilUsuarioBoton.setIcon(new ImageIcon(imagenUsuario));
        } catch (Exception e) {
            perfilUsuarioBoton.setText("Perfil");
            System.err.println("Error al cargar user.png: " + e.getMessage());
        }

        perfilUsuarioBoton.setBorder(BorderFactory.createEmptyBorder());
        perfilUsuarioBoton.setContentAreaFilled(false);
        perfilUsuarioBoton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        perfilUsuarioBoton.setBounds(673, 50, 50, 50);
        cabecera.add(perfilUsuarioBoton);

        JPopupMenu menuPerfil = new JPopupMenu();
        JMenuItem verPerfilItem = new JMenuItem("Ver Perfil");
        menuPerfil.add(verPerfilItem);

        verPerfilItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Mostrando perfil de usuario..."));

        perfilUsuarioBoton.addActionListener(e -> menuPerfil.show(perfilUsuarioBoton, perfilUsuarioBoton.getWidth() / 2, perfilUsuarioBoton.getHeight() / 2));

        JLabel logoEtiqueta = new JLabel();
        try {
            ImageIcon logoIcono = new ImageIcon(getClass().getResource("/images/chapi_logos_azulOscuro.png"));
            Image logoImagen = logoIcono.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            logoEtiqueta.setIcon(new ImageIcon(logoImagen));
        } catch (Exception e) {
            logoEtiqueta.setText("LOGO APP");
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }
        logoEtiqueta.setBounds(1190, 0, 200, 150);
        cabecera.add(logoEtiqueta);

        return cabecera;
    }

    public void recargarRecordatorios() {
        modeloLista.clear();
        List<Recordatorios> recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID);
        if (recordatorios != null) {
            for (Recordatorios recordatorio : recordatorios) {
                if ("Medicacion".equals(recordatorio.getTipoEvento())) {
                    modeloLista.addElement(recordatorio.toString());
                }
            }
        }
    }

    private JPanel footerVentana() {
        JPanel footer = new JPanel();
        footer.setBackground(new Color(113, 183, 188));
        footer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        footer.add(new JLabel("© 2025 Chapi"));
        return footer;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaAreaMedica(1, 1).setVisible(true));
    }
}
