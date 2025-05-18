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

import model.Expense;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ExpenseDAO {
    private Connection connection;

    public ExpenseDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection(); 
    }

// Fetch all expenses for a specific trip by tripId
public List<Expense> getExpensesByTripId(String tripId) {
    List<Expense> expenses = new ArrayList<>();
    String sql = "SELECT e.expense_id, e.expense_type, e.amount, e.expense_date, e.created_at, " +
                "t.trip_name " +
                "FROM expenses e " +
                "JOIN destinations d ON e.destination_id = d.destination_id " +
                "JOIN trips t ON d.trip_id = t.trip_id " +
                "WHERE t.trip_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, tripId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Expense expense = new Expense();
            expense.setExpenseId(rs.getInt("expense_id"));
            expense.setExpenseType(rs.getString("expense_type"));
            expense.setAmount(rs.getDouble("amount"));
            expense.setExpenseDate(rs.getDate("expense_date"));
            expense.setCreatedAt(rs.getTimestamp("created_at"));
            expense.setTripName(rs.getString("trip_name")); 
            expenses.add(expense);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching expenses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    return expenses;
}

    // Fetch a specific expense by its ID
    public Expense getExpenseById(int expenseId) {
        String sql = "SELECT * FROM expenses WHERE expense_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, expenseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Expense expense = new Expense();
                expense.setExpenseId(rs.getInt("expense_id"));
                expense.setUserId(rs.getInt("user_id"));
                expense.setDestinationId(rs.getInt("destination_id"));
                expense.setExpenseType(rs.getString("expense_type"));
                expense.setAmount(rs.getDouble("amount"));
                expense.setExpenseDate(rs.getDate("expense_date"));
                expense.setCreatedAt(rs.getTimestamp("created_at"));
                return expense;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching expense: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
}
    
    // Fetch all expenses by TripId
    public double getTotalExpensesByTripId(String tripId) {
    String query = "SELECT SUM(amount) AS total_expenses FROM expenses WHERE trip_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setString(1, tripId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble("total_expenses"); 
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching total expenses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    return 0.0; 
}

    // Add a new expense
    public boolean addExpense(Expense expense) {
    String sql = "INSERT INTO expenses (user_id, destination_id, expense_type, amount, expense_date, trip_id) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, expense.getUserId());
        stmt.setInt(2, expense.getDestinationId());
        stmt.setString(3, expense.getExpenseType());
        stmt.setDouble(4, expense.getAmount());
        stmt.setDate(5, new java.sql.Date(expense.getExpenseDate().getTime()));
        stmt.setString(6, expense.getTripId()); 
        return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding expense: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
}
return false;
}

    // Update an existing expense
    public boolean updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET expense_type = ?, amount = ?, expense_date = ? WHERE expense_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, expense.getExpenseType());
            stmt.setDouble(2, expense.getAmount());
            stmt.setDate(3, new java.sql.Date(expense.getExpenseDate().getTime()));
            stmt.setInt(4, expense.getExpenseId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating expense: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Delete an expense by its ID
    public boolean deleteExpense(int expenseId) {
        String sql = "DELETE FROM expenses WHERE expense_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, expenseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting destination: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}
