package gui;

import models.Employee;
import models.SalaryComponent;
import managers.SalaryComponentManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog for adding a salary component to an employee
 */
public class AddEmployeeComponentDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private transient SalaryComponentManager salaryComponentManager;
    private transient Employee employee;
    private boolean confirmed = false;
    
    private JComboBox<SalaryComponent> componentComboBox;
    private JTextField amountField;
    private JCheckBox percentageCheckBox;
    private JTextField effectiveDateField;
    private JTextField remarksField;
    
    @SuppressWarnings("this-escape")
    public AddEmployeeComponentDialog(Window parent, SalaryComponentManager manager, Employee employee) {
        super(parent, "Add Salary Component", ModalityType.APPLICATION_MODAL);
        this.salaryComponentManager = manager;
        this.employee = employee;
        
        setupLayout();
        loadComponents();
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }
    
    private void loadComponents() {
        // Load available components and exclude already assigned ones
        List<SalaryComponent> components = salaryComponentManager.getAllSalaryComponents();
        List<models.EmployeeSalaryComponent> existingComponents = salaryComponentManager.getEmployeeSalaryComponents(employee.getEmployeeIdString());
        
        for (SalaryComponent component : components) {
            boolean alreadyAssigned = false;
            for (models.EmployeeSalaryComponent existing : existingComponents) {
                if (existing.getSalaryComponentId() == component.getId() && existing.isActive()) {
                    alreadyAssigned = true;
                    break;
                }
            }
            
            if (!alreadyAssigned && component.isActive()) {
                componentComboBox.addItem(component);
            }
        }
        
        // Set custom renderer for better display
        componentComboBox.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SalaryComponent) {
                    SalaryComponent component = (SalaryComponent) value;
                    setText(component.getName() + " (" + component.getType() + ")");
                }
                return this;
            }
        });
        
        // Set default effective date to today
        effectiveDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Employee info
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(new JLabel("Employee: " + employee.getFirstName() + " " + employee.getLastName()), gbc);
        
        // Component selection
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Component:"), gbc);
        gbc.gridx = 1;
        componentComboBox = new JComboBox<>();
        componentComboBox.setPreferredSize(new Dimension(250, 25));
        mainPanel.add(componentComboBox, gbc);
        
        // Amount
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(15);
        mainPanel.add(amountField, gbc);
        
        // Percentage checkbox
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        percentageCheckBox = new JCheckBox("This is a percentage of base salary");
        mainPanel.add(percentageCheckBox, gbc);
        
        // Effective date
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Effective Date:"), gbc);
        gbc.gridx = 1;
        effectiveDateField = new JTextField(15);
        effectiveDateField.setToolTipText("Format: yyyy-MM-dd");
        mainPanel.add(effectiveDateField, gbc);
        
        // Remarks
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Remarks:"), gbc);
        gbc.gridx = 1;
        remarksField = new JTextField(15);
        mainPanel.add(remarksField, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Component");
        JButton cancelButton = new JButton("Cancel");
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addComponent();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add component selection listener
        componentComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SalaryComponent selected = (SalaryComponent) componentComboBox.getSelectedItem();
                if (selected != null) {
                    // Set default amount if available
                    if (selected.getAmount().doubleValue() > 0) {
                        amountField.setText(String.valueOf(selected.getAmount().doubleValue()));
                        percentageCheckBox.setSelected(selected.isPercentage());
                    }
                }
            }
        });
    }
    
    private void addComponent() {
        try {
            SalaryComponent selectedComponent = (SalaryComponent) componentComboBox.getSelectedItem();
            if (selectedComponent == null) {
                JOptionPane.showMessageDialog(this, "Please select a component.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(amountText);
            if (amount < 0) {
                JOptionPane.showMessageDialog(this, "Amount cannot be negative.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean isPercentage = percentageCheckBox.isSelected();
            if (isPercentage && amount > 100) {
                JOptionPane.showMessageDialog(this, "Percentage cannot exceed 100%.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate effectiveDate = LocalDate.parse(effectiveDateField.getText().trim(), 
                                                     DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // Create the assignment
            models.EmployeeSalaryComponent empComponent = salaryComponentManager.assignComponentToEmployee(
                employee.getEmployeeId(), selectedComponent.getId(), amount, isPercentage, effectiveDate);
            
            if (empComponent != null) {
                confirmed = true;
                dispose();
                JOptionPane.showMessageDialog(getParent(), "Component added successfully.", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add component.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", 
                                        "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date in yyyy-MM-dd format.", 
                                        "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding component: " + ex.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
