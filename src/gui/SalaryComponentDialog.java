package gui;

import models.SalaryComponent;
import managers.SalaryComponentManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

/**
 * Dialog for creating/editing salary components
 */
public class SalaryComponentDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private transient SalaryComponentManager salaryComponentManager;
    private transient SalaryComponent salaryComponent;
    private boolean confirmed = false;
    
    private JTextField nameField;
    private JComboBox<String> typeComboBox;
    private JTextField amountField;
    private JTextField descriptionField;
    private JCheckBox isPercentageCheckBox;
    private JCheckBox isActiveCheckBox;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    public SalaryComponentDialog(Window parent, SalaryComponentManager manager, SalaryComponent component) {
        super(parent, component == null ? "Create Salary Component" : "Edit Salary Component", ModalityType.APPLICATION_MODAL);
        this.salaryComponentManager = manager;
        this.salaryComponent = component;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        if (component != null) {
            populateFields();
        }
        
        setSize(450, 350);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        nameField = new JTextField(20);
        typeComboBox = new JComboBox<>(new String[]{"ALLOWANCE", "DEDUCTION", "BONUS"});
        amountField = new JTextField(20);
        descriptionField = new JTextField(20);
        isPercentageCheckBox = new JCheckBox("Is Percentage");
        isActiveCheckBox = new JCheckBox("Active");
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        // Style buttons
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(128, 128, 128));
        cancelButton.setForeground(Color.WHITE);
        
        // Set defaults
        isActiveCheckBox.setSelected(true);
        amountField.setText("0.00");
        
        // Add tooltips
        nameField.setToolTipText("Enter component name (e.g., HRA, Medical Allowance)");
        typeComboBox.setToolTipText("Select component type");
        amountField.setToolTipText("Enter amount (fixed amount or percentage)");
        isPercentageCheckBox.setToolTipText("Check if amount is percentage of base salary");
        descriptionField.setToolTipText("Enter description of the component");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel(""), gbc);
        gbc.gridx = 1;
        formPanel.add(isPercentageCheckBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel(""), gbc);
        gbc.gridx = 1;
        formPanel.add(isActiveCheckBox, gbc);
        
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
                saveComponent();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Update amount field tooltip based on percentage checkbox
        isPercentageCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPercentageCheckBox.isSelected()) {
                    amountField.setToolTipText("Enter percentage value (e.g., 12.5 for 12.5%)");
                } else {
                    amountField.setToolTipText("Enter fixed amount (e.g., 1500.00)");
                }
            }
        });
    }
    
    private void populateFields() {
        if (salaryComponent != null) {
            nameField.setText(salaryComponent.getName());
            typeComboBox.setSelectedItem(salaryComponent.getType());
            amountField.setText(String.valueOf(salaryComponent.getAmount()));
            descriptionField.setText(salaryComponent.getDescription());
            isPercentageCheckBox.setSelected(salaryComponent.isPercentage());
            isActiveCheckBox.setSelected(salaryComponent.isActive());
        }
    }
    
    private void saveComponent() {
        try {
            String name = nameField.getText().trim();
            String type = (String) typeComboBox.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText().trim());
            String description = descriptionField.getText().trim();
            boolean isPercentage = isPercentageCheckBox.isSelected();
            boolean isActive = isActiveCheckBox.isSelected();
            
            // Validation
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a component name.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (amount < 0) {
                JOptionPane.showMessageDialog(this, "Amount cannot be negative.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (isPercentage && amount > 100) {
                JOptionPane.showMessageDialog(this, "Percentage cannot exceed 100%.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (salaryComponent == null) {
                // Create new component
                salaryComponent = salaryComponentManager.createSalaryComponent(name, type, amount, description, isPercentage);
                if (!isActive) {
                    salaryComponent.setActive(false);
                }
            } else {
                // Update existing component
                salaryComponent.setName(name);
                salaryComponent.setType(type);
                salaryComponent.setAmount(BigDecimal.valueOf(amount));
                salaryComponent.setDescription(description);
                salaryComponent.setPercentage(isPercentage);
                salaryComponent.setActive(isActive);
                salaryComponentManager.updateSalaryComponent(salaryComponent);
            }
            
            confirmed = true;
            dispose();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", 
                                        "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving component: " + ex.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public SalaryComponent getSalaryComponent() {
        return salaryComponent;
    }
}
