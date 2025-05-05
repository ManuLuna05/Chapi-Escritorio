package org.example.ui;

import org.example.model.Medicacion;
import org.example.model.Recordatorios;
import org.example.service.ControladorMedicacion;
import org.example.service.ControladorRecordatorios;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class VentanaEliminarMedicación extends JFrame {
    private JComboBox<Medicacion> comboMedicaciones;
    private JList<Recordatorios> listaRecordatorios;
    private DefaultListModel<Recordatorios> modeloLista;
    private ControladorMedicacion controladorMedicacion;
    private ControladorRecordatorios controladorRecordatorios;
    private int usuarioID;
    private VentanaAreaMedica ventanaAreaMedicaPadre;

    public VentanaEliminarMedicación(int usuarioID, int usuarioCuidadorID, VentanaAreaMedica ventanaAreaMedicaPadre) {
        this.usuarioID = usuarioID;
        this.ventanaAreaMedicaPadre = ventanaAreaMedicaPadre;
        controladorMedicacion = new ControladorMedicacion();
        controladorRecordatorios = new ControladorRecordatorios();

        setTitle("Eliminar Medicación");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel superior: Selección de medicación
        JPanel panelMedicacion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMedicacion.add(new JLabel("Seleccionar Medicación:"));
        comboMedicaciones = new JComboBox<>();
        cargarMedicaciones();
        comboMedicaciones.addActionListener(e -> cargarRecordatorios());
        panelMedicacion.add(comboMedicaciones);
        add(panelMedicacion, BorderLayout.NORTH);

        // Panel central: Lista de recordatorios
        JPanel panelRecordatorios = new JPanel(new BorderLayout());
        panelRecordatorios.setBorder(BorderFactory.createTitledBorder("Recordatorios relacionados"));
        modeloLista = new DefaultListModel<>();
        listaRecordatorios = new JList<>(modeloLista);
        listaRecordatorios.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        panelRecordatorios.add(new JScrollPane(listaRecordatorios), BorderLayout.CENTER);
        add(panelRecordatorios, BorderLayout.CENTER);

        // Panel inferior: Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnEliminarMedicacion = new JButton("Eliminar Medicación");
        btnEliminarMedicacion.addActionListener(e -> {
            try {
                eliminarMedicacion();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        JButton btnEliminarRecordatorios = new JButton("Eliminar Recordatorios");
        btnEliminarRecordatorios.addActionListener(e -> {
            try {
                eliminarRecordatorios();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        panelBotones.add(btnEliminarMedicacion);
        panelBotones.add(btnEliminarRecordatorios);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarMedicaciones() {
        comboMedicaciones.removeAllItems();
        List<Medicacion> medicaciones = controladorMedicacion.obtenerMedicacionesPorUsuario(usuarioID);
        if (medicaciones != null) {
            for (Medicacion medicacion : medicaciones) {
                comboMedicaciones.addItem(medicacion);
            }
        }
    }

    private void cargarRecordatorios() {
        modeloLista.clear();
        Medicacion medicacionSeleccionada = (Medicacion) comboMedicaciones.getSelectedItem();
        if (medicacionSeleccionada != null) {
            List<Recordatorios> recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioID);
            for (Recordatorios recordatorio : recordatorios) {
                if (recordatorio.getMedicacionID() == medicacionSeleccionada.getMedicacionID()) {
                    modeloLista.addElement(recordatorio);
                }
            }
        }
    }

    private void eliminarMedicacion() throws SQLException {
        Medicacion medicacionSeleccionada = (Medicacion) comboMedicaciones.getSelectedItem();
        if (medicacionSeleccionada != null) {
            controladorMedicacion.eliminarMedicación(medicacionSeleccionada.getMedicacionID(), usuarioID);
            JOptionPane.showMessageDialog(this, "Medicación eliminada correctamente.");
            cargarMedicaciones();
            modeloLista.clear();
            if (ventanaAreaMedicaPadre != null) {
                ventanaAreaMedicaPadre.cargarRecordatorios();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una medicación para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarRecordatorios() throws SQLException {
        List<Recordatorios> recordatoriosSeleccionados = listaRecordatorios.getSelectedValuesList();
        if (!recordatoriosSeleccionados.isEmpty()) {
            for (Recordatorios recordatorio : recordatoriosSeleccionados) {
                controladorRecordatorios.eliminarRecordatorio(recordatorio.getRecordatorioID());
            }
            JOptionPane.showMessageDialog(this, "Recordatorios eliminados correctamente.");
            cargarRecordatorios();
            if (ventanaAreaMedicaPadre != null) {
                ventanaAreaMedicaPadre.cargarRecordatorios();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione al menos un recordatorio para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}