package com.example.employeesystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Simple JDBC connection manager backed by a properties file.
 */
public final class ConnectionManager {
    private static final Logger log = LoggerFactory.getLogger(ConnectionManager.class);
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConnectionManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found in classpath");
            }
            PROPS.load(in);
            Class.forName(PROPS.getProperty("db.driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private ConnectionManager() {
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPS.getProperty("db.url");
        String username = PROPS.getProperty("db.username");
        String password = PROPS.getProperty("db.password");
        log.debug("Opening JDBC connection to {}", url);
        return DriverManager.getConnection(url, username, password);
    }
}
