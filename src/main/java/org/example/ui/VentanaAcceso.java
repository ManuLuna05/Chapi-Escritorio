package org.example.ui;

import javax.swing.*;
import java.awt.*;

public class VentanaAcceso extends JFrame {
    public VentanaAcceso() {
        setTitle("Acceso a Chapi");
        setSize(500, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Panel principal de la ventana de acceso
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelPrincipal.setBackground(Color.WHITE);

        //Panel para el texto de bienvenida
        JLabel mensajeBienvenida = new JLabel("¡Bienvenido a Chapi!");
        mensajeBienvenida.setFont(new Font("Arial", Font.BOLD, 24));
        mensajeBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(mensajeBienvenida);
        panelPrincipal.add(Box.createVerticalStrut(20));

        //Panel para el logo
        JLabel logoEtiqueta = new JLabel();
        try {
            ImageIcon logoIcono = new ImageIcon(getClass().getResource("/images/chapi_logos_azulOscuro.png"));
            Image logoImagen = logoIcono.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            logoEtiqueta.setIcon(new ImageIcon(logoImagen));
        } catch (Exception e) {
            logoEtiqueta.setText("LOGO APP");
            logoEtiqueta.setFont(new Font("Arial", Font.BOLD, 24));
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }
        logoEtiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(logoEtiqueta);
        panelPrincipal.add(Box.createVerticalStrut(100));

        //Panel para los botones de acceso
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));

        //Personalización del botón de inicio de sesión
        JButton botonLogin = new JButton("Iniciar Sesión");
        botonLogin.setFont(new Font("Arial", Font.BOLD, 16));
        botonLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonLogin.setMaximumSize(new Dimension(200, 50));
        botonLogin.setPreferredSize(new Dimension(200, 50));
        botonLogin.setBackground(new Color(113, 183, 188));
        botonLogin.setForeground(Color.WHITE);
        panelBotones.add(botonLogin);
        panelBotones.add(Box.createVerticalStrut(20));

        //Personalización del botón de registro
        JButton botonRegistro = new JButton("Registrarse");
        botonRegistro.setFont(new Font("Arial", Font.BOLD, 16));
        botonRegistro.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonRegistro.setMaximumSize(new Dimension(200, 50));
        botonRegistro.setPreferredSize(new Dimension(200, 50));
        botonRegistro.setBackground(new Color(113, 183, 188));
        botonRegistro.setForeground(Color.WHITE);
        panelBotones.add(botonRegistro);

        panelPrincipal.add(panelBotones);
        panelPrincipal.add(Box.createVerticalGlue());

        //Listeners de acciones a realizar a pulsar los botones
        botonLogin.addActionListener(e -> { //Iniciar sesión
            new VentanaInicioSesion().setVisible(true);
            dispose();
        });

        botonRegistro.addActionListener(e -> { //Registrarse
            new VentanaRegistro().setVisible(true);
            dispose();
        });

        add(panelPrincipal);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaAcceso().setVisible(true);
        });
    }
}