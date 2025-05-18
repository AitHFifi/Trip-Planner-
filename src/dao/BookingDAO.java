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

import model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class BookingDAO {
    private Connection connection;

    public BookingDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection(); 
    }
    
    // Fetch all bookings for a specific user
    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setBookingDate(rs.getDate("booking_date"));
                booking.setPrice(rs.getDouble("price"));
                booking.setCreatedAt(rs.getTimestamp("created_at"));
                booking.setStatus(rs.getString("status"));
                booking.setTripId(rs.getString("trip_id"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching bookings: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return bookings;
    }

    // Fetch a specific booking by its ID
    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setBookingDate(rs.getDate("booking_date"));
                booking.setPrice(rs.getDouble("price"));
                booking.setCreatedAt(rs.getTimestamp("created_at"));
                booking.setStatus(rs.getString("status"));
                booking.setTripId(rs.getString("trip_id"));
                return booking;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching booking by ID: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    // Add a new booking
    public boolean addBooking(Booking booking) {
    String sql = "INSERT INTO bookings (user_id, booking_date, price, status, trip_id) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, booking.getUserId());
        stmt.setDate(2, new java.sql.Date(booking.getBookingDate().getTime()));
        stmt.setDouble(3, booking.getPrice());
        stmt.setString(4, booking.getStatus());
        stmt.setString(5, booking.getTripId()); 
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error adding booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    return false;
}

    // Update an existing booking
    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE bookings SET booking_date = ?, price = ?, status = ?, trip_id = ? WHERE booking_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(booking.getBookingDate().getTime()));
            stmt.setDouble(2, booking.getPrice());
            stmt.setString(3, booking.getStatus());
            stmt.setString(4, booking.getTripId());
            stmt.setInt(5, booking.getBookingId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error updating booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Delete a booking by its ID
    public boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}
