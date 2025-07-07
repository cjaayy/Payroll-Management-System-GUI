package utils;

import managers.UserManager;
import managers.PasswordSecurity;
import models.User;
import database.DatabaseConnection;

/**
 * Debug authentication specifically
 */
public class AuthDebug {
    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeDatabase();
            
            UserManager userManager = new UserManager();
            
            System.out.println("=== Authentication Debug ===");
            
            // Test getting user by username
            User user = userManager.getUserByUsername("admin");
            if (user != null) {
                System.out.println("Found user: " + user.getUsername());
                System.out.println("Full name: " + user.getFullName());
                System.out.println("Role: " + user.getRole());
                System.out.println("Active: " + user.isActive());
                System.out.println("Has password hash: " + (user.getPasswordHash() != null));
                System.out.println("Has salt: " + (user.getSalt() != null));
                
                // Test password verification with known password
                System.out.println("\nTesting password verification...");
                if (user.getPasswordHash() != null && user.getSalt() != null) {
                    boolean isValid1 = PasswordSecurity.verifyPassword("admin123", user.getPasswordHash(), user.getSalt());
                    System.out.println("Password 'admin123' valid: " + isValid1);
                    
                    boolean isValid2 = PasswordSecurity.verifyPassword("TestSecure456!", user.getPasswordHash(), user.getSalt());
                    System.out.println("Password 'TestSecure456!' valid: " + isValid2);
                    
                    boolean isValid3 = PasswordSecurity.verifyPassword("NewSecure123!", user.getPasswordHash(), user.getSalt());
                    System.out.println("Password 'NewSecure123!' valid: " + isValid3);
                }
                
                // Test authentication through UserManager
                System.out.println("\nTesting authentication through UserManager...");
                User auth1 = userManager.authenticateUser("admin", "admin123");
                System.out.println("Authenticate with 'admin123': " + (auth1 != null));
                
                User auth2 = userManager.authenticateUser("admin", "TestSecure456!");
                System.out.println("Authenticate with 'TestSecure456!': " + (auth2 != null));
                
                User auth3 = userManager.authenticateUser("admin", "NewSecure123!");
                System.out.println("Authenticate with 'NewSecure123!': " + (auth3 != null));
                
            } else {
                System.out.println("User 'admin' not found!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
