package gui;

import models.Employee;
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
        
        viewButton = new JButton("View Details");
        refreshButton = new JButton("Refresh");
        toggleStatusButton = new JButton("Toggle Status");
        
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
                emp.isActive() ? "Active" : "Inactive"
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
                emp.isActive() ? "Active" : "Inactive"
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
        StringBuilder details = new StringBuilder();
        details.append("Employee Details\n");
        details.append("=================\n\n");
        details.append("ID: ").append(employee.getFormattedEmployeeId()).append("\n");
        details.append("Name: ").append(employee.getFullName()).append("\n");
        details.append("Email: ").append(employee.getEmail()).append("\n");
        details.append("Phone: ").append(employee.getPhone() == null ? "N/A" : employee.getPhone()).append("\n");
        details.append("Department: ").append(employee.getDepartment()).append("\n");
        details.append("Position: ").append(employee.getPosition()).append("\n");
        details.append("Base Salary: $").append(String.format("%.2f", employee.getBaseSalary())).append("\n");
        details.append("Hire Date: ").append(employee.getHireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n");
        details.append("Status: ").append(employee.isActive() ? "Active" : "Inactive").append("\n");
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Employee Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void toggleEmployeeStatus() {
        if (!mainApp.getAuthManager().hasAdminRole()) {
            JOptionPane.showMessageDialog(this, "Access denied. Admin privileges required.", 
                                        "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to toggle status.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String formattedEmployeeId = (String) tableModel.getValueAt(selectedRow, 0);
        int employeeId = mainApp.getEmployeeManager().parseEmployeeId(formattedEmployeeId);
        String employeeName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 8);
        
        String action = currentStatus.equals("Active") ? "deactivate" : "activate";
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to " + action + " employee: " + employeeName + "?", 
                "Confirm Status Change", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Employee employee = mainApp.getEmployeeManager().getEmployee(employeeId);
            if (employee != null) {
                employee.setActive(!employee.isActive());
                if (mainApp.getEmployeeManager().updateEmployee(employee.getEmployeeId(), 
                        employee.getFirstName(), employee.getLastName(), employee.getEmail(), 
                        employee.getPhone(), employee.getDepartment(), employee.getPosition(), employee.getBaseSalary(), employee.getHireDate())) {
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
