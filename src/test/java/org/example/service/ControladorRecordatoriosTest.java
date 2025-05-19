package org.example.service;

import org.example.model.Recordatorios;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControladorRecordatoriosTest {

    @Test
    void obtenerRecordatoriosPorUsuario() {
        ControladorRecordatorios controlador = new ControladorRecordatorios();
        int usuarioID = 3;

        List<Recordatorios> lista = controlador.obtenerRecordatoriosPorUsuario(usuarioID);

        //Mostrar cuántos recordatorios se han obtenido
        System.out.println("Se obtuvieron " + lista.size() + " recordatorio(s) para el usuario " + usuarioID);

        //Si la lista está vacía, se muestra el fallo
        assertFalse(lista.isEmpty(), "La lista está vacía pero se esperaba al menos un recordatorio.");

        //Mostrar contenido de la lista
        for (Recordatorios r : lista) {
            System.out.println("ID: " + r.getRecordatorioID() + " - Descripción: " + r.getDescripcion());
        }

        //Validar el usuario
        for (Recordatorios r : lista) {
            assertEquals(usuarioID, r.getUsuarioID(), "El recordatorio no pertenece al usuario esperado.");
        }
    }
}