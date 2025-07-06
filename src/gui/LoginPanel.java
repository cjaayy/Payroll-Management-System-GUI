package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login Panel for user authentication
 */
public class LoginPanel extends JPanel {
    private PayrollManagementSystemGUI mainApp;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    
    public LoginPanel(PayrollManagementSystemGUI mainApp) {
        this.mainApp = mainApp;
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        setBackground(new Color(240, 248, 255));
        
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        statusLabel = new JLabel(" ");
        
        // Style components
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        statusLabel.setForeground(Color.RED);
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel("Payroll Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);
        
        // Login form
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);
        
        gbc.gridy = 4;
        add(statusLabel, gbc);
        
        // Default credentials info
        JLabel infoLabel = new JLabel("<html><center>Default credentials:<br>admin/admin123 or hr/hr123</center></html>");
        infoLabel.setForeground(Color.GRAY);
        gbc.gridy = 5;
        add(infoLabel, gbc);
    }
    
    private void setupEventListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Allow login with Enter key
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }
    
    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (mainApp.getAuthManager().login(username, password)) {
            statusLabel.setText("Logout successfully!");
            statusLabel.setForeground(Color.RED);
            
            // Clear fields
            usernameField.setText("");
            passwordField.setText("");
            
            // Show main menu
            mainApp.showMainMenuPanel();
        } else {
            statusLabel.setText("Invalid credentials. Please try again.");
            statusLabel.setForeground(Color.RED);
            passwordField.setText("");
        }
    }
}
