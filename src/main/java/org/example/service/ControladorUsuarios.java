package org.example.service;

import org.example.dao.DAOChapi;
import org.example.model.Usuario;
import org.example.model.UsuarioCuidador;

import java.sql.SQLException;
import java.util.List;

public class ControladorUsuarios {
    private final DAOChapi dao;

    public ControladorUsuarios() {
        this.dao = new DAOChapi();
    }

    //Función para registrar un usuario (cuidador o cuidado)
    public void registrarUsuario(Usuario usuario) throws SQLException {
        dao.registroUsuario(usuario);
    }

    //Función auxiliar para registrar un usuario cuidador
    public void registrarUsuario(UsuarioCuidador usuarioCuidador) throws SQLException {
        dao.registrarUsuarioCuidador(usuarioCuidador);
    }

    //Función para obtener el identificador de un usuario por su correo electrónico
    public int obtenerUsuarioIdPorCorreo(String email) throws SQLException {
        return dao.obtenerUsuarioIdPorCorreo(email);
    }

    //Función para iniciar sesión
    public Usuario iniciarSesionUsuario(String email, String password) {
        return dao.inicioSesionUsuario(email, password);
    }

    //Función para editar un usuario
    public void editarUsuario(Usuario usuario) throws SQLException {
        dao.editarUsuario(usuario);
    }

    public List<Integer> obtenerPacientesDeCuidador(int cuidadorId) throws SQLException {
        return dao.obtenerPacientesDeCuidador(cuidadorId);
    }

    public Usuario obtenerUsuarioPorId(int usuarioId) throws SQLException {
        return dao.obtenerUsuarioPorId(usuarioId);
    }

}
