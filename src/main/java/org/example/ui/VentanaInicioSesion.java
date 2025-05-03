// VentanaInicioSesion.java
package org.example.ui;

import org.example.model.Usuario;
import org.example.model.UsuarioCuidador;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;

public class VentanaInicioSesion extends JFrame {
    public VentanaInicioSesion() {
        setTitle("Inicio de Sesión");
        setSize(500, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelPrincipal.setBackground(Color.WHITE);

        //Título
        JLabel etiquetaTitulo = new JLabel("Iniciar Sesión");
        etiquetaTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        etiquetaTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(etiquetaTitulo);
        panelPrincipal.add(Box.createVerticalStrut(20));

        //Logo
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcono = new ImageIcon(getClass().getResource("/images/chapi_logos_azulOscuro.png"));
            Image logoImagen = logoIcono.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(logoImagen));
        } catch (Exception e) {
            logoLabel.setText("LOGO APP");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(logoLabel);
        panelPrincipal.add(Box.createVerticalStrut(50));

        //Formulario de inicio de sesión
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        formPanel.setOpaque(false);
        formPanel.setMaximumSize(new Dimension(400, 150));

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(lblEmail);

        JTextField txtEmail = new JTextField();
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        txtEmail.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtEmail);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(lblPassword);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPassword.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtPassword);

        panelPrincipal.add(formPanel);
        panelPrincipal.add(Box.createVerticalStrut(50));

        //Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAceptar.setPreferredSize(new Dimension(150, 40));
        btnAceptar.setBackground(new Color(113, 183, 188));
        btnAceptar.setForeground(Color.WHITE);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        btnVolver.setPreferredSize(new Dimension(150, 40));
        btnVolver.setBackground(new Color(113, 183, 188));
        btnVolver.setForeground(Color.WHITE);

        //Acción del botón "Aceptar"
        btnAceptar.addActionListener(e -> {
            try {
                String email = txtEmail.getText();
                String password = new String(txtPassword.getPassword());

                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ControladorUsuarios controlador = new ControladorUsuarios();
                Usuario usuario = controlador.iniciarSesionUsuario(email, password);

                if (usuario != null) {
                    if (usuario.getTipo().equals("cuidador")) {
                        JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso como Cuidador");
                        new VentanaPrincipal(usuario.getId()).setVisible(true); // Inicio como cuidador
                    } else if (usuario.getTipo().equals("cuidado")) {
                        JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso como Usuario Cuidado");
                        new VentanaPrincipal(usuario.getId()).setVisible(true); // Inicio como usuario cuidado
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al iniciar sesión: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        //Acción del botón "Volver"
        btnVolver.addActionListener(e -> {
            new VentanaAcceso().setVisible(true);
            dispose();
        });

        buttonPanel.add(btnAceptar);
        buttonPanel.add(btnVolver);

        panelPrincipal.add(buttonPanel);
        panelPrincipal.add(Box.createVerticalGlue());

        add(panelPrincipal);
    }
}