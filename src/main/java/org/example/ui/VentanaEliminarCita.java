package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.example.model.Recordatorios;
import org.example.service.ControladorCitasMedicas;
import org.example.service.ControladorRecordatorios;
import org.example.service.ControladorUsuarios;

public class VentanaEliminarCita extends JFrame {
    private JComboBox<String> comboEspecialistas;
    private JList<Recordatorios> listaRecordatorios;
    private DefaultListModel<Recordatorios> modeloLista;
    private ControladorRecordatorios controladorRecordatorios;
    private ControladorCitasMedicas controladorCitas;
    private int usuarioID;
    private int usuarioCuidadorID;
    private VentanaCitasMedicas ventanaCitasMedicas;
    private String tipoUsuario;

    public VentanaEliminarCita(int usuarioID, VentanaCitasMedicas ventanaCitasMedicas) {
        this.usuarioID = usuarioID;
        this.usuarioCuidadorID = ventanaCitasMedicas.getTipoUsuario().equals("cuidador") ? usuarioID : 0;
        this.tipoUsuario = ventanaCitasMedicas.getTipoUsuario();
        this.ventanaCitasMedicas = ventanaCitasMedicas;

        controladorRecordatorios = new ControladorRecordatorios();
        controladorCitas = new ControladorCitasMedicas();

        setTitle("Eliminar Cita Médica");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        modeloLista = new DefaultListModel<>();
        listaRecordatorios = new JList<>(modeloLista);

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel etiquetaEspecialistas = new JLabel("Filtrar por Especialista:");
        JPanel contenedorEtiqueta = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        contenedorEtiqueta.setOpaque(false);
        contenedorEtiqueta.add(etiquetaEspecialistas);
        panelSuperior.add(contenedorEtiqueta); // ✅ Aquí el cambio

        comboEspecialistas = new JComboBox<>();
        cargarEspecialistas();
        comboEspecialistas.addActionListener(e -> cargarRecordatorios());

        panelSuperior.add(comboEspecialistas);
        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createTitledBorder("Recordatorios asociados"));

        listaRecordatorios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelCentral.add(new JScrollPane(listaRecordatorios), BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton botonEliminarCita = new JButton("Eliminar Cita");
        estiloBoton(botonEliminarCita);
        botonEliminarCita.addActionListener(e -> {
            eliminarCita();
        });

        panelInferior.add(botonEliminarCita);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void estiloBoton(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(new Color(113, 183, 188));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(250, 40));
    }

    private void cargarEspecialistas() {
        comboEspecialistas.removeAllItems();
        Set<String> especialistas = new HashSet<>();
        List<Recordatorios> recordatorios = obtenerTodosRecordatorios();

        for (Recordatorios r : recordatorios) {
            if ("CitaMedica".equals(r.getTipoEvento())) {
                String desc = r.getDescripcion();
                String especialista = desc.contains("con ") ? desc.substring(desc.indexOf("con ") + 4).split(" en ")[0] : "Sin Especialista";
                especialistas.add(especialista);
            }
        }

        comboEspecialistas.addItem("Todos");
        for (String especialista : especialistas) {
            comboEspecialistas.addItem(especialista);
        }

        cargarRecordatorios();
    }

    private List<Recordatorios> obtenerTodosRecordatorios() {
        List<Recordatorios> recordatorios = new ArrayList<>();
        if ("cuidador".equals(tipoUsuario)) {
            ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
            List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);

            for (Integer pacienteId : pacientes) {
                List<Recordatorios> r = controladorRecordatorios.obtenerRecordatoriosPorUsuario(pacienteId);
                if (r != null) recordatorios.addAll(r);
            }
            List<Recordatorios> propios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID);
            if (propios != null) recordatorios.addAll(propios);
        } else {
            recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID);
        }
        return recordatorios;
    }

    private void cargarRecordatorios() {
        modeloLista.clear();
        String especialistaSeleccionado = (String) comboEspecialistas.getSelectedItem();

        List<Recordatorios> recordatorios = obtenerTodosRecordatorios();
        for (Recordatorios r : recordatorios) {
            if ("CitaMedica".equals(r.getTipoEvento())) {
                if ("Todos".equals(especialistaSeleccionado) ||
                        (especialistaSeleccionado != null && r.getDescripcion().toLowerCase().contains(especialistaSeleccionado.toLowerCase()))) {
                    modeloLista.addElement(r);
                }
            }
        }
    }

    private void eliminarCita() {
        String especialistaSeleccionado = (String) comboEspecialistas.getSelectedItem();

        if (especialistaSeleccionado == null || especialistaSeleccionado.equals("Todos")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un especialista concreto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar todas las citas y recordatorios del especialista '" + especialistaSeleccionado + "'?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) return;

        List<Recordatorios> recordatorios = obtenerTodosRecordatorios();
        int eliminados = 0;

        for (Recordatorios r : recordatorios) {
            if ("CitaMedica".equals(r.getTipoEvento()) &&
                    r.getDescripcion().toLowerCase().contains(especialistaSeleccionado.toLowerCase())) {

                try {
                    controladorRecordatorios.eliminarRecordatorio(r.getRecordatorioID());
                    controladorCitas.eliminarCita(r.getCitaMedicaID());
                    eliminados++;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Error eliminando cita/recordatorio con ID: " + r.getRecordatorioID() + ". " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        if (eliminados > 0) {
            JOptionPane.showMessageDialog(this, eliminados + " cita(s) y recordatorio(s) eliminados correctamente.");
        } else {
            JOptionPane.showMessageDialog(this, "No se encontraron citas para el especialista seleccionado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }

        cargarEspecialistas();
        ventanaCitasMedicas.cargarRecordatorios();
    }
}
