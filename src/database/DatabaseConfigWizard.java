package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Database Configuration Wizard
 * Helps users set up their MySQL database connection
 */
public class DatabaseConfigWizard {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== MySQL Database Configuration Wizard ===");
        System.out.println();
        
        // Test current configuration
        System.out.println("Testing current database configuration...");
        if (testConnection(DatabaseConfig.DB_USERNAME, DatabaseConfig.DB_PASSWORD)) {
            System.out.println("✓ Current configuration works! No changes needed.");
            System.out.println("You can run the application now.");
            scanner.close();
            return;
        }
        
        System.out.println("✗ Current configuration failed. Let's set it up...");
        System.out.println();
        
        // Get MySQL credentials from user
        System.out.println("Please enter your MySQL database credentials:");
        System.out.print("MySQL Username (default: root): ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            username = "root";
        }
        
        System.out.print("MySQL Password (leave empty if no password): ");
        String password = scanner.nextLine();
        
        // Test the credentials
        System.out.println("\nTesting connection with provided credentials...");
        if (testConnection(username, password)) {
            System.out.println("✓ Connection successful!");
            System.out.println();
            System.out.println("Update your DatabaseConfig.java file with these credentials:");
            System.out.println("public static final String DB_USERNAME = \"" + username + "\";");
            System.out.println("public static final String DB_PASSWORD = \"" + password + "\";");
            System.out.println();
            System.out.println("Location: src/database/DatabaseConfig.java");
        } else {
            System.out.println("✗ Connection failed with provided credentials.");
            System.out.println();
            System.out.println("Please check:");
            System.out.println("1. MySQL Server is running");
            System.out.println("2. Username and password are correct");
            System.out.println("3. MySQL is running on localhost:3306");
        }
        
        scanner.close();
    }
    
    private static boolean testConnection(String username, String password) {
        try {
            String url = "jdbc:mysql://localhost:3306/";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            conn.close();
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            return false;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}
