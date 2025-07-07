package utils;

import database.DatabaseConnection;
import java.sql.*;

/**
 * Debug tool to inspect database contents
 */
public class DatabaseDebug {
    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeDatabase();
            
            System.out.println("=== Database Debug Information ===");
            
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                // List all tables
                System.out.println("\n--- Tables in database ---");
                ResultSet tables = stmt.executeQuery("SHOW TABLES");
                while (tables.next()) {
                    System.out.println("Table: " + tables.getString(1));
                }
                
                // Check users table structure
                System.out.println("\n--- Users table structure ---");
                try {
                    ResultSet columns = stmt.executeQuery("DESCRIBE users");
                    while (columns.next()) {
                        System.out.println("Column: " + columns.getString("Field") + 
                                         " - Type: " + columns.getString("Type") + 
                                         " - Null: " + columns.getString("Null") + 
                                         " - Key: " + columns.getString("Key"));
                    }
                } catch (SQLException e) {
                    System.out.println("Users table doesn't exist or error: " + e.getMessage());
                }
                
                // Check users table contents
                System.out.println("\n--- Users table contents ---");
                try {
                    ResultSet users = stmt.executeQuery("SELECT user_id, username, email, full_name, role, is_active, created_at, password_hash, salt FROM users");
                    int count = 0;
                    while (users.next()) {
                        count++;
                        System.out.println("User " + count + ":");
                        System.out.println("  ID: " + users.getInt("user_id"));
                        System.out.println("  Username: " + users.getString("username"));
                        System.out.println("  Email: " + users.getString("email"));
                        System.out.println("  Full Name: " + users.getString("full_name"));
                        System.out.println("  Role: " + users.getString("role"));
                        System.out.println("  Active: " + users.getBoolean("is_active"));
                        System.out.println("  Created: " + users.getTimestamp("created_at"));
                        System.out.println("  Password Hash: " + (users.getString("password_hash") != null ? "EXISTS" : "NULL"));
                        System.out.println("  Salt: " + (users.getString("salt") != null ? "EXISTS" : "NULL"));
                    }
                    if (count == 0) {
                        System.out.println("No users found in database");
                    }
                } catch (SQLException e) {
                    System.out.println("Error reading users: " + e.getMessage());
                }
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
