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
import model.Destination;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

public class DestinationFormDialog extends JDialog {

    private JComboBox<String> tripIdComboBox;
    private JLabel tripNameLabel;
    private JTextField countryField;
    private JTextField cityField;
    private JTextField descriptionField;
    private JComboBox<String> transportModeComboBox;
    private JTextField estimatedCostField;
    private boolean submitted = false;
    private Destination destination;
    private Map<String, String> tripMap;
    private int userId;

    public DestinationFormDialog(Destination destination, int userId) throws SQLException {
        this.destination = destination;
        this.userId = userId;
        setTitle(destination == null ? "Add Destination" : "Edit Destination");
        setModal(true);
        setSize(400, 400);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() throws SQLException {
        // Fetch trip data
        tripMap = fetchTripData(); 

        // Main panel
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 

        // Labels and fields
        JLabel tripIdLabel = new JLabel("Trip ID:");
        tripIdComboBox = new JComboBox<>(tripMap.keySet().toArray(new String[0]));
        tripIdComboBox.addActionListener(e -> updateTripName());

        JLabel tripNameStaticLabel = new JLabel("Trip Name:"); 
        tripNameLabel = new JLabel(); 

        JLabel countryLabel = new JLabel("Country:"); 
        countryField = new JTextField(); 

        JLabel cityLabel = new JLabel("City:"); 
        cityField = new JTextField(); 

        JLabel descriptionLabel = new JLabel("Description:"); 
        descriptionField = new JTextField(); 

        JLabel transportModeLabel = new JLabel("Transport Mode:"); 
        transportModeComboBox = new JComboBox<>(new String[]{"Car", "Plane", "Train", "Bus", "Other"}); 

        JLabel estimatedCostLabel = new JLabel("Estimated Cost:"); 
        estimatedCostField = new JTextField(); 

        // Populate fields if editing
        if (destination != null) { 
            tripIdComboBox.setSelectedItem(destination.getTripId()); 
            updateTripName(); 
            countryField.setText(destination.getCountry()); 
            cityField.setText(destination.getCity()); 
            descriptionField.setText(destination.getDescription()); 
            transportModeComboBox.setSelectedItem(destination.getTransportMode()); 
            estimatedCostField.setText(String.valueOf(destination.getEstimatedCost())); 
        }

        // Add components to the panel
        panel.add(tripIdLabel);
        panel.add(tripIdComboBox);
        panel.add(tripNameStaticLabel);
        panel.add(tripNameLabel);
        panel.add(countryLabel);
        panel.add(countryField);
        panel.add(cityLabel);
        panel.add(cityField);
        panel.add(descriptionLabel);
        panel.add(descriptionField);
        panel.add(transportModeLabel);
        panel.add(transportModeComboBox);
        panel.add(estimatedCostLabel);
        panel.add(estimatedCostField);

        // Buttons
        JButton cancelButton = new JButton("Cancel"); 
        JButton submitButton = new JButton("Submit"); 

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        // Add action listeners
        cancelButton.addActionListener(e -> handleCancel());
        submitButton.addActionListener(e -> handleSubmit());
        
        // Add panels to the dialog
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateTripName() {
        String selectedTripId = (String) tripIdComboBox.getSelectedItem(); 
        tripNameLabel.setText(tripMap.get(selectedTripId)); 
    }

    private Map<String, String> fetchTripData() throws SQLException {
        TripDAO tripDAO = new TripDAO(); 
        return tripDAO.getTripIdNameMap(); 
    }

    private void handleSubmit() {
        // Validate input
        String tripId = (String) tripIdComboBox.getSelectedItem();
        String country = countryField.getText().trim();
        String city = cityField.getText().trim();
        String description = descriptionField.getText().trim();
        String transportMode = (String) transportModeComboBox.getSelectedItem();
        String estimatedCostStr = estimatedCostField.getText().trim();

        if (tripId.isEmpty() || country.isEmpty() || city.isEmpty() || description.isEmpty() || transportMode.isEmpty() || estimatedCostStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Parse estimated cost
            double estimatedCost = Double.parseDouble(estimatedCostStr); 
            if (estimatedCost < 0) { 
                JOptionPane.showMessageDialog(this, "Estimated cost must be a positive number.", "Validation Error", JOptionPane.ERROR_MESSAGE); 
                return; 
            }

            // Create or update the destination object
            if (destination == null) { 
                destination = new Destination(); 
            }
            destination.setTripId(tripId);
            destination.setCountry(country);
            destination.setCity(city);
            destination.setDescription(description);
            destination.setTransportMode(transportMode);
            destination.setEstimatedCost(estimatedCost);
            submitted = true;
            dispose();
        } catch (NumberFormatException e) { 
            JOptionPane.showMessageDialog(this, "Estimated cost must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void handleCancel() {
        submitted = false; 
        dispose(); 
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public Destination getDestination() {
        return destination;
    }
}