package gui;

import managers.AuthenticationManager;
import managers.EmployeeManager;
import managers.PayrollManager;
import managers.SalaryComponentManager;
import managers.UserManager;
import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI Application for Payroll Management System
 */
public class PayrollManagementSystemGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private transient AuthenticationManager authManager;
    private transient EmployeeManager employeeManager;
    private transient PayrollManager payrollManager;
    private transient SalaryComponentManager salaryComponentManager;
    private transient UserManager userManager;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    public PayrollManagementSystemGUI() {
        // Initialize database first
        try {
            DatabaseConnection.initializeDatabase();
            System.out.println("Database initialized successfully!");
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            
            // Show user-friendly error message
            String errorMessage = "Database connection failed!\n\n" +
                                "Common solutions:\n" +
                                "1. Make sure MySQL Server is running\n" +
                                "2. Check your database credentials in DatabaseConfig.java\n" +
                                "3. Run setup-database.bat for configuration help\n\n" +
                                "The application will continue in demo mode without database persistence.";
            
            JOptionPane.showMessageDialog(null, errorMessage, 
                "Database Connection Error", JOptionPane.WARNING_MESSAGE);
        }
        
        authManager = new AuthenticationManager();
        employeeManager = new EmployeeManager();
        payrollManager = new PayrollManager();
        salaryComponentManager = new SalaryComponentManager();
        userManager = authManager.getUserManager(); // Use the same UserManager instance
        
        // Set up dependencies
        payrollManager.setSalaryComponentManager(salaryComponentManager);
        
        initializeGUI();
        showLoginPanel();
    }
    
    private void initializeGUI() {
        setTitle("Payroll Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create and add panels
        LoginPanel loginPanel = new LoginPanel(this);
        MainMenuPanel mainMenuPanel = new MainMenuPanel(this);
        EmployeePanel employeePanel = new EmployeePanel(this);
        PayrollPanel payrollPanel = new PayrollPanel(this);
        ReportsPanel reportsPanel = new ReportsPanel(this);
        SalaryComponentPanel salaryComponentPanel = new SalaryComponentPanel(this);
        UserManagementPanel userManagementPanel = new UserManagementPanel(this);
        
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(mainMenuPanel, "MAIN_MENU");
        mainPanel.add(employeePanel, "EMPLOYEE");
        mainPanel.add(payrollPanel, "PAYROLL");
        mainPanel.add(reportsPanel, "REPORTS");
        mainPanel.add(salaryComponentPanel, "SALARY_COMPONENTS");
        mainPanel.add(userManagementPanel, "USER_MANAGEMENT");
        
        add(mainPanel);
    }
    
    public void showLoginPanel() {
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    public void showMainMenuPanel() {
        // Update welcome message when showing main menu
        Component[] components = mainPanel.getComponents();
        for (Component component : components) {
            if (component instanceof MainMenuPanel) {
                ((MainMenuPanel) component).updateWelcomeMessage();
                break;
            }
        }
        cardLayout.show(mainPanel, "MAIN_MENU");
    }
    
    public void showEmployeePanel() {
        cardLayout.show(mainPanel, "EMPLOYEE");
    }
    
    public void showPayrollPanel() {
        cardLayout.show(mainPanel, "PAYROLL");
    }
    
    public void showReportsPanel() {
        cardLayout.show(mainPanel, "REPORTS");
    }
    
    public void showSalaryComponentsPanel() {
        cardLayout.show(mainPanel, "SALARY_COMPONENTS");
    }
    
    public void showUserManagementPanel() {
        cardLayout.show(mainPanel, "USER_MANAGEMENT");
    }
    
    public AuthenticationManager getAuthManager() {
        return authManager;
    }
    
    public EmployeeManager getEmployeeManager() {
        return employeeManager;
    }
    
    public PayrollManager getPayrollManager() {
        return payrollManager;
    }
    
    public SalaryComponentManager getSalaryComponentManager() {
        return salaryComponentManager;
    }
    
    public UserManager getUserManager() {
        return userManager;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PayrollManagementSystemGUI().setVisible(true);
        });
    }
}
