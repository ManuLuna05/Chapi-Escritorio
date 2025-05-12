// VentanaPrincipal adaptada para ajustarse proporcionalmente a cualquier pantalla

package org.example.ui;

import org.example.model.Recordatorios;
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
            try {
                ControladorUsuarios controlador = new ControladorUsuarios();
                this.pacientesAsignados = controlador.obtenerPacientesDeCuidador(usuarioID);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al cargar pacientes: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        setTitle("Chapi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        if ("cuidador".equals(tipoUsuario)) {
            for (Integer pacienteId : pacientesAsignados) {
                controladorRecordatorios.eliminarRecordatoriosPasados(pacienteId);
            }
            controladorRecordatorios.eliminarRecordatoriosPasados(usuarioID);
        } else {
            controladorRecordatorios.eliminarRecordatoriosPasados(usuarioID);
        }

        List<String> recordatoriosHoy = obtenerRecordatoriosDelDia();

        add(cabeceraVentana(), BorderLayout.NORTH);
        add(panelPrincipal(recordatoriosHoy), BorderLayout.CENTER);
        add(footerVentana(), BorderLayout.SOUTH);
    }

    private List<String> obtenerRecordatoriosDelDia() throws SQLException {
        List<String> recordatoriosHoy = new ArrayList<>();
        List<Recordatorios> todosRecordatorios;
        LocalDate hoy = LocalDate.now();

        if ("cuidador".equals(tipoUsuario)) {
            todosRecordatorios = new ArrayList<>();
            for (Integer pacienteId : pacientesAsignados) {
                List<Recordatorios> recordatoriosPaciente = controladorRecordatorios.obtenerRecordatoriosPorUsuario(pacienteId);
                if (recordatoriosPaciente != null) {
                    todosRecordatorios.addAll(recordatoriosPaciente);
                }
            }
            List<Recordatorios> recordatoriosPropios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID);
            if (recordatoriosPropios != null) {
                todosRecordatorios.addAll(recordatoriosPropios);
            }
        } else {
            todosRecordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID);
        }

        for (Recordatorios recordatorio : todosRecordatorios) {
            if (recordatorio.getFecha().isEqual(hoy)) {
                String textoCompleto = recordatorio.getHora().toString() + " - " + recordatorio.getDescripcion();
                if (!recordatoriosHoy.contains(textoCompleto)) {
                    recordatoriosHoy.add(textoCompleto);
                }
            }
        }
        return recordatoriosHoy;
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
        }

        perfilUsuarioBoton.setBorder(BorderFactory.createEmptyBorder());
        perfilUsuarioBoton.setContentAreaFilled(false);
        perfilUsuarioBoton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        perfilUsuarioBoton.setBounds(673, 50, 50, 50);
        cabecera.add(perfilUsuarioBoton);

        JLabel textoPerfil = new JLabel("Tus Datos");
        textoPerfil.setFont(new Font("Segoe UI", Font.BOLD, 24));
        textoPerfil.setForeground(Color.WHITE);
        textoPerfil.setBounds(733, 55, 130, 40);
        cabecera.add(textoPerfil);

        perfilUsuarioBoton.addActionListener(e -> {
            try {
                new VentanaPerfilUsuario(usuarioID, tipoUsuario, "principal").setVisible(true);
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir el perfil: " + ex.getMessage());
            }
        });

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

    private JPanel panelPrincipal(List<String> recordatorios) {
        JPanel contenedorPrincipal = new JPanel(new GridBagLayout());
        contenedorPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        contenedorPrincipal.setBackground(Color.WHITE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int ancho = (int) (screenSize.width * 0.75);
        int alto = (int) (screenSize.height * 0.7);

        JPanel panelElementos = new JPanel(new GridLayout(2, 2, 15, 15));
        panelElementos.setPreferredSize(new Dimension(ancho, alto));
        panelElementos.setBackground(Color.WHITE);

        panelElementos.add(configuracionSecciones("Recordatorios", "/images/recordatorios.png", false, recordatorios));
        panelElementos.add(configuracionSecciones("Área Médica", "/images/seccionMedicacion.png", true, null));
        panelElementos.add(configuracionSecciones("Área Física", "/images/areaFisica.png", true, null));
        panelElementos.add(configuracionSecciones("Citas Médicas", "/images/citasMedicas.png", true, null));

        contenedorPrincipal.add(panelElementos);

        return contenedorPrincipal;
    }

    private JPanel configuracionSecciones(String titulo, String rutaImagen, boolean clickable, List<String> recordatorios) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon imagenIcono = new ImageIcon(getClass().getResource(rutaImagen));
                    Image imagen = imagenIcono.getImage();
                    g.drawImage(imagen, 0, 0, getWidth(), getHeight() - 50, this);
                } catch (Exception e) {
                    g.drawString("Imagen no disponible", getWidth() / 2 - 50, getHeight() / 2);
                }
            }
        };

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(250, 180));

        if (titulo.equals("Recordatorios")) {
            textoArea = new JTextArea();
            textoArea.setEditable(false);
            textoArea.setLineWrap(true);
            textoArea.setWrapStyleWord(true);
            textoArea.setFont(new Font("Arial", Font.PLAIN, 18));
            textoArea.setText("Aquí aparecerán los recordatorios...");

            if (recordatorios != null && !recordatorios.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String recordatorio : recordatorios) {
                    sb.append(recordatorio).append("\n");
                }
                textoArea.setText(sb.toString());
            }

            panel.add(new JScrollPane(textoArea), BorderLayout.CENTER);
        }

        JLabel tituloEtiqueta = new JLabel(titulo, SwingConstants.CENTER);
        tituloEtiqueta.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(tituloEtiqueta, BorderLayout.SOUTH);

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
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                }
            });
        }

        return panel;
    }

    private JPanel footerVentana() {
        JPanel footer = new JPanel();
        footer.setBackground(new Color(113, 183, 188));
        footer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        footer.add(new JLabel("© 2025 Chapi"));
        return footer;
    }
}
