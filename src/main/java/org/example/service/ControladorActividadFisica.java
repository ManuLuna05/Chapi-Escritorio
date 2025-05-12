package org.example.service;

import org.example.dao.DAOChapi;
import org.example.model.ActividadFisica;
import org.example.model.Recordatorios;

import java.sql.SQLException;
import java.util.List;

public class ControladorActividadFisica {
    private DAOChapi dao;

    public ControladorActividadFisica() {
        dao = new DAOChapi();
    }

    //Función para registrar una actividad física por medio de la función presente en el dao
    public int registrarActividad(ActividadFisica actividad) throws SQLException {
        return dao.registrarActividadFisica(actividad);
    }

    //Función para obtener todas las actividades físicas de un usuario por medio de la función presente en el dao
    public List<ActividadFisica> obtenerActividadesPorUsuario(int usuarioId) throws SQLException {
        return dao.obtenerActividadesPorUsuario(usuarioId);
    }

    //Función para actualizar una actividad por medio de la función presente en el dao
    public void actualizarActividad(ActividadFisica actividad) throws SQLException {
        dao.actualizarActividadFisica(actividad);
    }

    //Función para eliminar una actividad física por medio de la función presente en el dao
    public void eliminarActividad(int actividadId, int usuarioId) throws SQLException {
        //Eliminar recordatorios asociados primero
        ControladorRecordatorios controladorRecordatorios = new ControladorRecordatorios();
        List<Recordatorios> recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioId);

        for (Recordatorios recordatorio : recordatorios) {
            if (recordatorio.getActividadID() != null && recordatorio.getActividadID() == actividadId) {
                controladorRecordatorios.eliminarRecordatorio(recordatorio.getRecordatorioID());
            }
        }

        //Eliminar la actividad
        dao.eliminarActividadFisica(actividadId);
    }

    //Función para eliminar todas las actividades pasadas de un usuario por medio de la función presente en el dao
    public void eliminarActividadesPasadas(int usuarioID) {
        try {
            dao.eliminarActividadesPasadas(usuarioID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}