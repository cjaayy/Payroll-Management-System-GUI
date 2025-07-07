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
    private static final long serialVersionUID = 1L;
    private PayrollManagementSystemGUI mainApp;
    private JTable payrollTable;
    private DefaultTableModel tableModel;
    private JButton editButton, deleteButton, viewByEmployeeButton, backButton, philippinePayrollButton, viewPayslipButton;
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
        editButton = new JButton("Edit Payroll");
        deleteButton = new JButton("Delete Payroll");
        viewByEmployeeButton = new JButton("View by Employee");
        philippinePayrollButton = new JButton("Create Payroll");
        viewPayslipButton = new JButton("View Payslip");
        backButton = new JButton("Back to Main Menu");
        
        // Employee ID field
        employeeIdField = new JTextField(10);
        employeeIdField.setToolTipText("Enter employee ID (e.g., IT-202501-001, EMP001, or 1)");
        
        // Style buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        Color buttonColor = new Color(70, 130, 180);
        
        editButton.setFont(buttonFont);
        editButton.setBackground(buttonColor);
        editButton.setForeground(Color.WHITE);
        
        deleteButton.setFont(buttonFont);
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        
        viewByEmployeeButton.setFont(buttonFont);
        viewByEmployeeButton.setBackground(buttonColor);
        viewByEmployeeButton.setForeground(Color.WHITE);
        
        philippinePayrollButton.setFont(buttonFont);
        philippinePayrollButton.setBackground(new Color(32, 178, 170));
        philippinePayrollButton.setForeground(Color.WHITE);
        
        viewPayslipButton.setFont(buttonFont);
        viewPayslipButton.setBackground(new Color(255, 165, 0));
        viewPayslipButton.setForeground(Color.WHITE);
        
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
        
        // Control panel with multiple rows
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search panel (top row)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Employee ID:"));
        searchPanel.add(employeeIdField);
        searchPanel.add(viewByEmployeeButton);
        
        // Action buttons panel (middle row) 
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionPanel.add(philippinePayrollButton);
        actionPanel.add(editButton);
        actionPanel.add(viewPayslipButton);
        actionPanel.add(deleteButton);
        
        // Navigation panel (bottom row)
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navigationPanel.add(backButton);
        
        controlPanel.add(searchPanel, BorderLayout.NORTH);
        controlPanel.add(actionPanel, BorderLayout.CENTER);
        controlPanel.add(navigationPanel, BorderLayout.SOUTH);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
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
        
        philippinePayrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPhilippinePayrollDialog();
            }
        });
        
        viewPayslipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewSelectedPayslip();
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
                String.format("₱%.2f", payroll.getGrossPay()),
                String.format("₱%.2f", payroll.getTaxes()),
                String.format("₱%.2f", payroll.getNetPay()),
                payroll.getPayDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void showPhilippinePayrollDialog() {
        PhilippinePayrollDialog dialog = new PhilippinePayrollDialog(SwingUtilities.getWindowAncestor(this), 
                                                                    mainApp.getPayrollManager(), 
                                                                    mainApp.getEmployeeManager(), 
                                                                    mainApp.getSalaryComponentManager(), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshTable();
        }
    }
    
    private void editSelectedPayroll() {
        if (!mainApp.getAuthManager().hasAdminOrHROrPayrollRole()) {
            JOptionPane.showMessageDialog(this, "Access denied. Admin, HR, or Payroll privileges required.", 
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
        if (!mainApp.getAuthManager().hasAdminOrHRRole()) {
            JOptionPane.showMessageDialog(this, "Access denied. Admin or HR privileges required.", 
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
    
    private void viewSelectedPayslip() {
        int selectedRow = payrollTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a payroll record to view payslip.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int payrollId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Payroll payroll = mainApp.getPayrollManager().getPayroll(payrollId);
        
        if (payroll != null) {
            managers.PayslipGenerator payslipGenerator = new managers.PayslipGenerator(mainApp.getEmployeeManager());
            PayslipDialog.showPayslipDialog(SwingUtilities.getWindowAncestor(this), 
                                          mainApp.getEmployeeManager(), payslipGenerator, payroll);
        } else {
            JOptionPane.showMessageDialog(this, "Payroll record not found.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewPayrollsByEmployee() {
        String employeeIdText = employeeIdField.getText().trim();
        
        if (employeeIdText.isEmpty()) {
            refreshTable();
            return;
        }
        
        try {
            int employeeId;
            Employee employee;
            
            // Try to parse as formatted ID first (e.g., "EMP001" or "IT-202501-001")
            if (employeeIdText.toUpperCase().startsWith("EMP") || employeeIdText.matches("^[A-Z]{2,3}-\\d{6}-\\d{3}$")) {
                employeeId = mainApp.getEmployeeManager().parseEmployeeId(employeeIdText);
                if (employeeId == -1) {
                    JOptionPane.showMessageDialog(this, "Invalid employee ID format. Use format: IT-202501-001, EMP001, or just the number.", 
                                                "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                employee = mainApp.getEmployeeManager().getEmployee(employeeId);
            } else {
                // Try to parse as numeric ID
                employeeId = Integer.parseInt(employeeIdText);
                employee = mainApp.getEmployeeManager().getEmployee(employeeId);
            }
            
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
                    String.format("₱%.2f", payroll.getGrossPay()),
                    String.format("₱%.2f", payroll.getTaxes()),
                    String.format("₱%.2f", payroll.getNetPay()),
                    payroll.getPayDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                };
                tableModel.addRow(rowData);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid employee ID (e.g., IT-202501-001, EMP001, or 1).", 
                                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}
