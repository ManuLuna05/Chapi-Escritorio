package org.example.service;

import org.example.dao.DAOChapi;
import org.example.model.Medicacion;
import org.example.model.Medicamento;
import org.example.model.Recordatorios;

import java.sql.SQLException;
import java.util.List;

public class ControladorMedicacion {

    private DAOChapi dao;

    public ControladorMedicacion() {
        dao = new DAOChapi();
    }

    // Clase ControladorMedicacion
    public int registrarMedicacion(Medicacion medicacion) {
        try {
            return dao.registrarMedicación(medicacion); // Retorna el ID generado
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Retorna un valor de error en caso de excepción
        }
    }

    // Obtener todas las medicaciones de un usuario
    public List<Medicacion> obtenerMedicacionesPorUsuario(int usuarioId) {
        try {
            return dao.obtenerMedicacionesPorUsuario(usuarioId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Obtener medicación por ID
    public Medicacion obtenerMedicaciónPorId(int medicacionId) {
        try {
            return dao.obtenerMedicaciónPorId(medicacionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Actualizar medicación
    public void actualizarMedicación(Medicacion medicacion) {
        try {
            dao.actualizarMedicación(medicacion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Eliminar medicación
    public void eliminarMedicación(int medicacionId, int usuarioId) {
        try {
            // Eliminar los recordatorios vinculados a la medicación
            ControladorRecordatorios controladorRecordatorios = new ControladorRecordatorios();
            List<Recordatorios> recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioId);
            for (Recordatorios recordatorio : recordatorios) {
                if (recordatorio.getMedicacionID() == medicacionId) {
                    controladorRecordatorios.eliminarRecordatorio(recordatorio.getRecordatorioID());
                }
            }

            // Eliminar la medicación
            dao.eliminarMedicación(medicacionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Obtener todos los medicamentos disponibles
    public List<Medicamento> obtenerTodosMedicamentos() {
        try {
            return dao.obtenerTodosMedicamentos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Obtener nombre del medicamento por ID
    public String obtenerNombreMedicamentoPorId(int medicamentoId) {
        try {
            return dao.obtenerNombreMedicamentoPorId(medicamentoId);
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al obtener el nombre del medicamento";
        }
    }
}
