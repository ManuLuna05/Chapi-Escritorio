package org.example.ui;

import org.example.model.Medicacion;
import org.example.model.Recordatorios;
import org.example.model.Usuario;
import org.example.service.ControladorMedicacion;
import org.example.service.ControladorRecordatorios;
import org.example.service.ControladorUsuarios;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaEliminarMedicación extends JFrame {
    private JComboBox<Medicacion> comboMedicaciones;
    private JList<Recordatorios> listaRecordatorios;
    private DefaultListModel<Recordatorios> modeloLista;
    private ControladorMedicacion controladorMedicacion;
    private ControladorRecordatorios controladorRecordatorios;
    private int usuarioID;
    private VentanaAreaMedica ventanaAreaMedicaPadre;
    private int idPacienteActual;

    //Ventana encargada de eliminar una medicación y sus recordatorios asociados o solo recordatorios de medicación
    public VentanaEliminarMedicación(int usuarioID, VentanaAreaMedica ventanaAreaMedicaPadre) {
        this.usuarioID = usuarioID;
        this.ventanaAreaMedicaPadre = ventanaAreaMedicaPadre;
        this.controladorMedicacion = new ControladorMedicacion();
        this.controladorRecordatorios = new ControladorRecordatorios();

        ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
        Usuario usuarioActual = controladorUsuarios.obtenerUsuarioPorId(usuarioID);

        //Se hace una verificación del tipo de usuario, si es cuidador se obtiene el paciente asignado
        if (usuarioActual.getTipo().equals("cuidador")) {
            List<Integer> pacientes = controladorUsuarios.obtenerPacientesDeCuidador(usuarioID);
            if (!pacientes.isEmpty()) {
                this.idPacienteActual = pacientes.get(0);
            } else {
                JOptionPane.showMessageDialog(this, "No tiene pacientes asignados", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }
        } else {
            this.idPacienteActual = usuarioID;
        }
        interfazVentana();
    }

    //Función que crea la interfaz de la ventana
    private void interfazVentana() {
        setTitle("Eliminar Medicación");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(new JLabel("Medicaciones disponibles:"), BorderLayout.NORTH);

        comboMedicaciones = new JComboBox<>();
        cargarMedicaciones();
        comboMedicaciones.addActionListener(e -> cargarRecordatorios());
        panelSuperior.add(comboMedicaciones, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createTitledBorder("Recordatorios asociados"));

        modeloLista = new DefaultListModel<>();
        listaRecordatorios = new JList<>(modeloLista);
        listaRecordatorios.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        panelCentral.add(new JScrollPane(listaRecordatorios), BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        //Botones para eliminar medicación y recordatorios o solo los recordatorios
        JButton botonEliminarTodo = new JButton("Eliminar Medicación");
        botonEliminarTodo.addActionListener(e -> eliminarMedicacionYRecordatorios());
        estiloBoton(botonEliminarTodo);

        JButton botonEliminarRecordatorios = new JButton("Eliminar Recordatorio");
        botonEliminarRecordatorios.addActionListener(e -> eliminarRecordatoriosSeleccionados());
        estiloBoton(botonEliminarRecordatorios);

        panelBotones.add(botonEliminarTodo);
        panelBotones.add(botonEliminarRecordatorios);
        add(panelBotones, BorderLayout.SOUTH);
    }

    //Función para aplicar estilo a los botones
    private void estiloBoton(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(new Color(113, 183, 188));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(180, 40));
    }

    //Función para cargar las medicaciones del paciente actual
    private void cargarMedicaciones() {
        comboMedicaciones.removeAllItems();
        List<Medicacion> medicaciones = controladorMedicacion.obtenerMedicacionesPorUsuario(idPacienteActual);

        //Si hay medicaciones, se añaden al combo box
        if (medicaciones != null && !medicaciones.isEmpty()) {
            for (Medicacion medicacion : medicaciones) {
                comboMedicaciones.addItem(medicacion);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No hay medicaciones registradas", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //Función para cargar los recordatorios seleccionados asociados a la medicación seleccionada
    private void cargarRecordatorios() {
        modeloLista.clear();
        Medicacion medicacionSeleccionada = (Medicacion) comboMedicaciones.getSelectedItem();

        //Si hay una medicación seleccionada, se obtienen sus recordatorios
        if (medicacionSeleccionada != null) {
            List<Recordatorios> recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(idPacienteActual);

            //Se filtran los recordatorios que pertenecen a la medicación seleccionada
            for (Recordatorios recordatorio : recordatorios) {
                if (recordatorio.getMedicacionID() != null && recordatorio.getMedicacionID() == medicacionSeleccionada.getMedicacionID()) {
                    modeloLista.addElement(recordatorio);
                }
            }
        }
    }


    //Función para eliminar la medicación y sus recordatorios asociados
    private void eliminarMedicacionYRecordatorios() {
        Medicacion medicacionSeleccionada = (Medicacion) comboMedicaciones.getSelectedItem();

        //Si hay una medicación seleccionada, se procede a eliminarla junto con sus recordatorios
        if (medicacionSeleccionada != null) {
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar esta medicación y todos sus recordatorios?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

            //Si el usuario confirma la eliminación, se eliminan los recordatorios y la medicación
            if (confirmacion == JOptionPane.YES_OPTION) {
                for (int i = 0; i < modeloLista.size(); i++) {
                    Recordatorios recordatorio = modeloLista.get(i);
                    controladorRecordatorios.eliminarRecordatorio(recordatorio.getRecordatorioID());
                }

                controladorMedicacion.eliminarMedicación(medicacionSeleccionada.getMedicacionID(), idPacienteActual);

                JOptionPane.showMessageDialog(this, "Medicación y recordatorios eliminados correctamente");
                cargarMedicaciones();

                //Se recarga la lista de recordatorios
                if (ventanaAreaMedicaPadre != null) {
                    ventanaAreaMedicaPadre.cargarRecordatorios();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una medicación para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Función para eliminar los recordatorios seleccionados
    private void eliminarRecordatoriosSeleccionados() {
        List<Recordatorios> seleccionados = listaRecordatorios.getSelectedValuesList();

        //Si hay recordatorios seleccionados se procede a eliminarlos
        if (!seleccionados.isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar los recordatorios seleccionados?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

            //Si el usuario confirma la eliminación, se eliminan los recordatorios seleccionados
            if (confirmacion == JOptionPane.YES_OPTION) {
                for (Recordatorios recordatorio : seleccionados) {
                    controladorRecordatorios.eliminarRecordatorio(recordatorio.getRecordatorioID());
                }

                JOptionPane.showMessageDialog(this, "Recordatorios eliminados correctamente");
                cargarRecordatorios();

                //Se recarga la lista de recordatorios en la ventana padre
                if (ventanaAreaMedicaPadre != null) {
                    ventanaAreaMedicaPadre.cargarRecordatorios();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione al menos un recordatorio para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}