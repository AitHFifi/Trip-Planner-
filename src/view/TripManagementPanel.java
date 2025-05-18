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

import dao.TripDAO;
import model.Trip;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class TripManagementPanel extends JPanel {

    private JTable tripTable;
    private DefaultTableModel tableModel;
    private TripDAO tripDAO;
    private int userId;
    private Dashboard dashboard;

    public TripManagementPanel(int userId, Dashboard dashboard) throws SQLException {
        this.userId = userId;
        this.dashboard = dashboard;
        this.tripDAO = new TripDAO();
        initComponents();
        loadTrips();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Trip Management");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Trip ID", "Trip Name", "Start Date", "End Date", "Budget"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tripTable = new JTable(tableModel);
        tripTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tripTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Trip");
        JButton editButton = new JButton("Edit Trip");
        JButton deleteButton = new JButton("Delete Trip");
        JButton viewExpensesButton = new JButton("View Expenses");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewExpensesButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addTrip());
        editButton.addActionListener(e -> editTrip());
        deleteButton.addActionListener(e -> deleteTrip());
        viewExpensesButton.addActionListener(e -> viewExpenses());
    }

    private void loadTrips() {
        tableModel.setRowCount(0);

        List<Trip> trips = tripDAO.getTripsByUserId(userId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Trip trip : trips) {
            Object[] row = {
                trip.getTripId(),
                trip.getTripName(),
                dateFormat.format(trip.getStartDate()),
                dateFormat.format(trip.getEndDate()),
                trip.getBudget()
            };
            tableModel.addRow(row);
        }
    }

    private void addTrip() {
        TripFormDialog tripForm = new TripFormDialog(null, userId);
        tripForm.setVisible(true);

        if (tripForm.isSubmitted()) {
            Trip newTrip = tripForm.getTrip();
            try {
                if (tripDAO.addTrip(newTrip)) {
                    JOptionPane.showMessageDialog(this, "Trip added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTrips();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add trip.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding trip: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editTrip() {
        int selectedRow = tripTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a trip to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tripId = (String) tableModel.getValueAt(selectedRow, 0);
        try {
            Trip trip = tripDAO.getTripById(tripId);
            if (trip == null) {
                JOptionPane.showMessageDialog(this, "Trip not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            TripFormDialog tripForm = new TripFormDialog(trip, userId);
            tripForm.setVisible(true);

            if (tripForm.isSubmitted()) {
                Trip updatedTrip = tripForm.getTrip();
                if (tripDAO.updateTrip(updatedTrip)) {
                    JOptionPane.showMessageDialog(this, "Trip updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTrips();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update trip.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error editing trip: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTrip() {
        int selectedRow = tripTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a trip to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this trip?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String tripId = (String) tableModel.getValueAt(selectedRow, 0);
        try {
            if (tripDAO.deleteTrip(tripId)) {
                JOptionPane.showMessageDialog(this, "Trip deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTrips();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete trip.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting trip: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewExpenses() {
        int selectedRow = tripTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a trip to view expenses.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tripId = (String) tableModel.getValueAt(selectedRow, 0);
        dashboard.openExpensesManagementPanel(tripId);
    }
}
