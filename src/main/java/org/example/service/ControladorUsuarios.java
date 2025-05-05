package org.example.service;

import org.example.dao.DAOChapi;
import org.example.model.Usuario;
import org.example.model.UsuarioCuidador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        dao.registrarUsuarioCuidador(usuarioCuidador);
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

    public void obtenerPacientesDelCuidador(int cuidadorId) throws SQLException {
        dao.obtenerPacientesDeCuidador(cuidadorId);
    }

    public boolean existeRelacionCuidador(int usuarioCuidadoId, int cuidadorId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Cuidador_Usuario WHERE UsuarioID = ? AND UsuarioCuidadorID = ?";
        try (Connection conn = dao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioCuidadoId);
            stmt.setInt(2, cuidadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Integer> obtenerPacientesDeCuidador(int cuidadorId) throws SQLException {
        List<Integer> pacientes = new ArrayList<>();
        String query = "SELECT UsuarioID FROM Cuidador_Usuario WHERE UsuarioCuidadorID = ?";
        try (Connection conn = dao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cuidadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pacientes.add(rs.getInt("UsuarioID"));
                }
            }
        }
        return pacientes;
    }

    public Usuario obtenerUsuarioPorId(int usuarioId) throws SQLException {
        String query = "SELECT * FROM Usuario WHERE UsuarioID = ?";
        try (Connection conn = dao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario(
                            rs.getInt("UsuarioID"),
                            rs.getString("Nombre"),
                            rs.getString("Apellidos"),
                            rs.getString("Email"),
                            rs.getString("Password"),
                            rs.getString("Telefono"),
                            rs.getString("Tipo")
                    );
                    return usuario;
                }
            }
        }
        return null;
    }
}
