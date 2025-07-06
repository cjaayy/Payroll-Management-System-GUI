package gui;

import models.Employee;
import models.Payroll;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Payroll Management Panel
 */
public class PayrollPanel extends JPanel {
    private PayrollManagementSystemGUI mainApp;
    private JTable payrollTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, viewByEmployeeButton, backButton;
    private JTextField employeeIdField;
    
    public PayrollPanel(PayrollManagementSystemGUI mainApp) {
        this.mainApp = mainApp;
        initializeComponents();
        setupLayout();
        setupEventListeners();
        refreshTable();
    }
    
    private void initializeComponents() {
        setBackground(new Color(240, 248, 255));
        
        // Table setup
        String[] columnNames = {"Payroll ID", "Employee Name", "Pay Period", "Gross Pay", "Taxes", "Net Pay", "Pay Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        payrollTable = new JTable(tableModel);
        payrollTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        payrollTable.getTableHeader().setReorderingAllowed(false);
        
        // Buttons
        addButton = new JButton("Create Payroll");
        editButton = new JButton("Edit Payroll");
        deleteButton = new JButton("Delete Payroll");
        viewByEmployeeButton = new JButton("View by Employee");
        backButton = new JButton("Back to Main Menu");
        
        // Employee ID field
        employeeIdField = new JTextField(10);
        
        // Style buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        Color buttonColor = new Color(70, 130, 180);
        
        addButton.setFont(buttonFont);
        addButton.setBackground(buttonColor);
        addButton.setForeground(Color.WHITE);
        
        editButton.setFont(buttonFont);
        editButton.setBackground(buttonColor);
        editButton.setForeground(Color.WHITE);
        
        deleteButton.setFont(buttonFont);
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        
        viewByEmployeeButton.setFont(buttonFont);
        viewByEmployeeButton.setBackground(buttonColor);
        viewByEmployeeButton.setForeground(Color.WHITE);
        
        backButton.setFont(buttonFont);
        backButton.setBackground(new Color(128, 128, 128));
        backButton.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Payroll Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table in center
        JScrollPane scrollPane = new JScrollPane(payrollTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Employee ID:"));
        controlPanel.add(employeeIdField);
        controlPanel.add(viewByEmployeeButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(backButton);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddPayrollDialog();
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedPayroll();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedPayroll();
            }
        });
        
        viewByEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPayrollsByEmployee();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showMainMenuPanel();
            }
        });
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Payroll> payrolls = mainApp.getPayrollManager().getAllPayrolls();
        
        for (Payroll payroll : payrolls) {
            Employee employee = mainApp.getEmployeeManager().getEmployee(payroll.getEmployeeId());
            String employeeName = employee != null ? employee.getFullName() : "Unknown";
            
            Object[] rowData = {
                payroll.getPayrollId(),
                employeeName,
                payroll.getPayPeriod(),
                String.format("$%.2f", payroll.getGrossPay()),
                String.format("$%.2f", payroll.getTaxes()),
                String.format("$%.2f", payroll.getNetPay()),
                payroll.getPayDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void showAddPayrollDialog() {
        PayrollDialog dialog = new PayrollDialog(SwingUtilities.getWindowAncestor(this), 
                                                mainApp.getPayrollManager(), 
                                                mainApp.getEmployeeManager(), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshTable();
        }
    }
    
    private void editSelectedPayroll() {
        if (!mainApp.getAuthManager().hasAdminRole()) {
            JOptionPane.showMessageDialog(this, "Access denied. Admin privileges required.", 
                                        "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int selectedRow = payrollTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a payroll record to edit.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int payrollId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Payroll payroll = mainApp.getPayrollManager().getPayroll(payrollId);
        
        if (payroll != null) {
            PayrollDialog dialog = new PayrollDialog(SwingUtilities.getWindowAncestor(this), 
                                                    mainApp.getPayrollManager(), 
                                                    mainApp.getEmployeeManager(), payroll);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                refreshTable();
            }
        }
    }
    
    private void deleteSelectedPayroll() {
        if (!mainApp.getAuthManager().hasAdminRole()) {
            JOptionPane.showMessageDialog(this, "Access denied. Admin privileges required.", 
                                        "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int selectedRow = payrollTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a payroll record to delete.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int payrollId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this payroll record?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (mainApp.getPayrollManager().deletePayroll(payrollId)) {
                JOptionPane.showMessageDialog(this, "Payroll record deleted successfully.");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete payroll record.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewPayrollsByEmployee() {
        String employeeIdText = employeeIdField.getText().trim();
        
        if (employeeIdText.isEmpty()) {
            refreshTable();
            return;
        }
        
        try {
            int employeeId = Integer.parseInt(employeeIdText);
            Employee employee = mainApp.getEmployeeManager().getEmployee(employeeId);
            
            if (employee == null) {
                JOptionPane.showMessageDialog(this, "Employee not found.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            List<Payroll> payrolls = mainApp.getPayrollManager().getPayrollsByEmployee(employeeId);
            
            tableModel.setRowCount(0);
            for (Payroll payroll : payrolls) {
                Object[] rowData = {
                    payroll.getPayrollId(),
                    employee.getFullName(),
                    payroll.getPayPeriod(),
                    String.format("$%.2f", payroll.getGrossPay()),
                    String.format("$%.2f", payroll.getTaxes()),
                    String.format("$%.2f", payroll.getNetPay()),
                    payroll.getPayDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                };
                tableModel.addRow(rowData);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid employee ID.", 
                                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}
