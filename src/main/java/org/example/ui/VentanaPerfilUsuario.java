package org.example.ui;

import org.example.model.Usuario;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class VentanaPerfilUsuario extends JFrame {
    private final int usuarioID;
    private final String tipoUsuario;
    private final String ventanaOrigen;

    public VentanaPerfilUsuario(int usuarioID, String tipoUsuario, String ventanaOrigen) throws SQLException {
        this.usuarioID = usuarioID;
        this.tipoUsuario = tipoUsuario;
        this.ventanaOrigen = ventanaOrigen;

        setTitle("Perfil de Usuario");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(cabeceraVentana(), BorderLayout.NORTH);
        add(contenidoPerfil(), BorderLayout.CENTER);
        add(footerVentana(), BorderLayout.SOUTH);
    }

    private JPanel contenidoPerfil() throws SQLException {
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

        JButton btnEditar = new JButton("Modificar Información");
        btnEditar.setPreferredSize(new Dimension(200, 40));
        btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEditar.setBackground(new Color(113, 183, 188));
        btnEditar.setForeground(Color.WHITE);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setPreferredSize(new Dimension(120, 40));
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVolver.setBackground(new Color(113, 183, 188));
        btnVolver.setForeground(Color.WHITE);

        botones.add(btnEditar);
        botones.add(btnVolver);
        panel.add(botones);

        btnEditar.addActionListener(e -> {
            try {
                VentanaEditarPerfil panelEditar = new VentanaEditarPerfil(usuarioID, tipoUsuario, ventanaOrigen);
                JDialog dialogo = new JDialog(this, "Modificar información", true);
                dialogo.setContentPane(panelEditar);
                dialogo.pack();
                dialogo.setSize(900, 1100);
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                dialogo.setLocation(
                        (screenSize.width - dialogo.getWidth()) / 2,
                        (screenSize.height - dialogo.getHeight()) / 2
                );
                dialogo.setResizable(false);
                dialogo.setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir la edición: " + ex.getMessage());
            }
        });

        btnVolver.addActionListener(e -> {
            try {
                switch (ventanaOrigen) {
                    case "principal" -> new VentanaPrincipal(usuarioID, tipoUsuario).setVisible(true);
                    case "citas" -> new VentanaCitasMedicas(usuarioID, usuarioID).setVisible(true);
                    case "medica" -> new VentanaAreaMedica(usuarioID, usuarioID).setVisible(true);
                    case "fisica" -> new VentanaAreaFisica(usuarioID, usuarioID).setVisible(true);
                    default -> new VentanaPrincipal(usuarioID, tipoUsuario).setVisible(true);
                }
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al volver: " + ex.getMessage());
            }
        });

        return panel;
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

    private JPanel etiquetaInfo(String campo, String valor) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);

        JLabel lblCampo = new JLabel(campo + ": ");
        lblCampo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblValor = new JLabel(valor != null ? valor : "No definido");
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        fila.add(lblCampo, BorderLayout.WEST);
        fila.add(lblValor, BorderLayout.CENTER);

        fila.setMaximumSize(new Dimension(500, 30));
        return fila;
    }

    private JPanel cabeceraVentana() {
        JPanel cabecera = new JPanel(null);
        cabecera.setBackground(new Color(113, 183, 188));
        cabecera.setPreferredSize(new Dimension(getWidth(), 150));

        JLabel logoEtiqueta = new JLabel();
        try {
            ImageIcon logoIcono = new ImageIcon(getClass().getResource("/images/chapi_logos_azulOscuro.png"));
            Image logoImagen = logoIcono.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            logoEtiqueta.setIcon(new ImageIcon(logoImagen));
        } catch (Exception e) {
            logoEtiqueta.setText("LOGO APP");
        }
        logoEtiqueta.setBounds(1190, 0, 200, 150);
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