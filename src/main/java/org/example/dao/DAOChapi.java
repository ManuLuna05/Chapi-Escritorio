package org.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAOChapi extends ArrayList<Object> {
    //Declaración de las variables de conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/chapi";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    //Metodo para obtener la conexión a la base de datos
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
