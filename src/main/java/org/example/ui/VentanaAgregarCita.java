package org.example.ui;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.example.model.Recordatorios;
import org.example.service.ControladorCitasMedicas;
import org.example.service.ControladorRecordatorios;
import org.example.service.ControladorUsuarios;

public class VentanaAgregarCita extends JFrame {
    private JTextField txtLugar, txtEspecialista;
    private JDateChooser dateChooserFecha;
    private JSpinner spinnerHora;
    private JButton btnGuardar, btnCancelar;
    private int usuarioID;
    private int usuarioCuidadorID;
    private VentanaCitasMedicas ventanaCitasMedicas;

    public VentanaAgregarCita(int usuarioID, int usuarioCuidadorID, VentanaCitasMedicas padre) {
        this.usuarioID = usuarioID;
        this.usuarioCuidadorID = usuarioCuidadorID;
        this.ventanaCitasMedicas = padre;

        setTitle("Agregar Cita Médica");
        setSize(520, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 248, 248));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel titulo = new JLabel("AGREGAR CITA MÉDICA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(113, 183, 188));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        txtLugar = new JTextField();
        txtEspecialista = new JTextField();
        dateChooserFecha = new JDateChooser();
        dateChooserFecha.setDateFormatString("yyyy-MM-dd");

        spinnerHora = new JSpinner(new SpinnerDateModel());
        spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "HH:mm"));

        formularioCentrado(panel, "Lugar:", txtLugar);
        formularioCentrado(panel, "Especialista:", txtEspecialista);
        formularioCentrado(panel, "Fecha:", dateChooserFecha);
        formularioCentrado(panel, "Hora:", spinnerHora);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(248, 248, 248));

        btnGuardar = new JButton("GUARDAR");
        estiloBoton(btnGuardar);
        btnGuardar.addActionListener(e -> guardarCita());

        btnCancelar = new JButton("CANCELAR");
        estiloBoton(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        panel.add(buttonPanel);
        add(panel);
    }

    private void formularioCentrado(JPanel panel, String labelText, JComponent field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(new Color(248, 248, 248));
        fieldPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(80, 80, 80));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(300, 30));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        fieldPanel.add(label);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        fieldPanel.add(field);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(fieldPanel);
    }

    private void estiloBoton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(new Color(113, 183, 188));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
    }

    private void guardarCita() {
        try {
            String lugar = txtLugar.getText().trim();
            String especialista = txtEspecialista.getText().trim();

            if (lugar.isEmpty() || especialista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe completar el lugar y el especialista.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dateChooserFecha.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate fecha = dateChooserFecha.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            LocalTime hora = ((Date)spinnerHora.getValue()).toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalTime();

            int pacienteId = usuarioID;
            Integer cuidadorId = null;

            if ("cuidador".equals(ventanaCitasMedicas.getTipoUsuario())) {
                cuidadorId = usuarioID;
                ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
                List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);
                if (!pacientes.isEmpty()) {
                    pacienteId = pacientes.get(0);
                } else {
                    JOptionPane.showMessageDialog(this, "No tiene pacientes asignados. Se creará como paciente propio.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    cuidadorId = null;
                }
            }

            ControladorCitasMedicas controlador = new ControladorCitasMedicas();
            int citaID = controlador.registrarCita(
                    pacienteId,
                    cuidadorId,
                    fecha, hora, lugar, especialista);

            Recordatorios recordatorio = new Recordatorios();
            recordatorio.setUsuarioID(pacienteId);
            recordatorio.setUsuarioCuidadorID(cuidadorId);
            recordatorio.setDescripcion("Cita con " + especialista + " en " + lugar);
            recordatorio.setTipoEvento("CitaMedica");
            recordatorio.setNumeroDosis(0);
            recordatorio.setFecha(fecha);
            recordatorio.setHora(hora);
            recordatorio.setFechaInicio(LocalDateTime.of(fecha, hora));
            recordatorio.setFechaFin(LocalDateTime.of(fecha, hora).plusHours(1));
            recordatorio.setCitaMedicaID(citaID);

            new ControladorRecordatorios().crearRecordatorio(recordatorio);

            JOptionPane.showMessageDialog(this, "Cita y recordatorio guardados correctamente.");
            ventanaCitasMedicas.cargarRecordatorios();
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar la cita: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
