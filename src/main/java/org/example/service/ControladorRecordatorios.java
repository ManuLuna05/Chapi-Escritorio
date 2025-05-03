package org.example.service;

import org.example.dao.DAOChapi;
import org.example.model.Recordatorios;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ControladorRecordatorios {

    private DAOChapi dao;

    public ControladorRecordatorios() {
        dao = new DAOChapi();
    }

    // Crear nuevo recordatorio
    public void crearRecordatorio(Recordatorios recordatorio) {
        try {
            if (recordatorio.getFechaInicio() == null || recordatorio.getFechaFin() == null) {
                throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas.");
            }
            dao.crearRecordatorio(recordatorio);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Obtener todos los recordatorios de un usuario
    public List<Recordatorios> obtenerRecordatoriosPorUsuario(int usuarioID) {
        try {
            return dao.obtenerRecordatoriosPorUsuario(usuarioID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Actualizar recordatorio
    public void actualizarRecordatorio(Recordatorios recordatorio) {
        try {
            dao.actualizarRecordatorio(recordatorio);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Eliminar recordatorio
    public void eliminarRecordatorio(int recordatorioID) {
        try {
            dao.eliminarRecordatorio(recordatorioID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarRecordatoriosPasados(int usuarioID) {
        try {
            List<Recordatorios> recordatorios = dao.obtenerRecordatoriosPorUsuario(usuarioID);
            for (Recordatorios recordatorio : recordatorios) {
                if (recordatorio.getFecha().isBefore(LocalDate.now()) ||
                        (recordatorio.getFecha().isEqual(LocalDate.now()) && recordatorio.getHora().isBefore(LocalTime.now()))) {
                    dao.eliminarRecordatorio(recordatorio.getRecordatorioID());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
