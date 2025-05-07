package org.example.ui;

import org.example.model.ActividadFisica;
import org.example.model.Recordatorios;
import org.example.service.ControladorActividadFisica;
import org.example.service.ControladorRecordatorios;
import com.toedter.calendar.JDateChooser;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Date;
import java.util.Calendar;

public class VentanaAgregarActividad extends JFrame {
    private JTextField txtNombre;
    private JTextField txtDuracion;
    private JSpinner spinnerHoraInicio;
    private JSpinner spinnerHoraFin;
    private JDateChooser dateChooserFecha;
    private JComboBox<Integer> comboPacientes;
    private JButton btnGuardar, btnCancelar;

    private int usuarioID;
    private int usuarioCuidadorID;
    private String tipoUsuario;
    private VentanaAreaFisica ventanaAreaFisica;

    public VentanaAgregarActividad(int usuarioID, int usuarioCuidadorID, VentanaAreaFisica ventanaAreaFisica) {
        this.usuarioID = usuarioID;
        this.usuarioCuidadorID = usuarioCuidadorID;
        this.ventanaAreaFisica = ventanaAreaFisica;
        this.tipoUsuario = ventanaAreaFisica.getTipoUsuario();

        setTitle("Agregar Actividad Física");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 248, 248));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("AGREGAR ACTIVIDAD FÍSICA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(113, 183, 188));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        txtNombre = new JTextField();
        txtDuracion = new JTextField();
        txtDuracion.setEditable(false); // campo de solo lectura
        dateChooserFecha = new JDateChooser();
        dateChooserFecha.setDateFormatString("yyyy-MM-dd");

        spinnerHoraInicio = new JSpinner(new SpinnerDateModel());
        spinnerHoraInicio.setEditor(new JSpinner.DateEditor(spinnerHoraInicio, "HH:mm"));

        spinnerHoraFin = new JSpinner(new SpinnerDateModel());
        spinnerHoraFin.setEditor(new JSpinner.DateEditor(spinnerHoraFin, "HH:mm"));

        panel.add(crearCampo("Nombre de la actividad:", txtNombre));
        panel.add(crearCampo("Duración (minutos):", txtDuracion));
        panel.add(crearCampo("Fecha:", dateChooserFecha));
        panel.add(crearCampo("Hora de inicio:", spinnerHoraInicio));
        panel.add(crearCampo("Hora de fin:", spinnerHoraFin));

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(248, 248, 248));

        btnGuardar = new JButton("GUARDAR");
        estiloBoton(btnGuardar);
        btnGuardar.addActionListener(e -> guardarActividad());

        btnCancelar = new JButton("CANCELAR");
        estiloBoton(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        panel.add(buttonPanel);

        add(panel);

        // Actualizar duración automáticamente
        spinnerHoraInicio.addChangeListener(e -> actualizarDuracion());
        spinnerHoraFin.addChangeListener(e -> actualizarDuracion());
    }

    private JPanel crearCampo(String labelText, JComponent field) {
        JPanel panelCampo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCampo.setBackground(new Color(248, 248, 248));
        panelCampo.setMaximumSize(new Dimension(400, 50));

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(150, 30));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panelCampo.add(label);
        panelCampo.add(field);

        return panelCampo;
    }

    private void estiloBoton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(113, 183, 188));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
    }

    private void cargarPacientes() {
        try {
            ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
            List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);

            if (pacientes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No tienes pacientes asignados.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Integer pacienteId : pacientes) {
                    comboPacientes.addItem(pacienteId);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar pacientes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarDuracion() {
        try {
            LocalTime inicio = convertirSpinnerAHora(spinnerHoraInicio);
            LocalTime fin = convertirSpinnerAHora(spinnerHoraFin);

            long minutos = java.time.Duration.between(inicio, fin).toMinutes();
            if (minutos < 0) minutos += 1440; // cruzar medianoche

            txtDuracion.setText(String.valueOf(minutos));
        } catch (Exception ex) {
            txtDuracion.setText("");
        }
    }

    private LocalTime convertirSpinnerAHora(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    private void guardarActividad() {
        try {
            String nombre = txtNombre.getText();
            int duracion = Integer.parseInt(txtDuracion.getText());

            // Validar que el nombre no esté vacío
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre de la actividad es obligatorio",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar que se haya seleccionado una fecha
            if (dateChooserFecha.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Debes seleccionar una fecha para la actividad.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate fecha = dateChooserFecha.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            LocalTime horaInicio = ((java.util.Date) spinnerHoraInicio.getValue()).toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalTime();
            LocalTime horaFin = ((java.util.Date) spinnerHoraFin.getValue()).toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalTime();

            int pacienteId = usuarioID;
            Integer cuidadorId = (usuarioCuidadorID > 0) ? usuarioCuidadorID : null;

            if (tipoUsuario.equals("cuidador")) {
                ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
                List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);
                if (!pacientes.isEmpty()) {
                    pacienteId = pacientes.get(0); // O permite seleccionar el paciente
                    cuidadorId = usuarioID;
                } else {
                    JOptionPane.showMessageDialog(this, "No tienes pacientes asignados.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // 👇 Debug por consola
            System.out.println("Paciente ID: " + pacienteId);
            System.out.println("Cuidador ID: " + cuidadorId);

            ActividadFisica actividad = new ActividadFisica();
            actividad.setUsuarioId(pacienteId);
            actividad.setUsuarioCuidadorId(cuidadorId);  // Ahora sí puede ser null sin problemas
            actividad.setNombre(nombre);
            actividad.setDuracion(duracion);
            actividad.setHoraInicio(horaInicio);
            actividad.setHoraFin(horaFin);

            ControladorActividadFisica controlador = new ControladorActividadFisica();
            int actividadId = controlador.registrarActividad(actividad);

            // Crear recordatorio
            Recordatorios recordatorio = new Recordatorios();
            recordatorio.setUsuarioID(pacienteId);
            recordatorio.setUsuarioCuidadorID(cuidadorId);
            recordatorio.setDescripcion("Actividad: " + nombre);
            recordatorio.setTipoEvento("ActividadFisica");
            recordatorio.setFecha(fecha);
            recordatorio.setHora(horaInicio);
            recordatorio.setFechaInicio(LocalDateTime.of(fecha, horaInicio));
            recordatorio.setFechaFin(LocalDateTime.of(fecha, horaFin));
            recordatorio.setActividadID(actividadId);

            ControladorRecordatorios controladorRecordatorios = new ControladorRecordatorios();
            controladorRecordatorios.crearRecordatorio(recordatorio);

            JOptionPane.showMessageDialog(this, "Actividad y recordatorio guardados correctamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            if (ventanaAreaFisica != null) {
                ventanaAreaFisica.recargarRecordatorios();
            }
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la actividad: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}
