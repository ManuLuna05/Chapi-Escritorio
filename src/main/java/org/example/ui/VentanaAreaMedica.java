package org.example.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
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

    public VentanaAreaMedica(int usuarioID, int usuarioCuidadorID) throws SQLException {
        this.usuarioID = usuarioID;
        this.usuarioCuidadorID = usuarioCuidadorID;

        // Determinar el tipo de usuario
        ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
        Usuario usuario = controladorUsuarios.obtenerUsuarioPorId(usuarioID);
        this.tipoUsuario = usuario.getTipo();

        // Inicializar el controlador de recordatorios
        this.controladorRecordatorios = new ControladorRecordatorios();

        // Eliminar recordatorios pasados al abrir el área médica
        this.controladorRecordatorios.eliminarRecordatoriosPasados(usuarioID);

        ControladorMedicacion controladorMedicacion = new ControladorMedicacion();

        // Elimina las propias
        controladorMedicacion.eliminarMedicacionesPasadas(usuarioID);

        // Si es cuidador, también elimina de sus pacientes
        if ("cuidador".equals(tipoUsuario)) {
            List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);
            for (int pacienteId : pacientes) {
                controladorMedicacion.eliminarMedicacionesPasadas(pacienteId);
            }
        }


        setTitle("Área Médica");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

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

        searchField = new JTextField("Buscar...");
        searchField.setPreferredSize(new Dimension(600, 38));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(113, 183, 188), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Listeners para el placeholder
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Buscar...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Buscar...");
                }
            }
        });

        // Listener para búsqueda en tiempo real
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarRecordatorios();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarRecordatorios();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarRecordatorios();
            }
        });

        JButton backButton = new JButton("Volver");
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(113, 183, 188));
        backButton.setFocusPainted(false);
        backButton.setForeground(Color.WHITE);
        backButton.setContentAreaFilled(true);
        backButton.setOpaque(true);

        searchPanel.add(searchField);
        searchPanel.add(backButton);

        contentPanel.add(searchPanel);

        contentPanel.add(Box.createVerticalStrut(40));

        // Lista de recordatorios
        modeloLista = new DefaultListModel<>();
        listaRecordatorios = new JList<>(modeloLista);
        listaRecordatorios.setFont(new Font("Segoe UI", Font.PLAIN, 18));
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
        addButton.setContentAreaFilled(true);
        addButton.setOpaque(true);

        JButton deleteButton = new JButton("Eliminar");
        deleteButton.setPreferredSize(new Dimension(140, 45));
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        deleteButton.setBackground(new Color(113, 183, 188));
        deleteButton.setFocusPainted(false);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setContentAreaFilled(true);
        deleteButton.setOpaque(true);

        buttonPanel.add(addButton);
        buttonPanel.add(Box.createHorizontalStrut(30));
        buttonPanel.add(deleteButton);

        contentPanel.add(buttonPanel);

        centerPanel.add(contentPanel, new GridBagConstraints());

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Listeners
        backButton.addActionListener(e -> {
            try {
                new VentanaPrincipal(usuarioID, tipoUsuario).setVisible(true);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            dispose();
        });

        addButton.addActionListener(e -> {
            VentanaAgregarMedicacion ventana = new VentanaAgregarMedicacion(usuarioID, usuarioCuidadorID, this);
            ventana.setVisible(true);
        });

        deleteButton.addActionListener(e -> {
            try {
                VentanaEliminarMedicación ventanaEliminar = new VentanaEliminarMedicación(usuarioID, this);
                ventanaEliminar.setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir ventana de eliminación: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    void cargarRecordatorios() throws SQLException {
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
        //Panel contenedor para la cabecera
        JPanel cabecera = new JPanel(null);
        cabecera.setBackground(new Color(113, 183, 188)); //Color de la cabecera
        cabecera.setPreferredSize(new Dimension(getWidth(), 150)); //Tamaño que tendrá la cabecera

        //Botón de usuario con imagen
        JButton perfilUsuarioBoton = new JButton();
        try {
            ImageIcon usuarioIcono = new ImageIcon(getClass().getResource("/images/user2.png"));
            Image imagenUsuario = usuarioIcono.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            perfilUsuarioBoton.setIcon(new ImageIcon(imagenUsuario));
        } catch (Exception e) {
            perfilUsuarioBoton.setText("Perfil");
            System.err.println("Error al cargar user.png: " + e.getMessage());
        }

        //Configuración del botón
        perfilUsuarioBoton.setBorder(BorderFactory.createEmptyBorder());
        perfilUsuarioBoton.setContentAreaFilled(false);
        perfilUsuarioBoton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        perfilUsuarioBoton.setBounds(673, 50, 50, 50); //Ajuste coordenadas icono usuario
        cabecera.add(perfilUsuarioBoton);

        JLabel textoPerfil = new JLabel("Tus Datos");
        textoPerfil.setFont(new Font("Segoe UI", Font.BOLD, 24));
        textoPerfil.setForeground(Color.WHITE);
        textoPerfil.setBounds(733, 55, 130, 40); // Coordenadas a la derecha del icono
        cabecera.add(textoPerfil);

        //Crear el menú desplegable
        JPopupMenu menuPerfil = new JPopupMenu();
        JMenuItem verPerfilItem = new JMenuItem("Ver Perfil");
        menuPerfil.add(verPerfilItem);

        //Acción al hacer clic en la opción del menú
        verPerfilItem.addActionListener(e -> {
            try {
                new VentanaPerfilUsuario(usuarioID, tipoUsuario, "principal").setVisible(true);
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir el perfil: " + ex.getMessage());
            }
        });

        //Mostrar el menú desplegable al hacer clic en el botón de perfil
        perfilUsuarioBoton.addActionListener(e -> {
            try {
                new VentanaPerfilUsuario(usuarioID, tipoUsuario, "principal").setVisible(true);
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir el perfil: " + ex.getMessage());
            }
        });

        //Logo de la aplicación
        JLabel logoEtiqueta = new JLabel();
        try {
            ImageIcon logoIcono = new ImageIcon(getClass().getResource("/images/chapi_logos_azulOscuro.png"));
            Image logoImagen = logoIcono.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            logoEtiqueta.setIcon(new ImageIcon(logoImagen));
        } catch (Exception e) {
            logoEtiqueta.setText("LOGO APP");
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }
        logoEtiqueta.setBounds(1190, 0, 200, 150); //Ajuste de coordenadas del logo
        cabecera.add(logoEtiqueta);

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