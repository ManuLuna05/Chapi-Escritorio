package org.example.service;

import org.example.model.Recordatorios;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Test
    void testCrearRecordatorio() {
        ControladorRecordatorios controlador = new ControladorRecordatorios();
        Recordatorios recordatorio = new Recordatorios();

        recordatorio.setUsuarioID(3);
        recordatorio.setDescripcion("Recordatorio de prueba");
        recordatorio.setTipoEvento("medicación");
        recordatorio.setFecha(LocalDate.now().plusDays(1));
        recordatorio.setHora(LocalTime.of(9, 0));
        recordatorio.setFechaInicio(LocalDate.now().atStartOfDay());
        recordatorio.setFechaFin(LocalDate.now().plusDays(5).atStartOfDay());

        controlador.crearRecordatorio(recordatorio);

        List<Recordatorios> lista = controlador.obtenerRecordatoriosPorUsuario(3);

        boolean encontrado = lista.stream()
                .anyMatch(r -> r.getDescripcion().equals("Recordatorio de prueba"));

        assertTrue(encontrado, "El recordatorio no fue encontrado después de crearlo.");
    }


    @Test
    void testEliminarRecordatorio() {
        ControladorRecordatorios controlador = new ControladorRecordatorios();
        Recordatorios recordatorio = new Recordatorios();

        recordatorio.setUsuarioID(3);
        recordatorio.setDescripcion("Eliminar este");
        recordatorio.setTipoEvento("Medicacion");
        recordatorio.setFecha(LocalDate.now().plusDays(2));
        recordatorio.setHora(LocalTime.of(10, 0));
        recordatorio.setFechaInicio(LocalDate.now().atStartOfDay());
        recordatorio.setFechaFin(LocalDate.now().plusDays(5).atStartOfDay());

        try {
            controlador.crearRecordatorio(recordatorio);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error al crear recordatorio: " + e.getMessage());
        }

        List<Recordatorios> lista = controlador.obtenerRecordatoriosPorUsuario(3);
        Recordatorios creado = lista.stream()
                .filter(r -> r.getDescripcion().equals("Eliminar este"))
                .findFirst()
                .orElse(null);

        assertNotNull(creado, "El recordatorio debería haberse creado correctamente");

        controlador.eliminarRecordatorio(creado.getRecordatorioID());

        List<Recordatorios> despues = controlador.obtenerRecordatoriosPorUsuario(3);
        boolean sigue = despues.stream()
                .anyMatch(r -> r.getRecordatorioID() == creado.getRecordatorioID());

        assertFalse(sigue, "El recordatorio no fue eliminado correctamente.");
    }
}