package gui;

import models.EmployeeSalaryComponent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;

/**
 * Dialog for editing an employee's salary component
 */
public class EditEmployeeComponentDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private transient EmployeeSalaryComponent employeeSalaryComponent;
    private boolean confirmed = false;
    
    private JLabel componentNameLabel;
    private JTextField amountField;
    private JCheckBox isPercentageCheckBox;
    private JTextField effectiveDateField;
    private JTextField endDateField;
    private JCheckBox isActiveCheckBox;
    private JButton saveButton, cancelButton;
    
    public EditEmployeeComponentDialog(Window parent, EmployeeSalaryComponent empComponent) {
        super(parent, "Edit Salary Component", ModalityType.APPLICATION_MODAL);
        this.employeeSalaryComponent = empComponent;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        populateFields();
        
        setSize(400, 350);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        componentNameLabel = new JLabel();
        componentNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        componentNameLabel.setForeground(new Color(70, 130, 180));
        
        amountField = new JTextField(15);
        isPercentageCheckBox = new JCheckBox("Is Percentage");
        effectiveDateField = new JTextField(15);
        endDateField = new JTextField(15);
        isActiveCheckBox = new JCheckBox("Active");
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        // Style buttons
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(128, 128, 128));
        cancelButton.setForeground(Color.WHITE);
        
        // Add tooltips
        amountField.setToolTipText("Enter custom amount for this employee");
        isPercentageCheckBox.setToolTipText("Check if amount is percentage of base salary");
        effectiveDateField.setToolTipText("Format: yyyy-MM-dd");
        endDateField.setToolTipText("Format: yyyy-MM-dd (leave empty for ongoing)");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Component:"), gbc);
        gbc.gridx = 1;
        formPanel.add(componentNameLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel(""), gbc);
        gbc.gridx = 1;
        formPanel.add(isPercentageCheckBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Effective Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(effectiveDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(endDateField, gbc);
        
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
        
        // Update tooltip based on percentage checkbox
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
        if (employeeSalaryComponent != null) {
            if (employeeSalaryComponent.getSalaryComponent() != null) {
                componentNameLabel.setText(employeeSalaryComponent.getSalaryComponent().getName() + 
                                         " (" + employeeSalaryComponent.getSalaryComponent().getType() + ")");
            }
            
            amountField.setText(String.valueOf(employeeSalaryComponent.getCustomAmount()));
            isPercentageCheckBox.setSelected(employeeSalaryComponent.isPercentage());
            effectiveDateField.setText(employeeSalaryComponent.getEffectiveDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            if (employeeSalaryComponent.getEndDate() != null) {
                endDateField.setText(employeeSalaryComponent.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            
            isActiveCheckBox.setSelected(employeeSalaryComponent.isActive());
        }
    }
    
    private void saveComponent() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            boolean isPercentage = isPercentageCheckBox.isSelected();
            LocalDate effectiveDate = LocalDate.parse(effectiveDateField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            LocalDate endDate = null;
            String endDateText = endDateField.getText().trim();
            if (!endDateText.isEmpty()) {
                endDate = LocalDate.parse(endDateText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            
            boolean isActive = isActiveCheckBox.isSelected();
            
            // Validation
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
            
            if (endDate != null && endDate.isBefore(effectiveDate)) {
                JOptionPane.showMessageDialog(this, "End date cannot be before effective date.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update the component
            employeeSalaryComponent.setCustomAmount(BigDecimal.valueOf(amount));
            employeeSalaryComponent.setPercentage(isPercentage);
            employeeSalaryComponent.setEffectiveDate(effectiveDate);
            employeeSalaryComponent.setEndDate(endDate);
            employeeSalaryComponent.setActive(isActive);
            
            confirmed = true;
            dispose();
            JOptionPane.showMessageDialog(getParent(), "Component updated successfully.", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", 
                                        "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date in yyyy-MM-dd format.", 
                                        "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating component: " + ex.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
