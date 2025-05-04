
package org.example.ui;

import org.example.model.Recordatorios;
import org.example.service.ControladorRecordatorios;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private JTextArea textoArea;
    private int usuarioID;
    private int usuarioCuidadorID;
    private String tipoUsuario;
    private List<Integer> usuarioCuidado = new ArrayList<>();

    public VentanaPrincipal(int usuarioID, String tipoUsuario) {
        this.usuarioID = usuarioID;
        this.tipoUsuario = tipoUsuario;

        // Si es cuidador, obtener sus pacientes
        if ("cuidador".equals(tipoUsuario)) {
            try {
                ControladorUsuarios controlador = new ControladorUsuarios();
                this.usuarioCuidado = controlador.obtenerPacientesDeCuidador(usuarioID);
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

        // Eliminar recordatorios pasados
        ControladorRecordatorios controladorRecordatorios = new ControladorRecordatorios();
        controladorRecordatorios.eliminarRecordatoriosPasados(usuarioID);

        // Cargar los recordatorios del día desde la base de datos
        List<String> recordatorios = new ArrayList<>();
        List<Recordatorios> listaRecordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID);
        for (Recordatorios recordatorio : listaRecordatorios) {
            if (recordatorio.getFecha().isEqual(java.time.LocalDate.now())) {
                recordatorios.add(recordatorio.getDescripcion());
            }
        }

        // Cabecera, panel principal y footer
        add(cabeceraVentana(), BorderLayout.NORTH);
        add(panelPrincipal(recordatorios), BorderLayout.CENTER);
        add(footerVentana(), BorderLayout.SOUTH);
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

        //Crear el menú desplegable
        JPopupMenu menuPerfil = new JPopupMenu();
        JMenuItem verPerfilItem = new JMenuItem("Ver Perfil");
        menuPerfil.add(verPerfilItem);

        //Acción al hacer clic en la opción del menú
        verPerfilItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(VentanaPrincipal.this, "Mostrando perfil de usuario...");
        });

        //Mostrar el menú desplegable al hacer clic en el botón de perfil
        perfilUsuarioBoton.addActionListener(e -> menuPerfil.show(perfilUsuarioBoton, perfilUsuarioBoton.getWidth() / 2, perfilUsuarioBoton.getHeight() / 2));

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

    private JPanel panelPrincipal(List<String> recordatorios) {
        //Panel contenedor principal
        JPanel contenedorPrincipal = new JPanel(new GridBagLayout());
        contenedorPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        contenedorPrincipal.setBackground(Color.WHITE);

        //Panel contenedor para los elementos
        JPanel panelElementos = new JPanel(new GridLayout(2, 2, 15, 15));
        panelElementos.setPreferredSize(new Dimension(1200, 800)); //Tamaño del panel
        panelElementos.setBackground(Color.WHITE);

        //Creación de las distintas secciones de la aplicación
        JPanel recordatoriosPanel = configuracionSecciones("Recordatorios", "/images/recordatorios.png", false, recordatorios);
        JPanel areaMedicaPanel = configuracionSecciones("Área Médica", "/images/seccionMedicacion.png", true, null);
        JPanel areaFisicaPanel = configuracionSecciones("Área Física", "/images/areaFisica.png", true, null);
        JPanel citasMedicasPanel = configuracionSecciones("Citas Médicas", "/images/citasMedicas.png", true, null);

        //Se agregan las secciones al panel principal de elementos
        panelElementos.add(recordatoriosPanel);
        panelElementos.add(areaMedicaPanel);
        panelElementos.add(areaFisicaPanel);
        panelElementos.add(citasMedicasPanel);

        //Configuración de GridBagConstraints para el panel de elementos
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        //Se agrega el panel de elementos al contenedor principal
        contenedorPrincipal.add(panelElementos, gbc);

        return contenedorPrincipal;
    }

    private JPanel configuracionSecciones(String titulo, String rutaImagen, boolean clickable, List<String> recordatorios) {
        if (titulo.equals("Recordatorios")) {
            //Configuración del panel de recordatorios
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            panel.setPreferredSize(new Dimension(250, 180));

            //Cuadro de texto para los recordatorios
            textoArea = new JTextArea();
            textoArea.setEditable(false);
            textoArea.setLineWrap(true);
            textoArea.setWrapStyleWord(true);
            textoArea.setFont(new Font("Arial", Font.PLAIN, 14));
            textoArea.setText("Aquí aparecerán los recordatorios...");

            if (recordatorios != null && !recordatorios.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String recordatorio : recordatorios) {
                    sb.append(recordatorio).append("\n");
                }
                textoArea.setText(sb.toString());
            }

            //Agregar el cuadro de texto al panel
            panel.add(new JScrollPane(textoArea), BorderLayout.CENTER);

            //Configuración del título de la sección
            JLabel tituloEtiqueta = new JLabel(titulo, SwingConstants.CENTER);
            tituloEtiqueta.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(tituloEtiqueta, BorderLayout.SOUTH);

            return panel;
        } else {
            //Configuración del panel de otras secciones
            JPanel panel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    try {
                        ImageIcon imagenIcono = new ImageIcon(getClass().getResource(rutaImagen));
                        Image imagen = imagenIcono.getImage();
                        g.drawImage(imagen, 0, 0, getWidth(), getHeight() - 50, this); //Ajustar la altura de la imagen
                    } catch (Exception e) {
                        g.drawString("Imagen no disponible", getWidth() / 2 - 50, getHeight() / 2);
                        System.err.println("Error al cargar la imagen: " + e.getMessage());
                    }
                }
            };
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            panel.setPreferredSize(new Dimension(250, 180));

            //Configuración del título de la sección
            JLabel tituloEtiqueta = new JLabel(titulo, SwingConstants.CENTER);
            tituloEtiqueta.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(tituloEtiqueta, BorderLayout.SOUTH);

            //Configuración del icono de la sección en caso de que sea clickable
            if (clickable) {
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); //Cambia el cursor al pasar por encima
                //Configuración de la acción al hacer clic
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (titulo.equals("Área Médica")) {
                            dispose();
                            new VentanaAreaMedica(usuarioID, usuarioCuidadorID).setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                    "Accediendo a: " + titulo);
                        }
                    }
                });
            }

            return panel;
        }
    }

    private JPanel footerVentana() {
        //Creación y configuración del panel contenedor para el footer
        JPanel footer = new JPanel();
        footer.setBackground(new Color(113, 183, 188));
        footer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        footer.add(new JLabel("© 2025 Chapi"));
        return footer;
    }
}