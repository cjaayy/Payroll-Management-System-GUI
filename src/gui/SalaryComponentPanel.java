package gui;

import models.SalaryComponent;
import managers.SalaryComponentManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for managing salary components
 */
public class SalaryComponentPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private PayrollManagementSystemGUI mainApp;
    private transient SalaryComponentManager salaryComponentManager;
    private JTable componentsTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, backButton;
    private JComboBox<String> typeFilterCombo;
    
    public SalaryComponentPanel(PayrollManagementSystemGUI mainApp) {
        this.mainApp = mainApp;
        this.salaryComponentManager = mainApp.getSalaryComponentManager();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        refreshTable();
    }
    
    private void initializeComponents() {
        setBackground(new Color(240, 248, 255));
        
        // Table setup
        String[] columnNames = {"ID", "Name", "Type", "Default Amount", "Is %", "Description", "Active"};
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
        deleteButton = new JButton("Delete Component");
        backButton = new JButton("Back to Main Menu");
        
        // Filter combo
        typeFilterCombo = new JComboBox<>(new String[]{"All", "ALLOWANCE", "DEDUCTION", "BONUS"});
        
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
        
        backButton.setFont(buttonFont);
        backButton.setBackground(new Color(128, 128, 128));
        backButton.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Salary Components Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table in center
        JScrollPane scrollPane = new JScrollPane(componentsTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Filter by Type:"));
        controlPanel.add(typeFilterCombo);
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
                showAddComponentDialog();
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedComponent();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedComponent();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showMainMenuPanel();
            }
        });
        
        typeFilterCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<SalaryComponent> components;
        
        String selectedType = (String) typeFilterCombo.getSelectedItem();
        if ("All".equals(selectedType)) {
            components = salaryComponentManager.getAllSalaryComponents();
        } else {
            components = salaryComponentManager.getSalaryComponentsByType(selectedType);
        }
        
        for (SalaryComponent component : components) {
            String amountStr = component.isPercentage() ? 
                             String.format("%.2f%%", component.getAmount()) : 
                             String.format("$%.2f", component.getAmount());
            
            Object[] rowData = {
                component.getId(),
                component.getName(),
                component.getType(),
                amountStr,
                component.isPercentage() ? "Yes" : "No",
                component.getDescription(),
                component.isActive() ? "Active" : "Inactive"
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void showAddComponentDialog() {
        SalaryComponentDialog dialog = new SalaryComponentDialog(
            SwingUtilities.getWindowAncestor(this), 
            salaryComponentManager, 
            null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshTable();
        }
    }
    
    private void editSelectedComponent() {
        int selectedRow = componentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a component to edit.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int componentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        SalaryComponent component = salaryComponentManager.getSalaryComponent(componentId);
        
        if (component != null) {
            SalaryComponentDialog dialog = new SalaryComponentDialog(
                SwingUtilities.getWindowAncestor(this), 
                salaryComponentManager, 
                component);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                refreshTable();
            }
        }
    }
    
    private void deleteSelectedComponent() {
        int selectedRow = componentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a component to delete.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String componentName = (String) tableModel.getValueAt(selectedRow, 1);
        int componentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the component '" + componentName + "'?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (salaryComponentManager.deleteSalaryComponent(componentId)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Component deleted successfully.", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete component.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
