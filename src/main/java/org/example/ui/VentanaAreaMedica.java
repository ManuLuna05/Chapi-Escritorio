package org.example.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.example.model.Recordatorios;
import org.example.model.Usuario;
import org.example.service.ControladorMedicacion;
import org.example.service.ControladorRecordatorios;
import org.example.service.ControladorUsuarios;

public class VentanaAreaMedica extends JFrame {
    private JList<String> listaRecordatorios;
    private DefaultListModel<String> modeloLista;
    private ControladorRecordatorios controladorRecordatorios;
    private int usuarioID;
    private int usuarioCuidadorID;
    private String tipoUsuario;
    private JTextField searchField;
    private List<Recordatorios> todosRecordatorios = new ArrayList<>();

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public VentanaAreaMedica(int usuarioID, int usuarioCuidadorID) {
        this.usuarioID = usuarioID;
        this.usuarioCuidadorID = usuarioCuidadorID;

        ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
        Usuario usuario = controladorUsuarios.obtenerUsuarioPorId(usuarioID);
        this.tipoUsuario = usuario.getTipo();

        this.controladorRecordatorios = new ControladorRecordatorios();
        controladorRecordatorios.eliminarRecordatoriosPasados(usuarioID);

        ControladorMedicacion controladorMedicacion = new ControladorMedicacion();
        controladorMedicacion.eliminarMedicacionesPasadas(usuarioID);
        if ("cuidador".equals(tipoUsuario)) {
            List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);
            for (int pacienteId : pacientes) {
                controladorMedicacion.eliminarMedicacionesPasadas(pacienteId);
            }
        }

        setTitle("Área Médica");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(cabeceraVentana(), BorderLayout.NORTH);
        add(footerVentana(), BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tituloLabel = new JLabel("MEDICACIÓN", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
        tituloLabel.setForeground(new Color(113, 183, 188));
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(tituloLabel);
        contentPanel.add(Box.createVerticalStrut(50));

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setOpaque(false);

        searchField = new JTextField("Buscar...");
        searchField.setMaximumSize(new Dimension(800, 38));
        searchField.setPreferredSize(new Dimension(600, 38));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(113, 183, 188), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Buscar...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Buscar...");
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarRecordatorios(); }
            public void removeUpdate(DocumentEvent e) { filtrarRecordatorios(); }
            public void changedUpdate(DocumentEvent e) { filtrarRecordatorios(); }
        });

        JButton backButton = new JButton("Volver");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(113, 183, 188));
        backButton.setForeground(Color.WHITE);
        backButton.setMaximumSize(new Dimension(100, 38));

        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(backButton);
        contentPanel.add(searchPanel);
        contentPanel.add(Box.createVerticalStrut(40));

        modeloLista = new DefaultListModel<>();
        listaRecordatorios = new JList<>(modeloLista);
        listaRecordatorios.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        listaRecordatorios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaRecordatorios.setBorder(BorderFactory.createLineBorder(new Color(113, 183, 188), 2));
        listaRecordatorios.setFixedCellHeight(30);

        JScrollPane scrollPane = new JScrollPane(listaRecordatorios);
        scrollPane.setMaximumSize(new Dimension(1000, 400));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(113, 183, 188), 2),
                "Recordatorios de Medicación",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 18),
                new Color(113, 183, 188)
        ));
        contentPanel.add(scrollPane);

        cargarRecordatorios();
        contentPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        JButton addButton = new JButton("Añadir");
        addButton.setPreferredSize(new Dimension(140, 45));
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addButton.setBackground(new Color(113, 183, 188));
        addButton.setForeground(Color.WHITE);

        JButton deleteButton = new JButton("Eliminar");
        deleteButton.setPreferredSize(new Dimension(140, 45));
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        deleteButton.setBackground(new Color(113, 183, 188));
        deleteButton.setForeground(Color.WHITE);

        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.insets = new Insets(0, 10, 0, 10);
        gbcButtons.gridx = 0;
        buttonPanel.add(addButton, gbcButtons);
        gbcButtons.gridx = 1;
        buttonPanel.add(deleteButton, gbcButtons);
        contentPanel.add(buttonPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(contentPanel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        backButton.addActionListener(e -> {
            new VentanaPrincipal(usuarioID, tipoUsuario).setVisible(true);
            dispose();
        });

        addButton.addActionListener(e -> new VentanaAgregarMedicacion(usuarioID, usuarioCuidadorID, this).setVisible(true));
        deleteButton.addActionListener(e -> {

            new VentanaEliminarMedicación(usuarioID, this).setVisible(true);

        });
    }


    void cargarRecordatorios() {
        modeloLista.clear();
        todosRecordatorios.clear();
        List<Recordatorios> recordatorios = new ArrayList<>();

        if ("cuidador".equals(tipoUsuario)) {
            // Obtener los pacientes asignados
            ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
            List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);

            // Obtener recordatorios de cada paciente
            for (Integer pacienteId : pacientes) {
                List<Recordatorios> recordatoriosPaciente = controladorRecordatorios.obtenerRecordatoriosPorUsuario(pacienteId);
                if (recordatoriosPaciente != null) {
                    recordatorios.addAll(recordatoriosPaciente);
                }
            }

            // Añadir recordatorios propios del cuidador
            List<Recordatorios> recordatoriosPropios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID);
            if (recordatoriosPropios != null) {
                recordatorios.addAll(recordatoriosPropios);
            }
        } else {
            // Usuario normal (cuidado)
            recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID);
        }

        // Evitar duplicados usando un Set
        Set<Integer> idsVistos = new HashSet<>();
        if (recordatorios != null) {
            for (Recordatorios recordatorio : recordatorios) {
                if ("Medicacion".equals(recordatorio.getTipoEvento()) &&
                        !idsVistos.contains(recordatorio.getRecordatorioID())) {
                    todosRecordatorios.add(recordatorio);
                    modeloLista.addElement(recordatorio.toString());
                    idsVistos.add(recordatorio.getRecordatorioID());
                }
            }
        }
    }

    private void filtrarRecordatorios() {
        String textoBusqueda = searchField.getText().toLowerCase();

        // Si es el texto del placeholder o está vacío, mostrar todos
        if (textoBusqueda.equals("buscar...") || textoBusqueda.isEmpty()) {
            cargarTodosRecordatorios();
            return;
        }

        modeloLista.clear();
        for (Recordatorios recordatorio : todosRecordatorios) {
            if (recordatorio.toString().toLowerCase().contains(textoBusqueda)) {
                modeloLista.addElement(recordatorio.toString());
            }
        }
    }

    private void cargarTodosRecordatorios() {
        modeloLista.clear();
        for (Recordatorios recordatorio : todosRecordatorios) {
            modeloLista.addElement(recordatorio.toString());
        }
    }

    private JPanel cabeceraVentana() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(new Color(113, 183, 188));
        cabecera.setPreferredSize(new Dimension(0, 150));

        JPanel panelIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 45));
        panelIzq.setOpaque(false);

        JButton perfilBoton = new JButton();
        try {
            ImageIcon icono = new ImageIcon(getClass().getResource("/images/user2.png"));
            perfilBoton.setIcon(new ImageIcon(icono.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            perfilBoton.setText("Perfil");
        }
        perfilBoton.setBorder(BorderFactory.createEmptyBorder());
        perfilBoton.setContentAreaFilled(false);
        perfilBoton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Obtener nombre de usuario logado
        String nombreUsuario = "";
        ControladorUsuarios controlador = new ControladorUsuarios();
        Usuario u = controlador.obtenerUsuarioPorId(usuarioID);
        nombreUsuario = u.getNombre();

        JLabel texto = new JLabel("Tus Datos: " + nombreUsuario);
        texto.setFont(new Font("Segoe UI", Font.BOLD, 24));
        texto.setForeground(Color.WHITE);

        panelIzq.add(perfilBoton);
        panelIzq.add(texto);

        // Menú desplegable del botón de perfil
        JPopupMenu menuPerfil = new JPopupMenu();
        JMenuItem verDatos = new JMenuItem("Ver Datos");
        JMenuItem cerrarSesion = new JMenuItem("Cerrar Sesión");

        menuPerfil.add(verDatos);
        menuPerfil.add(cerrarSesion);

        perfilBoton.addActionListener(e -> {
            menuPerfil.show(perfilBoton, perfilBoton.getWidth(), 0);
        });

        verDatos.addActionListener(e -> {
            new VentanaPerfilUsuario(usuarioID, tipoUsuario, "medica").setVisible(true);
            dispose();
        });

        cerrarSesion.addActionListener(e -> {
            new VentanaInicioSesion().setVisible(true);
            dispose();
        });

        // Panel centro con logo centrado
        JPanel panelCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelCentro.setOpaque(false);

        JLabel logo = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/chapi_logos_azulOscuro.png"));
            logo.setIcon(new ImageIcon(logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            logo.setText("LOGO");
        }
        panelCentro.add(logo);

        // Panel derecho vacío para equilibrio
        JPanel panelDer = new JPanel();
        panelDer.setOpaque(false);
        panelDer.setPreferredSize(new Dimension(250, 150));

        cabecera.add(panelIzq, BorderLayout.WEST);
        cabecera.add(panelCentro, BorderLayout.CENTER);
        cabecera.add(panelDer, BorderLayout.EAST);

        return cabecera;
    }

    private JPanel footerVentana() {
        JPanel footer = new JPanel();
        footer.setBackground(new Color(113, 183, 188));
        footer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        footer.add(new JLabel("© 2025 Chapi"));
        return footer;
    }
}