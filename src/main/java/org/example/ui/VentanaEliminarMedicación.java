package org.example.ui;

import org.example.model.Medicacion;
import org.example.model.Recordatorios;
import org.example.model.Usuario;
import org.example.service.ControladorMedicacion;
import org.example.service.ControladorRecordatorios;
import org.example.service.ControladorUsuarios;

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
    private int usuarioID; // ID del usuario actual (cuidador o paciente)
    private VentanaAreaMedica ventanaAreaMedicaPadre;
    private int idPacienteActual; // ID del paciente cuyas medicaciones se muestran

    public VentanaEliminarMedicación(int usuarioID, VentanaAreaMedica ventanaAreaMedicaPadre) throws SQLException {
        this.usuarioID = usuarioID;
        this.ventanaAreaMedicaPadre = ventanaAreaMedicaPadre;
        this.controladorMedicacion = new ControladorMedicacion();
        this.controladorRecordatorios = new ControladorRecordatorios();

        // Determinar si es cuidador y cargar el primer paciente por defecto
        ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
        Usuario usuarioActual = controladorUsuarios.obtenerUsuarioPorId(usuarioID);

        if (usuarioActual.getTipo().equals("cuidador")) {
            List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);
            if (!pacientes.isEmpty()) {
                this.idPacienteActual = pacientes.get(0); // Tomar el primer paciente
            } else {
                JOptionPane.showMessageDialog(this, "No tiene pacientes asignados", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }
        } else {
            this.idPacienteActual = usuarioID; // Si es paciente, es él mismo
        }

        initUI();
    }

    private void initUI() {
        setTitle("Eliminar Medicación");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel superior: Selección de medicación
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(new JLabel("Medicaciones disponibles:"), BorderLayout.NORTH);

        comboMedicaciones = new JComboBox<>();
        cargarMedicaciones();
        comboMedicaciones.addActionListener(e -> cargarRecordatorios());
        panelSuperior.add(comboMedicaciones, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel central: Lista de recordatorios
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createTitledBorder("Recordatorios asociados"));

        modeloLista = new DefaultListModel<>();
        listaRecordatorios = new JList<>(modeloLista);
        listaRecordatorios.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        panelCentral.add(new JScrollPane(listaRecordatorios), BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior: Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnEliminarTodo = new JButton("Eliminar Medicación y Recordatorios");
        btnEliminarTodo.addActionListener(e -> {
            try {
                eliminarMedicacionYRecordatorios();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnEliminarRecordatorios = new JButton("Eliminar Solo Recordatorios");
        btnEliminarRecordatorios.addActionListener(e -> {
            try {
                eliminarRecordatoriosSeleccionados();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar recordatorios: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBotones.add(btnEliminarTodo);
        panelBotones.add(btnEliminarRecordatorios);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarMedicaciones() {
        comboMedicaciones.removeAllItems();
        List<Medicacion> medicaciones = controladorMedicacion.obtenerMedicacionesPorUsuario(idPacienteActual);

        if (medicaciones != null && !medicaciones.isEmpty()) {
            for (Medicacion medicacion : medicaciones) {
                comboMedicaciones.addItem(medicacion);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No hay medicaciones registradas",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cargarRecordatorios() {
        modeloLista.clear();
        Medicacion medicacionSeleccionada = (Medicacion) comboMedicaciones.getSelectedItem();

        if (medicacionSeleccionada != null) {
            List<Recordatorios> recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(idPacienteActual);

            for (Recordatorios recordatorio : recordatorios) {
                if (recordatorio.getMedicacionID() == medicacionSeleccionada.getMedicacionID()) {
                    modeloLista.addElement(recordatorio);
                }
            }
        }
    }

    private void eliminarMedicacionYRecordatorios() throws SQLException {
        Medicacion medicacionSeleccionada = (Medicacion) comboMedicaciones.getSelectedItem();

        if (medicacionSeleccionada != null) {
            // Confirmación antes de eliminar
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea eliminar esta medicación y todos sus recordatorios?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                // Eliminar todos los recordatorios asociados
                for (int i = 0; i < modeloLista.size(); i++) {
                    Recordatorios recordatorio = modeloLista.get(i);
                    controladorRecordatorios.eliminarRecordatorio(recordatorio.getRecordatorioID());
                }

                // Eliminar la medicación
                controladorMedicacion.eliminarMedicación(
                        medicacionSeleccionada.getMedicacionID(),
                        idPacienteActual
                );

                JOptionPane.showMessageDialog(this, "Medicación y recordatorios eliminados correctamente");
                cargarMedicaciones(); // Actualizar la lista

                if (ventanaAreaMedicaPadre != null) {
                    ventanaAreaMedicaPadre.cargarRecordatorios(); // Actualizar la ventana principal
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una medicación para eliminar",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarRecordatoriosSeleccionados() throws SQLException {
        List<Recordatorios> seleccionados = listaRecordatorios.getSelectedValuesList();

        if (!seleccionados.isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea eliminar los recordatorios seleccionados?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                for (Recordatorios recordatorio : seleccionados) {
                    controladorRecordatorios.eliminarRecordatorio(recordatorio.getRecordatorioID());
                }

                JOptionPane.showMessageDialog(this, "Recordatorios eliminados correctamente");
                cargarRecordatorios(); // Actualizar la lista

                if (ventanaAreaMedicaPadre != null) {
                    ventanaAreaMedicaPadre.cargarRecordatorios(); // Actualizar la ventana principal
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione al menos un recordatorio para eliminar",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}