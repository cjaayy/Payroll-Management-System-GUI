package gui;

import models.Employee;
import models.EmployeeDocument;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Employee Management Panel
 */
public class EmployeePanel extends JPanel {
    private PayrollManagementSystemGUI mainApp;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, searchButton, backButton;
    private JButton viewButton, refreshButton, toggleStatusButton;
    private JTextField searchField;
    
    public EmployeePanel(PayrollManagementSystemGUI mainApp) {
        this.mainApp = mainApp;
        initializeComponents();
        setupLayout();
        setupEventListeners();
        refreshTable();
    }
    
    private void initializeComponents() {
        setBackground(new Color(240, 248, 255));
        
        // Table setup
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Department", "Position", "Salary", "Hire Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getTableHeader().setReorderingAllowed(false);
        
        // Buttons
        addButton = new JButton("Add Employee");
        editButton = new JButton("Edit Employee");
        deleteButton = new JButton("Delete Employee");
        searchButton = new JButton("Search");
        backButton = new JButton("Back to Main Menu");
        
        viewButton = new JButton("View Full Details");
        viewButton.setToolTipText("View comprehensive employee information (read-only)");
        refreshButton = new JButton("Refresh");
        toggleStatusButton = new JButton("Change Status");
        
        // Search field
        searchField = new JTextField(20);
        searchField.setToolTipText("Search by name, email, phone, department, position, or ID");
        
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
        
        searchButton.setFont(buttonFont);
        searchButton.setBackground(buttonColor);
        searchButton.setForeground(Color.WHITE);
        
        backButton.setFont(buttonFont);
        backButton.setBackground(new Color(128, 128, 128));
        backButton.setForeground(Color.WHITE);
        
        viewButton.setFont(buttonFont);
        viewButton.setBackground(new Color(34, 139, 34));
        viewButton.setForeground(Color.WHITE);
        
        refreshButton.setFont(buttonFont);
        refreshButton.setBackground(new Color(255, 140, 0));
        refreshButton.setForeground(Color.WHITE);
        
        toggleStatusButton.setFont(buttonFont);
        toggleStatusButton.setBackground(new Color(138, 43, 226));
        toggleStatusButton.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Employee Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table in center
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Search row
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(searchField, gbc);
        gbc.gridx = 2;
        controlPanel.add(searchButton, gbc);
        gbc.gridx = 3;
        controlPanel.add(refreshButton, gbc);
        
        // Button row
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(addButton, gbc);
        gbc.gridx = 1;
        controlPanel.add(editButton, gbc);
        gbc.gridx = 2;
        controlPanel.add(deleteButton, gbc);
        gbc.gridx = 3;
        controlPanel.add(viewButton, gbc);
        gbc.gridx = 4;
        controlPanel.add(toggleStatusButton, gbc);
        gbc.gridx = 5;
        controlPanel.add(backButton, gbc);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddEmployeeDialog();
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedEmployee();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedEmployee();
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchEmployees();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showMainMenuPanel();
            }
        });
        
        // Allow search with Enter key
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchEmployees();
            }
        });
        
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewSelectedEmployee();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });
        
        toggleStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleEmployeeStatus();
            }
        });
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Employee> employees = mainApp.getEmployeeManager().getAllEmployees();
        
        for (Employee emp : employees) {
            Object[] rowData = {
                emp.getFormattedEmployeeId(), // Use the Employee model's formatted ID method
                emp.getFullName(),
                emp.getEmail(),
                emp.getPhone() == null ? "" : emp.getPhone(),
                emp.getDepartment(),
                emp.getPosition(),
                String.format("$%.2f", emp.getBaseSalary()),
                emp.getHireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                emp.getEmploymentStatus() != null ? emp.getEmploymentStatus() : (emp.isActive() ? "ACTIVE" : "INACTIVE")
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void showAddEmployeeDialog() {
        if (!mainApp.getAuthManager().hasAdminRole()) {
            JOptionPane.showMessageDialog(this, "Access denied. Admin privileges required.", 
                                        "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            EmployeeDialog dialog = new EmployeeDialog(SwingUtilities.getWindowAncestor(this), 
                                                      mainApp.getEmployeeManager(), null);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Employee newEmployee = dialog.getEmployee();
                if (mainApp.getEmployeeManager().saveEmployeeWithContactInfo(newEmployee)) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Employee added successfully!", 
                                                "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add employee!", 
                                                "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            // Fall back to basic dialog if enhanced dialog fails
            System.err.println("Enhanced dialog not available, using basic dialog: " + e.getMessage());
            EmployeeDialog dialog = new EmployeeDialog(SwingUtilities.getWindowAncestor(this), 
                                                      mainApp.getEmployeeManager(), null);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                refreshTable();
            }
        }
    }
    
    private void editSelectedEmployee() {
        if (!mainApp.getAuthManager().hasAdminRole()) {
            JOptionPane.showMessageDialog(this, "Access denied. Admin privileges required.", 
                                        "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String formattedEmployeeId = (String) tableModel.getValueAt(selectedRow, 0);
        int employeeId = mainApp.getEmployeeManager().parseEmployeeId(formattedEmployeeId);
        Employee employee = mainApp.getEmployeeManager().getEmployee(employeeId);
        
        if (employee != null) {
            // Load contact info and documents for the employee
            mainApp.getEmployeeManager().loadEmployeeContactInfo(employee);
            
            try {
                EmployeeDialog dialog = new EmployeeDialog(SwingUtilities.getWindowAncestor(this), 
                                                          mainApp.getEmployeeManager(), employee);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    Employee updatedEmployee = dialog.getEmployee();
                    if (mainApp.getEmployeeManager().saveEmployeeWithContactInfo(updatedEmployee)) {
                        refreshTable();
                        JOptionPane.showMessageDialog(this, "Employee updated successfully!", 
                                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update employee!", 
                                                    "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                // Fall back to basic dialog if enhanced dialog fails
                System.err.println("Enhanced dialog not available, using basic dialog: " + e.getMessage());
                EmployeeDialog dialog = new EmployeeDialog(SwingUtilities.getWindowAncestor(this), 
                                                          mainApp.getEmployeeManager(), employee);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    refreshTable();
                }
            }
        }
    }
    
    private void deleteSelectedEmployee() {
        if (!mainApp.getAuthManager().hasAdminRole()) {
            JOptionPane.showMessageDialog(this, "Access denied. Admin privileges required.", 
                                        "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String formattedEmployeeId = (String) tableModel.getValueAt(selectedRow, 0);
        int employeeId = mainApp.getEmployeeManager().parseEmployeeId(formattedEmployeeId);
        String employeeName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete employee: " + employeeName + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (mainApp.getEmployeeManager().deleteEmployee(employeeId)) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete employee.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void searchEmployees() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }
        
        List<Employee> results = mainApp.getEmployeeManager().searchEmployees(keyword);
        
        tableModel.setRowCount(0);
        for (Employee emp : results) {
            Object[] rowData = {
                emp.getFormattedEmployeeId(), // Use the Employee model's formatted ID method
                emp.getFullName(),
                emp.getEmail(),
                emp.getPhone() == null ? "" : emp.getPhone(),
                emp.getDepartment(),
                emp.getPosition(),
                String.format("$%.2f", emp.getBaseSalary()),
                emp.getHireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                emp.getEmploymentStatus() != null ? emp.getEmploymentStatus() : (emp.isActive() ? "ACTIVE" : "INACTIVE")
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void viewSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to view.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String formattedEmployeeId = (String) tableModel.getValueAt(selectedRow, 0);
        int employeeId = mainApp.getEmployeeManager().parseEmployeeId(formattedEmployeeId);
        Employee employee = mainApp.getEmployeeManager().getEmployee(employeeId);
        
        if (employee != null) {
            showEmployeeDetails(employee);
        }
    }
    
    private void showEmployeeDetails(Employee employee) {
        // Load complete employee information including contact info and documents
        mainApp.getEmployeeManager().loadEmployeeContactInfo(employee);
        
        // Create a tabbed dialog for comprehensive employee details
        JDialog detailsDialog = new JDialog();
        detailsDialog.setTitle("Employee Details - " + employee.getFullName());
        detailsDialog.setModal(true);
        detailsDialog.setSize(800, 600);
        detailsDialog.setLocationRelativeTo(this);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Basic Information Tab
        tabbedPane.addTab("Basic Info", createBasicInfoPanel(employee));
        
        // Contact Information Tab
        tabbedPane.addTab("Contact Info", createContactInfoPanel(employee));
        
        // Employment Details Tab
        tabbedPane.addTab("Employment", createEmploymentDetailsPanel(employee));
        
        // Bank & Payment Tab
        tabbedPane.addTab("Bank & Payment", createBankPaymentPanel(employee));
        
        // Documents Tab
        tabbedPane.addTab("Documents", createDocumentsPanel(employee));
        
        // Add close button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> detailsDialog.dispose());
        buttonPanel.add(closeButton);
        
        detailsDialog.setLayout(new BorderLayout());
        detailsDialog.add(tabbedPane, BorderLayout.CENTER);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        detailsDialog.setVisible(true);
    }
    
    private JPanel createBasicInfoPanel(Employee employee) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Employee ID
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getFormattedEmployeeId()), gbc);
        
        // Comprehensive ID
        if (employee.getComprehensiveEmployeeId() != null) {
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Comprehensive ID:"), gbc);
            gbc.gridx = 1;
            panel.add(new JLabel(employee.getComprehensiveEmployeeId()), gbc);
        }
        
        // Name
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getFirstName()), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getLastName()), gbc);
        
        // Email
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getEmail()), gbc);
        
        // Phone
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getPhone() != null ? employee.getPhone() : "N/A"), gbc);
        
        // Department & Position
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getDepartment()), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getPosition()), gbc);
        
        // Job Title & Manager
        if (employee.getJobTitle() != null) {
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Job Title:"), gbc);
            gbc.gridx = 1;
            panel.add(new JLabel(employee.getJobTitle()), gbc);
        }
        
        if (employee.getManager() != null) {
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Manager:"), gbc);
            gbc.gridx = 1;
            panel.add(new JLabel(employee.getManager()), gbc);
        }
        
        // Salary
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Base Salary:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.format("$%.2f", employee.getBaseSalary())), gbc);
        
        // Hire Date
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Hire Date:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getHireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))), gbc);
        
        return panel;
    }
    
    private JPanel createContactInfoPanel(Employee employee) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Personal Email
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Personal Email:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getPersonalEmail() != null ? employee.getPersonalEmail() : "N/A"), gbc);
        
        // Work Phone
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Work Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getWorkPhone() != null ? employee.getWorkPhone() : "N/A"), gbc);
        
        // Emergency Contact
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Emergency Contact:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getEmergencyContact() != null ? employee.getEmergencyContact() : "N/A"), gbc);
        
        // Emergency Phone
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Emergency Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getEmergencyPhone() != null ? employee.getEmergencyPhone() : "N/A"), gbc);
        
        // Address
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Street Address:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getStreetAddress() != null ? employee.getStreetAddress() : "N/A"), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Barangay:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getBarangay() != null ? employee.getBarangay() : "N/A"), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("City:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getCity() != null ? employee.getCity() : "N/A"), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Province/State:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getProvinceState() != null ? employee.getProvinceState() : "N/A"), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Country:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getCountry() != null ? employee.getCountry() : "N/A"), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("ZIP Code:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getZipCode() != null ? employee.getZipCode() : "N/A"), gbc);
        
        // Personal Information
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Birth Date:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getBirthDate() != null ? 
                           employee.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A"), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Social Security Number:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getSocialSecurityNumber() != null ? employee.getSocialSecurityNumber() : "N/A"), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Nationality:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getNationality() != null ? employee.getNationality() : "N/A"), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Marital Status:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getMaritalStatus() != null ? employee.getMaritalStatus() : "N/A"), gbc);
        
        return panel;
    }
    
    private JPanel createEmploymentDetailsPanel(Employee employee) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Employment Status
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Employment Status:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getEmploymentStatus() != null ? employee.getEmploymentStatus() : "N/A"), gbc);
        
        // Active Status
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Active Status:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.isActive() ? "Active" : "Inactive"), gbc);
        
        // Joining Date
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Joining Date:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getJoiningDate() != null ? 
                           employee.getJoiningDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A"), gbc);
        
        // Probation End Date
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Probation End Date:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getProbationEndDate() != null ? 
                           employee.getProbationEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A"), gbc);
        
        // Exit Date
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Exit Date:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getExitDate() != null ? 
                           employee.getExitDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A"), gbc);
        
        // Exit Reason
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Exit Reason:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getExitReason() != null ? employee.getExitReason() : "N/A"), gbc);
        
        return panel;
    }
    
    private JPanel createBankPaymentPanel(Employee employee) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Bank Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Bank Name:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getBankName() != null ? employee.getBankName() : "N/A"), gbc);
        
        // Account Number
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getAccountNumber() != null ? employee.getAccountNumber() : "N/A"), gbc);
        
        // Account Holder Name
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Account Holder Name:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getAccountHolderName() != null ? employee.getAccountHolderName() : "N/A"), gbc);
        
        // Bank Branch
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Bank Branch:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getBankBranch() != null ? employee.getBankBranch() : "N/A"), gbc);
        
        // Routing Number
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Routing Number:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getRoutingNumber() != null ? employee.getRoutingNumber() : "N/A"), gbc);
        
        // Payment Method
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getPaymentMethod() != null ? employee.getPaymentMethod() : "N/A"), gbc);
        
        // Payment Frequency
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Payment Frequency:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.getPaymentFrequency() != null ? employee.getPaymentFrequency() : "N/A"), gbc);
        
        return panel;
    }
    
    private JPanel createDocumentsPanel(Employee employee) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table for documents
        String[] columnNames = {"Document Type", "File Name", "File Size", "Upload Date", "Description"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable documentsTable = new JTable(tableModel);
        documentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Populate documents table
        if (employee.getDocuments() != null) {
            for (EmployeeDocument doc : employee.getDocuments()) {
                Object[] rowData = {
                    doc.getDocumentType(),
                    doc.getFileName(),
                    formatFileSize(doc.getFileSize()),
                    doc.getUploadDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    doc.getDescription() != null ? doc.getDescription() : ""
                };
                tableModel.addRow(rowData);
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(documentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add document action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton viewDocButton = new JButton("View Details");
        viewDocButton.addActionListener(e -> {
            int selectedRow = documentsTable.getSelectedRow();
            if (selectedRow >= 0 && employee.getDocuments() != null && selectedRow < employee.getDocuments().size()) {
                showDocumentDetails(employee.getDocuments().get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a document to view.", 
                                            "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton saveDocButton = new JButton("Save Document");
        saveDocButton.addActionListener(e -> {
            int selectedRow = documentsTable.getSelectedRow();
            if (selectedRow >= 0 && employee.getDocuments() != null && selectedRow < employee.getDocuments().size()) {
                saveDocumentFile(employee.getDocuments().get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a document to save.", 
                                            "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton previewDocButton = new JButton("Preview Document");
        previewDocButton.addActionListener(e -> {
            int selectedRow = documentsTable.getSelectedRow();
            if (selectedRow >= 0 && employee.getDocuments() != null && selectedRow < employee.getDocuments().size()) {
                previewDocument(employee.getDocuments().get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a document to preview.", 
                                            "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        buttonPanel.add(viewDocButton);
        buttonPanel.add(saveDocButton);
        buttonPanel.add(previewDocButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add double-click listener for quick preview
        documentsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = documentsTable.getSelectedRow();
                    if (selectedRow >= 0 && employee.getDocuments() != null && selectedRow < employee.getDocuments().size()) {
                        previewDocument(employee.getDocuments().get(selectedRow));
                    }
                }
            }
        });
        
        return panel;
    }
    
    private void showDocumentDetails(EmployeeDocument document) {
        StringBuilder details = new StringBuilder();
        details.append("Document Details\n");
        details.append("================\n\n");
        details.append("Type: ").append(document.getDocumentType()).append("\n");
        details.append("File Name: ").append(document.getFileName()).append("\n");
        details.append("File Size: ").append(formatFileSize(document.getFileSize())).append("\n");
        details.append("MIME Type: ").append(document.getMimeType()).append("\n");
        details.append("Upload Date: ").append(document.getUploadDate().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        details.append("Uploaded By: ").append(document.getUploadedBy()).append("\n");
        details.append("Description: ").append(document.getDescription() != null ? 
            document.getDescription() : "No description").append("\n");
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Document Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        else if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        else return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }
    
    private void toggleEmployeeStatus() {
        if (!mainApp.getAuthManager().hasAdminRole()) {
            JOptionPane.showMessageDialog(this, "Access denied. Admin privileges required.", 
                                        "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to change status.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String formattedEmployeeId = (String) tableModel.getValueAt(selectedRow, 0);
        int employeeId = mainApp.getEmployeeManager().parseEmployeeId(formattedEmployeeId);
        String employeeName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 8);
        
        // Show dialog to select new employment status
        String[] statusOptions = {"ACTIVE", "INACTIVE", "RESIGNED", "TERMINATED", "ON_LEAVE"};
        String newStatus = (String) JOptionPane.showInputDialog(this, 
                "Select new employment status for " + employeeName + ":\n" +
                "Current Status: " + currentStatus, 
                "Change Employment Status", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                statusOptions, 
                currentStatus);
        
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to change " + employeeName + "'s status from " + 
                    currentStatus + " to " + newStatus + "?", 
                    "Confirm Status Change", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                Employee employee = mainApp.getEmployeeManager().getEmployee(employeeId);
                if (employee != null) {
                    employee.setEmploymentStatus(newStatus);
                    
                    // Also update the isActive field based on the employment status
                    boolean isActive = "ACTIVE".equals(newStatus) || "ON_LEAVE".equals(newStatus);
                    employee.setActive(isActive);
                    
                    // If setting to inactive status, ask for exit date and reason
                    if ("RESIGNED".equals(newStatus) || "TERMINATED".equals(newStatus)) {
                        String exitDate = JOptionPane.showInputDialog(this, 
                                "Enter exit date (YYYY-MM-DD):", 
                                java.time.LocalDate.now().toString());
                        String exitReason = JOptionPane.showInputDialog(this, 
                                "Enter exit reason (optional):", "");
                        
                        if (exitDate != null && !exitDate.trim().isEmpty()) {
                            try {
                                employee.setExitDate(java.time.LocalDate.parse(exitDate));
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD");
                            }
                        }
                        
                        if (exitReason != null && !exitReason.trim().isEmpty()) {
                            employee.setExitReason(exitReason);
                        }
                    }
                    
                    // Save the updated employee
                    if (mainApp.getEmployeeManager().saveEmployeeWithContactInfo(employee)) {
                        JOptionPane.showMessageDialog(this, "Employee status updated successfully.");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update employee status.", 
                                                    "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    
    private void saveDocumentFile(EmployeeDocument document) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File(document.getFileName()));
            fileChooser.setDialogTitle("Save Document As");
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File outputFile = fileChooser.getSelectedFile();
                java.nio.file.Files.write(outputFile.toPath(), document.getFileData());
                
                JOptionPane.showMessageDialog(this, 
                    "Document saved successfully to:\n" + outputFile.getAbsolutePath(), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving document: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void previewDocument(EmployeeDocument document) {
        try {
            String fileName = document.getFileName().toLowerCase();
            String mimeType = document.getMimeType();
            
            // Check if it's an image
            if (isImageFile(fileName, mimeType)) {
                showImagePreview(document);
            } else {
                // For non-image files, try to open with system default application
                openWithSystemApplication(document);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error previewing document: " + e.getMessage(), 
                "Preview Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private boolean isImageFile(String fileName, String mimeType) {
        // Check by file extension
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
            fileName.endsWith(".png") || fileName.endsWith(".gif") || 
            fileName.endsWith(".bmp") || fileName.endsWith(".webp")) {
            return true;
        }
        
        // Check by MIME type
        if (mimeType != null && mimeType.startsWith("image/")) {
            return true;
        }
        
        return false;
    }
    
    private void showImagePreview(EmployeeDocument document) {
        try {
            // Create image from byte array
            java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(
                new java.io.ByteArrayInputStream(document.getFileData()));
            
            if (image != null) {
                // Scale image if too large
                int maxWidth = 800;
                int maxHeight = 600;
                
                int originalWidth = image.getWidth();
                int originalHeight = image.getHeight();
                
                double scaleX = (double) maxWidth / originalWidth;
                double scaleY = (double) maxHeight / originalHeight;
                double scale = Math.min(scaleX, scaleY);
                
                int scaledWidth = (int) (originalWidth * scale);
                int scaledHeight = (int) (originalHeight * scale);
                
                if (scale < 1.0) {
                    java.awt.Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, 
                                                                        java.awt.Image.SCALE_SMOOTH);
                    java.awt.image.BufferedImage bufferedScaledImage = new java.awt.image.BufferedImage(
                        scaledWidth, scaledHeight, java.awt.image.BufferedImage.TYPE_INT_RGB);
                    java.awt.Graphics2D g2d = bufferedScaledImage.createGraphics();
                    g2d.drawImage(scaledImage, 0, 0, null);
                    g2d.dispose();
                    image = bufferedScaledImage;
                }
                
                // Create preview dialog
                JDialog imageDialog = new JDialog();
                imageDialog.setTitle("Image Preview - " + document.getFileName());
                imageDialog.setModal(true);
                
                JLabel imageLabel = new JLabel(new ImageIcon(image));
                JScrollPane scrollPane = new JScrollPane(imageLabel);
                scrollPane.setPreferredSize(new Dimension(
                    Math.min(scaledWidth + 50, 850), 
                    Math.min(scaledHeight + 100, 650)));
                
                // Add image info panel
                JPanel infoPanel = new JPanel(new FlowLayout());
                infoPanel.add(new JLabel("File: " + document.getFileName()));
                infoPanel.add(new JLabel(" | Size: " + formatFileSize(document.getFileSize())));
                infoPanel.add(new JLabel(" | Dimensions: " + originalWidth + "x" + originalHeight));
                
                // Add buttons
                JPanel buttonPanel = new JPanel(new FlowLayout());
                JButton saveButton = new JButton("Save Image");
                saveButton.addActionListener(e -> {
                    imageDialog.dispose();
                    saveDocumentFile(document);
                });
                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(e -> imageDialog.dispose());
                
                buttonPanel.add(saveButton);
                buttonPanel.add(closeButton);
                
                imageDialog.setLayout(new BorderLayout());
                imageDialog.add(scrollPane, BorderLayout.CENTER);
                imageDialog.add(infoPanel, BorderLayout.NORTH);
                imageDialog.add(buttonPanel, BorderLayout.SOUTH);
                
                imageDialog.pack();
                imageDialog.setLocationRelativeTo(this);
                imageDialog.setVisible(true);
                
            } else {
                // If image can't be loaded, try system default
                openWithSystemApplication(document);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Could not display image preview. Error: " + e.getMessage() + 
                "\nWould you like to save the file instead?", 
                "Image Preview Error", JOptionPane.WARNING_MESSAGE);
            saveDocumentFile(document);
        }
    }
    
    private void openWithSystemApplication(EmployeeDocument document) {
        try {
            // Create a temporary file for preview
            String fileName = document.getFileName();
            String extension = "";
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = fileName.substring(dotIndex);
            }
            
            // Create temp file
            java.io.File tempFile = java.io.File.createTempFile("preview_", extension);
            tempFile.deleteOnExit(); // Clean up on exit
            
            // Write document data to temp file
            java.nio.file.Files.write(tempFile.toPath(), document.getFileData());
            
            // Try to open the file with the default system application
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                    desktop.open(tempFile);
                    JOptionPane.showMessageDialog(this, 
                        "Document opened in default application.\n" +
                        "File: " + fileName + "\n" +
                        "Note: This is a temporary preview file.", 
                        "Preview Opened", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Fallback: offer to save the file
                    int option = JOptionPane.showConfirmDialog(this, 
                        "Cannot open file for preview on this system.\n" +
                        "Would you like to save the file to view it manually?", 
                        "Preview Not Available", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        saveDocumentFile(document);
                    }
                }
            } else {
                // Desktop not supported, offer to save
                int option = JOptionPane.showConfirmDialog(this, 
                    "File preview not supported on this system.\n" +
                    "Would you like to save the file to view it manually?", 
                    "Preview Not Available", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    saveDocumentFile(document);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error opening document: " + e.getMessage() + "\n" +
                "You can try saving the file instead.", 
                "Preview Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
