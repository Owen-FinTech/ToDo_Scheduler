package com.scheduler.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    // SQLite connection string - adjust the file path as needed
    private static final String URL = "jdbc:sqlite:scheduler.db";

    /**
     * Establishes and returns a connection to the SQLite database.
     *
     * @return a Connection object to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

}

