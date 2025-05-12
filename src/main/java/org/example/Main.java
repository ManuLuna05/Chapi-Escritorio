package org.example;

import org.example.dao.BBDD;
import org.example.ui.VentanaAcceso;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    static String c_tipo_info = "I";
    static String c_tipo_error = "E";
    static String c_tipo_aviso = "W";
    static String ruta_log = "C:\\Users\\Usuario\\IdeaProjects\\Chapi\\src\\main\\java\\org\\example\\logs\\";
    static String nombre_fichero_log = ruta_log + "log_registro_" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".txt";

    public static void main(String[] args) {
        BufferedWriter v_log_buf = null;

        try {
            v_log_buf = new BufferedWriter(new FileWriter(nombre_fichero_log));
            escribe_log(v_log_buf, c_tipo_info, "Inicio de la aplicación");

            boolean baseCreada = BBDD.crearBaseDeDatos(v_log_buf);

            if (baseCreada) {
                escribe_log(v_log_buf, c_tipo_info, "Base de datos 'Chapi' preparada correctamente.");
                SwingUtilities.invokeLater(() -> new VentanaAcceso().setVisible(true));
            } else {
                escribe_log(v_log_buf, c_tipo_error, "Error al crear o preparar la base de datos.");
                JOptionPane.showMessageDialog(null, "No se pudo crear o conectar con la base de datos. Revisa los logs.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException e) {
            System.out.printf("Error de IO en el fichero de log: " + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.printf("Error general: " + e);
            System.exit(1);
        }
    }

    public static void escribe_log(BufferedWriter v_log_buf, String v_tipo, String v_traza) {
        Date v_fecha_actual = new Date();
        DateFormat v_fecha_hora_actual = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

        try {
            v_log_buf.write(v_fecha_hora_actual.format(v_fecha_actual) + " - " + v_tipo + ": " + v_traza + "\n");
            v_log_buf.flush();
        } catch (IOException e) {
            System.out.printf("Error de IO en el fichero de log: " + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.printf("Error escribiendo en el fichero log: " + e);
            System.exit(1);
        }
    }
}
