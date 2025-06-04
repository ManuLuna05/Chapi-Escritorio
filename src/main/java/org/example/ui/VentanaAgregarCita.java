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
    private JTextField textoLugar, textoEspecialista;
    private JDateChooser selectorFecha;
    private JSpinner spinnerHora;
    private JButton botonGuardar, botonCancelar;
    private int usuarioID;
    private int usuarioCuidadorID;
    private VentanaCitasMedicas ventanaCitasMedicas;

    //Creación de la ventana para agregar una cita médica
    public VentanaAgregarCita(int usuarioID, int usuarioCuidadorID, VentanaCitasMedicas ventanaCitasPrincipal) {
        this.usuarioID = usuarioID;
        this.usuarioCuidadorID = usuarioCuidadorID;
        this.ventanaCitasMedicas = ventanaCitasPrincipal;

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

        // Creación de los campos del formulario
        textoLugar = new JTextField();
        textoEspecialista = new JTextField();
        selectorFecha = new JDateChooser();
        selectorFecha.setDateFormatString("yyyy-MM-dd");

        //Spinner para la hora con su configuración de formato
        spinnerHora = new JSpinner(new SpinnerDateModel());
        spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "HH:mm"));

        formulario(panel, "Lugar:", textoLugar);
        formulario(panel, "Especialista:", textoEspecialista);
        formulario(panel, "Fecha:", selectorFecha);
        formulario(panel, "Hora:", spinnerHora);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelInferior.setBackground(new Color(248, 248, 248));

        //Acciones de los botones
        botonGuardar = new JButton("GUARDAR");
        estiloBoton(botonGuardar);
        botonGuardar.addActionListener(e -> guardarCita());

        botonCancelar = new JButton("CANCELAR");
        estiloBoton(botonCancelar);
        botonCancelar.addActionListener(e -> dispose());

        panelInferior.add(botonGuardar);
        panelInferior.add(botonCancelar);

        panel.add(panelInferior);
        add(panel);
    }

    //Función para crear el formulario a rellenar para la creación de la cita médica
    private void formulario(JPanel panel, String etiquetaTexto, JComponent campo) {
        JPanel panelCampo = new JPanel();
        panelCampo.setLayout(new BoxLayout(panelCampo, BoxLayout.Y_AXIS));
        panelCampo.setBackground(new Color(248, 248, 248));
        panelCampo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(etiquetaTexto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(80, 80, 80));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setMaximumSize(new Dimension(300, 30));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelCampo.add(label);
        panelCampo.add(Box.createRigidArea(new Dimension(0, 5)));
        panelCampo.add(campo);
        panelCampo.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(panelCampo);
    }

    //Estilo de los botones
    private void estiloBoton(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(new Color(113, 183, 188));
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(150, 40));
    }

    //Función para guardar la cita médica
    private void guardarCita() {
        try {
            String lugar = textoLugar.getText().trim();
            String especialista = textoEspecialista.getText().trim();

            //Validación de los campos requeridos
            if (lugar.isEmpty() || especialista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe completar el lugar y el especialista.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Validación de la fecha seleccionada
            if (selectorFecha.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Se convierte la fecha y hora a LocalDate y LocalTime para un mejor manejo de los datos
            LocalDate fecha = selectorFecha.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalTime hora = ((Date)spinnerHora.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();

            int pacienteId = usuarioID;
            Integer cuidadorId = null;

            //Si el usuario es un cuidador, se obtiene el ID del paciente asignado para el posterior guardado
            if ("cuidador".equals(ventanaCitasMedicas.getTipoUsuario())) {
                cuidadorId = usuarioID;
                ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
                List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);
                if (!pacientes.isEmpty()) {
                    pacienteId = pacientes.get(0);
                } else {
                    JOptionPane.showMessageDialog(this, "No tiene pacientes asignados. Se crea como paciente propio.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    cuidadorId = null;
                }
            }

            ControladorCitasMedicas controlador = new ControladorCitasMedicas();
            int citaID = controlador.registrarCita(pacienteId, cuidadorId, fecha, hora, lugar, especialista);

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
            JOptionPane.showMessageDialog(this, "Error al guardar la cita: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
