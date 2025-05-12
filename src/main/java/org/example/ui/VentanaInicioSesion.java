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

        btnAceptar.addActionListener(e -> {
            try {
                String email = txtEmail.getText().trim();
                String password = new String(txtPassword.getPassword());

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
