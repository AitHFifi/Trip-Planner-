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

import dao.BookingDAO; 
import model.Booking; 
import javax.swing.*; 
import javax.swing.table.DefaultTableModel; 
import java.awt.*; 
import java.sql.SQLException; 
import java.util.List;

public class BookingManagementPanel extends JPanel {

    private JTable bookingTable; 
    private DefaultTableModel tableModel;
    private BookingDAO bookingDAO; 
    private int userId; 

    public BookingManagementPanel(int userId) throws SQLException {
        this.userId = userId; 
        this.bookingDAO = new BookingDAO(); 
        initComponents(); 
        loadBookings(); 
    }

    private void initComponents() {
        setLayout(new BorderLayout()); 

        
        JLabel titleLabel = new JLabel("Booking Management"); 
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18)); 
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); 
        add(titleLabel, BorderLayout.NORTH); 

        // Table
        String[] columnNames = {"Booking ID", "Trip ID", "Booking Date", "Price", "Status"}; 
        tableModel = new DefaultTableModel(columnNames, 0); 
        bookingTable = new JTable(tableModel); 
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        JScrollPane scrollPane = new JScrollPane(bookingTable); 
        add(scrollPane, BorderLayout.CENTER); 

        // Buttons
        JPanel buttonPanel = new JPanel(); 
        JButton addButton = new JButton("Add Booking"); 
        JButton editButton = new JButton("Edit Booking"); 
        JButton deleteButton = new JButton("Delete Booking"); 

        buttonPanel.add(addButton); 
        buttonPanel.add(editButton); 
        buttonPanel.add(deleteButton); 
        add(buttonPanel, BorderLayout.SOUTH); 

        // Button actions
        addButton.addActionListener(e -> addBooking()); 
        editButton.addActionListener(e -> editBooking()); 
        deleteButton.addActionListener(e -> deleteBooking());
    }

    private void loadBookings() {
        tableModel.setRowCount(0); 

        // Fetch bookings from the database
        List<Booking> bookings = bookingDAO.getBookingsByUserId(userId); 

        for (Booking booking : bookings) { 
            Object[] row = { 
                booking.getBookingId(),
                booking.getTripId(),
                booking.getBookingDate(),
                booking.getPrice(),
                booking.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void addBooking() {
        BookingFormDialog bookingForm = new BookingFormDialog(null, userId); 
        bookingForm.setVisible(true); 

        if (bookingForm.isSubmitted()) { 
            Booking newBooking = bookingForm.getBooking(); 
            if (bookingDAO.addBooking(newBooking)) { 
                JOptionPane.showMessageDialog(this, "Booking added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE); 
                loadBookings(); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add booking.", "Error", JOptionPane.ERROR_MESSAGE); 
            }
        }
    }

    private void editBooking() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) { 
            JOptionPane.showMessageDialog(this, "Please select a booking to edit.", "Error", JOptionPane.ERROR_MESSAGE); 
            return; 
        }

        int bookingId = (int) tableModel.getValueAt(selectedRow, 0); 
        Booking booking = bookingDAO.getBookingById(bookingId); 

        BookingFormDialog bookingForm = new BookingFormDialog(booking, userId); 
        bookingForm.setVisible(true); 

        if (bookingForm.isSubmitted()) { 
            Booking updatedBooking = bookingForm.getBooking();
            if (bookingDAO.updateBooking(updatedBooking)) {
                JOptionPane.showMessageDialog(this, "Booking updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBookings(); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update booking.", "Error", JOptionPane.ERROR_MESSAGE); 
            }
        }
    }

    private void deleteBooking() {
        int selectedRow = bookingTable.getSelectedRow(); 
        if (selectedRow == -1) { 
            JOptionPane.showMessageDialog(this, "Please select a booking to delete.", "Error", JOptionPane.ERROR_MESSAGE); 
            return; 
        }

        int bookingId = (int) tableModel.getValueAt(selectedRow, 0); 

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this booking?", "Confirm Deletion", JOptionPane.YES_NO_OPTION); 
        if (confirm == JOptionPane.YES_OPTION) { 
            if (bookingDAO.deleteBooking(bookingId)) { 
                JOptionPane.showMessageDialog(this, "Booking deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE); 
                loadBookings(); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete booking.", "Error", JOptionPane.ERROR_MESSAGE); 
            }
        }
    }
}