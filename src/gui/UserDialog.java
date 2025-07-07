package gui;

import models.User;
import models.Role;
import managers.UserManager;
import managers.PasswordSecurity;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for creating and editing users
 */
public class UserDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private transient UserManager userManager;
    private transient User currentUser;
    private transient User editingUser;
    private boolean confirmed = false;
    
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JComboBox<Role> roleCombo;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox isActiveCheckBox;
    private JCheckBox changePasswordCheckBox;
    private JButton generatePasswordButton;
    private JLabel passwordStrengthLabel;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    @SuppressWarnings("this-escape")
    public UserDialog(Window parent, String title, User user, UserManager userManager, User currentUser) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        this.userManager = userManager;
        this.currentUser = currentUser;
        this.editingUser = user;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        if (user != null) {
            populateFields();
        }
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        fullNameField = new JTextField(20);
        
        roleCombo = new JComboBox<>(Role.values());
        roleCombo.setSelectedItem(Role.EMPLOYEE);
        
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        
        isActiveCheckBox = new JCheckBox("Active", true);
        changePasswordCheckBox = new JCheckBox("Change Password");
        
        generatePasswordButton = new JButton("Generate");
        generatePasswordButton.setToolTipText("Generate secure random password");
        
        passwordStrengthLabel = new JLabel();
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        // Style buttons
        saveButton.setBackground(new Color(46, 125, 50));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(158, 158, 158));
        cancelButton.setForeground(Color.WHITE);
        generatePasswordButton.setBackground(new Color(255, 152, 0));
        generatePasswordButton.setForeground(Color.WHITE);
        
        // Set field properties based on editing mode
        if (editingUser != null) {
            usernameField.setEditable(false);
            usernameField.setBackground(new Color(240, 240, 240));
            passwordField.setEnabled(false);
            confirmPasswordField.setEnabled(false);
            generatePasswordButton.setEnabled(false);
            changePasswordCheckBox.setSelected(false);
        }
        
        // Check permissions
        if (!currentUser.hasPermission("user.manage")) {
            roleCombo.setEnabled(false);
            isActiveCheckBox.setEnabled(false);
            changePasswordCheckBox.setEnabled(false);
        }
        
        // Admin role restriction
        if (currentUser.getRole() != Role.ADMIN) {
            roleCombo.removeItem(Role.ADMIN);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Username
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Username:*"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        mainPanel.add(usernameField, gbc);
        gbc.gridwidth = 1;
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Full Name:*"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        mainPanel.add(fullNameField, gbc);
        gbc.gridwidth = 1;
        
        // Email
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        mainPanel.add(emailField, gbc);
        gbc.gridwidth = 1;
        
        // Role
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Role:*"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        mainPanel.add(roleCombo, gbc);
        gbc.gridwidth = 1;
        
        // Active checkbox
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        mainPanel.add(isActiveCheckBox, gbc);
        gbc.gridwidth = 1;
        
        // Password section
        if (editingUser != null) {
            gbc.gridx = 0; gbc.gridy = ++row;
            mainPanel.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 2;
            mainPanel.add(changePasswordCheckBox, gbc);
            gbc.gridwidth = 1;
        }
        
        // Password field
        gbc.gridx = 0; gbc.gridy = ++row;
        JLabel passwordLabel = new JLabel(editingUser == null ? "Password:*" : "New Password:");
        mainPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        gbc.gridx = 2;
        mainPanel.add(generatePasswordButton, gbc);
        
        // Confirm password field
        gbc.gridx = 0; gbc.gridy = ++row;
        JLabel confirmLabel = new JLabel(editingUser == null ? "Confirm Password:*" : "Confirm New Password:");
        mainPanel.add(confirmLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        mainPanel.add(confirmPasswordField, gbc);
        gbc.gridwidth = 1;
        
        // Password strength indicator
        gbc.gridx = 1; gbc.gridy = ++row;
        gbc.gridwidth = 2;
        mainPanel.add(passwordStrengthLabel, gbc);
        gbc.gridwidth = 1;
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        saveButton.addActionListener(e -> saveUser());
        cancelButton.addActionListener(e -> dispose());
        
        generatePasswordButton.addActionListener(e -> generatePassword());
        
        changePasswordCheckBox.addActionListener(e -> {
            boolean enabled = changePasswordCheckBox.isSelected();
            passwordField.setEnabled(enabled);
            confirmPasswordField.setEnabled(enabled);
            generatePasswordButton.setEnabled(enabled);
        });
        
        // Password strength checking
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkPasswordStrength(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { checkPasswordStrength(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { checkPasswordStrength(); }
        });
    }
    
    private void populateFields() {
        if (editingUser != null) {
            usernameField.setText(editingUser.getUsername());
            emailField.setText(editingUser.getEmail() != null ? editingUser.getEmail() : "");
            fullNameField.setText(editingUser.getFullName() != null ? editingUser.getFullName() : "");
            roleCombo.setSelectedItem(editingUser.getRole());
            isActiveCheckBox.setSelected(editingUser.isActive());
        }
    }
    
    private void generatePassword() {
        String password = PasswordSecurity.generateSecurePassword(12);
        passwordField.setText(password);
        confirmPasswordField.setText(password);
        checkPasswordStrength();
    }
    
    private void checkPasswordStrength() {
        String password = new String(passwordField.getPassword());
        if (password.isEmpty()) {
            passwordStrengthLabel.setText("");
            return;
        }
        
        PasswordSecurity.PasswordStrength strength = PasswordSecurity.validatePasswordStrength(password);
        if (strength.isValid()) {
            passwordStrengthLabel.setText("✓ Strong password");
            passwordStrengthLabel.setForeground(new Color(0, 128, 0));
        } else {
            passwordStrengthLabel.setText("⚠ " + strength.getMessage());
            passwordStrengthLabel.setForeground(Color.RED);
        }
    }
    
    private void saveUser() {
        // Validate required fields
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }
        
        if (fullNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            fullNameField.requestFocus();
            return;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return;
        }
        
        // Validate email format
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return;
        }
        
        // Password validation for new users or when changing password
        boolean needsPassword = (editingUser == null) || (editingUser != null && changePasswordCheckBox.isSelected());
        if (needsPassword) {
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                passwordField.requestFocus();
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                confirmPasswordField.requestFocus();
                return;
            }
            
            PasswordSecurity.PasswordStrength strength = PasswordSecurity.validatePasswordStrength(password);
            if (!strength.isValid()) {
                JOptionPane.showMessageDialog(this, strength.getMessage(), "Weak Password", JOptionPane.WARNING_MESSAGE);
                passwordField.requestFocus();
                return;
            }
        }
        
        try {
            if (editingUser == null) {
                // Create new user
                User newUser = new User(
                    usernameField.getText().trim(),
                    "", // Password will be set by UserManager
                    "",
                    emailField.getText().trim(),
                    fullNameField.getText().trim(),
                    (Role) roleCombo.getSelectedItem(),
                    currentUser.getUsername()
                );
                newUser.setPassword(new String(passwordField.getPassword()));
                newUser.setActive(isActiveCheckBox.isSelected());
                
                boolean success = userManager.createUser(newUser, currentUser.getUsername());
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "User created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    confirmed = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create user. Username may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update existing user
                editingUser.setEmail(emailField.getText().trim());
                editingUser.setFullName(fullNameField.getText().trim());
                editingUser.setRole((Role) roleCombo.getSelectedItem());
                editingUser.setActive(isActiveCheckBox.isSelected());
                
                boolean success = userManager.updateUser(editingUser, currentUser.getUsername());
                
                // Change password if requested
                if (success && changePasswordCheckBox.isSelected()) {
                    success = userManager.changePassword(editingUser.getUserId(), 
                                                       new String(passwordField.getPassword()), 
                                                       currentUser.getUsername());
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    confirmed = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update user!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
