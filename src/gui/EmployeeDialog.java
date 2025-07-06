package gui;

import models.Employee;
import managers.EmployeeManager;
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
    
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField departmentField;
    private JTextField positionField;
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
        
        setSize(400, 350);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        emailField = new JTextField(20);
        departmentField = new JTextField(20);
        positionField = new JTextField(20);
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
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        formPanel.add(departmentField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        formPanel.add(positionField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Base Salary:"), gbc);
        gbc.gridx = 1;
        formPanel.add(salaryField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
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
        departmentField.setText(employee.getDepartment());
        positionField.setText(employee.getPosition());
        salaryField.setText(String.valueOf(employee.getBaseSalary()));
        hireDateField.setText(employee.getHireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
    
    private void saveEmployee() {
        try {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String department = departmentField.getText().trim();
            String position = positionField.getText().trim();
            String salaryText = salaryField.getText().trim();
            String hireDateText = hireDateField.getText().trim();
            
            // Validate required fields
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
                department.isEmpty() || position.isEmpty() || salaryText.isEmpty() || hireDateText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse salary
            double salary;
            try {
                salary = Double.parseDouble(salaryText);
                if (salary < 0) {
                    JOptionPane.showMessageDialog(this, "Salary must be a positive number.", 
                                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid salary format.", 
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
                employeeManager.createEmployee(firstName, lastName, email, department, position, salary, hireDate);
                JOptionPane.showMessageDialog(this, "Employee added successfully!");
            } else {
                // Update existing employee
                employeeManager.updateEmployee(employee.getEmployeeId(), firstName, lastName, 
                                             email, department, position, salary);
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            }
            
            confirmed = true;
            dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving employee: " + ex.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
