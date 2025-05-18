/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author Hp
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database connection details
    private static final String URL = "jdbc:postgresql://localhost:5432/trip_planner_db"; // 
    private static final String USER = "postgres"; 
    private static final String PASSWORD = "fifi"; 

    // Method to establish and return a database connection
    public static Connection getConnection() throws SQLException {
        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found. Include it in your library path.");
            throw new SQLException("Driver not found", e);
        }

        // Return the connection
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
