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

import dao.ExpenseDAO; 
import model.Expense; 
import javax.swing.*; 
import javax.swing.table.DefaultTableModel; 
import java.awt.*; 
import java.sql.SQLException; 
import java.util.List; 
import java.util.Map; 
import java.util.stream.Collectors; 

public class ExpensesManagementPanel extends JPanel {

    private JTable expensesTable; 
    private DefaultTableModel tableModel; 
    private ExpenseDAO expenseDAO; 
    private String tripId; 
    private JLabel summaryLabel; 
    private int userId; 

    public ExpensesManagementPanel(String tripId, int userId) throws SQLException {
        this.tripId = tripId; 
        this.userId = userId; 
        this.expenseDAO = new ExpenseDAO(); 
        initComponents(); 
        loadExpenses(); 
    }

    private void initComponents() {
        setLayout(new BorderLayout()); 

        JLabel headerLabel = new JLabel("Expenses Management for Trip: " + tripId); 
        headerLabel.setFont(new Font("Tahoma", Font.BOLD, 16)); 
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER); 
        add(headerLabel, BorderLayout.NORTH); 

        String[] columnNames = {"Expense ID", "Expense Type", "Amount", "Expense Date", "Created At"}; 
        tableModel = new DefaultTableModel(columnNames, 0); 
        expensesTable = new JTable(tableModel); 
        expensesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        JScrollPane scrollPane = new JScrollPane(expensesTable); 
        add(scrollPane, BorderLayout.CENTER); 

        summaryLabel = new JLabel("Total Expenses: RWF 0.00 | Food: RWF 0.00 | Transport: RWF 0.00 | Accommodation: RWF 0.00 | Other: RWF 0.00"); 
        summaryLabel.setFont(new Font("Tahoma", Font.PLAIN, 14)); 
        add(summaryLabel, BorderLayout.SOUTH); 

        JPanel buttonPanel = new JPanel(); 
        JButton addButton = new JButton("Add Expense"); 
        JButton editButton = new JButton("Edit Expense"); 
        JButton deleteButton = new JButton("Delete Expense"); 

        buttonPanel.add(addButton); 
        buttonPanel.add(editButton); 
        buttonPanel.add(deleteButton); 
        add(buttonPanel, BorderLayout.SOUTH); 

        addButton.addActionListener(e -> addExpense(userId)); 
        editButton.addActionListener(e -> editExpense()); 
        deleteButton.addActionListener(e -> deleteExpense()); 
    }

    private void loadExpenses() {
        tableModel.setRowCount(0); 

        List<Expense> expenses = expenseDAO.getExpensesByTripId(tripId); 

        for (Expense expense : expenses) { 
            Object[] row = { 
                expense.getExpenseId(),
                expense.getExpenseType(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getCreatedAt()
            };
            tableModel.addRow(row); 
        }

        updateSummary(expenses); 
    }

    private void updateSummary(List<Expense> expenses) {
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum(); 
        Map<String, Double> breakdown = expenses.stream() 
                .collect(Collectors.groupingBy(Expense::getExpenseType, Collectors.summingDouble(Expense::getAmount)));

        summaryLabel.setText(String.format(
                "Total Expenses: RWF %.2f | Food: RWF %.2f | Transport: RWF %.2f | Accommodation: RWF %.2f | Other: RWF %.2f",
                total,
                breakdown.getOrDefault("Food", 0.0),
                breakdown.getOrDefault("Transport", 0.0),
                breakdown.getOrDefault("Accommodation", 0.0),
                breakdown.getOrDefault("Other", 0.0)
        ));
    }

    private void addExpense(int userId) {
        ExpenseFormDialog expenseForm = new ExpenseFormDialog(null, userId); 
        expenseForm.setVisible(true); 

        if (expenseForm.isSubmitted()) { 
            Expense expense = expenseForm.getExpense(); 
            if (expenseDAO.addExpense(expense)) { 
                JOptionPane.showMessageDialog(this, "Expense added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE); 
                loadExpenses(); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add expense.", "Error", JOptionPane.ERROR_MESSAGE); 
            }
        }
    }

    private void editExpense() {
        int selectedRow = expensesTable.getSelectedRow(); 
        if (selectedRow == -1) { 
            JOptionPane.showMessageDialog(this, "Please select an expense to edit.", "Error", JOptionPane.ERROR_MESSAGE); 
            return; 
        }

        int expenseId = (int) tableModel.getValueAt(selectedRow, 0); 
        Expense expense = expenseDAO.getExpenseById(expenseId); 

        ExpenseFormDialog expenseForm = new ExpenseFormDialog(expense, userId); 
        expenseForm.setVisible(true); 

        if (expenseForm.isSubmitted()) { 
            Expense updatedExpense = expenseForm.getExpense(); 
            if (expenseDAO.updateExpense(updatedExpense)) { 
                JOptionPane.showMessageDialog(this, "Expense updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE); 
                loadExpenses(); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update expense.", "Error", JOptionPane.ERROR_MESSAGE); 
            }
        }
    }

    private void deleteExpense() {
        int selectedRow = expensesTable.getSelectedRow(); 
        if (selectedRow == -1) { 
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.", "Error", JOptionPane.ERROR_MESSAGE); 
            return; 
        }

        int expenseId = (int) tableModel.getValueAt(selectedRow, 0); 

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this expense?", "Confirm Deletion", JOptionPane.YES_NO_OPTION); 
        if (confirm == JOptionPane.YES_OPTION) { 
            if (expenseDAO.deleteExpense(expenseId)) { 
                JOptionPane.showMessageDialog(this, "Expense deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE); 
                loadExpenses(); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete expense.", "Error", JOptionPane.ERROR_MESSAGE); 
            }
        }
    }
}
