package com.cookim.cookimws.connection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MariaDBConnection {

    private static Connection connection = null; // a static variable to hold the database connection instance
    private static Properties properties = null; // a static variable to hold the database configuration properties
    private static final Logger LOGGER = LoggerFactory.getLogger(MariaDBConnection.class);

    static {
        // a static block that initializes the connection and properties objects
        try (InputStream input = MariaDBConnection.class.getResourceAsStream("/config.properties")) {
            if (input == null) {
                LOGGER.error("Could not find config.properties file."); // print an error message if the properties file is not found
            } else {
                properties = new Properties();
                properties.load(input); // load the properties from the input stream
                String driver = properties.getProperty("db.driver");
                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.user");
                String password = properties.getProperty("db.password");
                Class.forName(driver); // load the JDBC driver class
                connection = DriverManager.getConnection(url, user, password); // establish the database connection
                LOGGER.info("Database connection established successfully.");
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            LOGGER.error("An error occurred while initializing the database connection: {}", e.getMessage());
        }
    }

    /**
     * Get the database connection instance. If the connection is closed or
     * null, try to reconnect using the configuration properties.
     *
     * @return the database connection instance
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                LOGGER.warn("Database connection is null or closed. Trying to reconnect..."); // print a message if the connection is null or closed
                connection = DriverManager.getConnection(properties.getProperty("db.url"), properties.getProperty("db.user"), properties.getProperty("db.password")); // try to reconnect using the configuration properties
                LOGGER.info("Database connection reestablished successfully.");
            }
        } catch (SQLException e) {
            LOGGER.error("An error occurred while trying to reconnect to the database: {}", e.getMessage());
        }

        return connection; // return the database connection instance
    }

    /**
     * Close the database connection.
     */
    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close(); // close the connection if it is not null
                LOGGER.info("Database connection closed successfully.");
            }
        } catch (SQLException e) {
            LOGGER.error("An error occurred while trying to close the database connection: {}", e.getMessage());
        }
    }
}
