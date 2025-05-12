package org.example.ui;

import org.example.model.ActividadFisica;
import org.example.model.Recordatorios;
import org.example.model.Usuario;
import org.example.service.ControladorActividadFisica;
import org.example.service.ControladorRecordatorios;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VentanaEliminarActividad extends JFrame {
    private JComboBox<ActividadFisica> comboActividades;
    private JList<Recordatorios> listaRecordatorios;
    private DefaultListModel<Recordatorios> modeloLista;
    private ControladorActividadFisica controladorActividad;
    private ControladorRecordatorios controladorRecordatorios;
    private int usuarioID;
    private int usuarioCuidadorID;
    private VentanaAreaFisica ventanaAreaFisica;

    public VentanaEliminarActividad(int usuarioID, VentanaAreaFisica ventanaAreaFisica) throws SQLException {
        this.usuarioID = usuarioID;
        this.ventanaAreaFisica = ventanaAreaFisica;
        this.controladorActividad = new ControladorActividadFisica();
        this.controladorRecordatorios = new ControladorRecordatorios();

        // Detectar si es cuidador
        if (ventanaAreaFisica.getTipoUsuario().equals("cuidador")) {
            this.usuarioCuidadorID = usuarioID;
        } else {
            this.usuarioCuidadorID = 0;
        }

        setTitle("Eliminar Actividad Física");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblActividades = new JLabel("Seleccionar Actividad:");
        comboActividades = new JComboBox<>();
        cargarActividades();
        comboActividades.addActionListener(e -> cargarRecordatorios());

        panelSuperior.add(lblActividades);
        panelSuperior.add(comboActividades);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel central con lista de recordatorios
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createTitledBorder("Recordatorios asociados"));

        modeloLista = new DefaultListModel<>();
        listaRecordatorios = new JList<>(modeloLista);
        listaRecordatorios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelCentral.add(new JScrollPane(listaRecordatorios), BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnEliminarActividad = new JButton("Eliminar Actividad");
        btnEliminarActividad.addActionListener(e -> eliminarActividad());
        estiloBoton(btnEliminarActividad);

        JButton btnEliminarRecordatorio = new JButton("Eliminar Recordatorio");
        btnEliminarRecordatorio.addActionListener(e -> eliminarRecordatorio());
        estiloBoton(btnEliminarRecordatorio);

        panelInferior.add(btnEliminarActividad);
        panelInferior.add(btnEliminarRecordatorio);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void estiloBoton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(113, 183, 188));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 40));
    }

    private void cargarActividades() throws SQLException {
        comboActividades.removeAllItems();

        // Usamos Set para evitar duplicados
        Set<ActividadFisica> actividades = new HashSet<>();

        if (ventanaAreaFisica.getTipoUsuario().equals("cuidador")) {
            // Cuidador: cargar actividades de todos sus pacientes
            ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
            List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);

            for (Integer pacienteId : pacientes) {
                actividades.addAll(controladorActividad.obtenerActividadesPorUsuario(pacienteId));
            }
        } else {
            // Paciente: solo sus actividades
            actividades.addAll(controladorActividad.obtenerActividadesPorUsuario(usuarioID));
        }

        for (ActividadFisica actividad : actividades) {
            comboActividades.addItem(actividad);
        }
    }


    private void cargarRecordatorios() {
        modeloLista.clear();
        ActividadFisica actividadSeleccionada = (ActividadFisica) comboActividades.getSelectedItem();

        if (actividadSeleccionada != null) {
            try {
                int usuarioActividad = actividadSeleccionada.getUsuarioId();

                List<Recordatorios> recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioActividad);
                List<Integer> idsVistos = new ArrayList<>();

                for (Recordatorios recordatorio : recordatorios) {
                    if ("ActividadFisica".equals(recordatorio.getTipoEvento())
                            && recordatorio.getActividadID() != null && recordatorio.getActividadID() == actividadSeleccionada.getId()
                            && !idsVistos.contains(recordatorio.getRecordatorioID())) {

                        modeloLista.addElement(recordatorio);
                        idsVistos.add(recordatorio.getRecordatorioID());
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar recordatorios: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarActividad() {
        ActividadFisica actividadSeleccionada = (ActividadFisica) comboActividades.getSelectedItem();

        if (actividadSeleccionada != null) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea eliminar esta actividad y todos sus recordatorios?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    // Usa el usuario dueño de la actividad (paciente)
                    controladorActividad.eliminarActividad(actividadSeleccionada.getId(), actividadSeleccionada.getUsuarioId());

                    JOptionPane.showMessageDialog(this, "Actividad eliminada correctamente");

                    if (ventanaAreaFisica != null) {
                        ventanaAreaFisica.recargarRecordatorios();
                    }

                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar actividad: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una actividad para eliminar",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarRecordatorio() {
        Recordatorios recordatorioSeleccionado = listaRecordatorios.getSelectedValue();

        if (recordatorioSeleccionado != null) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea eliminar este recordatorio?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    controladorRecordatorios.eliminarRecordatorio(recordatorioSeleccionado.getRecordatorioID());
                    JOptionPane.showMessageDialog(this, "Recordatorio eliminado correctamente");

                    cargarRecordatorios();

                    if (ventanaAreaFisica != null) {
                        ventanaAreaFisica.recargarRecordatorios();
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar recordatorio: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un recordatorio para eliminar",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
