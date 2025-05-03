package org.example.service;

import org.example.dao.DAOChapi;
import org.example.model.Usuario;
import org.example.model.UsuarioCuidador;

import java.sql.SQLException;

public class ControladorUsuarios {
    private DAOChapi dao;

    public ControladorUsuarios() {
        this.dao = new DAOChapi();
    }

    // Método para registrar un usuario (cuidador o cuidado)
    public void registrarUsuario(Usuario usuario) throws SQLException {
        dao.registroUsuario(usuario);
    }

    public void registrarUsuario(UsuarioCuidador usuarioCuidador) throws SQLException {
        // Lógica para registrar UsuarioCuidador, por ejemplo, insertando en la tabla de relaciones
    }

    // Método para asignar un cuidador a un usuario cuidado
    public void asignarCuidador(int usuarioCuidadoId, int cuidadorId) throws SQLException {
        dao.asignarCuidador(usuarioCuidadoId, cuidadorId);
    }

    public int obtenerUsuarioIdPorCorreo(String email) throws SQLException {
        // Llamamos al DAO para obtener el ID por correo
        return dao.obtenerUsuarioIdPorCorreo(email);
    }

    // Método para iniciar sesión
    public Usuario iniciarSesionUsuario(String email, String password) throws SQLException {
        return dao.inicioSesionUsuario(email, password);
    }

    // Método para editar un usuario
    public void editarUsuario(Usuario usuario) throws SQLException {
        dao.editarUsuario(usuario);
    }

    // Método para eliminar un usuario
    public void eliminarUsuario(int id) throws SQLException {
        dao.eliminarUsuario(id);
    }
}
