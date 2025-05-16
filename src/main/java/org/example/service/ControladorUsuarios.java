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
    public void registrarUsuario(Usuario usuario) {
        try {
            dao.registroUsuario(usuario);
        } catch (SQLException e) {
            System.out.println("Error al registrar el usuario: " + e.getMessage());
        }
    }

    //Función auxiliar para registrar un usuario cuidador
    public void registrarUsuario(UsuarioCuidador usuarioCuidador) {
        try {
            dao.registrarUsuarioCuidador(usuarioCuidador);
        } catch (SQLException e) {
            System.out.println("Error al registrar el usuario cuidador: " + e.getMessage());
        }

    }

    //Función para obtener el identificador de un usuario por su correo electrónico
    public int obtenerUsuarioIdPorCorreo(String email) {
        try {
            return dao.obtenerUsuarioIdPorCorreo(email);
        } catch (SQLException e) {
            System.out.println("Error al obtener el ID del usuario por correo: " + e.getMessage());
        }
        return 0;
    }

    //Función para iniciar sesión
    public Usuario iniciarSesionUsuario(String email, String password) {
        return dao.inicioSesionUsuario(email, password);
    }

    //Función para editar un usuario
    public void editarUsuario(Usuario usuario) {
        try {
            dao.editarUsuario(usuario);
        } catch (SQLException e) {
            System.out.println("Error al editar el usuario: " + e.getMessage());
        }
    }

    //Función para obtener el paciente de un cuidador
    public List<Integer> obtenerPacientesDeCuidador(int cuidadorId) {
        try {
            return dao.obtenerPacientesDeCuidador(cuidadorId);
        } catch (SQLException e) {
            System.out.println("Error al obtener los pacientes del cuidador: " + e.getMessage());
        }
        return List.of();
    }

    //Función para obtener los usuarios por su id
    public Usuario obtenerUsuarioPorId(int usuarioId) {
        try {
            return dao.obtenerUsuarioPorId(usuarioId);
        } catch (SQLException e) {
            System.out.println("Error al obtener el usuario por ID: " + e.getMessage());
        }
        return null;
    }

    //Función para verificar si un correo electrónico tiene un formato válido
    public boolean esEmailValido(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    //Función para verificar si un teléfono tiene un formato válido
    public boolean esTelefonoValido(String telefono) {
        int numDigitos = telefono.replaceAll("\\D", "").length();
        return numDigitos == 9;
    }
}
