package org.example.service;

import org.example.dao.DAOChapi;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ControladorCitasMedicas {
    private DAOChapi dao = new DAOChapi();

    //Función para registrar una cita médica por medio de la función presente en el dao
    public int registrarCita(int usuarioID, Integer usuarioCuidadorID, LocalDate fecha, LocalTime hora, String lugar, String especialista) throws SQLException {
        return dao.registrarCitaMedica(usuarioID, usuarioCuidadorID, fecha, hora, lugar, especialista);
    }

    //Función para eliminar una cita por medio de la función presente en el dao
    public void eliminarCita(Integer citaID) {
        try {
            dao.eliminarCitaMedica(citaID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Función para obtener todas las citas médicas pasadas de un usuario por medio de la función presente en el dao
    public void eliminarCitasPasadas(int usuarioID) {
        try {
            dao.eliminarCitasPasadas(usuarioID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
