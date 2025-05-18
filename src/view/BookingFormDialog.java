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
import model.Booking; 
import javax.swing.*; 
import java.awt.*; 
import java.sql.SQLException;
import java.util.Date; 
import model.Trip; 
import java.util.logging.Level; 
import java.util.logging.Logger; 

public class BookingFormDialog extends JDialog {

    private JComboBox<String> tripIdComboBox; 
    private JDateChooser bookingDateChooser; 
    private JTextField priceField; 
    private JComboBox<String> statusComboBox; 
    private boolean submitted = false; 
    private Booking booking; 
    private int userId; 

    public BookingFormDialog(Booking booking, int userId) {
        this.booking = booking; 
        this.userId = userId; 
        setTitle(booking == null ? "Add Booking" : "Edit Booking"); 
        setModal(true); 
        setSize(400, 300); 
        setLocationRelativeTo(null); 
        initComponents(); 
    }

    private void initComponents() {
        // Main panel
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 

        // Labels and fields
        JLabel tripIdLabel = new JLabel("Trip ID:"); 
        tripIdComboBox = new JComboBox<>(fetchTrips()); 

        JLabel bookingDateLabel = new JLabel("Booking Date:"); 
        bookingDateChooser = new JDateChooser(); 
        bookingDateChooser.setDateFormatString("yyyy-MM-dd"); 

        JLabel priceLabel = new JLabel("Price:"); 
        priceField = new JTextField(); 

        JLabel statusLabel = new JLabel("Status:"); 
        statusComboBox = new JComboBox<>(new String[]{"Pending", "Confirmed", "Cancelled"}); 

        // Populate fields if editing
        if (booking != null) { 
            tripIdComboBox.setSelectedItem(booking.getTripId()); 
            bookingDateChooser.setDate(booking.getBookingDate());
            priceField.setText(String.valueOf(booking.getPrice())); 
            statusComboBox.setSelectedItem(booking.getStatus()); 
        }

        // Add components to the panel
        panel.add(tripIdLabel); 
        panel.add(tripIdComboBox); 
        panel.add(bookingDateLabel); 
        panel.add(bookingDateChooser); 
        panel.add(priceLabel); 
        panel.add(priceField); 
        panel.add(statusLabel); 
        panel.add(statusComboBox); 

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

    private String[] fetchTrips() {
        try {
            TripDAO tripDAO = new TripDAO(); 
            java.util.List<Trip> trips = tripDAO.getAllTrips(); 
            String[] tripNames = new String[trips.size()]; 
            for (int i = 0; i < trips.size(); i++) { 
                tripNames[i] = trips.get(i).getTripId() + " - " + trips.get(i).getTripName(); 
            }
            return tripNames; 
        } catch (SQLException e) { 
            Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, "Database connection error", e);
            JOptionPane.showMessageDialog(this, "Unable to connect to the server. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return new String[0]; 
    }

    private void handleSubmit() {
        // Validate input
        String tripId = (String) tripIdComboBox.getSelectedItem(); 
        Date bookingDate = bookingDateChooser.getDate(); 
        String priceStr = priceField.getText().trim(); 
        String status = (String) statusComboBox.getSelectedItem(); 

        if (tripId == null || bookingDate == null || priceStr.isEmpty() || status == null) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE); 
            return; 
        }

        try {
            // Parse price
            double price = Double.parseDouble(priceStr); 
            if (price < 0) { 
                JOptionPane.showMessageDialog(this, "Price must be a positive number.", "Validation Error", JOptionPane.ERROR_MESSAGE); 
                return; 
            }

            // Create or update the booking object
            if (booking == null) { 
                booking = new Booking(); 
            }
            booking.setTripId(tripId); 
            booking.setBookingDate(bookingDate); 
            booking.setPrice(price); 
            booking.setStatus(status); 
            booking.setUserId(userId); 
            submitted = true; 
            dispose(); 
        } catch (NumberFormatException e) { 
            JOptionPane.showMessageDialog(this, "Price must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void handleCancel() {
        submitted = false; 
        dispose(); 
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public Booking getBooking() {
        return booking;
    }
}