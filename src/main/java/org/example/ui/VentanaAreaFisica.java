package org.example.ui;

import org.example.model.Recordatorios;
import org.example.model.Usuario;
import org.example.service.ControladorActividadFisica;
import org.example.service.ControladorRecordatorios;
import org.example.service.ControladorUsuarios;

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

public class VentanaAreaFisica extends JFrame {
    private JList<String> listaRecordatorios;
    private DefaultListModel<String> modeloLista;
    private ControladorRecordatorios controladorRecordatorios;
    private int usuarioID;
    private int usuarioCuidadorID;
    private String tipoUsuario;
    private JTextField campoBusqueda;
    private List<Recordatorios> todosRecordatorios = new ArrayList<>();

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    //Configuración para crear la ventana del área física
    public VentanaAreaFisica(int usuarioID, int usuarioCuidadorID) {
        this.usuarioID = usuarioID;
        this.usuarioCuidadorID = usuarioCuidadorID;

        ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
        Usuario usuario = controladorUsuarios.obtenerUsuarioPorId(usuarioID);
        this.tipoUsuario = usuario.getTipo();

        this.controladorRecordatorios = new ControladorRecordatorios();
        this.controladorRecordatorios.eliminarRecordatoriosPasados(usuarioID);

        ControladorActividadFisica controladorActividadFisica = new ControladorActividadFisica();
        controladorActividadFisica.eliminarActividadesPasadas(usuarioID);

        //Se eliminan las actividades pasadas
        if ("cuidador".equals(tipoUsuario)) {
            List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);
            for (int pacienteId : pacientes) {
                controladorActividadFisica.eliminarActividadesPasadas(pacienteId);
            }
        }

        setTitle("Área Física");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);

        panelPrincipal.add(cabeceraVentana(), BorderLayout.NORTH);
        panelPrincipal.add(footerVentana(), BorderLayout.SOUTH);

        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(Color.WHITE);

        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(Color.WHITE);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelTitulo = new JPanel();
        panelTitulo.setOpaque(false);
        JLabel tituloLabel = new JLabel("ACTIVIDAD FÍSICA", JLabel.CENTER);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
        tituloLabel.setForeground(new Color(113, 183, 188));
        panelTitulo.add(tituloLabel);
        panelContenido.add(panelTitulo);

        panelContenido.add(Box.createVerticalStrut(70));

        JPanel panelBuscador = new JPanel();
        panelBuscador.setLayout(new BoxLayout(panelBuscador, BoxLayout.X_AXIS));
        panelBuscador.setOpaque(false);

        //Configuración de la barra de búsqueda
        campoBusqueda = new JTextField("Buscar...");
        campoBusqueda.setMaximumSize(new Dimension(800, 38));
        campoBusqueda.setPreferredSize(new Dimension(600, 38));
        campoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campoBusqueda.setForeground(Color.GRAY);
        campoBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(113, 183, 188), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        //Se añaden listeners para manejar el foco del campo de búsqueda
        campoBusqueda.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campoBusqueda.getText().equals("Buscar...")) {
                    campoBusqueda.setText("");
                    campoBusqueda.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campoBusqueda.getText().isEmpty()) {
                    campoBusqueda.setForeground(Color.GRAY);
                    campoBusqueda.setText("Buscar...");
                }
            }
        });

        //Se añade un DocumentListener para filtrar recordatorios al escribir en el campo de búsqueda
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarRecordatorios(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarRecordatorios(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarRecordatorios(); }
        });

        JButton botonVolver = new JButton("Volver");
        botonVolver.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botonVolver.setFocusPainted(false);
        botonVolver.setBackground(new Color(113, 183, 188));
        botonVolver.setForeground(Color.WHITE);
        botonVolver.setMaximumSize(new Dimension(100, 38));

        panelBuscador.add(campoBusqueda);
        panelBuscador.add(Box.createRigidArea(new Dimension(10, 0)));
        panelBuscador.add(botonVolver);
        panelContenido.add(panelBuscador);
        panelContenido.add(Box.createVerticalStrut(40));

        modeloLista = new DefaultListModel<>();
        listaRecordatorios = new JList<>(modeloLista);
        listaRecordatorios.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        listaRecordatorios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaRecordatorios.setBorder(BorderFactory.createLineBorder(new Color(113, 183, 188), 2));
        listaRecordatorios.setFixedCellHeight(30);

        //Panel con barra de desplazamiento para mostrar la lista de recordatorios de actividad física
        JScrollPane scrollRecordatorios = new JScrollPane(listaRecordatorios);
        scrollRecordatorios.setMaximumSize(new Dimension(1000, 400));
        scrollRecordatorios.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(113, 183, 188), 2),
                "Recordatorios de Actividad Física ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 18),
                new Color(113, 183, 188)
        ));
        panelContenido.add(scrollRecordatorios);

        cargarRecordatorios();
        panelContenido.add(Box.createVerticalStrut(20));

        //Panel para los botones de añadir y eliminar recordatorios
        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setOpaque(false);

        //Configuración del botón para "Añadir"
        JButton botonAgregar = new JButton("Añadir");
        botonAgregar.setPreferredSize(new Dimension(140, 45));
        botonAgregar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botonAgregar.setBackground(new Color(113, 183, 188));
        botonAgregar.setForeground(Color.WHITE);
        botonAgregar.addActionListener(e -> new VentanaAgregarActividad(usuarioID, usuarioCuidadorID, this).setVisible(true));

        //Configuración del botón para "Eliminar"
        JButton botonEliminar = new JButton("Eliminar");
        botonEliminar.setPreferredSize(new Dimension(140, 45));
        botonEliminar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botonEliminar.setBackground(new Color(113, 183, 188));
        botonEliminar.setForeground(Color.WHITE);
        botonEliminar.addActionListener(e -> {
            new VentanaEliminarActividad(usuarioID, this).setVisible(true);
        });

        GridBagConstraints ajustarBotones = new GridBagConstraints();
        ajustarBotones.insets = new Insets(0, 10, 0, 10);
        ajustarBotones.gridx = 0;
        panelBotones.add(botonAgregar, ajustarBotones);
        ajustarBotones.gridx = 1;
        panelBotones.add(botonEliminar, ajustarBotones);
        panelContenido.add(panelBotones);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        panelCentral.add(panelContenido, gbc);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        add(panelPrincipal);

        //Acción del botón "Volver"
        botonVolver.addActionListener(e -> {
                new VentanaPrincipal(usuarioID, tipoUsuario).setVisible(true);
                dispose();
        });
    }

    //Función para crear la cabecera de la ventana (Explicada a fondo en VentanaPrincipal)
    private JPanel cabeceraVentana() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(new Color(113, 183, 188));
        cabecera.setPreferredSize(new Dimension(0, 150));

        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 45));
        panelIzquierdo.setOpaque(false);

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

        String nombreUsuario = "";
        ControladorUsuarios controlador = new ControladorUsuarios();
        Usuario usuario = controlador.obtenerUsuarioPorId(usuarioID);
        nombreUsuario = usuario.getNombre();

        JLabel texto = new JLabel("Tus Datos: " + nombreUsuario);
        texto.setFont(new Font("Segoe UI", Font.BOLD, 24));
        texto.setForeground(Color.WHITE);

        panelIzquierdo.add(perfilBoton);
        panelIzquierdo.add(texto);

        JPopupMenu menuPerfil = new JPopupMenu();
        JMenuItem verDatos = new JMenuItem("Ver Datos");
        JMenuItem cerrarSesion = new JMenuItem("Cerrar Sesión");

        menuPerfil.add(verDatos);
        menuPerfil.add(cerrarSesion);

        perfilBoton.addActionListener(e -> {
            menuPerfil.show(perfilBoton, 0, perfilBoton.getHeight());
        });

        verDatos.addActionListener(e -> {
            new VentanaPerfilUsuario(usuarioID, tipoUsuario, "fisica").setVisible(true);
            dispose();
        });

        cerrarSesion.addActionListener(e -> {
            new VentanaInicioSesion().setVisible(true);
            dispose();
        });

        JPanel panelCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelCentro.setOpaque(false);

        JLabel logo = new JLabel();
        try {
            ImageIcon logoIcono = new ImageIcon(getClass().getResource("/images/chapi_logos_azulOscuro.png"));
            logo.setIcon(new ImageIcon(logoIcono.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            logo.setText("LOGO");
        }
        panelCentro.add(logo);

        JPanel panelDerecho = new JPanel();
        panelDerecho.setOpaque(false);
        panelDerecho.setPreferredSize(new Dimension(250, 150));

        cabecera.add(panelIzquierdo, BorderLayout.WEST);
        cabecera.add(panelCentro, BorderLayout.CENTER);
        cabecera.add(panelDerecho, BorderLayout.EAST);

        return cabecera;
    }

    private JPanel footerVentana() {
        JPanel footer = new JPanel();
        footer.setBackground(new Color(113, 183, 188));
        footer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        footer.add(new JLabel("© 2025 Chapi"));
        return footer;
    }

    public void recargarRecordatorios() {
        cargarRecordatorios();
    }

    //Función para cargar los recordatorios de actividad física
    void cargarRecordatorios()  {
        modeloLista.clear();
        todosRecordatorios.clear();
        List<Recordatorios> recordatorios = new ArrayList<>();

        //Cargar recordatorios según el tipo de usuario
        if ("cuidador".equals(tipoUsuario)) {
            ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
            List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);

            //Obtener recordatorios del paciente asignado al cuidador
            for (Integer pacienteId : pacientes) {
                List<Recordatorios> recordatoriosPaciente = controladorRecordatorios.obtenerRecordatoriosPorUsuario(pacienteId);
                if (recordatoriosPaciente != null) {
                    recordatorios.addAll(recordatoriosPaciente);
                }
            }

            //Obtener recordatorios creados por el propio cuidador
            List<Recordatorios> recordatoriosPropios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID);
            if (recordatoriosPropios != null) {
                recordatorios.addAll(recordatoriosPropios);
            }
        } else {
            recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID);
        }

        //Filtrar los recordatorios de actividad física y evitar así recordatorios duplicados
        Set<Integer> idsVistos = new HashSet<>();
        if (recordatorios != null) {
            for (Recordatorios recordatorio : recordatorios) {
                if ("ActividadFisica".equals(recordatorio.getTipoEvento()) &&
                        !idsVistos.contains(recordatorio.getRecordatorioID())) {
                    todosRecordatorios.add(recordatorio);
                    modeloLista.addElement(recordatorio.toString());
                    idsVistos.add(recordatorio.getRecordatorioID());
                }
            }
        }
    }

    //Función para filtrar los recordatorios según el texto ingresado en el campo de búsqueda
    private void filtrarRecordatorios() {
        String textoBusqueda = campoBusqueda.getText().toLowerCase();

        //Si el texto es "buscar..." o está vacío, se cargan todos los recordatorios
        if (textoBusqueda.equals("buscar...") || textoBusqueda.isEmpty()) {
            cargarTodosRecordatorios();
            return;
        }

        //Filtrado de los recordatorios que contengan el texto de búsqueda
        modeloLista.clear();
        for (Recordatorios recordatorio : todosRecordatorios) {
            if (recordatorio.toString().toLowerCase().contains(textoBusqueda)) {
                modeloLista.addElement(recordatorio.toString());
            }
        }
    }

    //Función para cargar todos los recordatorios sin filtrar
    private void cargarTodosRecordatorios() {
        modeloLista.clear();
        //Para cada recordatorio en la lista, se añade al modelo de lista
        for (Recordatorios recordatorio : todosRecordatorios) {
            modeloLista.addElement(recordatorio.toString());
        }
    }
}
