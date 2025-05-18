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


import model.Trip;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class TripDAO {
    private Connection connection;

    public TripDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }
    // Get all trips created by a specific user
    public List<Trip> getTripsByUserId(int userId) {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM trips WHERE created_by = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Trip trip = new Trip();
                trip.setTripId(rs.getString("trip_id"));
                trip.setTripName(rs.getString("trip_name"));
                trip.setStartDate(rs.getDate("start_date"));
                trip.setEndDate(rs.getDate("end_date"));
                trip.setCreatedBy(rs.getInt("created_by"));
                trip.setBudget(rs.getDouble("budget"));
                trip.setCreatedAt(rs.getTimestamp("created_at"));
                trips.add(trip);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching trips: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return trips;
    }
    
    
    // Get a trip by its ID
    public Trip getTripById(String tripId) throws SQLException {
    String sql = "SELECT * FROM trips WHERE trip_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, tripId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Trip trip = new Trip();
            trip.setTripId(rs.getString("trip_id"));
            trip.setTripName(rs.getString("trip_name"));
            trip.setStartDate(rs.getDate("start_date"));
            trip.setEndDate(rs.getDate("end_date"));
            trip.setBudget(rs.getDouble("budget"));
            trip.setCreatedBy(rs.getInt("created_by"));
            return trip;
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching trip: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    return null;
}
    
    public Map<String, String> getTripIdNameMap() {
        Map<String, String> tripMap = new HashMap<>();
        String sql = "SELECT trip_id, trip_name FROM trips";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tripMap.put(rs.getString("trip_id"), rs.getString("trip_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching trip ID and name map: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return tripMap;
    }
    
    // Fetch all trips from the database
    public List<Trip> getAllTrips() {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM trips"; 
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Trip trip = new Trip();
                trip.setTripId(rs.getString("trip_id")); 
                trip.setTripName(rs.getString("trip_name")); 
                trips.add(trip);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching all trips: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return trips;
    }
    
    // Get the budget of a trip by its ID
    public double getTripBudgetById(String tripId) {
    String query = "SELECT budget FROM trips WHERE trip_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setString(1, tripId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble("budget"); // Return the trip budget
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching trip budget: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    return 0.0; // Return 0 if no budget is found
}

    // Add a new trip
    public boolean addTrip(Trip trip) throws SQLException {
    String sql = "INSERT INTO trips (trip_id, trip_name, start_date, end_date, budget, created_by) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, trip.getTripId());
        stmt.setString(2, trip.getTripName());
        stmt.setDate(3, new java.sql.Date(trip.getStartDate().getTime()));
        stmt.setDate(4, new java.sql.Date(trip.getEndDate().getTime()));
        stmt.setDouble(5, trip.getBudget());
        stmt.setInt(6, trip.getCreatedBy()); // Set the created_by field
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error adding trip: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}

// Update an existing trip
public boolean updateTrip(Trip trip) throws SQLException {
    String sql = "UPDATE trips SET trip_name = ?, start_date = ?, end_date = ?, budget = ? WHERE trip_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, trip.getTripName());
        stmt.setDate(2, new java.sql.Date(trip.getStartDate().getTime()));
        stmt.setDate(3, new java.sql.Date(trip.getEndDate().getTime()));
        stmt.setDouble(4, trip.getBudget());
        stmt.setString(5, trip.getTripId());
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error updating trip: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}

// Delete an existing trip by its ID
public boolean deleteTrip(String tripId) throws SQLException {
    String sql = "DELETE FROM trips WHERE trip_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, tripId);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error deleting trip: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}
}
