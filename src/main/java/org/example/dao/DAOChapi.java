package org.example.dao;

import org.example.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DAOChapi {
    //Declaración de las variables de conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/chapi";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    //Función para obtener la conexión a la base de datos
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    //Función para registrar un usuario
    public void registroUsuario(Usuario usuario) throws SQLException {
        String query = "INSERT INTO usuario (nombre, apellidos, email, password, telefono, tipo) VALUES (?, ?, ?, ?, ?, ?)";
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

    //Inicio de sesión del usuario
    public Usuario inicioSesionUsuario(String email, String password) {
        // Conexión con el mismo usuario y contraseña para acceder a la base de datos
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM usuario WHERE email = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, password);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        //Obtener los datos del usuario
                        int id = rs.getInt("usuarioID");
                        String nombre = rs.getString("nombre");
                        String apellidos = rs.getString("apellidos");
                        String tipo = rs.getString("tipo");
                        String telefono = rs.getString("telefono");

                        //Crear el objeto Usuario y devolverlo
                        return new Usuario(id, nombre, apellidos, email, password, telefono, tipo);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //Función para registrar un usuario cuidador
    public void registrarUsuarioCuidador(UsuarioCuidador usuarioCuidador) throws SQLException {
        String sql = "INSERT INTO cuidador_usuario (usuarioID, usuarioCuidadorID) VALUES (?, ?)";

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

    //Función para editar un usuario
    public void editarUsuario(Usuario usuario) throws SQLException {
        String query = "UPDATE usuario SET nombre = ?, apellidos = ?, email = ?, password = ?, telefono = ?, tipo = ? WHERE usuarioID = ?";
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

    //Función para obtener el ID de un usuario por su correo electrónico
    public int obtenerUsuarioIdPorCorreo(String email) throws SQLException {
        try (Connection connection = getConnection()) {
            String sql = "SELECT usuarioID FROM usuario WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setBytes(1, email.getBytes());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("usuarioID");
                } else {
                    return -1;
                }
            }
        }
    }

    //Función para obtener el ID de un usuario por su ID, de tal forma que se obtenga al usuario cuidado asignado a un cuidador
    public List<Integer> obtenerPacientesDeCuidador(int cuidadorId) throws SQLException {
        List<Integer> pacientes = new ArrayList<>();
        String query = "SELECT usuarioID FROM cuidador_usuario WHERE usuarioCuidadorID = ?";
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

    //Función para obtener un usuario por su ID
    public Usuario obtenerUsuarioPorId(int usuarioId) throws SQLException {
        String query = "SELECT * FROM usuario WHERE usuarioID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario(
                            rs.getInt("usuarioID"),
                            rs.getString("nombre"),
                            rs.getString("apellidos"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("telefono"),
                            rs.getString("tipo")
                    );
                    return usuario;
                }
            }
        }
        return null;
    }

    //Función para obtener los medicamentos de la base de datos
    public List<Medicamento> obtenerTodosMedicamentos() throws SQLException {
        List<Medicamento> medicamentos = new ArrayList<>();
        String query = "SELECT * FROM medicamento";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Medicamento medicamento = new Medicamento();
                medicamento.setId(rs.getInt("medicamentoID"));
                medicamento.setNombre(rs.getString("nombre"));
                medicamento.setFechaCaducidad(rs.getDate("fechaCaducidad").toString());
                medicamento.setDescripcion(rs.getString("descripcion"));
                medicamento.setFoto(rs.getString("foto"));
                medicamentos.add(medicamento);
            }
        }
        return medicamentos;
    }

    // Función para obtener el nombre de un medicamento por su ID
    public String obtenerNombreMedicamentoPorId(int medicamentoId) throws SQLException {
        String query = "SELECT nombre FROM medicamento WHERE medicamentoID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, medicamentoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                } else {
                    throw new SQLException("No se encontró el medicamento con ID: " + medicamentoId);
                }
            }
        }
    }

    // Función para crear un nuevo recordatorio
    public void crearRecordatorio(Recordatorios recordatorio) throws SQLException {
        String query = "INSERT INTO recordatorio (usuarioID, usuarioCuidadorID, descripcion, tipoEvento, numeroDosis, fecha, hora, fechaInicio, fechaFin, citaMedicaID, medicacionID, actividadID) " +
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

    //Función para obtener todos los recordatorios de un usuario
    public List<Recordatorios> obtenerRecordatoriosPorUsuario(int usuarioID) throws SQLException {
        List<Recordatorios> lista = new ArrayList<>();
        String query = "SELECT * FROM recordatorio WHERE usuarioID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioID);
            try (ResultSet rs = stmt.executeQuery()) {
                //Mientras haya resultados, se van añadiendo a la lista
                while (rs.next()) {
                    Recordatorios recordatorio = new Recordatorios();
                    recordatorio.setRecordatorioID(rs.getInt("recordatorioID"));
                    recordatorio.setUsuarioID(rs.getInt("usuarioID"));

                    //Control correcto de posibles usuarios nulos
                    int cuidadorId = rs.getInt("usuarioCuidadorID");
                    if (rs.wasNull()) { //Si el resultado es nulo, se asigna null
                        recordatorio.setUsuarioCuidadorID(null);
                    } else {
                        recordatorio.setUsuarioCuidadorID(cuidadorId);
                    }

                    //Asignación de los valores a la clase Recordatorios
                    recordatorio.setDescripcion(rs.getString("descripcion"));
                    recordatorio.setTipoEvento(rs.getString("tipoEvento"));
                    recordatorio.setNumeroDosis(rs.getInt("numeroDosis"));
                    recordatorio.setFecha(rs.getDate("fecha").toLocalDate());
                    recordatorio.setHora(rs.getTime("hora").toLocalTime());
                    recordatorio.setFechaInicio(rs.getTimestamp("fechaInicio").toLocalDateTime());
                    recordatorio.setFechaFin(rs.getTimestamp("fechaFin").toLocalDateTime());

                    // Control correcto de posibles nulos
                    int citaId = rs.getInt("citaMedicaID");
                    if (rs.wasNull()) { // Si el resultado es nulo, se asigna null
                        recordatorio.setCitaMedicaID(null);
                    } else {
                        recordatorio.setCitaMedicaID(citaId);
                    }

                    //Lo mismo que los anteriores pero para la medicación y la actividad
                    int medicacionId = rs.getInt("medicacionID");
                    if (rs.wasNull()) {
                        recordatorio.setMedicacionID(null);
                    } else {
                        recordatorio.setMedicacionID(medicacionId);
                    }

                    int actividadId = rs.getInt("actividadID");
                    if (rs.wasNull()) {
                        recordatorio.setActividadID(null);
                    } else {
                        recordatorio.setActividadID(actividadId);
                    }

                    lista.add(recordatorio);
                }
            }
        }
        return lista;
    }



    //Función para obtener recordatorios donde el usuario es el cuidador
    public List<Recordatorios> obtenerRecordatoriosPorCuidador(int usuarioCuidadorID) throws SQLException {
        List<Recordatorios> lista = new ArrayList<>();
        String query = "SELECT * FROM recordatorio WHERE usuarioCuidadorID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioCuidadorID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recordatorios recordatorio = new Recordatorios();
                    recordatorio.setRecordatorioID(rs.getInt("recordatorioID"));
                    recordatorio.setUsuarioID(rs.getInt("usuarioID"));

                    int cuidadorId = rs.getInt("usuarioCuidadorID");
                    if (rs.wasNull()) {
                        recordatorio.setUsuarioCuidadorID(null);
                    } else {
                        recordatorio.setUsuarioCuidadorID(cuidadorId);
                    }

                    recordatorio.setDescripcion(rs.getString("descripcion"));
                    recordatorio.setTipoEvento(rs.getString("tipoEvento"));
                    recordatorio.setNumeroDosis(rs.getInt("numeroDosis"));
                    recordatorio.setFecha(rs.getDate("fecha").toLocalDate());
                    recordatorio.setHora(rs.getTime("hora").toLocalTime());
                    recordatorio.setFechaInicio(rs.getTimestamp("fechaInicio").toLocalDateTime());
                    recordatorio.setFechaFin(rs.getTimestamp("fechaFin").toLocalDateTime());

                    // Control correcto de posibles nulls, similar a los de la función anterior
                    int citaId = rs.getInt("citaMedicaID");
                    if (rs.wasNull()) {
                        recordatorio.setCitaMedicaID(null);
                    } else {
                        recordatorio.setCitaMedicaID(citaId);
                    }

                    int medicacionId = rs.getInt("medicacionID");
                    if (rs.wasNull()) {
                        recordatorio.setMedicacionID(null);
                    } else {
                        recordatorio.setMedicacionID(medicacionId);
                    }

                    int actividadId = rs.getInt("actividadID");
                    if (rs.wasNull()) {
                        recordatorio.setActividadID(null);
                    } else {
                        recordatorio.setActividadID(actividadId);
                    }

                    lista.add(recordatorio);
                }
            }
        }
        return lista;
    }


    //Función para actualizar los recordatorios
    public void actualizarRecordatorio(Recordatorios recordatorio) throws SQLException {
        String query = "UPDATE recordatorio SET usuarioID = ?, usuarioCuidadorID = ?, descripcion = ?, tipoEvento = ?, numeroDosis = ?, fecha = ?, hora = ?, fechaInicio = ?, fechaFin = ?, citaMedicaID = ?, medicacionID = ?, actividadID = ? WHERE recordatorioID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, recordatorio.getUsuarioID());

            if (recordatorio.getUsuarioCuidadorID() != null) {
                stmt.setInt(2, recordatorio.getUsuarioCuidadorID());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            stmt.setString(3, recordatorio.getDescripcion());
            stmt.setString(4, recordatorio.getTipoEvento());
            stmt.setInt(5, recordatorio.getNumeroDosis());
            stmt.setDate(6, Date.valueOf(recordatorio.getFecha()));
            stmt.setTime(7, Time.valueOf(recordatorio.getHora()));
            stmt.setTimestamp(8, Timestamp.valueOf(recordatorio.getFechaInicio()));
            stmt.setTimestamp(9, Timestamp.valueOf(recordatorio.getFechaFin()));

            // Control correcto de posibles nulls, similar a los de las funciones anteriores
            if (recordatorio.getCitaMedicaID() != null) {
                stmt.setInt(10, recordatorio.getCitaMedicaID());
            } else {
                stmt.setNull(10, java.sql.Types.INTEGER);
            }

            if (recordatorio.getMedicacionID() != null) {
                stmt.setInt(11, recordatorio.getMedicacionID());
            } else {
                stmt.setNull(11, java.sql.Types.INTEGER);
            }

            if (recordatorio.getActividadID() != null) {
                stmt.setInt(12, recordatorio.getActividadID());
            } else {
                stmt.setNull(12, java.sql.Types.INTEGER);
            }

            stmt.setInt(13, recordatorio.getRecordatorioID());
            stmt.executeUpdate();
        }
    }


    //Función para eliminar un recordatorio
    public void eliminarRecordatorio(int recordatorioID) throws SQLException {
        String query = "DELETE FROM recordatorio WHERE recordatorioID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recordatorioID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró el recordatorio con ID: " + recordatorioID);
            }
        }
    }

    //Función para registrar una medicación
    public int registrarMedicación(Medicacion medicacion) throws SQLException {
        String query = "INSERT INTO medicacion (usuarioID, medicamentoID, dosis, frecuencia, duracion, fechaInicio, fechaFin) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, medicacion.getUsuarioID());
            stmt.setInt(2, medicacion.getMedicamentoID());
            stmt.setInt(3, medicacion.getDosis());
            stmt.setString(4, medicacion.getFrecuencia());
            stmt.setInt(5, medicacion.getDuracion());
            stmt.setDate(6, Date.valueOf(medicacion.getFechaInicio()));
            stmt.setDate(7, Date.valueOf(medicacion.getFechaFin()));
            stmt.executeUpdate();

            //Obtener el ID generado para la medicación
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la medicación.");
                }
            }
        }
    }

    //Función para obtener todas las medicaciones de un usuario
    public List<Medicacion> obtenerMedicacionesPorUsuario(int usuarioId) throws SQLException {
        List<Medicacion> medicaciones = new ArrayList<>();
        String query = "SELECT * FROM medicacion WHERE usuarioID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Medicacion medicacion = new Medicacion();
                    medicacion.setMedicacionID(rs.getInt("medicacionID"));
                    medicacion.setUsuarioID(rs.getInt("usuarioID"));
                    medicacion.setMedicamentoID(rs.getInt("medicamentoID"));
                    medicacion.setDosis(rs.getInt("dosis"));
                    medicacion.setFrecuencia(rs.getString("frecuencia"));
                    medicacion.setDuracion(rs.getInt("duracion"));
                    medicacion.setFechaInicio(rs.getDate("fechaInicio").toLocalDate());
                    medicacion.setFechaFin(rs.getDate("fechaFin").toLocalDate());
                    medicaciones.add(medicacion);
                }
            }
        }
        return medicaciones;
    }

    //Función para eliminar medicaciones que hayan pasado de tiempo
    public void eliminarMedicacionesPasadas(int usuarioID) throws SQLException {
        String query = "DELETE FROM medicacion WHERE (usuarioID = ?) AND fechaFin < CURDATE()";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioID);
            stmt.executeUpdate();
        }
    }


    //Función para actualizar una medicación
    public void actualizarMedicación(Medicacion medicacion) throws SQLException {
        String query = "UPDATE medicacion SET usuarioID = ?, medicamentoID = ?, dosis = ?, frecuencia = ?, duracion = ?, fechaInicio = ?, fechaFin = ? WHERE medicacionID = ?";
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

    //Función para eliminar una medicación
    public void eliminarMedicación(int medicacionId) throws SQLException {
        String query = "DELETE FROM medicacion WHERE medicacionID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, medicacionId);
            stmt.executeUpdate();
        }
    }

    //Función para registrar una actividad física
    public int registrarActividadFisica(ActividadFisica actividad) throws SQLException {
        String query = "INSERT INTO actividadFisica (usuarioID, usuarioCuidadorID, nombre, duracion, horaInicio, horaFin) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, actividad.getUsuarioId());

            if (actividad.getUsuarioCuidadorId() != null) {
                stmt.setInt(2, actividad.getUsuarioCuidadorId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            stmt.setString(3, actividad.getNombre());
            stmt.setInt(4, actividad.getDuracion());
            stmt.setTime(5, Time.valueOf(actividad.getHoraInicio()));
            stmt.setTime(6, Time.valueOf(actividad.getHoraFin()));
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la actividad física.");
                }
            }
        }
    }

    //Función para obtener todas las actividades físicas de un usuario
    public List<ActividadFisica> obtenerActividadesPorUsuario(int usuarioId) throws SQLException {
        List<ActividadFisica> actividades = new ArrayList<>();
        String query = "SELECT DISTINCT * FROM actividadFisica WHERE usuarioID = ? OR usuarioCuidadorID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ActividadFisica actividad = new ActividadFisica();
                    actividad.setId(rs.getInt("actividadID"));
                    actividad.setUsuarioId(rs.getInt("usuarioID"));
                    actividad.setUsuarioCuidadorId(rs.getInt("usuarioCuidadorID"));
                    actividad.setNombre(rs.getString("nombre"));
                    actividad.setDuracion(rs.getInt("duracion"));
                    actividad.setHoraInicio(rs.getTime("horaInicio").toLocalTime());
                    actividad.setHoraFin(rs.getTime("horaFin").toLocalTime());
                    actividades.add(actividad);
                }
            }
        }
        return actividades;
    }

    //Función para actualizar una actividad física
    public void actualizarActividadFisica(ActividadFisica actividad) throws SQLException {
        String query = "UPDATE actividadFisica SET usuarioID = ?, usuarioCuidadorID = ?, nombre = ?, duracion = ?, horaInicio = ?, horaFin = ? WHERE actividadID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, actividad.getUsuarioId());

            if (actividad.getUsuarioCuidadorId() != null) {
                stmt.setInt(2, actividad.getUsuarioCuidadorId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            stmt.setString(3, actividad.getNombre());
            stmt.setInt(4, actividad.getDuracion());
            stmt.setTime(5, Time.valueOf(actividad.getHoraInicio()));
            stmt.setTime(6, Time.valueOf(actividad.getHoraFin()));
            stmt.setInt(7, actividad.getId());

            stmt.executeUpdate();
        }
    }

    //Función para eliminar una actividad física
    public void eliminarActividadFisica(int actividadId) throws SQLException {
        String query = "DELETE FROM actividadFisica WHERE actividadID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, actividadId);
            stmt.executeUpdate();
        }
    }

    //Función para eliminar actividades pasadas de tiempo
    public void eliminarActividadesPasadas(int usuarioID) throws SQLException {
        String query = "DELETE FROM actividadFisica WHERE (usuarioID = ? OR usuarioCuidadorID = ?) AND horaFin < CURTIME()";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioID);
            stmt.setInt(2, usuarioID);
            stmt.executeUpdate();
        }
    }


    //Función para registrar una cita médica
    public int registrarCitaMedica(int usuarioID, Integer usuarioCuidadorID, LocalDate fecha, LocalTime hora, String lugar, String especialista) throws SQLException {
        String query = "INSERT INTO citaMedica (usuarioID, usuarioCuidadorID, fechaCita, lugar, especialista) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, usuarioID);

            if (usuarioCuidadorID != null) {
                stmt.setInt(2, usuarioCuidadorID);
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.of(fecha, hora)));
            stmt.setString(4, lugar);
            stmt.setString(5, especialista);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                else throw new SQLException("No se pudo obtener el ID de la cita.");
            }
        }
    }

    //Función para obtener todas las citas médicas de un usuario
    public void eliminarCitaMedica(int citaID) throws SQLException {
        String query = "DELETE FROM citaMedica WHERE citaMedicaID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, citaID);
            stmt.executeUpdate();
        }
    }

    public void eliminarCitasPasadas(int usuarioID) throws SQLException {
        String query = "DELETE FROM citaMedica WHERE (usuarioID = ? OR usuarioCuidadorID = ?) AND fechaCita < NOW()";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioID);
            stmt.setInt(2, usuarioID);
            stmt.executeUpdate();
        }
    }
}
