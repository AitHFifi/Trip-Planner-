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

import dao.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableCellRenderer;

public class Dashboard extends javax.swing.JFrame {

    private JPanel mainPanel;
    private JLabel dateTimeLabel;
    private JLabel userLabel;
    private String username;
    private int userId;


    public Dashboard(int userId) throws SQLException {
        this.userId = userId;
        UserDAO userDAO = new UserDAO();
        this.username = userDAO.getUsernameById(userId);
        initComponents();
        updateDateTime();
        switchPanel("Home");
    }

    private void initComponents() throws SQLException {
        // Frame settings
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Main container
        JPanel container = new JPanel(new BorderLayout()); 

        // Left navigation bar
        JPanel navBar = new JPanel();
        navBar.setBackground(new Color(51, 51, 51));
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.Y_AXIS));

        // Buttons for navigation
        JButton homeButton = new JButton("Home");
        JButton tripButton = new JButton("Trip Management");
        JButton destinationButton = new JButton("Destination Management");
        JButton bookingButton = new JButton("Booking Management");
        JButton expenseButton = new JButton("Expense Management");
        JButton exitButton = new JButton("Logout");

        // Style buttons
        JButton[] buttons = {homeButton, tripButton, destinationButton, bookingButton, expenseButton, exitButton};
        for (JButton button : buttons) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(200, 40));
            button.setBackground(new Color(102, 102, 102));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
        }

        // Add buttons to the navigation bar
        navBar.add(Box.createVerticalStrut(20));
        navBar.add(homeButton);
        navBar.add(Box.createVerticalStrut(10));
        navBar.add(tripButton);
        navBar.add(Box.createVerticalStrut(10));
        navBar.add(destinationButton);
        navBar.add(Box.createVerticalStrut(10));
        navBar.add(bookingButton);
        navBar.add(Box.createVerticalStrut(10));
        navBar.add(expenseButton);
        navBar.add(Box.createVerticalStrut(10));
        navBar.add(exitButton);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(0, 0, 0));
        topBar.setPreferredSize(new Dimension(900, 50));

        // Date and time label
        dateTimeLabel = new JLabel();
        dateTimeLabel.setForeground(Color.WHITE);
        dateTimeLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        topBar.add(dateTimeLabel, BorderLayout.WEST);

        // User label
        userLabel = new JLabel("Welcome, " + username + "  ");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        topBar.add(userLabel, BorderLayout.EAST);

        // Main content area
        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(createHomePanel(), "Home");

        // Add components to the container
        container.add(navBar, BorderLayout.WEST);
        container.add(topBar, BorderLayout.NORTH);
        container.add(mainPanel, BorderLayout.CENTER);

        // Add container to the frame
        add(container); 

        // Button actions
        homeButton.addActionListener(e -> switchPanel("Home"));
        tripButton.addActionListener(e -> switchPanel("TripManagement"));
        destinationButton.addActionListener(e -> switchPanel("DestinationManagement"));
        bookingButton.addActionListener(e -> switchPanel("BookingManagement"));
        expenseButton.addActionListener(e -> switchPanel("ExpenseManagement"));
        exitButton.addActionListener(e -> { 
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION); 
            if (confirm == JOptionPane.YES_OPTION) { 
                this.dispose();
                new LoginForm().setVisible(true); 
            }
        });

        // Add other panels
        mainPanel.add(new TripManagementPanel(userId, this), "TripManagement");
        mainPanel.add(new DestinationManagementPanel(userId), "DestinationManagement"); 
        mainPanel.add(new BookingManagementPanel(userId), "BookingManagement"); 
    }

    private void switchPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout(); 
        cardLayout.show(mainPanel, panelName);
    }

    public void openExpensesManagementPanel(String tripId) {
        try {
            ExpensesManagementPanel panel = new ExpensesManagementPanel(tripId, userId); 
            mainPanel.add(panel, "ExpenseManagement"); 
            switchPanel("ExpenseManagement"); 
        } catch (SQLException e) { 
            JOptionPane.showMessageDialog(this, "Failed to load expenses for the selected trip.", "Error", JOptionPane.ERROR_MESSAGE); 
        }
    }

    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout()); 
        homePanel.setBackground(Color.WHITE); 

        // Fetch data from the database
        UserDAO userDAO = new UserDAO();
        int totalTrips = userDAO.getTotalTrips(userId);
        int upcomingTrips = userDAO.getUpcomingTrips(userId);
        double totalExpenses = userDAO.getTotalExpenses(userId);
        int pendingBookings = userDAO.getPendingBookings(userId);

        // Table data
        String[] columnNames = {"Description", "Details"}; 
        Object[][] data = { 
            {"Total Trips", totalTrips},
            {"Upcoming Trips", upcomingTrips},
            {"Total Expenses", "RWF" + totalExpenses},
            {"Pending Bookings", pendingBookings}
        };

        // Create JTable
        JTable summaryTable = new JTable(data, columnNames);
        summaryTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
        summaryTable.setRowHeight(30);
        summaryTable.setEnabled(false);

        // Center align the table content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < summaryTable.getColumnCount(); i++) {
            summaryTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(summaryTable); 

        // Add components to the panel
        JLabel titleLabel = new JLabel("Home Summary"); 
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18)); 
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); 
        homePanel.add(titleLabel, BorderLayout.NORTH); 
        homePanel.add(scrollPane, BorderLayout.CENTER); 

        return homePanel;
    }

    private void updateDateTime() {
        Timer timer = new Timer(1000, e -> { 
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy HH:mm"); 
            dateTimeLabel.setText(dateFormat.format(new Date())); 
        });
        timer.start(); 
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> { 
//            try {
//                new Dashboard(1).setVisible(true); 
//            } catch (SQLException ex) { 
//                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex); 
//            }
//        });
//    }
}