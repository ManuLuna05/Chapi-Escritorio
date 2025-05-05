package org.example.dao;

import org.example.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DAOChapi {
    // Declaración de las variables de conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/chapi";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Método para obtener la conexión a la base de datos
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Métodos CRUD para Usuario
    public void registroUsuario(Usuario usuario) throws SQLException {
        String query = "INSERT INTO Usuario (Nombre, Apellidos, Email, Password, Telefono, Tipo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellidos());
            stmt.setBytes(3, usuario.getEmail().getBytes());
            stmt.setBytes(4, usuario.getPassword().getBytes());
            stmt.setBytes(5, usuario.getTelefono() != null ? usuario.getTelefono().getBytes() : null);
            stmt.setString(6, usuario.getTipo());
            stmt.executeUpdate();
        }
    }

    // Inicio de sesión del usuario
    public Usuario inicioSesionUsuario(String email, String password) {
        // Conexión con el mismo usuario y contraseña para acceder a la base de datos
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM Usuario WHERE Email = ? AND Password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, password);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Obtener los datos del usuario
                        int id = rs.getInt("UsuarioID");
                        String nombre = rs.getString("Nombre");
                        String apellidos = rs.getString("Apellidos");
                        String tipo = rs.getString("Tipo");
                        String telefono = rs.getString("Telefono");

                        // Crear el objeto Usuario y devolverlo
                        return new Usuario(id, nombre, apellidos, email, password, telefono, tipo);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void registrarUsuarioCuidador(UsuarioCuidador usuarioCuidador) throws SQLException {
        String sql = "INSERT INTO Cuidador_Usuario (UsuarioID, UsuarioCuidadorID) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioCuidador.getUsuarioId());
            stmt.setInt(2, usuarioCuidador.getCuidadorId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new SQLException("Error al registrar la relación cuidador-cuidado", ex);
        }
    }

    public void editarUsuario(Usuario usuario) throws SQLException {
        String query = "UPDATE Usuario SET Nombre = ?, Apellidos = ?, Email = ?, Password = ?, Telefono = ?, Tipo = ? WHERE UsuarioID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellidos());
            stmt.setBytes(3, usuario.getEmail().getBytes());
            stmt.setBytes(4, usuario.getPassword().getBytes());
            stmt.setBytes(5, usuario.getTelefono() != null ? usuario.getTelefono().getBytes() : null);
            stmt.setString(6, usuario.getTipo());
            stmt.setInt(7, usuario.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminarCuidador(int usuarioId, int cuidadorId) throws SQLException {
        String query = "DELETE FROM Cuidador_Usuario WHERE UsuarioID = ? AND UsuarioCuidadorID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, cuidadorId);
            stmt.executeUpdate();
        }
    }

    // Métodos CRUD para Medicamento
    public void registrarMedicamento(Medicamento medicamento) throws SQLException {
        String query = "INSERT INTO Medicamento (Nombre, FechaCaducidad, Descripcion, Foto) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, medicamento.getNombre());
            stmt.setDate(2, Date.valueOf(medicamento.getFechaCaducidad()));
            stmt.setString(3, medicamento.getDescripcion());
            stmt.setString(4, medicamento.getFoto());
            stmt.executeUpdate();
        }
    }

    public int obtenerUsuarioIdPorCorreo(String email) throws SQLException {
        try (Connection connection = getConnection()) {
            String sql = "SELECT UsuarioID FROM Usuario WHERE Email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setBytes(1, email.getBytes());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("UsuarioID");
                } else {
                    throw new SQLException("No se encontró un usuario con ese correo.");
                }
            }
        }
    }

    public List<Integer> obtenerPacientesDeCuidador(int cuidadorId) throws SQLException {
        List<Integer> pacientes = new ArrayList<>();
        String query = "SELECT UsuarioID FROM Cuidador_Usuario WHERE UsuarioCuidadorID = ?";
        try (Connection conn = getConnection();
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
        try (Connection conn = getConnection();
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

    public List<Medicamento> obtenerTodosMedicamentos() throws SQLException {
        List<Medicamento> medicamentos = new ArrayList<>();
        String query = "SELECT * FROM Medicamento";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Medicamento medicamento = new Medicamento();
                medicamento.setId(rs.getInt("MedicamentoID"));
                medicamento.setNombre(rs.getString("Nombre"));
                medicamento.setFechaCaducidad(rs.getDate("FechaCaducidad").toString());
                medicamento.setDescripcion(rs.getString("Descripcion"));
                medicamento.setFoto(rs.getString("Foto"));
                medicamentos.add(medicamento);
            }
        }
        return medicamentos;
    }

    public String obtenerNombreMedicamentoPorId(int medicamentoId) throws SQLException {
        String query = "SELECT Nombre FROM Medicamento WHERE MedicamentoID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, medicamentoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Nombre");
                } else {
                    throw new SQLException("No se encontró el medicamento con ID: " + medicamentoId);
                }
            }
        }
    }

    // Método para crear un nuevo recordatorio
    public void crearRecordatorio(Recordatorios recordatorio) throws SQLException {
        String query = "INSERT INTO Recordatorio (UsuarioID, UsuarioCuidadorID, Descripcion, TipoEvento, NumeroDosis, Fecha, Hora, FechaInicio, FechaFin, CitaMedicaID, MedicacionID, ActividadID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recordatorio.getUsuarioID());
            stmt.setObject(2, recordatorio.getUsuarioCuidadorID());
            stmt.setString(3, recordatorio.getDescripcion());
            stmt.setString(4, recordatorio.getTipoEvento());
            stmt.setInt(5, recordatorio.getNumeroDosis());
            stmt.setDate(6, Date.valueOf(recordatorio.getFecha()));
            stmt.setTime(7, Time.valueOf(recordatorio.getHora()));
            stmt.setTimestamp(8, Timestamp.valueOf(recordatorio.getFechaInicio()));
            stmt.setTimestamp(9, Timestamp.valueOf(recordatorio.getFechaFin()));
            stmt.setObject(10, recordatorio.getCitaMedicaID());
            stmt.setObject(11, recordatorio.getMedicacionID());
            stmt.setObject(12, recordatorio.getActividadID());
            stmt.executeUpdate();
        }
    }

    // Método para obtener todos los recordatorios de un usuario
    public List<Recordatorios> obtenerRecordatoriosPorUsuario(int usuarioID) throws SQLException {
        List<Recordatorios> recordatorios = new ArrayList<>();
        String query = "SELECT * FROM Recordatorio WHERE UsuarioID = ? OR UsuarioCuidadorID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioID);
            stmt.setInt(2, usuarioID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recordatorios recordatorio = new Recordatorios();
                    recordatorio.setRecordatorioID(rs.getInt("RecordatorioID"));
                    recordatorio.setUsuarioID(rs.getInt("UsuarioID"));
                    recordatorio.setUsuarioCuidadorID(rs.getInt("UsuarioCuidadorID"));
                    recordatorio.setDescripcion(rs.getString("Descripcion"));
                    recordatorio.setTipoEvento(rs.getString("TipoEvento"));
                    recordatorio.setNumeroDosis(rs.getInt("NumeroDosis"));
                    recordatorio.setFecha(rs.getDate("Fecha").toLocalDate());
                    recordatorio.setHora(rs.getTime("Hora").toLocalTime());
                    recordatorio.setFechaInicio(rs.getTimestamp("FechaInicio").toLocalDateTime());
                    recordatorio.setFechaFin(rs.getTimestamp("FechaFin").toLocalDateTime());
                    recordatorio.setCitaMedicaID(rs.getInt("CitaMedicaID"));
                    recordatorio.setMedicacionID(rs.getInt("MedicacionID"));
                    recordatorio.setActividadID(rs.getInt("ActividadID"));
                    recordatorios.add(recordatorio);
                }
            }
        }
        return recordatorios;
    }

    // Método para obtener recordatorios donde el usuario es el cuidador
    public List<Recordatorios> obtenerRecordatoriosPorCuidador(int cuidadorID) throws SQLException {
        List<Recordatorios> recordatorios = new ArrayList<>();
        String query = "SELECT * FROM Recordatorio WHERE UsuarioCuidadorID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cuidadorID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recordatorios recordatorio = new Recordatorios();
                    recordatorio.setRecordatorioID(rs.getInt("RecordatorioID"));
                    recordatorio.setUsuarioID(rs.getInt("UsuarioID"));
                    recordatorio.setUsuarioCuidadorID(rs.getInt("UsuarioCuidadorID"));
                    recordatorio.setDescripcion(rs.getString("Descripcion"));
                    recordatorio.setTipoEvento(rs.getString("TipoEvento"));
                    recordatorio.setNumeroDosis(rs.getInt("NumeroDosis"));
                    recordatorio.setFecha(rs.getDate("Fecha").toLocalDate());
                    recordatorio.setHora(rs.getTime("Hora").toLocalTime());
                    recordatorio.setFechaInicio(rs.getTimestamp("FechaInicio").toLocalDateTime());
                    recordatorio.setFechaFin(rs.getTimestamp("FechaFin").toLocalDateTime());
                    recordatorio.setCitaMedicaID(rs.getInt("CitaMedicaID"));
                    recordatorio.setMedicacionID(rs.getInt("MedicacionID"));
                    recordatorio.setActividadID(rs.getInt("ActividadID"));
                    recordatorios.add(recordatorio);
                }
            }
        }
        return recordatorios;
    }

    // Método para actualizar un recordatorio
    public void actualizarRecordatorio(Recordatorios recordatorio) throws SQLException {
        String query = "UPDATE Recordatorio SET UsuarioID = ?, UsuarioCuidadorID = ?, Descripcion = ?, TipoEvento = ?, NumeroDosis = ?, Fecha = ?, Hora = ?, FechaInicio = ?, FechaFin = ?, CitaMedicaID = ?, MedicacionID = ?, ActividadID = ? " +
                "WHERE RecordatorioID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recordatorio.getUsuarioID());
            stmt.setInt(2, recordatorio.getUsuarioCuidadorID() != null ? recordatorio.getUsuarioCuidadorID() : 0);
            stmt.setString(3, recordatorio.getDescripcion());
            stmt.setString(4, recordatorio.getTipoEvento());
            stmt.setInt(5, recordatorio.getNumeroDosis());
            stmt.setDate(6, Date.valueOf(recordatorio.getFecha()));
            stmt.setTime(7, Time.valueOf(recordatorio.getHora()));
            stmt.setTimestamp(8, Timestamp.valueOf(recordatorio.getFechaInicio()));
            stmt.setTimestamp(9, Timestamp.valueOf(recordatorio.getFechaFin()));
            stmt.setInt(10, recordatorio.getCitaMedicaID() != null ? recordatorio.getCitaMedicaID() : 0);
            stmt.setInt(11, recordatorio.getMedicacionID() != null ? recordatorio.getMedicacionID() : 0);
            stmt.setInt(12, recordatorio.getActividadID() != null ? recordatorio.getActividadID() : 0);
            stmt.setInt(13, recordatorio.getRecordatorioID());
            stmt.executeUpdate();
        }
    }

    public void eliminarRecordatorio(int recordatorioID) throws SQLException {
        String query = "DELETE FROM Recordatorio WHERE RecordatorioID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recordatorioID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró el recordatorio con ID: " + recordatorioID);
            }
        }
    }

    public int registrarMedicación(Medicacion medicacion) throws SQLException {
        String query = "INSERT INTO Medicacion (UsuarioID, MedicamentoID, Dosis, Frecuencia, Duracion, FechaInicio, FechaFin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, medicacion.getUsuarioID());
            stmt.setInt(2, medicacion.getMedicamentoID());
            stmt.setInt(3, medicacion.getDosis());
            stmt.setString(4, medicacion.getFrecuencia());
            stmt.setInt(5, medicacion.getDuracion());
            stmt.setDate(6, Date.valueOf(medicacion.getFechaInicio()));
            stmt.setDate(7, Date.valueOf(medicacion.getFechaFin()));
            stmt.executeUpdate();

            // Obtener el ID generado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la medicación.");
                }
            }
        }
    }

    // Método para obtener todas las medicaciones de un usuario
    public List<Medicacion> obtenerMedicacionesPorUsuario(int usuarioId) throws SQLException {
        List<Medicacion> medicaciones = new ArrayList<>();
        String query = "SELECT * FROM Medicacion WHERE UsuarioID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Medicacion medicacion = new Medicacion();
                    medicacion.setMedicacionID(rs.getInt("MedicacionID"));
                    medicacion.setUsuarioID(rs.getInt("UsuarioID"));
                    medicacion.setMedicamentoID(rs.getInt("MedicamentoID"));
                    medicacion.setDosis(rs.getInt("Dosis"));
                    medicacion.setFrecuencia(rs.getString("Frecuencia"));
                    medicacion.setDuracion(rs.getInt("Duracion"));
                    medicacion.setFechaInicio(rs.getDate("FechaInicio").toLocalDate());
                    medicacion.setFechaFin(rs.getDate("FechaFin").toLocalDate());
                    medicaciones.add(medicacion);
                }
            }
        }
        return medicaciones;
    }

    // Método para obtener una medicación por su ID
    public Medicacion obtenerMedicaciónPorId(int medicacionId) throws SQLException {
        String query = "SELECT * FROM Medicacion WHERE MedicacionID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, medicacionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Medicacion medicacion = new Medicacion();
                    medicacion.setMedicacionID(rs.getInt("MedicacionID"));
                    medicacion.setUsuarioID(rs.getInt("UsuarioID"));
                    medicacion.setMedicamentoID(rs.getInt("MedicamentoID"));
                    medicacion.setDosis(rs.getInt("Dosis"));
                    medicacion.setFrecuencia(rs.getString("Frecuencia"));
                    medicacion.setDuracion(rs.getInt("Duracion"));
                    medicacion.setFechaInicio(rs.getDate("FechaInicio").toLocalDate());
                    medicacion.setFechaFin(rs.getDate("FechaFin").toLocalDate());
                    return medicacion;
                } else {
                    throw new SQLException("No se encontró la medicación con ID: " + medicacionId);
                }
            }
        }
    }

    // Método para actualizar una medicación
    public void actualizarMedicación(Medicacion medicacion) throws SQLException {
        String query = "UPDATE Medicacion SET UsuarioID = ?, MedicamentoID = ?, Dosis = ?, Frecuencia = ?, Duracion = ?, FechaInicio = ?, FechaFin = ? WHERE MedicacionID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, medicacion.getUsuarioID());
            stmt.setInt(2, medicacion.getMedicamentoID());
            stmt.setInt(3, medicacion.getDosis());
            stmt.setString(4, medicacion.getFrecuencia());
            stmt.setInt(5, medicacion.getDuracion());
            stmt.setDate(6, Date.valueOf(medicacion.getFechaInicio()));
            stmt.setDate(7, Date.valueOf(medicacion.getFechaFin()));
            stmt.setInt(8, medicacion.getMedicacionID());
            stmt.executeUpdate();
        }
    }

    // Método para eliminar una medicación
    public void eliminarMedicación(int medicacionId) throws SQLException {
        String query = "DELETE FROM Medicacion WHERE MedicacionID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, medicacionId);
            stmt.executeUpdate();
        }
    }

}

