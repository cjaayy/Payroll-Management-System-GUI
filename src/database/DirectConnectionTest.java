package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Direct MySQL Connection Test with your credentials
 */
public class DirectConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/";
        String username = "root";
        String password = "Jisoo@010322";
        
        System.out.println("Testing direct MySQL connection...");
        System.out.println("URL: " + url);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println();
        
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ MySQL JDBC Driver loaded successfully");
            
            // Test connection
            System.out.println("Attempting to connect...");
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Connection successful!");
            
            // Test database operations
            System.out.println("Testing database operations...");
            java.sql.Statement stmt = conn.createStatement();
            
            // Create database
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS payroll_system");
            System.out.println("✓ Database created/verified");
            
            // Switch to database
            stmt.executeUpdate("USE payroll_system");
            System.out.println("✓ Database selected");
            
            // Test query
            java.sql.ResultSet rs = stmt.executeQuery("SELECT DATABASE() as current_db");
            if (rs.next()) {
                System.out.println("✓ Current database: " + rs.getString("current_db"));
            }
            
            // Clean up
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println("\n🎉 ALL TESTS PASSED! Your credentials work perfectly.");
            System.out.println("You can now run the main application with: run.bat");
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL JDBC Driver not found!");
            System.err.println("Make sure mysql-connector-*.jar is in the lib/ directory");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println();
            System.err.println("Common solutions:");
            System.err.println("1. Check if MySQL Server is running");
            System.err.println("2. Verify username and password");
            System.err.println("3. Check if root user has login permissions");
            System.err.println("4. Try connecting via MySQL Workbench first");
            e.printStackTrace();
        }
    }
}
