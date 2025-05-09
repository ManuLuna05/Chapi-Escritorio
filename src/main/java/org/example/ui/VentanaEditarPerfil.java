package org.example.ui;

import org.example.model.Usuario;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class VentanaEditarPerfil extends JPanel {
    private final int usuarioID;
    private final String tipoUsuario;
    private final String ventanaOrigen;

    private JTextField txtNombre, txtApellidos, txtEmail, txtTelefono, txtNombrePaciente, txtApellidosPaciente, txtEmailPaciente, txtTelefonoPaciente;
    private JPasswordField txtPassword;
    private JPanel panelPaciente;

    public VentanaEditarPerfil(int usuarioID, String tipoUsuario, String ventanaOrigen) throws SQLException {
        this.usuarioID = usuarioID;
        this.tipoUsuario = tipoUsuario;
        this.ventanaOrigen = ventanaOrigen;

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

        JPanel contenedorTitulo = new JPanel();
        contenedorTitulo.setLayout(new BoxLayout(contenedorTitulo, BoxLayout.X_AXIS));
        contenedorTitulo.setOpaque(false);
        contenedorTitulo.add(Box.createHorizontalGlue());
        contenedorTitulo.add(titulo);
        contenedorTitulo.add(Box.createHorizontalGlue());
        contenedorTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        panel.add(contenedorTitulo);
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

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setPreferredSize(new Dimension(180, 40));
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setBackground(new Color(113, 183, 188));
        btnGuardar.setForeground(Color.WHITE);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(180, 40));
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setBackground(new Color(113, 183, 188));
        btnCancelar.setForeground(Color.WHITE);

        botones.add(btnGuardar);
        botones.add(btnCancelar);
        panel.add(botones);

        boolean finalEsCuidador = esCuidador;

        btnCancelar.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        btnGuardar.addActionListener(e -> {
            try {
                if (validarCampos(txtNombre, txtApellidos, txtEmail, txtTelefono)) {
                    String nuevaPassword = new String(txtPassword.getPassword()).trim();
                    String passwordFinal;

                    if (nuevaPassword.isEmpty()) {
                        // Recuperar la contraseña original si no se escribió una nueva
                        passwordFinal = controlador.obtenerUsuarioPorId(usuarioID).getPassword();
                    } else {
                        passwordFinal = nuevaPassword;
                    }

                    Usuario nuevo = new Usuario(
                            usuarioID,
                            txtNombre.getText().trim(),
                            txtApellidos.getText().trim(),
                            txtEmail.getText().trim(),
                            passwordFinal,
                            txtTelefono.getText().trim(),
                            tipoUsuario
                    );

                    controlador.editarUsuario(nuevo);

                    if (finalEsCuidador && panelPaciente != null) {
                        List<Integer> pacientes = controlador.obtenerPacientesDeCuidador(usuarioID);
                        if (!pacientes.isEmpty()) {
                            String passwordPaciente = controlador.obtenerUsuarioPorId(pacientes.get(0)).getPassword();
                            Usuario actualizado = new Usuario(
                                    pacientes.get(0),
                                    txtNombrePaciente.getText().trim(),
                                    txtApellidosPaciente.getText().trim(),
                                    txtEmailPaciente.getText().trim(),
                                    passwordPaciente,
                                    txtTelefonoPaciente.getText().trim(),
                                    "cuidado");
                            controlador.editarUsuario(actualizado);
                        }
                    }

                    JOptionPane.showMessageDialog(this, "Datos actualizados correctamente.");

                    // Recargar la ventana de perfil si sigue abierta
                    Window padre = SwingUtilities.getWindowAncestor(this);
                    if (padre instanceof JDialog dialogo) {
                        dialogo.dispose();
                        JFrame perfil = new VentanaPerfilUsuario(usuarioID, tipoUsuario, ventanaOrigen);
                        perfil.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        perfil.setVisible(true);
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Por favor, rellene todos los campos obligatorios.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar los cambios: " + ex.getMessage());
            }
        });

        add(panel, BorderLayout.CENTER);

        // Ajustar altura del contenedor según tipo de usuario
        Dimension preferredSize = esCuidador ? new Dimension(700, 850) : new Dimension(650, 750);
        setPreferredSize(new Dimension(preferredSize.width, preferredSize.height + 200));
    }

    private JPanel crearFormularioUsuario(String titulo, Usuario usuario, boolean esPrincipal) {
        JPanel bloque = new JPanel();
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        bloque.setBackground(new Color(245, 245, 245));
        bloque.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(titulo),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JTextField nombre = new JTextField(usuario.getNombre());
        JTextField apellidos = new JTextField(usuario.getApellidos());
        JTextField email = new JTextField(usuario.getEmail());
        JTextField telefono = new JTextField(usuario.getTelefono() != null ? usuario.getTelefono() : "");

        if (esPrincipal) {
            txtNombre = nombre;
            txtApellidos = apellidos;
            txtEmail = email;
            txtTelefono = telefono;
            txtPassword = new JPasswordField();
        } else {
            txtNombrePaciente = nombre;
            txtApellidosPaciente = apellidos;
            txtEmailPaciente = email;
            txtTelefonoPaciente = telefono;
        }

        bloque.add(campoEditable("Nombre:", nombre));
        bloque.add(campoEditable("Apellidos:", apellidos));
        bloque.add(campoEditable("Email:", email));
        bloque.add(campoEditable("Teléfono:", telefono));
        if (esPrincipal) {
            bloque.add(campoEditable("Nueva contraseña (opcional):", txtPassword));
        }

        return bloque;
    }

    private JPanel campoEditable(String label, JComponent field) {
        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(500, 30));
        fila.add(lbl);
        fila.add(field);
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
