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
import dao.TripDAO;
import model.Trip;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class TripFormDialog extends JDialog {

    private JTextField tripNameField;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JTextField budgetField;
    private boolean submitted = false;
    private Trip trip;
    private int userId;

    public TripFormDialog(Trip trip, int userId) {
        this.trip = trip;
        this.userId = userId;
        setTitle(trip == null ? "Add Trip" : "Edit Trip");
        setModal(true);
        setSize(350, 300);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel tripNameLabel = new JLabel("Trip Name:");
        tripNameField = new JTextField();
        tripNameField.setPreferredSize(new Dimension(150, 25));

        JLabel startDateLabel = new JLabel("Start Date:");
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        startDateChooser.setPreferredSize(new Dimension(150, 25));

        JLabel endDateLabel = new JLabel("End Date:");
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("yyyy-MM-dd");
        endDateChooser.setPreferredSize(new Dimension(150, 25));

        JLabel budgetLabel = new JLabel("Budget:");
        budgetField = new JTextField();
        budgetField.setPreferredSize(new Dimension(150, 25));

        if (trip != null) {
            tripNameField.setText(trip.getTripName());
            startDateChooser.setDate(trip.getStartDate());
            endDateChooser.setDate(trip.getEndDate());
            budgetField.setText(String.valueOf(trip.getBudget()));
        }

        panel.add(tripNameLabel);
        panel.add(tripNameField);
        panel.add(startDateLabel);
        panel.add(startDateChooser);
        panel.add(endDateLabel);
        panel.add(endDateChooser);
        panel.add(budgetLabel);
        panel.add(budgetField);

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);

        cancelButton.addActionListener(e -> handleCancel());
        submitButton.addActionListener(e -> handleSubmit());

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleSubmit() {
        String tripName = tripNameField.getText().trim();
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();
        String budgetStr = budgetField.getText().trim();

        if (tripName.isEmpty() || startDate == null || endDate == null || budgetStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (startDate.after(endDate)) {
                JOptionPane.showMessageDialog(this, "Start date must be before end date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double budget = Double.parseDouble(budgetStr);
            if (budget < 0) {
                JOptionPane.showMessageDialog(this, "Budget must be a positive number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (trip == null) {
                trip = new Trip();
                trip.setTripId("TRIP-" + System.currentTimeMillis());
            }
            trip.setTripName(tripName);
            trip.setStartDate(startDate);
            trip.setEndDate(endDate);
            trip.setBudget(budget);
            trip.setCreatedBy(userId);

            submitted = true;
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Budget must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isDateRangeOverlapping(Date startDate, Date endDate) {
        try {
            TripDAO tripDAO = new TripDAO();
            List<Trip> existingTrips = tripDAO.getTripsByUserId(userId);

            for (Trip existingTrip : existingTrips) {
                if (startDate.before(existingTrip.getEndDate()) && endDate.after(existingTrip.getStartDate())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking date overlap: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void handleCancel() {
        submitted = false;
        dispose();
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public Trip getTrip() {
        return trip;
    }
}
