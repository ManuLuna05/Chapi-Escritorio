package org.example.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControladorUsuariosTest {

    @Test
    void esEmailValido() {
        assertTrue(ControladorUsuarios.esEmailValido("usuario@gmail.com"));
        assertFalse(ControladorUsuarios.esEmailValido("usuario.com"));
        assertFalse(ControladorUsuarios.esEmailValido("usuario@gmail"));
        assertFalse(ControladorUsuarios.esEmailValido(null));
    }

    @Test
    void esTelefonoValido() {
        assertTrue(ControladorUsuarios.esTelefonoValido("654987321"));
        assertFalse(ControladorUsuarios.esTelefonoValido("12345"));
        assertFalse(ControladorUsuarios.esTelefonoValido("abcdefghi"));
        assertFalse(ControladorUsuarios.esTelefonoValido(null));
    }
}