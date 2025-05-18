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
import java.sql.*;
import model.User;
import javax.swing.JOptionPane;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        try {
            String url = "jdbc:postgresql://localhost:5432/trip_planner_db";
            String username = "postgres";
            String password = "fifi";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Create a new user
    public boolean addUser(User user) {
        String sql = "INSERT INTO Users (first_name, last_name, username, password, birthdate, phone_number, gender, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getPassword());
            stmt.setDate(5, Date.valueOf(user.getBirthdate()));
            stmt.setString(6, user.getPhoneNumber());
            stmt.setString(7, user.getGender());
            stmt.setString(8, user.getEmail());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Retrieve a user by ID
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setBirthdate(rs.getString("birthdate"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setGender(rs.getString("gender"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getString("created_at"));
                return user;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
        // Method to fetch user by username for the forgot password
    public User getUserByUsernameForgot(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null; 
    }

   // Helper method to map ResultSet to User object
private User mapResultSetToUser(ResultSet rs) throws SQLException {
    User user = new User();
    try {
        user.setUserId(rs.getInt("user_id")); 
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setBirthdate(rs.getString("birthdate"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setGender(rs.getString("gender"));
        user.setEmail(rs.getString("email")); 
        user.setCreatedAt(rs.getString("created_at")); 
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error mapping ResultSet to User: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        throw e;
    }
    return user;
}
    
    // Method to update the password
    public boolean updatePassword(String username, String newPassword) throws SQLException {
    String query = "UPDATE users SET password = ? WHERE username = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, newPassword);
        stmt.setString(2, username);
        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0; 
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error updating password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false; 
    }
}
    
    // Get total trips for the user
public int getTotalTrips(int userId) {
    String sql = "SELECT COUNT(*) AS total_trips FROM trips WHERE created_by = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, userId); 
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("total_trips");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error retrieving total trips: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return 0;
}
   // Get usernane By their ID
    public String getUsernameById(int userId) {
    String sql = "SELECT username FROM users WHERE user_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("username");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error retrieving username: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return null;
}

    // Get upcoming trips for the user
    public int getUpcomingTrips(int userId) {
    String sql = "SELECT COUNT(*) AS upcoming_trips FROM trips WHERE created_by = ? AND start_date > CURRENT_DATE";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, userId); // Use user_id
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("upcoming_trips");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error retrieving upcoming trips: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return 0;
}

    // Get total expenses for the user
    public double getTotalExpenses(int userId) {
    String sql = "SELECT COALESCE(SUM(amount), 0) AS total_expenses FROM expenses WHERE user_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, userId); // Use user_id
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble("total_expenses");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error retrieving total expenses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return 0.0;
}

    // Get pending bookings for the user
    public int getPendingBookings(int userId) {
    String sql = "SELECT COUNT(*) AS pending_bookings FROM bookings WHERE user_id = ? AND status = 'Pending'";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, userId); // Use user_id
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("pending_bookings");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error retrieving pending bookings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return 0;
}
}
