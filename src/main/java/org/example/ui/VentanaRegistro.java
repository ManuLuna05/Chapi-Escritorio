package org.example.ui;

import org.example.model.Usuario;
import org.example.model.UsuarioCuidador;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class VentanaRegistro extends JFrame {
    public VentanaRegistro() {
        setTitle("Registro de Usuario");
        setSize(500, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelPrincipal.setBackground(Color.WHITE);

        // Título
        JLabel etiquetaTitulo = new JLabel("Registrar");
        etiquetaTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        etiquetaTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(etiquetaTitulo);
        panelPrincipal.add(Box.createVerticalStrut(20));

        // Formulario de registro
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 10, 15)); // Se añadió una fila más
        panelFormulario.setOpaque(false);
        panelFormulario.setMaximumSize(new Dimension(400, 330));

        String[] nombreCampos = {"Nombre:", "Apellidos:", "Email:", "Contraseña:", "Teléfono:"};
        JTextField[] campos = {
                new JTextField(), new JTextField(), new JTextField(),
                new JPasswordField(), new JTextField()
        };

        for (int i = 0; i < nombreCampos.length; i++) {
            JLabel label = new JLabel(nombreCampos[i]);
            label.setFont(new Font("Arial", Font.PLAIN, 16));
            panelFormulario.add(label);

            campos[i].setFont(new Font("Arial", Font.PLAIN, 16));
            campos[i].setPreferredSize(new Dimension(300, 35));
            panelFormulario.add(campos[i]);
        }

        // Campo para el correo del usuario cuidado
        JLabel labelUsuarioCuidado = new JLabel("Correo usuario cuidado:");
        labelUsuarioCuidado.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField campoUsuarioCuidado = new JTextField();
        campoUsuarioCuidado.setFont(new Font("Arial", Font.PLAIN, 16));
        campoUsuarioCuidado.setPreferredSize(new Dimension(300, 35));
        labelUsuarioCuidado.setVisible(false);
        campoUsuarioCuidado.setVisible(false);
        panelFormulario.add(labelUsuarioCuidado);
        panelFormulario.add(campoUsuarioCuidado);

        panelPrincipal.add(panelFormulario);
        panelPrincipal.add(Box.createVerticalStrut(20));

        // Checkbox para cuidador
        JCheckBox seleccionaCuidador = new JCheckBox("Soy un usuario cuidador");
        seleccionaCuidador.setFont(new Font("Arial", Font.PLAIN, 16));
        seleccionaCuidador.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(seleccionaCuidador);
        panelPrincipal.add(Box.createVerticalStrut(20));

        // Mostrar/Ocultar campo de usuario cuidado
        seleccionaCuidador.addItemListener(e -> {
            boolean visible = seleccionaCuidador.isSelected();
            labelUsuarioCuidado.setVisible(visible);
            campoUsuarioCuidado.setVisible(visible);
        });

        // Botones
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBoton.setOpaque(false);

        JButton botonAceptar = new JButton("Aceptar");
        botonAceptar.setFont(new Font("Arial", Font.BOLD, 14));
        botonAceptar.setPreferredSize(new Dimension(150, 40));
        botonAceptar.setBackground(new Color(113, 183, 188));
        botonAceptar.setForeground(Color.WHITE);

        JButton botonVolver = new JButton("Volver");
        botonVolver.setFont(new Font("Arial", Font.BOLD, 14));
        botonVolver.setPreferredSize(new Dimension(150, 40));
        botonVolver.setBackground(new Color(113, 183, 188));
        botonVolver.setForeground(Color.WHITE);

        // Acción botón Aceptar
        botonAceptar.addActionListener(e -> {
            boolean camposVacios = false;
            for (int i = 0; i < campos.length; i++) {
                if (campos[i].getText().trim().isEmpty()) {
                    camposVacios = true;
                    break;
                }
            }

            if (camposVacios) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
            try {
                if (seleccionaCuidador.isSelected()) {
                    String correoUsuarioCuidado = campoUsuarioCuidado.getText().trim();
                    if (correoUsuarioCuidado.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Por favor ingrese el correo del usuario cuidado", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Crear el cuidador como usuario
                    Usuario usuarioCuidador = new Usuario(0,
                            campos[0].getText(),
                            campos[1].getText(),
                            campos[2].getText(),
                            new String(((JPasswordField) campos[3]).getPassword()),
                            campos[4].getText(),
                            "cuidador");

                    controladorUsuarios.registrarUsuario(usuarioCuidador); // esto asigna el ID dentro

                    int cuidadorId = controladorUsuarios.obtenerUsuarioIdPorCorreo(usuarioCuidador.getEmail());
                    int usuarioCuidadoId = controladorUsuarios.obtenerUsuarioIdPorCorreo(correoUsuarioCuidado);

                    // Registrar la relación
                    UsuarioCuidador relacion = new UsuarioCuidador();
                    relacion.setUsuarioId(usuarioCuidadoId); // ID del usuario cuidado
                    relacion.setCuidadorId(cuidadorId); // ID del cuidador

                    controladorUsuarios.registrarUsuario(relacion);

                } else {
                    Usuario usuario = new Usuario(0,
                            campos[0].getText(),
                            campos[1].getText(),
                            campos[2].getText(),
                            new String(((JPasswordField) campos[3]).getPassword()),
                            campos[4].getText(),
                            "cuidado");

                    controladorUsuarios.registrarUsuario(usuario);
                }

                JOptionPane.showMessageDialog(this, "Registro exitoso");
                new VentanaInicioSesion().setVisible(true);
                dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar el usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });



        // Acción botón Volver
        botonVolver.addActionListener(e -> {
            new VentanaAcceso().setVisible(true);
            dispose();
        });

        panelBoton.add(botonAceptar);
        panelBoton.add(botonVolver);
        panelPrincipal.add(panelBoton);
        panelPrincipal.add(Box.createVerticalGlue());

        add(panelPrincipal);
    }
}
