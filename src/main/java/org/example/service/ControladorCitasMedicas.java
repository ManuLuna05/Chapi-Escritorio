package org.example.service;

import org.example.dao.DAOChapi;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ControladorCitasMedicas {
    private DAOChapi dao = new DAOChapi();

    public int registrarCita(int usuarioID, Integer usuarioCuidadorID, LocalDate fecha, LocalTime hora, String lugar, String especialista) throws SQLException {
        return dao.registrarCitaMedica(usuarioID, usuarioCuidadorID, fecha, hora, lugar, especialista);
    }

    public void eliminarCita(Integer citaID) {
        try {
            dao.eliminarCitaMedica(citaID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarCitasPasadas(int usuarioID) {
        try {
            dao.eliminarCitasPasadas(usuarioID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
