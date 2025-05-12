package org.example.dao;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class BBDD {
    public static boolean crearBaseDeDatos(BufferedWriter log) {
        String url = "jdbc:mysql://localhost:3306/?serverTimezone=UTC"; // sin especificar la BBDD
        String user = "root";
        String password = "root";

        String scriptSQL = """
            DROP DATABASE IF EXISTS Chapi;
            CREATE DATABASE IF NOT EXISTS Chapi;
            USE Chapi;

            CREATE TABLE Usuario (
                UsuarioID INT PRIMARY KEY AUTO_INCREMENT,
                Nombre VARCHAR(100) NOT NULL,
                Apellidos VARCHAR(100) NOT NULL,
                Email VARBINARY(255) UNIQUE NOT NULL,
                Password VARBINARY(255) NOT NULL,
                Telefono VARBINARY(255) UNIQUE,
                Tipo ENUM('cuidador', 'cuidado') NOT NULL
            );

            CREATE TABLE Cuidador_Usuario (
                UsuarioID INT NOT NULL,
                UsuarioCuidadorID INT NOT NULL,
                PRIMARY KEY (UsuarioID, UsuarioCuidadorID),
                FOREIGN KEY (UsuarioID) REFERENCES Usuario(UsuarioID) ON DELETE CASCADE,
                FOREIGN KEY (UsuarioCuidadorID) REFERENCES Usuario(UsuarioID) ON DELETE CASCADE
            );

            CREATE TABLE Medicamento (
                MedicamentoID INT PRIMARY KEY AUTO_INCREMENT,
                Nombre VARCHAR(100) NOT NULL,
                FechaCaducidad DATE NOT NULL,
                Descripcion TEXT,
                Foto VARCHAR(255)
            );

            CREATE TABLE Medicacion (
                MedicacionID INT PRIMARY KEY AUTO_INCREMENT,
                UsuarioID INT NOT NULL,
                MedicamentoID INT NOT NULL,
                Dosis INT NOT NULL,
                Frecuencia VARCHAR(50) NOT NULL,
                Duracion INT NOT NULL,
                FechaInicio DATE NOT NULL,
                FechaFin DATE NOT NULL,
                FOREIGN KEY (UsuarioID) REFERENCES Usuario(UsuarioID) ON DELETE CASCADE,
                FOREIGN KEY (MedicamentoID) REFERENCES Medicamento(MedicamentoID) ON DELETE CASCADE
            );

            CREATE TABLE CitaMedica (
                CitaMedicaID INT PRIMARY KEY AUTO_INCREMENT,
                UsuarioID INT NOT NULL,
                UsuarioCuidadorID INT NULL,
                FechaCita DATETIME NOT NULL,
                Lugar VARCHAR(255) NOT NULL,
                Especialista VARCHAR(100) NOT NULL,
                FOREIGN KEY (UsuarioID) REFERENCES Usuario(UsuarioID) ON DELETE CASCADE,
                FOREIGN KEY (UsuarioCuidadorID) REFERENCES Usuario(UsuarioID) ON DELETE CASCADE
            );

            CREATE TABLE ActividadFisica (
                ActividadID INT PRIMARY KEY AUTO_INCREMENT,
                UsuarioID INT NOT NULL,
                UsuarioCuidadorID INT NULL,
                Nombre VARCHAR(100) NOT NULL,
                Duracion INT NOT NULL,
                HoraInicio TIME NOT NULL,
                HoraFin TIME NOT NULL,
                FOREIGN KEY (UsuarioID) REFERENCES Usuario(UsuarioID) ON DELETE CASCADE,
                FOREIGN KEY (UsuarioCuidadorID) REFERENCES Usuario(UsuarioID) ON DELETE SET NULL
            );

            CREATE TABLE Recordatorio (
                RecordatorioID INT PRIMARY KEY AUTO_INCREMENT,
                UsuarioID INT NOT NULL,
                UsuarioCuidadorID INT NULL,
                Descripcion VARCHAR(255) NOT NULL,
                TipoEvento ENUM('Medicacion', 'ActividadFisica', 'CitaMedica') NOT NULL,
                NumeroDosis INT NOT NULL,
                Fecha DATE NOT NULL,
                Hora TIME NOT NULL,
                FechaInicio DATETIME NOT NULL,
                FechaFin DATETIME NOT NULL,
                CitaMedicaID INT DEFAULT NULL,
                MedicacionID INT DEFAULT NULL,
                ActividadID INT DEFAULT NULL,
                FOREIGN KEY (UsuarioID) REFERENCES Usuario(UsuarioID) ON DELETE CASCADE,
                FOREIGN KEY (UsuarioCuidadorID) REFERENCES Usuario(UsuarioID) ON DELETE SET NULL,
                FOREIGN KEY (CitaMedicaID) REFERENCES CitaMedica(CitaMedicaID) ON DELETE SET NULL,
                FOREIGN KEY (MedicacionID) REFERENCES Medicacion(MedicacionID) ON DELETE SET NULL,
                FOREIGN KEY (ActividadID) REFERENCES ActividadFisica(ActividadID) ON DELETE SET NULL
            );

            CREATE INDEX idx_cuidador_usuario ON Cuidador_Usuario(UsuarioCuidadorID);
            CREATE INDEX idx_medicacion_usuario ON Medicacion(UsuarioID);
            CREATE INDEX idx_citamedica_usuario ON CitaMedica(UsuarioID);
            CREATE INDEX idx_actividad_usuario ON ActividadFisica(UsuarioID);
            CREATE INDEX idx_recordatorio_usuario ON Recordatorio(UsuarioID);
            CREATE INDEX idx_recordatorio_cuidador ON Recordatorio(UsuarioCuidadorID);
            """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            for (String sql : scriptSQL.split(";")) {
                if (!sql.trim().isEmpty()) stmt.execute(sql);
            }

            log.write("✔ Se ha creado o reemplazado correctamente la base de datos.\n");
            log.flush();
            return true;

        } catch (Exception e) {
            try {
                log.write("❌ Error creando la base de datos: " + e.getMessage() + "\n");
                log.flush();
            } catch (IOException ignored) {}
            return false;
        }
    }
}
