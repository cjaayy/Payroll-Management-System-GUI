package gui;

import models.Employee;
import models.Payroll;
import managers.EmployeeManager;
import managers.PayrollManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Dialog for creating/editing payroll records
 */
public class PayrollDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private transient PayrollManager payrollManager;
    private transient EmployeeManager employeeManager;
    private transient Payroll payroll;
    private boolean confirmed = false;
    
    private JTextField employeeIdField;
    private JTextField payPeriodField;
    private JTextField basePayField;
    private JTextField overtimeField;
    private JTextField bonusesField;
    private JTextField deductionsField;
    private JTextField payDateField;
    
    private JButton saveButton;
    private JButton cancelButton;
    private JButton salaryBreakdownButton;
    private JLabel employeeNameLabel;
    
    public PayrollDialog(Window parent, PayrollManager payrollManager, 
                        EmployeeManager employeeManager, Payroll payroll) {
        super(parent, payroll == null ? "Create Payroll" : "Edit Payroll", ModalityType.APPLICATION_MODAL);
        this.payrollManager = payrollManager;
        this.employeeManager = employeeManager;
        this.payroll = payroll;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        if (payroll != null) {
            populateFields();
        }
        
        setSize(450, 400);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        employeeIdField = new JTextField(20);
        employeeIdField.setToolTipText("Enter employee ID (e.g., IT-202501-001, EMP001, or 1)");
        payPeriodField = new JTextField(20);
        basePayField = new JTextField(20);
        overtimeField = new JTextField(20);
        bonusesField = new JTextField(20);
        deductionsField = new JTextField(20);
        payDateField = new JTextField(20);
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        salaryBreakdownButton = new JButton("Salary Breakdown");
        employeeNameLabel = new JLabel();
        
        // Style buttons
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(128, 128, 128));
        cancelButton.setForeground(Color.WHITE);
        salaryBreakdownButton.setBackground(new Color(32, 178, 170));
        salaryBreakdownButton.setForeground(Color.WHITE);
        
        // Set default values
        overtimeField.setText("0.00");
        bonusesField.setText("0.00");
        deductionsField.setText("0.00");
        
        // Add tooltips
        payPeriodField.setToolTipText("Format: YYYY-MM");
        payDateField.setToolTipText("Format: YYYY-MM-DD");
        
        // Make employee name label stand out
        employeeNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        employeeNameLabel.setForeground(new Color(70, 130, 180));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(employeeIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Employee:"), gbc);
        gbc.gridx = 1;
        formPanel.add(employeeNameLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Pay Period:"), gbc);
        gbc.gridx = 1;
        formPanel.add(payPeriodField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Base Pay:"), gbc);
        gbc.gridx = 1;
        formPanel.add(basePayField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Overtime:"), gbc);
        gbc.gridx = 1;
        formPanel.add(overtimeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Bonuses:"), gbc);
        gbc.gridx = 1;
        formPanel.add(bonusesField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Deductions:"), gbc);
        gbc.gridx = 1;
        formPanel.add(deductionsField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Pay Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(payDateField, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(salaryBreakdownButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        employeeIdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmployeeName();
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePayroll();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        salaryBreakdownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSalaryBreakdown();
            }
        });
    }
    
    private void populateFields() {
        Employee employee = employeeManager.getEmployee(payroll.getEmployeeId());
        if (employee != null) {
            employeeIdField.setText(employee.getFormattedEmployeeId());
        } else {
            employeeIdField.setText(employeeManager.getFormattedEmployeeId(payroll.getEmployeeId()));
        }
        payPeriodField.setText(payroll.getPayPeriod());
        basePayField.setText(String.valueOf(payroll.getBasePay()));
        overtimeField.setText(String.valueOf(payroll.getOvertime()));
        bonusesField.setText(String.valueOf(payroll.getBonuses()));
        deductionsField.setText(String.valueOf(payroll.getDeductions()));
        payDateField.setText(payroll.getPayDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // Disable employee ID field when editing
        employeeIdField.setEnabled(false);
        
        updateEmployeeName();
    }
    
    private void updateEmployeeName() {
        try {
            String employeeIdText = employeeIdField.getText().trim();
            if (employeeIdText.isEmpty()) {
                employeeNameLabel.setText("");
                return;
            }
            
            int employeeId;
            
            // Try to parse as formatted ID first (e.g., "EMP001" or "IT-202501-001")
            if (employeeIdText.toUpperCase().startsWith("EMP") || employeeIdText.matches("^[A-Z]{2,3}-\\d{6}-\\d{3}$")) {
                employeeId = employeeManager.parseEmployeeId(employeeIdText);
                if (employeeId == -1) {
                    employeeNameLabel.setText("Invalid ID format");
                    return;
                }
            } else {
                // Try to parse as numeric ID
                employeeId = Integer.parseInt(employeeIdText);
            }
            
            Employee employee = employeeManager.getEmployee(employeeId);
            
            if (employee != null && employee.isActive()) {
                employeeNameLabel.setText(employee.getFullName());
            } else {
                employeeNameLabel.setText("Employee not found or inactive");
            }
        } catch (NumberFormatException ex) {
            employeeNameLabel.setText("Invalid ID");
        }
    }
    
    private void savePayroll() {
        try {
            String employeeIdText = employeeIdField.getText().trim();
            String payPeriod = payPeriodField.getText().trim();
            String basePayText = basePayField.getText().trim();
            String overtimeText = overtimeField.getText().trim();
            String bonusesText = bonusesField.getText().trim();
            String deductionsText = deductionsField.getText().trim();
            String payDateText = payDateField.getText().trim();
            
            // Validate required fields
            if (employeeIdText.isEmpty() || payPeriod.isEmpty() || basePayText.isEmpty() || payDateText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse employee ID
            int employeeId;
            try {
                // Try to parse as formatted ID first (e.g., "EMP001" or "IT-202501-001")
                if (employeeIdText.toUpperCase().startsWith("EMP") || employeeIdText.matches("^[A-Z]{2,3}-\\d{6}-\\d{3}$")) {
                    employeeId = employeeManager.parseEmployeeId(employeeIdText);
                    if (employeeId == -1) {
                        JOptionPane.showMessageDialog(this, "Invalid employee ID format. Use format: IT-202501-001, EMP001, or just the number.", 
                                                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    // Try to parse as numeric ID
                    employeeId = Integer.parseInt(employeeIdText);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid employee ID format.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if employee exists
            Employee employee = employeeManager.getEmployee(employeeId);
            if (employee == null || !employee.isActive()) {
                JOptionPane.showMessageDialog(this, "Employee not found or inactive.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse monetary values
            double basePay, overtime, bonuses, deductions;
            try {
                basePay = Double.parseDouble(basePayText);
                overtime = overtimeText.isEmpty() ? 0.0 : Double.parseDouble(overtimeText);
                bonuses = bonusesText.isEmpty() ? 0.0 : Double.parseDouble(bonusesText);
                deductions = deductionsText.isEmpty() ? 0.0 : Double.parseDouble(deductionsText);
                
                if (basePay < 0 || overtime < 0 || bonuses < 0 || deductions < 0) {
                    JOptionPane.showMessageDialog(this, "All monetary values must be non-negative.", 
                                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid monetary value format.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse pay date
            LocalDate payDate;
            try {
                payDate = LocalDate.parse(payDateText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (payroll == null) {
                // Create new payroll
                Payroll newPayroll = payrollManager.createPayroll(employeeId, payPeriod, basePay, 
                                                                overtime, bonuses, deductions, payDate);
                JOptionPane.showMessageDialog(this, 
                    String.format("Payroll created successfully!\nGross Pay: $%.2f\nNet Pay: $%.2f", 
                                newPayroll.getGrossPay(), newPayroll.getNetPay()));
            } else {
                // Update existing payroll
                payrollManager.updatePayroll(payroll.getPayrollId(), payPeriod, basePay, 
                                           overtime, bonuses, deductions, payDate);
                JOptionPane.showMessageDialog(this, "Payroll updated successfully!");
            }
            
            confirmed = true;
            dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving payroll: " + ex.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    private void showSalaryBreakdown() {
        try {
            String employeeIdText = employeeIdField.getText().trim();
            if (employeeIdText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an employee ID first.", 
                                            "No Employee Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Find the employee
            Employee employee = null;
            if (employeeIdText.toUpperCase().startsWith("EMP") || employeeIdText.matches("^[A-Z]{2,3}-\\d{6}-\\d{3}$")) {
                employee = employeeManager.getEmployeeByFormattedId(employeeIdText);
            } else {
                try {
                    int empId = Integer.parseInt(employeeIdText);
                    employee = employeeManager.getEmployee(empId);
                } catch (NumberFormatException e) {
                    // Invalid ID format
                }
            }
            
            if (employee == null) {
                JOptionPane.showMessageDialog(this, "Employee not found.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get salary breakdown from PayrollManager's SalaryComponentManager
            if (payrollManager.getSalaryComponentManager() != null) {
                java.util.Map<String, Double> breakdown = payrollManager.getSalaryComponentManager()
                    .getSalaryBreakdown(employee.getEmployeeId(), employee.getBaseSalary());
                
                // Create detailed breakdown text
                StringBuilder sb = new StringBuilder();
                sb.append("Salary Breakdown for ").append(employee.getFullName()).append("\n\n");
                sb.append(String.format("Base Salary: $%.2f\n", employee.getBaseSalary()));
                
                double totalAllowances = 0;
                double totalDeductions = 0;
                double totalBonuses = 0;
                
                for (java.util.Map.Entry<String, Double> entry : breakdown.entrySet()) {
                    String key = entry.getKey();
                    Double value = entry.getValue();
                    
                    if (key.equals("baseSalary") || key.equals("totalAllowances") || 
                        key.equals("totalDeductions") || key.equals("totalBonuses")) {
                        continue; // Skip summary entries
                    }
                    
                    sb.append(String.format("%s: $%.2f\n", key, value));
                    
                    // Categorize the amount (this is a simplified approach)
                    if (key.toLowerCase().contains("allowance") || key.toLowerCase().contains("hra") || 
                        key.toLowerCase().contains("transport") || key.toLowerCase().contains("medical")) {
                        totalAllowances += value;
                    } else if (key.toLowerCase().contains("deduction") || key.toLowerCase().contains("tax") || 
                               key.toLowerCase().contains("pf") || key.toLowerCase().contains("esi")) {
                        totalDeductions += value;
                    } else if (key.toLowerCase().contains("bonus") || key.toLowerCase().contains("overtime")) {
                        totalBonuses += value;
                    }
                }
                
                sb.append("\n--- Summary ---\n");
                sb.append(String.format("Total Allowances: $%.2f\n", totalAllowances));
                sb.append(String.format("Total Bonuses: $%.2f\n", totalBonuses));
                sb.append(String.format("Gross Salary: $%.2f\n", employee.getBaseSalary() + totalAllowances + totalBonuses));
                sb.append(String.format("Total Deductions: $%.2f\n", totalDeductions));
                sb.append(String.format("Net Salary: $%.2f\n", employee.getBaseSalary() + totalAllowances + totalBonuses - totalDeductions));
                
                // Show in a dialog
                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
                
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                
                JOptionPane.showMessageDialog(this, scrollPane, "Salary Breakdown - " + employee.getFullName(), 
                                            JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Salary component system not available.", 
                                            "Feature Not Available", JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error showing salary breakdown: " + ex.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
