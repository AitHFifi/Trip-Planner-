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

import dao.DestinationDAO; 
import model.Destination; 
import javax.swing.*; 
import javax.swing.table.DefaultTableModel; 
import java.awt.*; 
import java.sql.SQLException; 
import java.util.List; 
import java.util.logging.Level; 
import java.util.logging.Logger;

public class DestinationManagementPanel extends JPanel {

    private JTable destinationTable;
    private DefaultTableModel tableModel;
    private DestinationDAO destinationDAO;
    private int userId;

    public DestinationManagementPanel(int userId) throws SQLException {
        this.userId = userId;
        this.destinationDAO = new DestinationDAO();
        initComponents();
        loadDestinations();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Destination Management");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Destination ID", "Trip ID", "Country", "City", "Transport Mode", "Estimated Cost", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0);
        destinationTable = new JTable(tableModel);
        destinationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(destinationTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Destination");
        JButton editButton = new JButton("Edit Destination");
        JButton deleteButton = new JButton("Delete Destination");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            try {
                addDestination();
            } catch (SQLException ex) {
                Logger.getLogger(DestinationManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        editButton.addActionListener(e -> {
            try {
                editDestination();
            } catch (SQLException ex) {
                Logger.getLogger(DestinationManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        deleteButton.addActionListener(e -> deleteDestination());
    }

    private void loadDestinations() {
        tableModel.setRowCount(0);

        List<Destination> destinations = destinationDAO.getDestinationsByUserId(userId);

        for (Destination destination : destinations) {
            Object[] row = {
                destination.getDestinationId(),
                destination.getTripId(),
                destination.getCountry(),
                destination.getCity(),
                destination.getTransportMode(),
                destination.getEstimatedCost(),
                destination.getDescription()
            };
            tableModel.addRow(row);
        }
    }

    private void addDestination() throws SQLException {
        DestinationFormDialog destinationForm = new DestinationFormDialog(null, userId);
        destinationForm.setVisible(true);

        if (destinationForm.isSubmitted()) {
            Destination newDestination = destinationForm.getDestination();
            newDestination.setUserId(userId);
            if (destinationDAO.addDestination(newDestination)) {
                JOptionPane.showMessageDialog(this, "Destination added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDestinations();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add destination.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editDestination() throws SQLException {
        int selectedRow = destinationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a destination to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int destinationId = (int) tableModel.getValueAt(selectedRow, 0);
        Destination destination = destinationDAO.getDestinationById(destinationId);

        DestinationFormDialog destinationForm = new DestinationFormDialog(destination, userId);
        destinationForm.setVisible(true);

        if (destinationForm.isSubmitted()) {
            Destination updatedDestination = destinationForm.getDestination();
            if (destinationDAO.updateDestination(updatedDestination)) {
                JOptionPane.showMessageDialog(this, "Destination updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDestinations();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update destination.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteDestination() {
        int selectedRow = destinationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a destination to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int destinationId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this destination?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (destinationDAO.deleteDestination(destinationId)) {
                JOptionPane.showMessageDialog(this, "Destination deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDestinations();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete destination.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
