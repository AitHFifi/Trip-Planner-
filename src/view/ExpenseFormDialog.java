/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view; 

/**
 *
 * @author Hp
 */

import com.toedter.calendar.JDateChooser;
import dao.DestinationDAO;
import dao.ExpenseDAO;
import dao.TripDAO;
import model.Destination;
import model.Expense;
import model.Trip;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpenseFormDialog extends JDialog {

    private JComboBox<String> tripComboBox;
    private JComboBox<String> expenseTypeComboBox;
    private JTextField amountField;
    private JDateChooser expenseDateChooser;
    private boolean submitted = false;
    private Expense expense;
    private int userId;

    public ExpenseFormDialog(Expense expense, int userId) {
        this.expense = expense;
        this.userId = userId;
        setTitle(expense == null ? "Add Expense" : "Edit Expense");
        setModal(true);
        setSize(400, 400);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel tripLabel = new JLabel("Trip:");
        tripComboBox = new JComboBox<>(fetchTrips());

        JLabel expenseTypeLabel = new JLabel("Expense Type:");
        expenseTypeComboBox = new JComboBox<>(new String[]{"Food", "Transport", "Accommodation", "Other"});

        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField();

        JLabel expenseDateLabel = new JLabel("Expense Date:");
        expenseDateChooser = new JDateChooser();
        expenseDateChooser.setDateFormatString("yyyy-MM-dd");

        if (expense != null) {
            tripComboBox.setSelectedItem(expense.getTripId());
            expenseTypeComboBox.setSelectedItem(expense.getExpenseType());
            amountField.setText(String.valueOf(expense.getAmount()));
            expenseDateChooser.setDate(expense.getExpenseDate());
        }

        panel.add(tripLabel);
        panel.add(tripComboBox);
        panel.add(expenseTypeLabel);
        panel.add(expenseTypeComboBox);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(expenseDateLabel);
        panel.add(expenseDateChooser);

        JButton cancelButton = new JButton("Cancel");
        JButton submitButton = new JButton("Submit");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);

        cancelButton.addActionListener(e -> handleCancel());
        submitButton.addActionListener(e -> {
            try {
                handleSubmit();
            } catch (SQLException ex) {
                Logger.getLogger(ExpenseFormDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private String[] fetchTrips() {
        try {
            TripDAO tripDAO = new TripDAO();
            List<Trip> trips = tripDAO.getAllTrips();
            String[] tripNames = new String[trips.size()];
            for (int i = 0; i < trips.size(); i++) {
                tripNames[i] = trips.get(i).getTripId() + " - " + trips.get(i).getTripName();
            }
            return tripNames;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching trips: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return new String[0];
    }

    private void handleSubmit() throws SQLException {
        String selectedTrip = (String) tripComboBox.getSelectedItem();
        String expenseType = (String) expenseTypeComboBox.getSelectedItem();
        String amountStr = amountField.getText().trim();
        Date expenseDate = expenseDateChooser.getDate();

        if (selectedTrip == null || expenseType == null || amountStr.isEmpty() || expenseDate == null) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount < 0) {
                JOptionPane.showMessageDialog(this, "Amount must be a positive number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (expenseDate.after(new Date())) {
                JOptionPane.showMessageDialog(this, "Expense date cannot be in the future.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String tripId = selectedTrip.split(" - ")[0];
            TripDAO tripDAO = new TripDAO();
            double tripBudget = tripDAO.getTripBudgetById(tripId);

            ExpenseDAO expenseDAO = new ExpenseDAO();
            double totalExpenses = expenseDAO.getTotalExpensesByTripId(tripId);

            if (totalExpenses + amount > tripBudget) {
                JOptionPane.showMessageDialog(this, "This expense exceeds the trip budget. Please adjust the amount.", "Budget Exceeded", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DestinationDAO destinationDAO = new DestinationDAO();
            Destination destination = destinationDAO.getDestinationByTripId(tripId);

            if (destination == null) {
                JOptionPane.showMessageDialog(this, "No destination found for the selected trip.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (expense == null) {
                expense = new Expense();
            }
            expense.setUserId(userId);
            expense.setTripId(tripId);
            expense.setDestinationId(destination.getDestinationId());
            expense.setExpenseType(expenseType);
            expense.setAmount(amount);
            expense.setExpenseDate(expenseDate);

            submitted = true;
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancel() {
        submitted = false;
        dispose();
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public Expense getExpense() {
        return expense;
    }
}
