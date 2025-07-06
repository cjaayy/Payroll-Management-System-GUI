package database;

import java.sql.*;

/**
 * Database Schema Updater
 */
public class DatabaseSchemaUpdater {
    public static void main(String[] args) {
        try {
            // Initialize database connection
            DatabaseConnection.initializeDatabase();
            Connection conn = DatabaseConnection.getConnection();
            
            // Drop and recreate the employee_contact_info table
            Statement stmt = conn.createStatement();
            
            System.out.println("Dropping employee_contact_info table...");
            stmt.executeUpdate("DROP TABLE IF EXISTS employee_contact_info");
            
            System.out.println("Recreating employee_contact_info table...");
            String createContactInfoTable = "CREATE TABLE IF NOT EXISTS employee_contact_info (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "employee_id VARCHAR(20) NOT NULL, " +
                "personal_email VARCHAR(100), " +
                "work_phone VARCHAR(20), " +
                "emergency_contact VARCHAR(100), " +
                "emergency_phone VARCHAR(20), " +
                "street_address TEXT, " +
                "barangay VARCHAR(100), " +
                "city VARCHAR(50), " +
                "province_state VARCHAR(50), " +
                "country VARCHAR(50), " +
                "zip_code VARCHAR(10), " +
                "birth_date DATE, " +
                "social_security_number VARCHAR(20), " +
                "nationality VARCHAR(50), " +
                "marital_status VARCHAR(20), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE" +
                ")";
            stmt.executeUpdate(createContactInfoTable);
            
            System.out.println("Table recreated successfully!");
            
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.err.println("Error updating database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
