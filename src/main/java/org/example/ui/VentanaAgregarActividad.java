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
    private JTextField textoNombre;
    private JTextField textoDuracion;
    private JSpinner spinnerHoraInicio;
    private JSpinner spinnerHoraFin;
    private JDateChooser selectorFecha;
    private JComboBox<Integer> comboPacientes;
    private JButton botonGuardar, botonCancelar;

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

        textoNombre = new JTextField();
        textoDuracion = new JTextField();
        textoDuracion.setEditable(false);
        selectorFecha = new JDateChooser();
        selectorFecha.setDateFormatString("yyyy-MM-dd");

        spinnerHoraInicio = new JSpinner(new SpinnerDateModel());
        spinnerHoraInicio.setEditor(new JSpinner.DateEditor(spinnerHoraInicio, "HH:mm"));

        spinnerHoraFin = new JSpinner(new SpinnerDateModel());
        spinnerHoraFin.setEditor(new JSpinner.DateEditor(spinnerHoraFin, "HH:mm"));

        panel.add(crearCampo("Nombre de la actividad:", textoNombre));
        panel.add(crearCampo("Duración (minutos):", textoDuracion));
        panel.add(crearCampo("Fecha:", selectorFecha));
        panel.add(crearCampo("Hora de inicio:", spinnerHoraInicio));
        panel.add(crearCampo("Hora de fin:", spinnerHoraFin));

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelInferior.setBackground(new Color(248, 248, 248));

        //Acción del botón "Guardar"
        botonGuardar = new JButton("GUARDAR");
        estiloBoton(botonGuardar);
        botonGuardar.addActionListener(e -> guardarActividad());

        //Acción del botón "Cancelar"
        botonCancelar = new JButton("CANCELAR");
        estiloBoton(botonCancelar);
        botonCancelar.addActionListener(e -> dispose());

        panelInferior.add(botonGuardar);
        panelInferior.add(botonCancelar);
        panel.add(panelInferior);

        add(panel);

        spinnerHoraInicio.addChangeListener(e -> actualizarDuracion());
        spinnerHoraFin.addChangeListener(e -> actualizarDuracion());
    }

    private JPanel crearCampo(String etiquetaTexto, JComponent campo) {
        JPanel panelCampo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCampo.setBackground(new Color(248, 248, 248));
        panelCampo.setMaximumSize(new Dimension(400, 50));

        JLabel etiqueta = new JLabel(etiquetaTexto);
        etiqueta.setPreferredSize(new Dimension(150, 30));
        etiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        campo.setPreferredSize(new Dimension(200, 30));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panelCampo.add(etiqueta);
        panelCampo.add(campo);

        return panelCampo;
    }

    private void estiloBoton(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(new Color(113, 183, 188));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(150, 40));
    }

    //Función para actualizar la duración de la actividad
    private void actualizarDuracion() {
        try {
            LocalTime inicio = convertirSpinnerAHora(spinnerHoraInicio);
            LocalTime fin = convertirSpinnerAHora(spinnerHoraFin);

            long minutos = java.time.Duration.between(inicio, fin).toMinutes();
            if (minutos < 0) minutos += 1440;

            textoDuracion.setText(String.valueOf(minutos));
        } catch (Exception ex) {
            textoDuracion.setText("");
        }
    }

    //Función para convertir el valor del spinner a LocalTime y manejar correctamente el tiempo
    private LocalTime convertirSpinnerAHora(JSpinner spinner) {
        Date fecha = (Date) spinner.getValue();
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(fecha);
        return LocalTime.of(calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE));
    }

    //Función para guardar la actividad física
    private void guardarActividad() {
        try {
            String nombre = textoNombre.getText();
            int duracion = Integer.parseInt(textoDuracion.getText());

            //Se valida que el nombre no esté vacío
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre de la actividad obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Se valida que se haya seleccionado una fecha
            if (selectorFecha.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Selecciona una fecha para la actividad.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Se pasan las fechas y horas a LocalDate y LocalTime para un mejor manejo de los datos
            LocalDate fecha = selectorFecha.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalTime horaInicio = ((java.util.Date) spinnerHoraInicio.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
            LocalTime horaFin = ((java.util.Date) spinnerHoraFin.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();

            int pacienteId = usuarioID;
            Integer cuidadorId = (usuarioCuidadorID > 0) ? usuarioCuidadorID : null;

            if (tipoUsuario.equals("cuidador")) {
                ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
                List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);
                if (!pacientes.isEmpty()) {
                    pacienteId = pacientes.get(0);
                    cuidadorId = usuarioID;
                } else {
                    JOptionPane.showMessageDialog(this, "No tienes pacientes asignados.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            //Se imprimen ID por consola para realizar comprobaciones con los identificadores de los usuarios
            System.out.println("Paciente ID: " + pacienteId);
            System.out.println("Cuidador ID: " + cuidadorId);

            //Se crea la actividad física y se guarda en la base de datos
            ActividadFisica actividad = new ActividadFisica();
            actividad.setUsuarioId(pacienteId);
            actividad.setUsuarioCuidadorId(cuidadorId);
            actividad.setNombre(nombre);
            actividad.setDuracion(duracion);
            actividad.setHoraInicio(horaInicio);
            actividad.setHoraFin(horaFin);

            ControladorActividadFisica controladorActividad = new ControladorActividadFisica();
            int actividadId = controladorActividad.registrarActividad(actividad);

            //Se crea el recordatorio para la actividad y se guarda en la base de datos
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

            //Se guarda el recordatorio en la base de datos
            ControladorRecordatorios controladorRecordatorios = new ControladorRecordatorios();
            controladorRecordatorios.crearRecordatorio(recordatorio);

            JOptionPane.showMessageDialog(this, "Actividad creada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            //Recargar la lista de actividades en la ventana principal
            if (ventanaAreaFisica != null) {
                ventanaAreaFisica.recargarRecordatorios();
            }
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al crear la actividad: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
