package org.example.service;

import org.example.dao.DAOChapi;
import org.example.model.Recordatorios;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ControladorRecordatorios {

    private DAOChapi dao;

    public ControladorRecordatorios() {
        dao = new DAOChapi();
    }

    //Función para crear un recordatorio por medio de la función presente en el dao
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

    //Función que obtiene todos los recordatorios de un usuario por medio de la función presente en el dao
    public List<Recordatorios> obtenerRecordatoriosPorUsuario(int usuarioID) {
        try {
            return dao.obtenerRecordatoriosPorUsuario(usuarioID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Función para actualizar un recordatorio por medio de la función presente en el dao
    public void actualizarRecordatorio(Recordatorios recordatorio) {
        try {
            dao.actualizarRecordatorio(recordatorio);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Función para eliminar recordatorio por medio de la función presente en el dao
    public void eliminarRecordatorio(int recordatorioID) {
        try {
            dao.eliminarRecordatorio(recordatorioID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Función para eliminar recordatorios pasados de un usuario
    public void eliminarRecordatoriosPasados(int usuarioID) {
        try {
            List<Recordatorios> recordatorios = obtenerRecordatoriosPorUsuarioOCuidador(usuarioID);
            for (Recordatorios recordatorio : recordatorios) {
                if (recordatorio.getFecha().isBefore(LocalDate.now()) ||
                        (recordatorio.getFecha().isEqual(LocalDate.now()) && recordatorio.getHora().isBefore(LocalTime.now()))) {
                    eliminarRecordatorio(recordatorio.getRecordatorioID());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Función para obtener recordatorios donde el usuario es el cuidador por medio de la función presente en el dao
    public List<Recordatorios> obtenerRecordatoriosPorCuidador(int cuidadorID) {
        try {
            return dao.obtenerRecordatoriosPorCuidador(cuidadorID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Función para obtener recordatorios de un usuario o cuidador
    public List<Recordatorios> obtenerRecordatoriosPorUsuarioOCuidador(int usuarioID) throws SQLException {
        List<Recordatorios> recordatorios = new ArrayList<>();
        recordatorios.addAll(obtenerRecordatoriosPorUsuario(usuarioID));
        recordatorios.addAll(obtenerRecordatoriosPorCuidador(usuarioID));
        return recordatorios;
    }
}
