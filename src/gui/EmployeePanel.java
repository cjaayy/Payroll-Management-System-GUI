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
        String[] columnNames = {"ID", "Name", "Email", "Department", "Position", "Salary", "Hire Date"};
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
        
        // Search field
        searchField = new JTextField(20);
        
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
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
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
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Employee> employees = mainApp.getEmployeeManager().getActiveEmployees();
        
        for (Employee emp : employees) {
            Object[] rowData = {
                emp.getEmployeeId(),
                emp.getFullName(),
                emp.getEmail(),
                emp.getDepartment(),
                emp.getPosition(),
                String.format("$%.2f", emp.getBaseSalary()),
                emp.getHireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
        
        EmployeeDialog dialog = new EmployeeDialog(SwingUtilities.getWindowAncestor(this), 
                                                  mainApp.getEmployeeManager(), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshTable();
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
        
        int employeeId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Employee employee = mainApp.getEmployeeManager().getEmployee(employeeId);
        
        if (employee != null) {
            EmployeeDialog dialog = new EmployeeDialog(SwingUtilities.getWindowAncestor(this), 
                                                      mainApp.getEmployeeManager(), employee);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                refreshTable();
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
        
        int employeeId = (Integer) tableModel.getValueAt(selectedRow, 0);
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
            if (emp.isActive()) {
                Object[] rowData = {
                    emp.getEmployeeId(),
                    emp.getFullName(),
                    emp.getEmail(),
                    emp.getDepartment(),
                    emp.getPosition(),
                    String.format("$%.2f", emp.getBaseSalary()),
                    emp.getHireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                };
                tableModel.addRow(rowData);
            }
        }
    }
}
