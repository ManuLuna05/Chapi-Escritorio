package org.example.dao;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class BBDD {
    public static boolean crearBaseDeDatos(BufferedWriter log) {
        String url = "jdbc:mysql://localhost:3306/?serverTimezone=UTC";
        String user = "root";
        String password = "root";

        String crearBBDD = "CREATE DATABASE IF NOT EXISTS Chapi";

        String scriptSQL = """
        CREATE TABLE IF NOT EXISTS Usuario (
            UsuarioID INT PRIMARY KEY AUTO_INCREMENT,
            Nombre VARCHAR(100) NOT NULL,
            Apellidos VARCHAR(100) NOT NULL,
            Email VARBINARY(255) UNIQUE NOT NULL,
            Password VARBINARY(255) NOT NULL,
            Telefono VARBINARY(255) UNIQUE,
            Tipo ENUM('cuidador', 'cuidado') NOT NULL
        );

        CREATE TABLE IF NOT EXISTS Cuidador_Usuario (
            UsuarioID INT NOT NULL,
            UsuarioCuidadorID INT NOT NULL,
            PRIMARY KEY (UsuarioID, UsuarioCuidadorID),
            FOREIGN KEY (UsuarioID) REFERENCES Usuario(UsuarioID) ON DELETE CASCADE,
            FOREIGN KEY (UsuarioCuidadorID) REFERENCES Usuario(UsuarioID) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS Medicamento (
            MedicamentoID INT PRIMARY KEY AUTO_INCREMENT,
            Nombre VARCHAR(100) NOT NULL,
            FechaCaducidad DATE NOT NULL,
            Descripcion TEXT,
            Foto VARCHAR(255)
        );

        CREATE TABLE IF NOT EXISTS Medicacion (
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

        CREATE TABLE IF NOT EXISTS CitaMedica (
            CitaMedicaID INT PRIMARY KEY AUTO_INCREMENT,
            UsuarioID INT NOT NULL,
            UsuarioCuidadorID INT NULL,
            FechaCita DATETIME NOT NULL,
            Lugar VARCHAR(255) NOT NULL,
            Especialista VARCHAR(100) NOT NULL,
            FOREIGN KEY (UsuarioID) REFERENCES Usuario(UsuarioID) ON DELETE CASCADE,
            FOREIGN KEY (UsuarioCuidadorID) REFERENCES Usuario(UsuarioID) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS ActividadFisica (
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

        CREATE TABLE IF NOT EXISTS Recordatorio (
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

        INSERT INTO Medicamento (Nombre, FechaCaducidad, Descripcion, Foto)
        SELECT * FROM (
            SELECT 'Ibuprofeno', '2026-07-08', 'Uso médico común de ibuprofeno', 'ibuprofeno.jpg' UNION ALL
            SELECT 'Paracetamol', '2026-04-17', 'Uso médico común de paracetamol', 'paracetamol.jpg' UNION ALL
            SELECT 'Amoxicilina', '2026-05-28', 'Uso médico común de amoxicilina', 'amoxicilina.jpg' UNION ALL
            SELECT 'Omeprazol', '2026-05-24', 'Uso médico común de omeprazol', 'omeprazol.jpg' UNION ALL
            SELECT 'Metformina', '2026-10-16', 'Uso médico común de metformina', 'metformina.jpg' UNION ALL
            SELECT 'Loratadina', '2025-10-20', 'Uso médico común de loratadina', 'loratadina.jpg' UNION ALL
            SELECT 'Simvastatina', '2026-03-29', 'Uso médico común de simvastatina', 'simvastatina.jpg' UNION ALL
            SELECT 'Atorvastatina', '2027-01-17', 'Uso médico común de atorvastatina', 'atorvastatina.jpg' UNION ALL
            SELECT 'Enalapril', '2026-12-17', 'Uso médico común de enalapril', 'enalapril.jpg' UNION ALL
            SELECT 'Amlodipino', '2026-12-08', 'Uso médico común de amlodipino', 'amlodipino.jpg' UNION ALL
            SELECT 'Levotiroxina', '2025-11-01', 'Uso médico común de levotiroxina', 'levotiroxina.jpg' UNION ALL
            SELECT 'Losartán', '2026-12-22', 'Uso médico común de losartán', 'losartán.jpg' UNION ALL
            SELECT 'Diclofenaco', '2026-12-23', 'Uso médico común de diclofenaco', 'diclofenaco.jpg' UNION ALL
            SELECT 'Tramadol', '2025-11-04', 'Uso médico común de tramadol', 'tramadol.jpg' UNION ALL
            SELECT 'Cetirizina', '2026-11-30', 'Uso médico común de cetirizina', 'cetirizina.jpg' UNION ALL
            SELECT 'Clonazepam', '2026-05-28', 'Uso médico común de clonazepam', 'clonazepam.jpg' UNION ALL
            SELECT 'Fluoxetina', '2027-03-25', 'Uso médico común de fluoxetina', 'fluoxetina.jpg' UNION ALL
            SELECT 'Sertralina', '2026-01-15', 'Uso médico común de sertralina', 'sertralina.jpg' UNION ALL
            SELECT 'Prednisona', '2026-05-31', 'Uso médico común de prednisona', 'prednisona.jpg' UNION ALL
            SELECT 'Azitromicina', '2026-01-22', 'Uso médico común de azitromicina', 'azitromicina.jpg' UNION ALL
            SELECT 'Insulina', '2027-03-07', 'Uso médico común de insulina', 'insulina.jpg' UNION ALL
            SELECT 'Furosemida', '2026-05-06', 'Uso médico común de furosemida', 'furosemida.jpg' UNION ALL
            SELECT 'Ranitidina', '2026-04-04', 'Uso médico común de ranitidina', 'ranitidina.jpg' UNION ALL
            SELECT 'Meloxicam', '2026-08-11', 'Uso médico común de meloxicam', 'meloxicam.jpg' UNION ALL
            SELECT 'Gabapentina', '2026-02-16', 'Uso médico común de gabapentina', 'gabapentina.jpg' UNION ALL
            SELECT 'Alprazolam', '2026-03-16', 'Uso médico común de alprazolam', 'alprazolam.jpg' UNION ALL
            SELECT 'Dexametasona', '2025-11-11', 'Uso médico común de dexametasona', 'dexametasona.jpg' UNION ALL
            SELECT 'Naproxeno', '2025-07-02', 'Uso médico común de naproxeno', 'naproxeno.jpg' UNION ALL
            SELECT 'Salbutamol', '2026-02-26', 'Uso médico común de salbutamol', 'salbutamol.jpg' UNION ALL
            SELECT 'Bromazepam', '2026-12-01', 'Uso médico común de bromazepam', 'bromazepam.jpg' UNION ALL
            SELECT 'Ciprofloxacino', '2027-03-08', 'Uso médico común de ciprofloxacino', 'ciprofloxacino.jpg' UNION ALL
            SELECT 'Ibuprofeno Plus', '2026-10-18', 'Uso médico común de ibuprofeno plus', 'ibuprofeno_plus.jpg' UNION ALL
            SELECT 'Paracetamol Forte', '2026-09-21', 'Uso médico común de paracetamol forte', 'paracetamol_forte.jpg' UNION ALL
            SELECT 'Metamizol', '2025-11-01', 'Uso médico común de metamizol', 'metamizol.jpg' UNION ALL
            SELECT 'Lansoprazol', '2025-10-05', 'Uso médico común de lansoprazol', 'lansoprazol.jpg' UNION ALL
            SELECT 'Carvedilol', '2025-10-24', 'Uso médico común de carvedilol', 'carvedilol.jpg' UNION ALL
            SELECT 'Tamsulosina', '2025-08-08', 'Uso médico común de tamsulosina', 'tamsulosina.jpg' UNION ALL
            SELECT 'Budesonida', '2026-11-04', 'Uso médico común de budesonida', 'budesonida.jpg' UNION ALL
            SELECT 'Montelukast', '2025-10-31', 'Uso médico común de montelukast', 'montelukast.jpg' UNION ALL
            SELECT 'Clopidogrel', '2026-05-29', 'Uso médico común de clopidogrel', 'clopidogrel.jpg' UNION ALL
            SELECT 'Risperidona', '2026-07-14', 'Uso médico común de risperidona', 'risperidona.jpg' UNION ALL
            SELECT 'Quetiapina', '2026-04-28', 'Uso médico común de quetiapina', 'quetiapina.jpg' UNION ALL
            SELECT 'Olanzapina', '2025-08-20', 'Uso médico común de olanzapina', 'olanzapina.jpg' UNION ALL
            SELECT 'Valproato', '2025-07-12', 'Uso médico común de valproato', 'valproato.jpg' UNION ALL
            SELECT 'Lamotrigina', '2027-02-13', 'Uso médico común de lamotrigina', 'lamotrigina.jpg' UNION ALL
            SELECT 'Topiramato', '2025-10-12', 'Uso médico común de topiramato', 'topiramato.jpg' UNION ALL
            SELECT 'Amoxicilina Duo', '2026-11-23', 'Uso médico común de amoxicilina duo', 'amoxicilina_duo.jpg' UNION ALL
            SELECT 'Cefalexina', '2025-10-19', 'Uso médico común de cefalexina', 'cefalexina.jpg' UNION ALL
            SELECT 'Nitrofurantoína', '2027-03-06', 'Uso médico común de nitrofurantoína', 'nitrofurantoína.jpg'
        ) AS nuevos
        WHERE NOT EXISTS (SELECT 1 FROM Medicamento);
        """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            stmt.execute(crearBBDD);
            stmt.execute("USE Chapi");

            for (String sql : scriptSQL.split(";")) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed + ";");
                }
            }

            log.write("Base de datos 'Chapi' preparada correctamente (tablas e inserciones).\n");
            log.flush();
            return true;

        } catch (Exception e) {
            try {
                log.write("Error creando o actualizando la base de datos: " + e.getMessage() + "\n");
                log.flush();
            } catch (IOException ignored) {}
            return false;
        }
    }

}
