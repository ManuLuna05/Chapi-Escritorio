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

    //Función para registrar una medicación por medio de la función presente en el dao
    public int registrarMedicacion(Medicacion medicacion) {
        try {
            return dao.registrarMedicación(medicacion);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //Obtener todas las medicaciones de un usuario por medio de la función presente en el dao
    public List<Medicacion> obtenerMedicacionesPorUsuario(int usuarioId) {
        try {
            return dao.obtenerMedicacionesPorUsuario(usuarioId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Función para actualizar medicación por medio de la función presente en el dao
    public void actualizarMedicación(Medicacion medicacion) {
        try {
            dao.actualizarMedicación(medicacion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Función para eliminar medicación por medio de la función presente en el dao
    public void eliminarMedicación(int medicacionId, int usuarioId) {
        try {
            //Eliminar los recordatorios vinculados a la medicación
            ControladorRecordatorios controladorRecordatorios = new ControladorRecordatorios();
            List<Recordatorios> recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioId);
            for (Recordatorios recordatorio : recordatorios) {
                if (recordatorio.getMedicacionID() != null &&
                        recordatorio.getMedicacionID() == medicacionId) {
                    controladorRecordatorios.eliminarRecordatorio(recordatorio.getRecordatorioID());
                }
            }

            //Eliminar la medicación
            dao.eliminarMedicación(medicacionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Función que obtiene todos los medicamentos disponibles
    public List<Medicamento> obtenerTodosMedicamentos() {
        try {
            return dao.obtenerTodosMedicamentos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Función que obtiene el nombre del medicamento por ID
    public String obtenerNombreMedicamentoPorId(int medicamentoId) {
        try {
            return dao.obtenerNombreMedicamentoPorId(medicamentoId);
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al obtener el nombre del medicamento";
        }
    }

    //Función para eliminar todas las medicaciones pasadas de un usuario por medio de la función presente en el dao
    public void eliminarMedicacionesPasadas(int usuarioID) {
        try {
            dao.eliminarMedicacionesPasadas(usuarioID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
