package org.example.ui;

import org.example.model.Usuario;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaEditarPerfil extends JPanel {
    private final int usuarioID;
    private final String tipoUsuario;
    private final String ventanaInicial;
    private final VentanaPerfilUsuario ventanaPerfilPrincipal;

    private JTextField textoNombre, textoApellidos, textoEmail, textoTelefono, textoNombrePaciente, textoApellidosPaciente, textoEmailPaciente, textoTelefonoPaciente;
    private JPasswordField textoPassword;
    private JPanel panelPaciente;

    public VentanaEditarPerfil(int usuarioID, String tipoUsuario, String ventanaInicial, VentanaPerfilUsuario ventanaPerfil) {
        this.usuarioID = usuarioID;
        this.tipoUsuario = tipoUsuario;
        this.ventanaInicial = ventanaInicial;
        this.ventanaPerfilPrincipal = ventanaPerfil;

        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        ControladorUsuarios controlador = new ControladorUsuarios();
        Usuario usuario = controlador.obtenerUsuarioPorId(usuarioID);

        JLabel titulo = new JLabel("EDITAR PERFIL");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(new Color(113, 183, 188));

        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.X_AXIS));
        panelTitulo.setOpaque(false);
        panelTitulo.add(Box.createHorizontalGlue());
        panelTitulo.add(titulo);
        panelTitulo.add(Box.createHorizontalGlue());
        panelTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        panel.add(panelTitulo);
        panel.add(Box.createVerticalStrut(30));

        panel.add(crearFormularioUsuario("Mis datos", usuario, true));

        boolean esCuidador = false;

        if ("cuidador".equalsIgnoreCase(tipoUsuario)) {
            List<Integer> pacientes = controlador.obtenerPacientesDeCuidador(usuarioID);
            if (!pacientes.isEmpty()) {
                Usuario paciente = controlador.obtenerUsuarioPorId(pacientes.get(0));
                panelPaciente = crearFormularioUsuario("Usuario cuidado asignado", paciente, false);
                panel.add(Box.createVerticalStrut(20));
                panel.add(panelPaciente);
                esCuidador = true;
            }
        }

        panel.add(Box.createVerticalStrut(30));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        botones.setOpaque(false);

        JButton botonGuardar = new JButton("Guardar Cambios");
        botonGuardar.setPreferredSize(new Dimension(180, 40));
        botonGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonGuardar.setBackground(new Color(113, 183, 188));
        botonGuardar.setForeground(Color.WHITE);

        JButton botonCancelar = new JButton("Cancelar");
        botonCancelar.setPreferredSize(new Dimension(180, 40));
        botonCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonCancelar.setBackground(new Color(113, 183, 188));
        botonCancelar.setForeground(Color.WHITE);

        botones.add(botonGuardar);
        botones.add(botonCancelar);
        panel.add(botones);

        boolean finalEsCuidador = esCuidador;

        botonCancelar.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        botonGuardar.addActionListener(e -> {
            try {
                if (validarCampos(textoNombre, textoApellidos, textoEmail, textoTelefono)) {
                    String nuevaPassword = new String(textoPassword.getPassword()).trim();
                    String passwordFinal = nuevaPassword.isEmpty()
                            ? controlador.obtenerUsuarioPorId(usuarioID).getPassword()
                            : nuevaPassword;

                    Usuario nuevo = new Usuario(
                            usuarioID,
                            textoNombre.getText().trim(),
                            textoApellidos.getText().trim(),
                            textoEmail.getText().trim(),
                            passwordFinal,
                            textoTelefono.getText().trim(),
                            tipoUsuario
                    );

                    controlador.editarUsuario(nuevo);

                    if (finalEsCuidador && panelPaciente != null) {
                        List<Integer> pacientes = controlador.obtenerPacientesDeCuidador(usuarioID);
                        if (!pacientes.isEmpty()) {
                            String passwordPaciente = controlador.obtenerUsuarioPorId(pacientes.get(0)).getPassword();
                            Usuario actualizado = new Usuario(
                                    pacientes.get(0),
                                    textoNombrePaciente.getText().trim(),
                                    textoApellidosPaciente.getText().trim(),
                                    textoEmailPaciente.getText().trim(),
                                    passwordPaciente,
                                    textoTelefonoPaciente.getText().trim(),
                                    "cuidado");
                            controlador.editarUsuario(actualizado);
                        }
                    }

                    JOptionPane.showMessageDialog(this, "Datos actualizados correctamente.");
                    if (ventanaPerfil != null) ventanaPerfil.recargarContenido();
                    SwingUtilities.getWindowAncestor(this).dispose();

                } else {
                    JOptionPane.showMessageDialog(this, "Por favor, rellene todos los campos obligatorios.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar los cambios: " + ex.getMessage());
            }
        });

        add(panel, BorderLayout.CENTER);
        Dimension dimensionar = esCuidador ? new Dimension(700, 850) : new Dimension(650, 750);
        setPreferredSize(new Dimension(dimensionar.width, dimensionar.height + 200));
    }

    private JPanel crearFormularioUsuario(String titulo, Usuario usuario, boolean esPrincipal) {
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(new Color(245, 245, 245));
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(titulo),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JTextField nombre = new JTextField(usuario.getNombre());
        JTextField apellidos = new JTextField(usuario.getApellidos());
        JTextField email = new JTextField(usuario.getEmail());
        JTextField telefono = new JTextField(usuario.getTelefono() != null ? usuario.getTelefono() : "");

        if (esPrincipal) {
            textoNombre = nombre;
            textoApellidos = apellidos;
            textoEmail = email;
            textoTelefono = telefono;
            textoPassword = new JPasswordField();
        } else {
            textoNombrePaciente = nombre;
            textoApellidosPaciente = apellidos;
            textoEmailPaciente = email;
            textoTelefonoPaciente = telefono;
        }

        panelFormulario.add(campoEditable("Nombre:", nombre));
        panelFormulario.add(campoEditable("Apellidos:", apellidos));
        panelFormulario.add(campoEditable("Email:", email));
        panelFormulario.add(campoEditable("Teléfono:", telefono));
        if (esPrincipal) {
            panelFormulario.add(campoEditable("Nueva contraseña (opcional):", textoPassword));
        }

        return panelFormulario;
    }

    private JPanel campoEditable(String texto, JComponent campo) {
        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setOpaque(false);
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campo.setMaximumSize(new Dimension(500, 30));
        fila.add(etiqueta);
        fila.add(campo);
        fila.add(Box.createVerticalStrut(10));
        return fila;
    }

    private boolean validarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            if (campo.getText().trim().isEmpty()) return false;
        }
        return true;
    }
}
