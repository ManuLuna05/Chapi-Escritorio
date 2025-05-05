package org.example.ui;

import org.example.model.Medicamento;
import org.example.model.Medicacion;
import org.example.model.Recordatorios;
import org.example.service.ControladorMedicacion;
import org.example.service.ControladorRecordatorios;
import com.toedter.calendar.JDateChooser;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class VentanaAgregarMedicacion extends JFrame {
    private JSpinner spinnerDosis;
    private JDateChooser dateChooserInicio, dateChooserFin;
    private JComboBox<Medicamento> comboMedicamentos;
    private JTextField txtFrecuencia;
    private JButton btnGuardar, btnCancelar, btnConfigurarHoras;
    private int usuarioID;
    private List<LocalTime> horasDosis;
    private VentanaAreaMedica ventanaAreaMedica;

    public VentanaAgregarMedicacion(int usuarioID, int usuarioCuidadorID, VentanaAreaMedica ventanaAreaMedicaPadre) {
        this.usuarioID = usuarioID;
        this.horasDosis = new ArrayList<>();
        this.ventanaAreaMedica = ventanaAreaMedicaPadre;

        setTitle("Agregar Medicación");
        setSize(520, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 248, 248));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));
        centerPanel.setBackground(new Color(248, 248, 248));

        JLabel titulo = new JLabel("AGREGAR MEDICACIÓN");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(113, 183, 188));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        centerPanel.add(titulo);

        comboMedicamentos = new JComboBox<>();
        spinnerDosis = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        dateChooserInicio = crearSeleccionFecha();
        dateChooserFin = crearSeleccionFecha();
        txtFrecuencia = new JTextField();

        formularioCentrado(centerPanel, "Medicamento:", comboMedicamentos);
        formularioCentrado(centerPanel, "Dosis:", spinnerDosis);
        formularioCentrado(centerPanel, "Fecha de Inicio:", dateChooserInicio);
        formularioCentrado(centerPanel, "Fecha de Fin:", dateChooserFin);
        formularioCentrado(centerPanel, "Frecuencia:", txtFrecuencia);

        cargarMedicamentos();

        btnConfigurarHoras = new JButton("CONFIGURAR HORAS DE DOSIS");
        estiloBoton(btnConfigurarHoras, new Color(113, 183, 188));
        btnConfigurarHoras.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConfigurarHoras.addActionListener(e -> configurarHorasDosis());

        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(btnConfigurarHoras);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setBackground(new Color(248, 248, 248));

        btnGuardar = new JButton("GUARDAR");
        estiloBoton(btnGuardar, new Color(113, 183, 188));
        btnGuardar.setPreferredSize(new Dimension(150, 40));
        btnGuardar.addActionListener(e -> guardarMedicacion());

        btnCancelar = new JButton("CANCELAR");
        estiloBoton(btnCancelar, new Color(113, 183, 188));
        btnCancelar.setPreferredSize(new Dimension(150, 40));
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        centerPanel.add(buttonPanel);

        panel.add(Box.createVerticalGlue());
        panel.add(centerPanel);
        panel.add(Box.createVerticalGlue());

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

        if (field instanceof JSpinner) {
            ((JSpinner.DefaultEditor)((JSpinner)field).getEditor()).getTextField().setBackground(Color.WHITE);
        } else if (field instanceof JComboBox) {
            field.setBackground(Color.WHITE);
        } else if (field instanceof JTextField) {
            ((JTextField)field).setBackground(Color.WHITE);
        }

        fieldPanel.add(label);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        fieldPanel.add(field);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(fieldPanel);
    }

    private JDateChooser crearSeleccionFecha() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.getCalendarButton().setBackground(new Color(113, 183, 188));
        dateChooser.getCalendarButton().setForeground(Color.WHITE);
        dateChooser.getDateEditor().getUiComponent().setBackground(Color.WHITE);
        dateChooser.setPreferredSize(new Dimension(300, 30));
        dateChooser.setMaximumSize(new Dimension(300, 30));
        return dateChooser;
    }

    private void estiloBoton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(300, 40));

        Border lineBorder = BorderFactory.createLineBorder(new Color(200, 200, 200));
        Border emptyBorder = BorderFactory.createEmptyBorder(8, 20, 8, 20);
        button.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
    }

    private void configurarHorasDosis() {
        int dosis = (int) spinnerDosis.getValue();
        horasDosis.clear();

        JPanel panelHoras = new JPanel();
        panelHoras.setLayout(new BoxLayout(panelHoras, BoxLayout.Y_AXIS));
        panelHoras.setBackground(Color.WHITE);

        List<JSpinner> spinnersHoras = new ArrayList<>();
        for (int i = 0; i < dosis; i++) {
            JLabel lblHora = new JLabel("Hora de la dosis " + (i + 1) + ":");
            lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            panelHoras.add(lblHora);

            JSpinner spinnerHora = new JSpinner(new SpinnerDateModel());
            spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "HH:mm"));
            spinnerHora.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            spinnersHoras.add(spinnerHora);
            panelHoras.add(spinnerHora);
            panelHoras.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        int result = JOptionPane.showConfirmDialog(this, panelHoras,
                "Configurar Horas de Dosis", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            for (JSpinner spinner : spinnersHoras) {
                horasDosis.add(((java.util.Date) spinner.getValue()).toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalTime());
            }
        }
    }

    private void guardarMedicacion() {
        try {
            Medicamento medicamentoSeleccionado = (Medicamento) comboMedicamentos.getSelectedItem();
            int dosis = (int) spinnerDosis.getValue();

            LocalDate fechaInicio = dateChooserInicio.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFin = dateChooserFin.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            String frecuencia = txtFrecuencia.getText();

            if (horasDosis.size() != dosis) {
                JOptionPane.showMessageDialog(this, "Debe configurar todas las horas de las dosis.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int duracion = (int) java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);

            Medicacion medicacion = new Medicacion();

            ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
            int idUsuario = usuarioID; // Por defecto, el usuario actual
            Integer idCuidador = null;

            if (ventanaAreaMedica != null && "cuidador".equals(ventanaAreaMedica.getTipoUsuario())) {
                List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);
                if (!pacientes.isEmpty()) {
                    idUsuario = pacientes.get(0); // Solo el primer paciente
                    idCuidador = usuarioID;
                } else {
                    JOptionPane.showMessageDialog(this, "No hay pacientes asignados.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            medicacion.setUsuarioID(idUsuario);
            medicacion.setMedicamentoID(medicamentoSeleccionado.getId());
            medicacion.setDosis(dosis);
            medicacion.setFrecuencia(frecuencia);
            medicacion.setDuracion(duracion);
            medicacion.setFechaInicio(fechaInicio);
            medicacion.setFechaFin(fechaFin);

            ControladorMedicacion controladorMedicacion = new ControladorMedicacion();
            int medicacionID = controladorMedicacion.registrarMedicacion(medicacion);

            ControladorRecordatorios controladorRecordatorios = new ControladorRecordatorios();
            for (LocalDate fecha = fechaInicio; !fecha.isAfter(fechaFin); fecha = fecha.plusDays(1)) {
                for (LocalTime hora : horasDosis) {
                    Recordatorios recordatorio = new Recordatorios();
                    recordatorio.setUsuarioID(idUsuario);
                    recordatorio.setUsuarioCuidadorID(idCuidador); // Puede ser null si no es cuidador
                    recordatorio.setDescripcion("Tomar " + medicamentoSeleccionado.getNombre());
                    recordatorio.setTipoEvento("Medicacion");
                    recordatorio.setNumeroDosis(1);
                    recordatorio.setFecha(fecha);
                    recordatorio.setHora(hora);
                    recordatorio.setFechaInicio(LocalDateTime.of(fecha, hora));
                    recordatorio.setFechaFin(LocalDateTime.of(fecha, hora).plusMinutes(30));
                    recordatorio.setMedicacionID(medicacionID);

                    controladorRecordatorios.crearRecordatorio(recordatorio);
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Medicación y recordatorios guardados correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            if (ventanaAreaMedica != null) {
                ventanaAreaMedica.cargarRecordatorios();
            }
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar la medicación: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarMedicamentos() {
        ControladorMedicacion controlador = new ControladorMedicacion();
        List<Medicamento> medicamentos = controlador.obtenerTodosMedicamentos();
        for (Medicamento medicamento : medicamentos) {
            comboMedicamentos.addItem(medicamento);
        }
    }
}