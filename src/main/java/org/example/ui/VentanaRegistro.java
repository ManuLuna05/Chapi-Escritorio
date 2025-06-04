package org.example.ui;

import org.example.model.Usuario;
import org.example.model.UsuarioCuidador;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;

public class VentanaRegistro extends JFrame {

    //Ventana de registro de usuario
    public VentanaRegistro() {
        setTitle("Registro de Usuario");
        setSize(500, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelPrincipal.setBackground(Color.WHITE);

        JLabel etiquetaTitulo = new JLabel("Registrar");
        etiquetaTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        etiquetaTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(etiquetaTitulo);
        panelPrincipal.add(Box.createVerticalStrut(20));

        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 10, 15));
        panelFormulario.setOpaque(false);
        panelFormulario.setMaximumSize(new Dimension(400, 330));

        //Definición de los campos del formulario
        String[] nombreCampos = {"Nombre:", "Apellidos:", "Email:", "Contraseña:", "Teléfono:"};
        JTextField[] campos = {
                new JTextField(), new JTextField(), new JTextField(),
                new JPasswordField(), new JTextField()
        };

        //Configuración de los campos del formulario
        for (int i = 0; i < nombreCampos.length; i++) {
            JLabel etiqueta = new JLabel(nombreCampos[i]);
            etiqueta.setFont(new Font("Arial", Font.PLAIN, 16));
            panelFormulario.add(etiqueta);

            campos[i].setFont(new Font("Arial", Font.PLAIN, 16));
            campos[i].setPreferredSize(new Dimension(300, 35));
            panelFormulario.add(campos[i]);
        }

        JLabel etiquetaUsuarioCuidado = new JLabel("Correo usuario cuidado:");
        etiquetaUsuarioCuidado.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField campoUsuarioCuidado = new JTextField();
        campoUsuarioCuidado.setFont(new Font("Arial", Font.PLAIN, 16));
        campoUsuarioCuidado.setPreferredSize(new Dimension(300, 35));
        etiquetaUsuarioCuidado.setVisible(false);
        campoUsuarioCuidado.setVisible(false);
        panelFormulario.add(etiquetaUsuarioCuidado);
        panelFormulario.add(campoUsuarioCuidado);

        panelPrincipal.add(panelFormulario);
        panelPrincipal.add(Box.createVerticalStrut(20));

        /*Checkbox para seleccionar si el usuario es un cuidador, si es así, se muestra el campo para
        el correo del paciente que se le asigna */
        JCheckBox seleccionaCuidador = new JCheckBox("Soy un usuario cuidador");
        seleccionaCuidador.setFont(new Font("Arial", Font.PLAIN, 16));
        seleccionaCuidador.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(seleccionaCuidador);
        panelPrincipal.add(Box.createVerticalStrut(20));

        seleccionaCuidador.addItemListener(e -> {
            boolean visible = seleccionaCuidador.isSelected();
            etiquetaUsuarioCuidado.setVisible(visible);
            campoUsuarioCuidado.setVisible(visible);
        });

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);

        //Configuración del botón "Aceptar"
        JButton botonAceptar = new JButton("Aceptar");
        botonAceptar.setFont(new Font("Arial", Font.BOLD, 14));
        botonAceptar.setPreferredSize(new Dimension(150, 40));
        botonAceptar.setBackground(new Color(113, 183, 188));
        botonAceptar.setForeground(Color.WHITE);

        //Configuración del botón "Volver"
        JButton botonVolver = new JButton("Volver");
        botonVolver.setFont(new Font("Arial", Font.BOLD, 14));
        botonVolver.setPreferredSize(new Dimension(150, 40));
        botonVolver.setBackground(new Color(113, 183, 188));
        botonVolver.setForeground(Color.WHITE);

        //Acción del botón "Aceptar"
        botonAceptar.addActionListener(e -> {
            boolean camposVacios = false;
            for (JTextField campo : campos) { //Validación de campos vacíos
                if (campo.getText().trim().isEmpty()) {
                    camposVacios = true;
                    break;
                }
            }

            //Si hay campos vacíos, se muestra un mensaje de error
            if (camposVacios) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String email = campos[2].getText().trim();
            String telefono = campos[4].getText().trim();
            ControladorUsuarios controladorUsuarios = new ControladorUsuarios();

            //Validación de email y teléfono
            if (!controladorUsuarios.esEmailValido(email)) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese un email válido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!controladorUsuarios.esTelefonoValido(telefono)) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese un teléfono válido de 9 dígitos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Verificación del email en caso de que ya esté registrado
            if (controladorUsuarios.obtenerUsuarioIdPorCorreo(email) != -1) {
                JOptionPane.showMessageDialog(this, "Este correo ya está registrado. Por favor, use otro.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Si el usuario es cuidador, se valida el correo del usuario cuidado
            if (seleccionaCuidador.isSelected()) {
                String correoUsuarioCuidado = campoUsuarioCuidado.getText().trim();

                //Validación del correo del usuario cuidado
                if (correoUsuarioCuidado.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor ingrese el correo del usuario cuidado", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!controladorUsuarios.esEmailValido(correoUsuarioCuidado)) {
                    JOptionPane.showMessageDialog(this, "El correo del usuario cuidado no tiene un formato válido", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (controladorUsuarios.obtenerUsuarioIdPorCorreo(correoUsuarioCuidado) == -1) {
                    JOptionPane.showMessageDialog(this, "El usuario cuidado con ese correo no está registrado", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Registro del usuario cuidador y su relación con el usuario cuidado
                Usuario usuarioCuidador = new Usuario(0,
                        campos[0].getText(),
                        campos[1].getText(),
                        email,
                        new String(((JPasswordField) campos[3]).getPassword()),
                        telefono,
                        "cuidador");

                controladorUsuarios.registrarUsuario(usuarioCuidador);

                int cuidadorId = controladorUsuarios.obtenerUsuarioIdPorCorreo(email);
                int usuarioCuidadoId = controladorUsuarios.obtenerUsuarioIdPorCorreo(correoUsuarioCuidado);

                UsuarioCuidador relacion = new UsuarioCuidador();
                relacion.setUsuarioId(usuarioCuidadoId);
                relacion.setCuidadorId(cuidadorId);

                controladorUsuarios.registrarUsuario(relacion);

            } else { //Registro de un usuario normal (paciente)
                Usuario usuario = new Usuario(0,
                        campos[0].getText(),
                        campos[1].getText(),
                        email,
                        new String(((JPasswordField) campos[3]).getPassword()),
                        telefono,
                        "cuidado");

                controladorUsuarios.registrarUsuario(usuario);
            }

            JOptionPane.showMessageDialog(this, "Registro exitoso");
            new VentanaInicioSesion().setVisible(true);
            dispose();
        });

        //Acción del botón "Volver"
        botonVolver.addActionListener(e -> {
            new VentanaAcceso().setVisible(true);
            dispose();
        });

        panelBotones.add(botonAceptar);
        panelBotones.add(botonVolver);
        panelPrincipal.add(panelBotones);
        panelPrincipal.add(Box.createVerticalGlue());

        add(panelPrincipal);
    }
}
