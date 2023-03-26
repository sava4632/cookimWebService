package com.cookim.cookimws.connection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cookimadmin
 */
public class MariaDBConnection {

    private static final String DRIVER = "org.mariadb.jdbc.Driver";
    private static final String URL = "jdbc:mariadb://91.107.198.64:3306/cookImDB";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminadmin";

    //Connection conn;
    /**
     * Este método establece una conexión con la base de datos MariaDB
     * utilizando el controlador JDBC proporcionado por la biblioteca MariaDB
     * JDBC. 
     *
     * @return objeto Connection si la conexión es exitosa, null si hay algún error.
     */
    public static Connection getConnection() {    
        Connection conn = null;
        try{
            Class.forName(DRIVER);
            conn = (Connection) DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Successful connection to the database");
        } catch (ClassNotFoundException e) {
            System.out.println("Error loading JDBC driver: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
        return conn;
    }
    
    public void closeConnection(){
        try {
            //conn.close();
            getConnection().close();
            System.out.println("the connection finished");
        } catch (SQLException ex) {
            Logger.getLogger(MariaDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Can't close the connection: " + ex.toString());
        }
    }
}
