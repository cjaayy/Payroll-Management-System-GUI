package gui;

import models.AuditTrail;
import managers.AuditTrailManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog for viewing audit trail records
 */
public class AuditTrailDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private transient AuditTrailManager auditManager;
    private String targetUsername;
    private JTable auditTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton closeButton;
    private JComboBox<String> actionFilterCombo;
    private JSpinner limitSpinner;
    
    public AuditTrailDialog(Window parent, String targetUsername, AuditTrailManager auditManager) {
        super(parent, "Audit Trail - " + targetUsername, Dialog.ModalityType.APPLICATION_MODAL);
        this.auditManager = auditManager;
        this.targetUsername = targetUsername;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadAuditTrail();
        
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        // Initialize table
        String[] columnNames = {"Timestamp", "Action", "Details", "IP Address", "User Agent"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        auditTable = new JTable(tableModel);
        auditTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        auditTable.getTableHeader().setReorderingAllowed(false);
        auditTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Set column widths
        auditTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        auditTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        auditTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        auditTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        auditTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        // Initialize buttons
        refreshButton = new JButton("Refresh");
        closeButton = new JButton("Close");
        
        // Style buttons
        refreshButton.setBackground(new Color(25, 118, 210));
        refreshButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(158, 158, 158));
        closeButton.setForeground(Color.WHITE);
        
        // Filter components
        actionFilterCombo = new JComboBox<>();
        actionFilterCombo.addItem("All Actions");
        actionFilterCombo.addItem("LOGIN");
        actionFilterCombo.addItem("LOGOUT");
        actionFilterCombo.addItem("USER_CREATED");
        actionFilterCombo.addItem("USER_UPDATED");
        actionFilterCombo.addItem("USER_DEACTIVATED");
        actionFilterCombo.addItem("PASSWORD_CHANGED");
        actionFilterCombo.addItem("EMPLOYEE_CREATED");
        actionFilterCombo.addItem("EMPLOYEE_UPDATED");
        actionFilterCombo.addItem("PAYROLL_CALCULATED");
        actionFilterCombo.addItem("REPORT_GENERATED");
        
        limitSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 1000, 10));
        limitSpinner.setToolTipText("Maximum number of records to display");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel with filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        topPanel.add(new JLabel("Action:"));
        topPanel.add(actionFilterCombo);
        topPanel.add(new JLabel("Limit:"));
        topPanel.add(limitSpinner);
        topPanel.add(refreshButton);
        
        // Center panel with table
        JScrollPane scrollPane = new JScrollPane(auditTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Audit Trail Records"));
        
        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        refreshButton.addActionListener(e -> loadAuditTrail());
        closeButton.addActionListener(e -> dispose());
        actionFilterCombo.addActionListener(e -> loadAuditTrail());
        limitSpinner.addChangeListener(e -> loadAuditTrail());
    }
    
    private void loadAuditTrail() {
        try {
            String actionFilter = (String) actionFilterCombo.getSelectedItem();
            int limit = (Integer) limitSpinner.getValue();
            
            List<AuditTrail> auditRecords;
            if ("All Actions".equals(actionFilter)) {
                auditRecords = auditManager.getAuditTrailByUser(targetUsername, limit);
            } else {
                auditRecords = auditManager.getAuditTrailByUserAndAction(targetUsername, actionFilter, limit);
            }
            
            updateTable(auditRecords);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading audit trail: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<AuditTrail> auditRecords) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (AuditTrail record : auditRecords) {
            Object[] row = {
                record.getTimestamp().format(formatter),
                record.getAction(),
                record.getNewValues() != null ? record.getNewValues() : "",
                record.getIpAddress() != null ? record.getIpAddress() : "",
                record.getUserAgent() != null ? record.getUserAgent() : ""
            };
            tableModel.addRow(row);
        }
        
        // Update dialog title with record count
        setTitle("Audit Trail - " + targetUsername + " (" + auditRecords.size() + " records)");
    }
}
