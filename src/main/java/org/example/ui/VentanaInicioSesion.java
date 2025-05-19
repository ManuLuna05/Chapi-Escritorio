package org.example.ui;

import org.example.model.Usuario;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;

public class VentanaInicioSesion extends JFrame {
    public VentanaInicioSesion() {
        setTitle("Inicio de Sesión");
        setSize(500, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelPrincipal.setBackground(Color.WHITE);

        JLabel etiquetaTitulo = new JLabel("Iniciar Sesión");
        etiquetaTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        etiquetaTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(etiquetaTitulo);
        panelPrincipal.add(Box.createVerticalStrut(20));

        JLabel logoEtiqueta = new JLabel();
        try {
            ImageIcon logoIcono = new ImageIcon(getClass().getResource("/images/chapi_logos_azulOscuro.png"));
            Image logoImagen = logoIcono.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            logoEtiqueta.setIcon(new ImageIcon(logoImagen));
        } catch (Exception e) {
            logoEtiqueta.setText("LOGO APP");
            logoEtiqueta.setFont(new Font("Arial", Font.BOLD, 24));
        }
        logoEtiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(logoEtiqueta);
        panelPrincipal.add(Box.createVerticalStrut(50));

        JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 10, 15));
        panelFormulario.setOpaque(false);
        panelFormulario.setMaximumSize(new Dimension(400, 150));

        JLabel etiquetaEmail = new JLabel("Email:");
        etiquetaEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        panelFormulario.add(etiquetaEmail);

        JTextField textoEmail = new JTextField();
        textoEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        textoEmail.setPreferredSize(new Dimension(300, 35));
        panelFormulario.add(textoEmail);

        JLabel etiquetaPassword = new JLabel("Contraseña:");
        etiquetaPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        panelFormulario.add(etiquetaPassword);

        JPasswordField textoPassword = new JPasswordField();
        textoPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        textoPassword.setPreferredSize(new Dimension(300, 35));
        panelFormulario.add(textoPassword);

        panelPrincipal.add(panelFormulario);
        panelPrincipal.add(Box.createVerticalStrut(50));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);

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

        botonAceptar.addActionListener(e -> {
            try {
                String email = textoEmail.getText().trim();
                String password = new String(textoPassword.getPassword());

                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ControladorUsuarios controlador = new ControladorUsuarios();

                if (!controlador.esEmailValido(email)) {
                    JOptionPane.showMessageDialog(this, "Por favor, introduzca un correo válido", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Usuario usuario = controlador.iniciarSesionUsuario(email, password);

                if (usuario != null) {
                    JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso como " + usuario.getTipo());
                    new VentanaPrincipal(usuario.getId(), usuario.getTipo()).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al iniciar sesión: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

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
