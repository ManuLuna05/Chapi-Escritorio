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

    public int registrarActividad(ActividadFisica actividad) throws SQLException {
        return dao.registrarActividadFisica(actividad);
    }

    public List<ActividadFisica> obtenerActividadesPorUsuario(int usuarioId) throws SQLException {
        return dao.obtenerActividadesPorUsuario(usuarioId);
    }

    public ActividadFisica obtenerActividadPorId(int actividadId) throws SQLException {
        return dao.obtenerActividadPorId(actividadId);
    }

    public void actualizarActividad(ActividadFisica actividad) throws SQLException {
        dao.actualizarActividadFisica(actividad);
    }

    public void eliminarActividad(int actividadId, int usuarioId) throws SQLException {
        // Eliminar recordatorios asociados primero
        ControladorRecordatorios controladorRecordatorios = new ControladorRecordatorios();
        List<Recordatorios> recordatorios = controladorRecordatorios.obtenerRecordatoriosPorUsuario(usuarioId);

        for (Recordatorios recordatorio : recordatorios) {
            if (recordatorio.getActividadID() != null && recordatorio.getActividadID() == actividadId) {
                controladorRecordatorios.eliminarRecordatorio(recordatorio.getRecordatorioID());
            }
        }

        // Eliminar la actividad
        dao.eliminarActividadFisica(actividadId);
    }

}