package gui;

import models.User;
import models.Role;
import managers.UserManager;
import managers.PasswordSecurity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * User Management Panel for admin users to manage system users
 */
public class UserManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private PayrollManagementSystemGUI mainApp;
    private transient UserManager userManager;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addUserButton;
    private JButton editUserButton;
    private JButton deactivateUserButton;
    private JButton resetPasswordButton;
    private JButton refreshButton;
    private JButton auditTrailButton;
    private JButton backButton;
    private JTextField searchField;
    private JComboBox<Role> roleFilterCombo;
    
    public UserManagementPanel(PayrollManagementSystemGUI mainApp) {
        this.mainApp = mainApp;
        this.userManager = mainApp.getUserManager();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadUsers();
    }
    
    private void initializeComponents() {
        // Initialize table
        String[] columnNames = {"ID", "Username", "Full Name", "Email", "Role", "Status", "Last Login", "Created By"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        userTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        userTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        
        // Initialize buttons
        addUserButton = new JButton("Add User");
        editUserButton = new JButton("Edit User");
        deactivateUserButton = new JButton("Deactivate");
        resetPasswordButton = new JButton("Reset Password");
        refreshButton = new JButton("Refresh");
        auditTrailButton = new JButton("Audit Trail");
        
        // Style buttons
        addUserButton.setBackground(new Color(46, 125, 50));
        addUserButton.setForeground(Color.WHITE);
        editUserButton.setBackground(new Color(25, 118, 210));
        editUserButton.setForeground(Color.WHITE);
        deactivateUserButton.setBackground(new Color(211, 47, 47));
        deactivateUserButton.setForeground(Color.WHITE);
        resetPasswordButton.setBackground(new Color(255, 152, 0));
        resetPasswordButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(158, 158, 158));
        refreshButton.setForeground(Color.WHITE);
        auditTrailButton.setBackground(new Color(103, 58, 183));
        auditTrailButton.setForeground(Color.WHITE);
        
        // Initially disable buttons that require selection
        editUserButton.setEnabled(false);
        deactivateUserButton.setEnabled(false);
        resetPasswordButton.setEnabled(false);
        auditTrailButton.setEnabled(false);
        
        // Search and filter components
        searchField = new JTextField(20);
        searchField.setToolTipText("Search by username or email");
        
        roleFilterCombo = new JComboBox<>();
        roleFilterCombo.addItem(null); // All roles
        for (Role role : Role.values()) {
            roleFilterCombo.addItem(role);
        }
        roleFilterCombo.setToolTipText("Filter by role");
        
        // Check permissions
        User currentUser = getCurrentUser();
        if (currentUser == null || !currentUser.hasPermission("user.manage")) {
            addUserButton.setEnabled(false);
            editUserButton.setEnabled(false);
            deactivateUserButton.setEnabled(false);
            resetPasswordButton.setEnabled(false);
        }
        
        // Initialize back button
        backButton = new JButton("Back to Main Menu");
        backButton.setBackground(new Color(96, 125, 139));
        backButton.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("User Management"));
        
        // Top panel with search and filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(new JLabel("Role:"));
        topPanel.add(roleFilterCombo);
        topPanel.add(refreshButton);
        topPanel.add(backButton);
        
        // Center panel with table
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Bottom panel with action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addUserButton);
        buttonPanel.add(editUserButton);
        buttonPanel.add(resetPasswordButton);
        buttonPanel.add(deactivateUserButton);
        buttonPanel.add(auditTrailButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // Table selection listener
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = userTable.getSelectedRow() != -1;
                User currentUser = getCurrentUser();
                boolean hasPermission = currentUser != null && currentUser.hasPermission("user.manage");
                editUserButton.setEnabled(hasSelection && hasPermission);
                deactivateUserButton.setEnabled(hasSelection && hasPermission);
                resetPasswordButton.setEnabled(hasSelection && hasPermission);
                auditTrailButton.setEnabled(hasSelection);
            }
        });
        
        // Button listeners
        addUserButton.addActionListener(e -> showAddUserDialog());
        editUserButton.addActionListener(e -> showEditUserDialog());
        deactivateUserButton.addActionListener(e -> deactivateSelectedUser());
        resetPasswordButton.addActionListener(e -> showResetPasswordDialog());
        refreshButton.addActionListener(e -> loadUsers());
        auditTrailButton.addActionListener(e -> showAuditTrailDialog());
        backButton.addActionListener(e -> mainApp.showMainMenuPanel());
        
        // Search and filter listeners
        searchField.addActionListener(e -> filterUsers());
        roleFilterCombo.addActionListener(e -> filterUsers());
    }
    
    private void loadUsers() {
        try {
            List<User> users = userManager.getAllUsers();
            updateTable(users);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<User> users) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getUsername(),
                user.getFullName() != null ? user.getFullName() : "",
                user.getEmail() != null ? user.getEmail() : "",
                user.getRole() != null ? user.getRole().getDisplayName() : "Unknown",
                user.isActive() ? "Active" : "Inactive",
                user.getLastLogin() != null ? user.getLastLogin().format(formatter) : "Never",
                user.getCreatedBy() != null ? user.getCreatedBy() : ""
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterUsers() {
        String searchText = searchField.getText().trim().toLowerCase();
        Role selectedRole = (Role) roleFilterCombo.getSelectedItem();
        
        try {
            List<User> allUsers = userManager.getAllUsers();
            List<User> filteredUsers = allUsers.stream()
                .filter(user -> {
                    // Search filter
                    if (!searchText.isEmpty()) {
                        boolean matchesSearch = 
                            user.getUsername().toLowerCase().contains(searchText) ||
                            (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchText)) ||
                            (user.getFullName() != null && user.getFullName().toLowerCase().contains(searchText));
                        if (!matchesSearch) return false;
                    }
                    
                    // Role filter
                    if (selectedRole != null && !user.getRole().equals(selectedRole)) {
                        return false;
                    }
                    
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
            
            updateTable(filteredUsers);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error filtering users: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddUserDialog() {
        UserDialog dialog = new UserDialog(
            SwingUtilities.getWindowAncestor(this), 
            "Add New User", 
            null, 
            userManager, 
            getCurrentUser()
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadUsers();
        }
    }
    
    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Get full user object
        User user = userManager.getUserByUsername(username);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        UserDialog dialog = new UserDialog(
            SwingUtilities.getWindowAncestor(this), 
            "Edit User", 
            user, 
            userManager, 
            getCurrentUser()
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadUsers();
        }
    }
    
    private void deactivateSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to deactivate user '" + username + "'?",
            "Confirm Deactivation", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
                User currentUser = getCurrentUser();
                String currentUsername = currentUser != null ? currentUser.getUsername() : "System";
                boolean success = userManager.deactivateUser(userId, currentUsername);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "User deactivated successfully!", 
                                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to deactivate user!", 
                                                "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deactivating user: " + e.getMessage(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showResetPasswordDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        String[] options = {"Generate Random Password", "Set Custom Password", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
            "How would you like to reset the password for '" + username + "'?",
            "Reset Password",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        String newPassword = null;
        
        if (choice == 0) { // Generate random password
            newPassword = PasswordSecurity.generateSecurePassword(12);
        } else if (choice == 1) { // Custom password
            newPassword = JOptionPane.showInputDialog(this, 
                "Enter new password for '" + username + "':");
            
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                PasswordSecurity.PasswordStrength strength = 
                    PasswordSecurity.validatePasswordStrength(newPassword);
                if (!strength.isValid()) {
                    JOptionPane.showMessageDialog(this, strength.getMessage(), 
                                                "Weak Password", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                return;
            }
        } else {
            return; // Cancel
        }
        
        try {
            User currentUser = getCurrentUser();
            String currentUsername = currentUser != null ? currentUser.getUsername() : "System";
            boolean success = userManager.changePassword(userId, newPassword, currentUsername);
            
            if (success) {
                String message = "Password reset successfully!\n\nNew password: " + newPassword + 
                               "\n\nPlease provide this password to the user securely.";
                JOptionPane.showMessageDialog(this, message, "Password Reset", 
                                            JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reset password!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error resetting password: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAuditTrailDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        AuditTrailDialog dialog = new AuditTrailDialog(
            SwingUtilities.getWindowAncestor(this),
            username,
            mainApp.getUserManager().getAuditTrailManager()
        );
        dialog.setVisible(true);
    }
    
    private User getCurrentUser() {
        if (mainApp.getAuthManager().isLoggedIn()) {
            return mainApp.getAuthManager().getCurrentUser();
        }
        return null;
    }
}
