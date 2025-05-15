package org.example.ui;

import org.example.model.Recordatorios;
import org.example.model.Usuario;
import org.example.service.ControladorRecordatorios;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
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

    public VentanaPrincipal(int usuarioID, String tipoUsuario) throws SQLException {
        this.usuarioID = usuarioID;
        this.tipoUsuario = tipoUsuario;
        this.controladorRecordatorios = new ControladorRecordatorios();

        if ("cuidador".equals(tipoUsuario)) {
            ControladorUsuarios controlador = new ControladorUsuarios();
            this.pacientesAsignados = controlador.obtenerPacientesDeCuidador(usuarioID);
        }

        setTitle("Chapi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

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

    private List<String> obtenerRecordatoriosDelDia() {
        List<String> recordatoriosHoy = new ArrayList<>();
        List<Recordatorios> todosRecordatorios = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        if ("cuidador".equals(tipoUsuario)) {
            for (Integer pacienteId : pacientesAsignados) {
                todosRecordatorios.addAll(controladorRecordatorios.obtenerRecordatoriosPorUsuario(pacienteId));
            }
        }
        todosRecordatorios.addAll(controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID));

        for (Recordatorios r : todosRecordatorios) {
            if (r.getFecha().isEqual(hoy)) {
                String texto = r.getHora() + " - " + r.getDescripcion();
                if (!recordatoriosHoy.contains(texto)) {
                    recordatoriosHoy.add(texto);
                }
            }
        }
        return recordatoriosHoy;
    }

    private JPanel cabeceraVentana() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(new Color(113, 183, 188));
        cabecera.setPreferredSize(new Dimension(0, 150));

        // Panel izquierdo con icono + texto
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

        String nombreUsuario = "";
        try {
            ControladorUsuarios controlador = new ControladorUsuarios();
            Usuario u = controlador.obtenerUsuarioPorId(usuarioID);
            nombreUsuario = u.getNombre();
        } catch (SQLException e) {
            nombreUsuario = "Usuario";
        }

        JLabel texto = new JLabel("Tus Datos: " + nombreUsuario);
        texto.setFont(new Font("Segoe UI", Font.BOLD, 24));
        texto.setForeground(Color.WHITE);

        panelIzq.add(perfilBoton);
        panelIzq.add(texto);

        // Menú desplegable
        JPopupMenu menuPerfil = new JPopupMenu();
        JMenuItem verDatos = new JMenuItem("Ver Datos");
        JMenuItem cerrarSesion = new JMenuItem("Cerrar Sesión");

        menuPerfil.add(verDatos);
        menuPerfil.add(cerrarSesion);

        perfilBoton.addActionListener(e -> menuPerfil.show(perfilBoton, perfilBoton.getWidth(), 0));

        verDatos.addActionListener(e -> {
            try {
                new VentanaPerfilUsuario(usuarioID, tipoUsuario, "principal").setVisible(true);
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir el perfil.");
            }
        });

        cerrarSesion.addActionListener(e -> {
            new VentanaInicioSesion().setVisible(true);
            dispose();
        });

        // Panel central con logo
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

        // Panel derecho vacío para equilibrar el ancho del izquierdo
        JPanel panelDer = new JPanel();
        panelDer.setOpaque(false);
        panelDer.setPreferredSize(new Dimension(400, 150)); // ajusta este valor según lo que ocupe el texto largo

        cabecera.add(panelIzq, BorderLayout.WEST);
        cabecera.add(panelCentro, BorderLayout.CENTER);
        cabecera.add(panelDer, BorderLayout.EAST);

        return cabecera;
    }



    private JPanel panelPrincipal(List<String> recordatorios) {
        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(Color.WHITE);

        JPanel grid = new JPanel(new GridLayout(2, 2, 25, 25));
        grid.setBackground(Color.WHITE);
        grid.setMaximumSize(new Dimension(1000, 600));
        grid.setPreferredSize(new Dimension(900, 500));
        grid.setMinimumSize(new Dimension(600, 400));

        grid.add(crearSeccion("Recordatorios", null, false, recordatorios));
        grid.add(crearSeccion("Área Médica", "/images/seccionMedicacion.png", true, null));
        grid.add(crearSeccion("Área Física", "/images/areaFisica.png", true, null));
        grid.add(crearSeccion("Citas Médicas", "/images/citasMedicas.png", true, null));

        contenedor.add(grid);
        return contenedor;
    }

    private JPanel crearSeccion(String titulo, String rutaImagen, boolean clickable, List<String> recordatorios) {
        JPanel panel;

        if ("Recordatorios".equals(titulo)) {
            panel = new JPanel(new BorderLayout());
            textoArea = new JTextArea();
            textoArea.setEditable(false);
            textoArea.setFont(new Font("Arial", Font.PLAIN, 16));
            textoArea.setLineWrap(true);
            textoArea.setWrapStyleWord(true);

            if (recordatorios != null && !recordatorios.isEmpty()) {
                textoArea.setText(String.join("\n", recordatorios));
            } else {
                textoArea.setText("Aquí aparecerán los recordatorios...");
            }

            panel.add(new JScrollPane(textoArea), BorderLayout.CENTER);
        } else {
            panel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    try {
                        ImageIcon icono = new ImageIcon(getClass().getResource(rutaImagen));
                        Image img = icono.getImage();
                        g.drawImage(img, 0, 0, getWidth(), getHeight() - 40, this);
                    } catch (Exception e) {
                        g.drawString("Sin imagen", getWidth() / 2 - 30, getHeight() / 2);
                    }
                }
            };

            if (clickable) {
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            switch (titulo) {
                                case "Área Médica" -> new VentanaAreaMedica(usuarioID, usuarioCuidadorID).setVisible(true);
                                case "Área Física" -> new VentanaAreaFisica(usuarioID, usuarioCuidadorID).setVisible(true);
                                case "Citas Médicas" -> new VentanaCitasMedicas(usuarioID, usuarioCuidadorID).setVisible(true);
                            }
                            dispose();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(panel, "Error al abrir la ventana.");
                        }
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
