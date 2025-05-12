package org.example;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    //Declaración de variables a utilizar en el manejo de los logs
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

            //Se lanza la ventana de acceso
            SwingUtilities.invokeLater(() -> new org.example.ui.VentanaAcceso().setVisible(true));

        } catch (IOException e) {
            System.out.printf("Error de IO en el fichero de log: " + e.toString());
            System.exit(1);
        } catch (Exception e) {
            System.out.printf("Error abriendo el fichero de log: " + e.toString());
            System.exit(1);
        }
    }


    /* Función encargada de la creación de los logs */
    public static void escribe_log(BufferedWriter v_log_buf, String v_tipo, String v_traza) {
        Date v_fecha_actual = new Date();
        DateFormat v_fecha_hora_actual = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

        try { //Se escribe en el log la información del tipo, fecha y traza recibida
            v_log_buf.write(v_fecha_hora_actual.format(v_fecha_actual) + " - " + v_tipo + ": " + v_traza + "\n");
            v_log_buf.flush();
        } catch (IOException e) {
            System.out.printf("Error de IO en el fichero de log: " + e.toString());
            System.exit(1);
        } catch (Exception e) {
            System.out.printf("Error escribiendo en el fichero log: " + e.toString());
            System.exit(1);
        }
    }
}