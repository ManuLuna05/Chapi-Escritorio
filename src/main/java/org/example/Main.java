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
    static String ruta_log = "C:\\Users\\Usuario\\IdeaProjects\\Chapi\\src\\main\\java\\org\\example\\logs\\";
    static String nombre_fichero_log = ruta_log + "log_registro_" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".txt";

    //Función principal de la aplicación
    public static void main(String[] args) {
        BufferedWriter v_log_buf = null;
        try { //Se inicializa el fichero de log que dejará constancia de lo que va ocurriendo en la aplicación durante su ejecución
            v_log_buf = new BufferedWriter(new FileWriter(nombre_fichero_log));
            escribe_log(v_log_buf, c_tipo_info, "Inicio de la aplicación");

            boolean bbddCreada = BBDD.crearBaseDeDatos(v_log_buf);

            //Si la base de datos se ha creado correctamente, se muestra un mensaje de éxito y se abre la ventana de acceso
            if (bbddCreada) {
                escribe_log(v_log_buf, c_tipo_info, "Base de datos 'Chapi' lista para su uso.");
                SwingUtilities.invokeLater(() -> new VentanaAcceso().setVisible(true));
            } else { //Si no se ha podido crear o preparar la base de datos, se muestra un mensaje de error y se escribe en el log
                escribe_log(v_log_buf, c_tipo_error, "Error al crear o preparar la base de datos.");
                System.err.println("Error al crear o preparar la base de datos. Consulta el log.");
            }

        } catch (IOException e) {
            System.out.println("Error de IO en el fichero de log: " + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error general: " + e);
            System.exit(1);
        }
    }

    //Función para escribir en el fichero de log
    public static void escribe_log(BufferedWriter v_log_buf, String v_tipo, String v_traza) {
        Date v_fecha_actual = new Date();
        DateFormat v_fecha_hora_actual = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

        try { //Se escribe la traza en el fichero de log con la fecha y hora actual
            v_log_buf.write(v_fecha_hora_actual.format(v_fecha_actual) + " - " + v_tipo + ": " + v_traza + "\n");
            v_log_buf.flush();
        } catch (IOException e) {
            System.out.println("Error de IO en el fichero de log: " + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error escribiendo en el fichero log: " + e);
            System.exit(1);
        }
    }
}
