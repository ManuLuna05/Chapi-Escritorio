package org.example.ui;

import org.example.model.Usuario;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;

public class VentanaPerfilUsuario extends JFrame {
    private final int usuarioID;
    private final String tipoUsuario;
    private final String ventanaInicial;

    public VentanaPerfilUsuario(int usuarioID, String tipoUsuario, String ventanaInicial) {
        this.usuarioID = usuarioID;
        this.tipoUsuario = tipoUsuario;
        this.ventanaInicial = ventanaInicial;

        setTitle("Perfil de Usuario");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(cabeceraVentana(), BorderLayout.NORTH);
        add(contenidoPerfil(), BorderLayout.CENTER);
        add(footerVentana(), BorderLayout.SOUTH);
    }

    private JPanel contenidoPerfil() {
        ControladorUsuarios controlador = new ControladorUsuarios();
        Usuario usuario = controlador.obtenerUsuarioPorId(usuarioID);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel titulo = new JLabel("MI PERFIL");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setForeground(new Color(113, 183, 188));
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(30));

        panel.add(crearBloqueUsuario("Mis datos", usuario));

        if ("cuidador".equalsIgnoreCase(tipoUsuario)) {
            java.util.List<Integer> pacientes = controlador.obtenerPacientesDeCuidador(usuarioID);
            if (!pacientes.isEmpty()) {
                Usuario paciente = controlador.obtenerUsuarioPorId(pacientes.get(0));
                panel.add(Box.createVerticalStrut(30));
                panel.add(crearBloqueUsuario("Usuario cuidado asignado", paciente));
            }
        }

        panel.add(Box.createVerticalStrut(30));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        botones.setOpaque(false);

        JButton botonEditar = new JButton("Modificar Información");
        botonEditar.setPreferredSize(new Dimension(200, 40));
        botonEditar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonEditar.setBackground(new Color(113, 183, 188));
        botonEditar.setForeground(Color.WHITE);

        JButton botonVolver = new JButton("Volver");
        botonVolver.setPreferredSize(new Dimension(120, 40));
        botonVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonVolver.setBackground(new Color(113, 183, 188));
        botonVolver.setForeground(Color.WHITE);

        botones.add(botonEditar);
        botones.add(botonVolver);
        panel.add(botones);

        botonEditar.addActionListener(e -> {
            VentanaEditarPerfil panelEditar = new VentanaEditarPerfil(usuarioID, tipoUsuario, ventanaInicial, this);
            JDialog dialogo = new JDialog(this, "Modificar información", true);
            dialogo.setContentPane(panelEditar);
            dialogo.pack();
            dialogo.setSize(900, 1100);
            Dimension dimensionPantalla = Toolkit.getDefaultToolkit().getScreenSize();
            dialogo.setLocation(
                    (dimensionPantalla.width - dialogo.getWidth()) / 2,
                    (dimensionPantalla.height - dialogo.getHeight()) / 2
                );
            dialogo.setResizable(false);
            dialogo.setVisible(true);
        });

        botonVolver.addActionListener(e -> {
            switch (ventanaInicial) {
                case "principal" -> new VentanaPrincipal(usuarioID, tipoUsuario).setVisible(true);
                case "citas" -> new VentanaCitasMedicas(usuarioID, usuarioID).setVisible(true);
                case "medica" -> new VentanaAreaMedica(usuarioID, usuarioID).setVisible(true);
                case "fisica" -> new VentanaAreaFisica(usuarioID, usuarioID).setVisible(true);
                default -> new VentanaPrincipal(usuarioID, tipoUsuario).setVisible(true);
            }
            dispose();
        });

        return panel;
    }

    public void recargarContenido() {
        getContentPane().removeAll();
        add(cabeceraVentana(), BorderLayout.NORTH);
        add(contenidoPerfil(), BorderLayout.CENTER);
        add(footerVentana(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }


    private JPanel crearBloqueUsuario(String titulo, Usuario usuario) {
        JPanel bloque = new JPanel();
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        bloque.setBackground(new Color(245, 245, 245));
        bloque.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(titulo),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        bloque.add(etiquetaInfo("Nombre", usuario.getNombre()));
        bloque.add(etiquetaInfo("Apellidos", usuario.getApellidos()));
        bloque.add(etiquetaInfo("Email", usuario.getEmail()));
        bloque.add(etiquetaInfo("Teléfono", usuario.getTelefono()));

        return bloque;
    }

    private JPanel etiquetaInfo(String campo, String dato) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);

        JLabel etiquetaCampo = new JLabel(campo + ": ");
        etiquetaCampo.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel etiquetaDatos = new JLabel(dato != null ? dato : "No definido");
        etiquetaDatos.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        fila.add(etiquetaCampo, BorderLayout.WEST);
        fila.add(etiquetaDatos, BorderLayout.CENTER);

        fila.setMaximumSize(new Dimension(500, 30));
        return fila;
    }

    private JPanel cabeceraVentana() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(new Color(113, 183, 188));
        cabecera.setPreferredSize(new Dimension(0, 150));

        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setOpaque(false);
        panelIzquierdo.setPreferredSize(new Dimension(250, 150));

        JPanel panelCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelCentro.setOpaque(false);

        JLabel logo = new JLabel();
        try {
            ImageIcon logoIcono = new ImageIcon(getClass().getResource("/images/chapi_logos_azulOscuro.png"));
            Image logoImagen = logoIcono.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(logoImagen));
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
}