package gui;

import models.Employee;
import managers.EmployeeManager;
import managers.DepartmentPositionManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Dialog for adding/editing employees
 */
public class EmployeeDialog extends JDialog {
    private EmployeeManager employeeManager;
    private Employee employee;
    private boolean confirmed = false;
    
    private JLabel employeeIdLabel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> departmentComboBox;
    private JComboBox<String> positionComboBox;
    private JTextField salaryField;
    private JTextField hireDateField;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    public EmployeeDialog(Window parent, EmployeeManager employeeManager, Employee employee) {
        super(parent, employee == null ? "Add Employee" : "Edit Employee", ModalityType.APPLICATION_MODAL);
        this.employeeManager = employeeManager;
        this.employee = employee;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        if (employee != null) {
            populateFields();
        }
        
        setSize(400, 400);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        employeeIdLabel = new JLabel();
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        
        // Initialize department combo box with all departments
        departmentComboBox = new JComboBox<>(DepartmentPositionManager.getAllDepartments());
        departmentComboBox.setEditable(false);
        departmentComboBox.addActionListener(e -> updatePositionComboBox());
        
        // Initialize position combo box (will be populated based on department selection)
        positionComboBox = new JComboBox<>(new String[0]);
        positionComboBox.setEditable(false);
        
        salaryField = new JTextField(20);
        hireDateField = new JTextField(20);
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        // Style buttons
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(128, 128, 128));
        cancelButton.setForeground(Color.WHITE);
        
        // Add placeholder text for date field
        hireDateField.setToolTipText("Format: YYYY-MM-DD");
        
        // Add tooltip for salary field
        salaryField.setToolTipText("Maximum salary: $99,999,999.99");
        
        // Set employee ID label
        if (employee != null) {
            String displayId = employee.getFormattedEmployeeId();
            employeeIdLabel.setText("Employee ID: " + displayId);
            employeeIdLabel.setFont(new Font("Arial", Font.BOLD, 12));
            employeeIdLabel.setForeground(new Color(70, 130, 180));
        } else {
            employeeIdLabel.setText("Employee ID: (Auto-generated based on department and hire date)");
            employeeIdLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            employeeIdLabel.setForeground(new Color(100, 100, 100));
        }
    }
    
    /**
     * Update the position combo box based on selected department
     */
    private void updatePositionComboBox() {
        String selectedDepartment = (String) departmentComboBox.getSelectedItem();
        if (selectedDepartment != null) {
            String[] positions = DepartmentPositionManager.getPositionsForDepartment(selectedDepartment);
            positionComboBox.removeAllItems();
            for (String position : positions) {
                positionComboBox.addItem(position);
            }
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Employee ID - spans across both columns
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(employeeIdLabel, gbc);
        
        // Reset gridwidth for other components
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        formPanel.add(departmentComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        formPanel.add(positionComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Base Salary:"), gbc);
        gbc.gridx = 1;
        formPanel.add(salaryField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Hire Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(hireDateField, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveEmployee();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void populateFields() {
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        emailField.setText(employee.getEmail());
        phoneField.setText(employee.getPhone() == null ? "" : employee.getPhone());
        
        // Set department and update positions
        departmentComboBox.setSelectedItem(employee.getDepartment());
        updatePositionComboBox();
        positionComboBox.setSelectedItem(employee.getPosition());
        
        salaryField.setText(String.valueOf(employee.getBaseSalary()));
        hireDateField.setText(employee.getHireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
    
    private void saveEmployee() {
        try {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String department = (String) departmentComboBox.getSelectedItem();
            String position = (String) positionComboBox.getSelectedItem();
            String salaryText = salaryField.getText().trim();
            String hireDateText = hireDateField.getText().trim();
            
            // Validate required fields
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
                department == null || department.isEmpty() || position == null || position.isEmpty() || 
                salaryText.isEmpty() || hireDateText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.\nNote: Phone is optional.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate email format
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check for duplicate email (for updates, exclude current employee)
            int excludeId = (employee != null) ? employee.getEmployeeId() : -1;
            if (!employeeManager.isEmailAvailable(email, excludeId)) {
                JOptionPane.showMessageDialog(this, "This email address is already in use by another employee.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate phone format (optional field)
            if (!phone.isEmpty() && !isValidPhone(phone)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid phone number (e.g., 123-456-7890).", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse salary
            double salary;
            try {
                salary = Double.parseDouble(salaryText);
                System.out.println("Parsed salary: " + salary);
                if (salary < 0) {
                    JOptionPane.showMessageDialog(this, "Salary must be a positive number.", 
                                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Check if salary exceeds database limit (DECIMAL(10,2) = max 99,999,999.99)
                if (salary > 99999999.99) {
                    JOptionPane.showMessageDialog(this, "Salary cannot exceed $99,999,999.99.", 
                                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid salary format. Please enter a valid number.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse hire date
            LocalDate hireDate;
            try {
                hireDate = LocalDate.parse(hireDateText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (employee == null) {
                // Add new employee
                Employee newEmployee = employeeManager.createEmployee(firstName, lastName, email, 
                    phone.isEmpty() ? null : phone, department, position, salary, hireDate);
                if (newEmployee != null) {
                    JOptionPane.showMessageDialog(this, "Employee added successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add employee.", 
                                                "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Update existing employee
                employee.setPhone(phone.isEmpty() ? null : phone);
                boolean updateSuccess = employeeManager.updateEmployee(employee.getEmployeeId(), firstName, lastName, 
                                             email, phone.isEmpty() ? null : phone, department, position, salary, hireDate);
                if (updateSuccess) {
                    JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update employee. Please check for duplicate email addresses.", 
                                                "Update Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            confirmed = true;
            dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving employee: " + ex.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailPattern);
    }
    
    private boolean isValidPhone(String phone) {
        // Allow various phone formats: 123-456-7890, (123) 456-7890, 123.456.7890, 1234567890
        String phonePattern = "^[\\+]?[1-9]?[0-9]{0,2}[-\\s\\.]?\\(?[0-9]{3}\\)?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4}$";
        return phone.matches(phonePattern);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
