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

import model.Destination;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class DestinationDAO {
    private Connection connection;

    public DestinationDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    // Fetch all destinations for a specific user by user ID
    public List<Destination> getDestinationsByUserId(int userId) {
        List<Destination> destinations = new ArrayList<>();
        String sql = "SELECT * FROM destinations WHERE user_id = ?"; 
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Destination destination = new Destination();
                destination.setDestinationId(rs.getInt("destination_id"));
                destination.setTripId(rs.getString("trip_id"));
                destination.setCountry(rs.getString("country"));
                destination.setCity(rs.getString("city"));
                destination.setDescription(rs.getString("description"));
                destination.setTransportMode(rs.getString("transport_mode"));
                destination.setEstimatedCost(rs.getDouble("estimated_cost"));
                destination.setCreatedAt(rs.getString("created_at"));
                destination.setUserId(rs.getInt("user_id")); 
                destinations.add(destination);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching destinations: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return destinations;
    }
    
    // Fetch destination details by trip ID
    public Destination getDestinationByTripId(String tripId) {
        String sql = "SELECT * FROM destinations WHERE trip_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tripId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Destination destination = new Destination();
                destination.setDestinationId(rs.getInt("destination_id"));
                destination.setTripId(rs.getString("trip_id"));
                destination.setCountry(rs.getString("country"));
                destination.setCity(rs.getString("city"));
                destination.setDescription(rs.getString("description"));
                destination.setTransportMode(rs.getString("transport_mode"));
                destination.setEstimatedCost(rs.getDouble("estimated_cost"));
                return destination;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching destination by trip ID: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    // Fetch destination details by destination ID
    public Destination getDestinationById(int destinationId) {
        String sql = "SELECT * FROM destinations WHERE destination_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, destinationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Destination destination = new Destination();
                destination.setDestinationId(rs.getInt("destination_id"));
                destination.setTripId(rs.getString("trip_id"));
                destination.setCountry(rs.getString("country"));
                destination.setCity(rs.getString("city"));
                destination.setDescription(rs.getString("description"));
                destination.setTransportMode(rs.getString("transport_mode"));
                destination.setEstimatedCost(rs.getDouble("estimated_cost"));
                destination.setCreatedAt(rs.getString("created_at"));
                destination.setUserId(rs.getInt("user_id"));
                return destination;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching destination by ID: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    // Add a new destination
    public boolean addDestination(Destination destination) {
        String sql = "INSERT INTO destinations (trip_id, country, city, description, transport_mode, estimated_cost, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, destination.getTripId());
            stmt.setString(2, destination.getCountry());
            stmt.setString(3, destination.getCity());
            stmt.setString(4, destination.getDescription());
            stmt.setString(5, destination.getTransportMode());
            stmt.setDouble(6, destination.getEstimatedCost());
            stmt.setInt(7, destination.getUserId()); 

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding destination: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Update an existing destination
    public boolean updateDestination(Destination destination) {
        String sql = "UPDATE destinations SET trip_id = ?, country = ?, city = ?, description = ?, transport_mode = ?, estimated_cost = ? WHERE destination_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, destination.getTripId());
            stmt.setString(2, destination.getCountry());
            stmt.setString(3, destination.getCity());
            stmt.setString(4, destination.getDescription());
            stmt.setString(5, destination.getTransportMode());
            stmt.setDouble(6, destination.getEstimatedCost());
            stmt.setInt(7, destination.getDestinationId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating destination: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }


    // Delete a destination by its ID
    public boolean deleteDestination(int destinationId) {
        String sql = "DELETE FROM destinations WHERE destination_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, destinationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting destination: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}