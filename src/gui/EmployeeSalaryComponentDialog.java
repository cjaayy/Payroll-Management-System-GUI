package gui;

import models.Employee;
import models.SalaryComponent;
import models.EmployeeSalaryComponent;
import managers.SalaryComponentManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog for managing employee salary components
 */
public class EmployeeSalaryComponentDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private transient SalaryComponentManager salaryComponentManager;
    private transient Employee employee;
    private boolean confirmed = false;
    
    private JTable componentsTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, removeButton, closeButton;
    private JLabel employeeInfoLabel;
    private JLabel salaryBreakdownLabel;
    
    @SuppressWarnings("this-escape")
    public EmployeeSalaryComponentDialog(Window parent, SalaryComponentManager manager, Employee employee) {
        super(parent, "Manage Salary Components - " + employee.getFullName(), ModalityType.APPLICATION_MODAL);
        this.salaryComponentManager = manager;
        this.employee = employee;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        refreshTable();
        updateSalaryBreakdown();
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        // Employee info
        employeeInfoLabel = new JLabel();
        employeeInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        employeeInfoLabel.setForeground(new Color(70, 130, 180));
        
        // Salary breakdown
        salaryBreakdownLabel = new JLabel();
        salaryBreakdownLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        salaryBreakdownLabel.setVerticalAlignment(SwingConstants.TOP);
        
        // Table setup
        String[] columnNames = {"Component", "Type", "Amount", "Is %", "Effective Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        componentsTable = new JTable(tableModel);
        componentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        componentsTable.getTableHeader().setReorderingAllowed(false);
        
        // Buttons
        addButton = new JButton("Add Component");
        editButton = new JButton("Edit Component");
        removeButton = new JButton("Remove Component");
        closeButton = new JButton("Close");
        
        // Style buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        Color buttonColor = new Color(70, 130, 180);
        
        addButton.setFont(buttonFont);
        addButton.setBackground(buttonColor);
        addButton.setForeground(Color.WHITE);
        
        editButton.setFont(buttonFont);
        editButton.setBackground(buttonColor);
        editButton.setForeground(Color.WHITE);
        
        removeButton.setFont(buttonFont);
        removeButton.setBackground(new Color(220, 20, 60));
        removeButton.setForeground(Color.WHITE);
        
        closeButton.setFont(buttonFont);
        closeButton.setBackground(new Color(128, 128, 128));
        closeButton.setForeground(Color.WHITE);
        
        updateEmployeeInfo();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel with employee info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(employeeInfoLabel, BorderLayout.NORTH);
        
        // Salary breakdown panel
        JPanel breakdownPanel = new JPanel(new BorderLayout());
        breakdownPanel.setBorder(BorderFactory.createTitledBorder("Salary Breakdown"));
        breakdownPanel.add(salaryBreakdownLabel, BorderLayout.CENTER);
        topPanel.add(breakdownPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Salary Components"));
        
        JScrollPane scrollPane = new JScrollPane(componentsTable);
        scrollPane.setPreferredSize(new Dimension(750, 300));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addComponent();
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editComponent();
            }
        });
        
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeComponent();
            }
        });
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = true;
                dispose();
            }
        });
    }
    
    private void updateEmployeeInfo() {
        String info = String.format("Employee: %s | ID: %s | Department: %s | Base Salary: $%.2f",
                                  employee.getFullName(), employee.getEmployeeId(), 
                                  employee.getDepartment(), employee.getBaseSalary());
        employeeInfoLabel.setText(info);
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<EmployeeSalaryComponent> components = salaryComponentManager.getEmployeeSalaryComponents(employee.getEmployeeIdString());
        
        for (EmployeeSalaryComponent empComponent : components) {
            SalaryComponent component = empComponent.getSalaryComponent();
            if (component != null) {
                String amountStr = empComponent.isPercentage() ? 
                                 String.format("%.2f%%", empComponent.getCustomAmount()) : 
                                 String.format("$%.2f", empComponent.getCustomAmount());
                
                Object[] rowData = {
                    component.getName(),
                    component.getType(),
                    amountStr,
                    empComponent.isPercentage() ? "Yes" : "No",
                    empComponent.getEffectiveDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    empComponent.isCurrentlyEffective() ? "Active" : "Inactive"
                };
                tableModel.addRow(rowData);
            }
        }
        
        updateSalaryBreakdown();
    }
    
    private void updateSalaryBreakdown() {
        double baseSalary = employee.getBaseSalary();
        double totalAllowances = salaryComponentManager.calculateTotalAllowances(employee.getEmployeeId(), baseSalary);
        double totalDeductions = salaryComponentManager.calculateTotalDeductions(employee.getEmployeeId(), baseSalary);
        double totalBonuses = salaryComponentManager.calculateTotalBonuses(employee.getEmployeeId(), baseSalary);
        
        double grossSalary = baseSalary + totalAllowances + totalBonuses;
        double netSalary = grossSalary - totalDeductions;
        
        String breakdown = String.format(
            "<html><body style='font-family: Arial; font-size: 12px;'>" +
            "<b>Base Salary:</b> $%.2f<br>" +
            "<b>Total Allowances:</b> $%.2f<br>" +
            "<b>Total Bonuses:</b> $%.2f<br>" +
            "<b>Gross Salary:</b> $%.2f<br>" +
            "<b>Total Deductions:</b> $%.2f<br>" +
            "<b>Net Salary:</b> $%.2f" +
            "</body></html>",
            baseSalary, totalAllowances, totalBonuses, grossSalary, totalDeductions, netSalary
        );
        
        salaryBreakdownLabel.setText(breakdown);
    }
    
    private void addComponent() {
        AddEmployeeComponentDialog dialog = new AddEmployeeComponentDialog(this, salaryComponentManager, employee);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshTable();
            updateSalaryBreakdown();
        }
    }
    
    private void editComponent() {
        int selectedRow = componentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a component to edit.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String componentName = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Find the component
        List<EmployeeSalaryComponent> components = salaryComponentManager.getEmployeeSalaryComponents(employee.getEmployeeIdString());
        EmployeeSalaryComponent selectedComponent = null;
        
        for (EmployeeSalaryComponent empComponent : components) {
            if (empComponent.getSalaryComponent() != null && 
                empComponent.getSalaryComponent().getName().equals(componentName)) {
                selectedComponent = empComponent;
                break;
            }
        }
        
        if (selectedComponent != null) {
            EditEmployeeComponentDialog dialog = new EditEmployeeComponentDialog(this, selectedComponent);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                refreshTable();
            }
        }
    }
    
    private void removeComponent() {
        int selectedRow = componentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a component to remove.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String componentName = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove the component '" + componentName + "'?", 
            "Confirm Removal", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Find and remove the component
            List<EmployeeSalaryComponent> components = salaryComponentManager.getEmployeeSalaryComponents(employee.getEmployeeIdString());
            
            for (EmployeeSalaryComponent empComponent : components) {
                if (empComponent.getSalaryComponent() != null && 
                    empComponent.getSalaryComponent().getName().equals(componentName)) {
                    salaryComponentManager.removeComponentFromEmployee(employee.getEmployeeId(), empComponent.getSalaryComponentId());
                    break;
                }
            }
            
            refreshTable();
            JOptionPane.showMessageDialog(this, "Component removed successfully.", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
