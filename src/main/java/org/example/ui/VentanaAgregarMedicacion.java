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
    private JDateChooser selectorFechaInicio, selectorFechaFin;
    private JComboBox<Medicamento> comboMedicamentos;
    private JTextField textoFrecuencia;
    private JButton botonGuardar, botonCancelar, botonConfigurarHoras;
    private int usuarioID;
    private List<LocalTime> horasDosis;
    private VentanaAreaMedica ventanaAreaMedica;

    //Configuración de elementos de la ventana para agregar una medicación
    public VentanaAgregarMedicacion(int usuarioID, int usuarioCuidadorID, VentanaAreaMedica ventanaAreaMedicaPrincipal) {
        this.usuarioID = usuarioID;
        this.horasDosis = new ArrayList<>();
        this.ventanaAreaMedica = ventanaAreaMedicaPrincipal;

        setTitle("Agregar Medicación");
        setSize(520, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 248, 248));

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));
        panelCentral.setBackground(new Color(248, 248, 248));

        JLabel titulo = new JLabel("AGREGAR MEDICACIÓN");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(113, 183, 188));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        panelCentral.add(titulo);

        comboMedicamentos = new JComboBox<>();
        spinnerDosis = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        selectorFechaInicio = crearSeleccionFecha();
        selectorFechaFin = crearSeleccionFecha();
        textoFrecuencia = new JTextField();

        formularioCentrado(panelCentral, "Medicamento:", comboMedicamentos);
        formularioCentrado(panelCentral, "Dosis:", spinnerDosis);
        formularioCentrado(panelCentral, "Fecha de Inicio:", selectorFechaInicio);
        formularioCentrado(panelCentral, "Fecha de Fin:", selectorFechaFin);
        formularioCentrado(panelCentral, "Frecuencia:", textoFrecuencia);

        cargarMedicamentos();

        //Botón para configurar horas de dosis
        botonConfigurarHoras = new JButton("CONFIGURAR HORAS DE DOSIS");
        estiloBoton(botonConfigurarHoras, new Color(113, 183, 188));
        botonConfigurarHoras.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonConfigurarHoras.addActionListener(e -> configurarHorasDosis());

        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));
        panelCentral.add(botonConfigurarHoras);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
        panelInferior.setBackground(new Color(248, 248, 248));

        //Acciones de los botones
        botonGuardar = new JButton("GUARDAR");
        estiloBoton(botonGuardar, new Color(113, 183, 188));
        botonGuardar.setPreferredSize(new Dimension(150, 40));
        botonGuardar.addActionListener(e -> guardarMedicacion());

        botonCancelar = new JButton("CANCELAR");
        estiloBoton(botonCancelar, new Color(113, 183, 188));
        botonCancelar.setPreferredSize(new Dimension(150, 40));
        botonCancelar.addActionListener(e -> dispose());

        panelInferior.add(botonGuardar);
        panelInferior.add(botonCancelar);
        panelCentral.add(panelInferior);

        panel.add(Box.createVerticalGlue());
        panel.add(panelCentral);
        panel.add(Box.createVerticalGlue());

        add(panel);
    }

    //Función para crear el formulario a rellenar para la creación de la medicación
    private void formularioCentrado(JPanel panel, String etiquetaTexto, JComponent campo) {
        JPanel panelCampo = new JPanel();
        panelCampo.setLayout(new BoxLayout(panelCampo, BoxLayout.Y_AXIS));
        panelCampo.setBackground(new Color(248, 248, 248));
        panelCampo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel etiqueta = new JLabel(etiquetaTexto);
        etiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        etiqueta.setForeground(new Color(80, 80, 80));
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setMaximumSize(new Dimension(300, 30));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Estilo de fondo dependiendo del tipo de campo
        if (campo instanceof JSpinner) {
            ((JSpinner.DefaultEditor)((JSpinner) campo).getEditor()).getTextField().setBackground(Color.WHITE);
        } else if (campo instanceof JComboBox) {
            campo.setBackground(Color.WHITE);
        } else if (campo instanceof JTextField) {
            ((JTextField) campo).setBackground(Color.WHITE);
        }

        panelCampo.add(etiqueta);
        panelCampo.add(Box.createRigidArea(new Dimension(0, 5)));
        panelCampo.add(campo);
        panelCampo.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(panelCampo);
    }

    //Función para crear un selector de fecha con el formato correcto
    private JDateChooser crearSeleccionFecha() {
        JDateChooser selectorFecha = new JDateChooser();
        selectorFecha.setDateFormatString("yyyy-MM-dd");
        selectorFecha.getCalendarButton().setBackground(new Color(113, 183, 188));
        selectorFecha.getCalendarButton().setForeground(Color.WHITE);
        selectorFecha.getDateEditor().getUiComponent().setBackground(Color.WHITE);
        selectorFecha.setPreferredSize(new Dimension(300, 30));
        selectorFecha.setMaximumSize(new Dimension(300, 30));
        return selectorFecha;
    }

    //Función para aplicar el estilo a los botones
    private void estiloBoton(JButton boton, Color color) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(color);
        boton.setFocusPainted(false);
        boton.setMaximumSize(new Dimension(300, 40));
        boton.setForeground(Color.WHITE);

        Border lineaBorde = BorderFactory.createLineBorder(new Color(200, 200, 200));
        Border vacioBorde = BorderFactory.createEmptyBorder(8, 20, 8, 20);
        boton.setBorder(BorderFactory.createCompoundBorder(lineaBorde, vacioBorde));
    }

    //Función para configurar las horas de las dosis
    private void configurarHorasDosis() {
        int dosis = (int) spinnerDosis.getValue();
        horasDosis.clear();

        JPanel panelHoras = new JPanel();
        panelHoras.setLayout(new BoxLayout(panelHoras, BoxLayout.Y_AXIS));
        panelHoras.setBackground(Color.WHITE);

        List<JSpinner> spinnersHoras = new ArrayList<>();
        for (int i = 0; i < dosis; i++) { //Para cada dosis, se crea un campo para seleccionar la hora correspondiente
            JLabel etiquetaHora = new JLabel("Hora de la dosis " + (i + 1) + ":");
            etiquetaHora.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            panelHoras.add(etiquetaHora);

            JSpinner spinnerHora = new JSpinner(new SpinnerDateModel());
            spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "HH:mm"));
            spinnerHora.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            spinnersHoras.add(spinnerHora);
            panelHoras.add(spinnerHora);
            panelHoras.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        int resultado = JOptionPane.showConfirmDialog(this, panelHoras, "Configurar Horas de Dosis", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            for (JSpinner spinner : spinnersHoras) {
                horasDosis.add(((java.util.Date) spinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime());
            }
        }
    }

    //Función para guardar la medicación y los recordatorios asociados
    private void guardarMedicacion() {
        try {
            Medicamento medicamentoSeleccionado = (Medicamento) comboMedicamentos.getSelectedItem();
            int dosis = (int) spinnerDosis.getValue();

            LocalDate fechaInicio = selectorFechaInicio.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFin = selectorFechaFin.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            String frecuencia = textoFrecuencia.getText();

            if (horasDosis.size() != dosis) {
                JOptionPane.showMessageDialog(this, "Debe configurar todas las horas de las dosis.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int duracion = (int) java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);

            Medicacion medicacion = new Medicacion();

            ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
            int idUsuario = usuarioID;
            Integer idCuidador = null;

            // Si el usuario es un cuidador, se obtiene el ID del paciente asignado para el posterior guardado de la medicación y recordatorios
            if (ventanaAreaMedica != null && "cuidador".equals(ventanaAreaMedica.getTipoUsuario())) {
                List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);
                if (!pacientes.isEmpty()) {
                    idUsuario = pacientes.get(0);
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
            //Creación de recordatorios para cada día y hora de dosis
            for (LocalDate fecha = fechaInicio; !fecha.isAfter(fechaFin); fecha = fecha.plusDays(1)) {
                for (LocalTime hora : horasDosis) {
                    Recordatorios recordatorio = new Recordatorios();
                    recordatorio.setUsuarioID(idUsuario);
                    recordatorio.setUsuarioCuidadorID(idCuidador);
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

            JOptionPane.showMessageDialog(this, "Medicación y recordatorios guardados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            if (ventanaAreaMedica != null) {
                ventanaAreaMedica.cargarRecordatorios();
            }
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la medicación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Función para cargar los medicamentos disponibles
    private void cargarMedicamentos() {
        ControladorMedicacion controlador = new ControladorMedicacion();
        List<Medicamento> medicamentos = controlador.obtenerTodosMedicamentos();
        for (Medicamento medicamento : medicamentos) {
            comboMedicamentos.addItem(medicamento);
        }
    }
}