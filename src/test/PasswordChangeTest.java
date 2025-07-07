package test;

import managers.AuthenticationManager;
import models.User;
import database.DatabaseConnection;

/**
 * Simple test to verify password change functionality
 */
public class PasswordChangeTest {
    public static void main(String[] args) {
        try {
            // Initialize database
            DatabaseConnection.initializeDatabase();
            
            // Test authentication
            AuthenticationManager authManager = new AuthenticationManager();
            
            System.out.println("Testing login with default password...");
            boolean loginSuccess = authManager.login("admin", "admin123");
            System.out.println("Login successful: " + loginSuccess);
            
            if (loginSuccess) {
                User currentUser = authManager.getCurrentUser();
                System.out.println("Current user: " + currentUser.getUsername());
                System.out.println("Full name: " + currentUser.getFullName());
                System.out.println("Email: " + currentUser.getEmail());
                System.out.println("Role: " + currentUser.getRole());
                System.out.println("Last login: " + currentUser.getLastLogin());
                System.out.println("Created at: " + currentUser.getCreatedAt());
                
                // Test password change
                System.out.println("\nTesting password change...");
                boolean changeSuccess = authManager.changePassword("admin123", "NewSecure123!");
                System.out.println("Password change successful: " + changeSuccess);
                
                if (changeSuccess) {
                    // Test login with new password
                    authManager.logout();
                    System.out.println("\nTesting login with new password...");
                    boolean newLoginSuccess = authManager.login("admin", "NewSecure123!");
                    System.out.println("New login successful: " + newLoginSuccess);
                    
                    if (newLoginSuccess) {
                        User refreshedUser = authManager.getCurrentUser();
                        System.out.println("Last login after password change: " + refreshedUser.getLastLogin());
                        System.out.println("Last password change: " + refreshedUser.getLastPasswordChange());
                    }
                    
                    // Change password back to original
                    System.out.println("\nChanging password back to original...");
                    authManager.changePassword("NewSecure123!", "admin123");
                    System.out.println("Password restored to original");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
