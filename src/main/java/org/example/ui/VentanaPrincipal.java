package org.example.ui;

import org.example.model.Recordatorios;
import org.example.model.Usuario;
import org.example.service.ControladorRecordatorios;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private JTextArea textoArea;
    private int usuarioID;
    private int usuarioCuidadorID;
    private String tipoUsuario;
    private List<Integer> pacientesAsignados = new ArrayList<>();
    private ControladorRecordatorios controladorRecordatorios;

    //Ventana principal de la aplicación
    public VentanaPrincipal(int usuarioID, String tipoUsuario) {
        this.usuarioID = usuarioID;
        this.tipoUsuario = tipoUsuario;
        this.controladorRecordatorios = new ControladorRecordatorios();

        //Si el usuario es un cuidador, se obtiene el paciente asignado
        if ("cuidador".equals(tipoUsuario)) {
            ControladorUsuarios controlador = new ControladorUsuarios();
            this.pacientesAsignados = controlador.obtenerPacientesDeCuidador(usuarioID);
        }

        setTitle("Chapi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        //Se eliminan los recordatorios pasados
        if ("cuidador".equals(tipoUsuario)) {
            for (Integer pacienteId : pacientesAsignados) {
                controladorRecordatorios.eliminarRecordatoriosPasados(pacienteId);
            }
        }
        controladorRecordatorios.eliminarRecordatoriosPasados(usuarioID);

        List<String> recordatoriosHoy = obtenerRecordatoriosDelDia();

        add(cabeceraVentana(), BorderLayout.NORTH);
        add(panelPrincipal(recordatoriosHoy), BorderLayout.CENTER);
        add(footerVentana(), BorderLayout.SOUTH);
    }

    //Función que obtiene los recordatorios del día para el usuario actual
    private List<String> obtenerRecordatoriosDelDia() {
        List<Recordatorios> todosRecordatorios = new ArrayList<>();
        List<String> recordatoriosHoy = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        //Si el usuario es un cuidador, se obtienen los recordatorios del paciente asignado
        if ("cuidador".equals(tipoUsuario)) {
            for (Integer pacienteId : pacientesAsignados) {
                todosRecordatorios.addAll(controladorRecordatorios.obtenerRecordatoriosPorUsuario(pacienteId));
            }
        }
        todosRecordatorios.addAll(controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID));

        //Filtrar los recordatorios que son del día actual
        List<Recordatorios> recordatoriosFiltrados = new ArrayList<>();
        for (Recordatorios recordatorio : todosRecordatorios) {
            if (recordatorio.getFecha().isEqual(hoy)) {
                recordatoriosFiltrados.add(recordatorio);
            }
        }

        //Se ordenan los recordatorios por la hora
        recordatoriosFiltrados.sort((r1, r2) -> r1.getHora().compareTo(r2.getHora()));

        //Se crea una lista de recordatorios con el formato "hora - descripción"
        for (Recordatorios recordatorio : recordatoriosFiltrados) {
            String texto = recordatorio.getHora() + " - " + recordatorio.getDescripcion();
            if (!recordatoriosHoy.contains(texto)) {
                recordatoriosHoy.add(texto);
            }
        }

        return recordatoriosHoy;
    }


    //Función que crea la cabecera de la ventana será igual en las ventanas de Área Médica, Área Física y Citas Médicas
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

        //Configuración del nombre de usuario que accede a la aplicación
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

        perfilBoton.addActionListener(e -> menuPerfil.show(perfilBoton, 0, perfilBoton.getHeight()));

        /*Acción para ver los datos del usuario, se pasa id de la ventana para poder volver a la ventana
        desde la que accede a ver los datos del usuario */
        verDatos.addActionListener(e -> {
            new VentanaPerfilUsuario(usuarioID, tipoUsuario, "principal").setVisible(true);
            dispose();
        });

        //Acción para cerrar sesión
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
        panelDerecho.setPreferredSize(new Dimension(400, 150));

        cabecera.add(panelIzquierdo, BorderLayout.WEST);
        cabecera.add(panelCentro, BorderLayout.CENTER);
        cabecera.add(panelDerecho, BorderLayout.EAST);

        return cabecera;
    }

    //Función que crea el panel principal de la ventana
    private JPanel panelPrincipal(List<String> recordatorios) {
        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(Color.WHITE);

        //Ajuste de las distintas secciones
        JPanel ajusteSecciones = new JPanel(new GridLayout(2, 2, 25, 25));
        ajusteSecciones.setBackground(Color.WHITE);
        ajusteSecciones.setMaximumSize(new Dimension(1000, 600));
        ajusteSecciones.setPreferredSize(new Dimension(900, 500));
        ajusteSecciones.setMinimumSize(new Dimension(600, 400));

        ajusteSecciones.add(crearSeccion("Recordatorios", null, false, recordatorios));
        ajusteSecciones.add(crearSeccion("Área Médica", "/images/seccionMedicacion.png", true, null));
        ajusteSecciones.add(crearSeccion("Área Física", "/images/areaFisica.png", true, null));
        ajusteSecciones.add(crearSeccion("Citas Médicas", "/images/citasMedicas.png", true, null));

        contenedor.add(ajusteSecciones);
        return contenedor;
    }

    //Función que crea una sección del panel principal
    private JPanel crearSeccion(String titulo, String rutaImagen, boolean clickable, List<String> recordatorios) {
        JPanel panel;

        //Si la sección es Recordatorios, se crea un JTextArea para mostrar los recordatorios
        if ("Recordatorios".equals(titulo)) {
            panel = new JPanel(new BorderLayout());

            JPanel contenido = new JPanel(new BorderLayout());
            contenido.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            contenido.setOpaque(false);

            textoArea = new JTextArea();
            textoArea.setEditable(false);
            textoArea.setFont(new Font("Arial", Font.PLAIN, 16));
            textoArea.setLineWrap(true);
            textoArea.setWrapStyleWord(true);

            //Si hay recordatorios, se muestran en el JTextArea
            if (recordatorios != null && !recordatorios.isEmpty()) {
                textoArea.setText(String.join("\n", recordatorios));
            } else {
                textoArea.setText("Aquí aparecerán los recordatorios...");
            }

            JScrollPane scroll = new JScrollPane(textoArea);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            contenido.add(scroll, BorderLayout.CENTER);

            panel.add(contenido, BorderLayout.CENTER);
        } else { //Para las demás secciones, se crea un JPanel con una imagen de fondo
            panel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    try {
                        ImageIcon icono = new ImageIcon(getClass().getResource(rutaImagen));
                        Image imagen = icono.getImage();
                        g.drawImage(imagen, 0, 0, getWidth(), getHeight() - 40, this);
                    } catch (Exception e) {
                        g.drawString("Sin imagen", getWidth() / 2 - 30, getHeight() / 2);
                    }
                }
            };

            /*Si la sección es clickeable, se añade un MouseListener para abrir la ventana correspondiente
            dependiendo de la sección seleccionada */
            if (clickable) {
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        switch (titulo) {
                            case "Área Médica" -> new VentanaAreaMedica(usuarioID, usuarioCuidadorID).setVisible(true);
                            case "Área Física" -> new VentanaAreaFisica(usuarioID, usuarioCuidadorID).setVisible(true);
                            case "Citas Médicas" -> new VentanaCitasMedicas(usuarioID, usuarioCuidadorID).setVisible(true);
                        }
                        dispose();
                    }
                });
            }
        }

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JLabel tituloLabel = new JLabel(titulo, SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(tituloLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel footerVentana() {
        JPanel footer = new JPanel();
        footer.setBackground(new Color(113, 183, 188));
        footer.add(new JLabel("© 2025 Chapi"));
        return footer;
    }
}
