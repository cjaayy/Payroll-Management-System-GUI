package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main Menu Panel
 */
public class MainMenuPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private PayrollManagementSystemGUI mainApp;
    private JLabel welcomeLabel;
    private JButton employeeButton;
    private JButton payrollButton;
    private JButton reportsButton;
    private JButton salaryComponentsButton;
    private JButton userManagementButton;
    private JButton logoutButton;
    
    public MainMenuPanel(PayrollManagementSystemGUI mainApp) {
        this.mainApp = mainApp;
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        setBackground(new Color(240, 248, 255));
        
        welcomeLabel = new JLabel();
        employeeButton = new JButton("Employee Management");
        payrollButton = new JButton("Payroll Management");
        reportsButton = new JButton("Reports");
        salaryComponentsButton = new JButton("Salary Components");
        userManagementButton = new JButton("User Management");
        logoutButton = new JButton("Logout");
        
        // Style buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Color buttonColor = new Color(70, 130, 180);
        
        employeeButton.setFont(buttonFont);
        employeeButton.setBackground(buttonColor);
        employeeButton.setForeground(Color.WHITE);
        employeeButton.setPreferredSize(new Dimension(250, 50));
        
        payrollButton.setFont(buttonFont);
        payrollButton.setBackground(buttonColor);
        payrollButton.setForeground(Color.WHITE);
        payrollButton.setPreferredSize(new Dimension(250, 50));
        
        reportsButton.setFont(buttonFont);
        reportsButton.setBackground(buttonColor);
        reportsButton.setForeground(Color.WHITE);
        reportsButton.setPreferredSize(new Dimension(250, 50));
        
        salaryComponentsButton.setFont(buttonFont);
        salaryComponentsButton.setBackground(new Color(32, 178, 170));
        salaryComponentsButton.setForeground(Color.WHITE);
        salaryComponentsButton.setPreferredSize(new Dimension(250, 50));
        
        userManagementButton.setFont(buttonFont);
        userManagementButton.setBackground(new Color(156, 39, 176));
        userManagementButton.setForeground(Color.WHITE);
        userManagementButton.setPreferredSize(new Dimension(250, 50));
        
        logoutButton.setFont(buttonFont);
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setPreferredSize(new Dimension(250, 50));
        
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(70, 130, 180));
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        
        // Title
        JLabel titleLabel = new JLabel("Payroll Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0; gbc.gridy = 0;
        add(titleLabel, gbc);
        
        // Welcome message
        gbc.gridy = 1;
        add(welcomeLabel, gbc);
        
        // Menu buttons
        gbc.gridy = 2;
        add(employeeButton, gbc);
        
        gbc.gridy = 3;
        add(payrollButton, gbc);
        
        gbc.gridy = 4;
        add(reportsButton, gbc);
        
        gbc.gridy = 5;
        add(salaryComponentsButton, gbc);
        
        gbc.gridy = 6;
        add(userManagementButton, gbc);
        
        gbc.gridy = 7;
        add(logoutButton, gbc);
    }
    
    private void setupEventListeners() {
        employeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showEmployeePanel();
            }
        });
        
        payrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showPayrollPanel();
            }
        });
        
        reportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showReportsPanel();
            }
        });
        
        salaryComponentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showSalaryComponentsPanel();
            }
        });
        
        userManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showUserManagementPanel();
            }
        });
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.getAuthManager().logout();
                JOptionPane.showMessageDialog(MainMenuPanel.this, "Logged out successfully.");
                mainApp.showLoginPanel();
            }
        });
    }
    
    public void updateWelcomeMessage() {
        if (mainApp.getAuthManager().isLoggedIn()) {
            String username = mainApp.getAuthManager().getCurrentUser().getUsername();
            String role = mainApp.getAuthManager().getCurrentUser().getRoleString();
            welcomeLabel.setText("Welcome, " + username + " (" + role + ")!");
            
            // Show/hide user management button based on permissions
            models.User currentUser = mainApp.getAuthManager().getCurrentUser();
            userManagementButton.setVisible(currentUser != null && currentUser.hasPermission("user.manage"));
        }
    }
}
