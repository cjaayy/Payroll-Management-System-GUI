package utils;

import managers.PasswordSecurity;
import database.DatabaseConnection;
import java.sql.*;

/**
 * Password Reset Tool - Resets admin password to default
 */
public class PasswordReset {
    public static void main(String[] args) {
        try {
            System.out.println("=== Password Reset Tool ===");
            
            // Initialize database
            DatabaseConnection.initializeDatabase();
            
            // Check current admin user
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT user_id, username, email, full_name FROM users WHERE username = 'admin'")) {
                
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    System.out.println("Found admin user: " + username);
                    System.out.println("User ID: " + userId);
                    System.out.println("Email: " + rs.getString("email"));
                    System.out.println("Full Name: " + rs.getString("full_name"));
                    
                    // Reset password to default
                    String newPassword = "admin123";
                    String salt = PasswordSecurity.generateSalt();
                    String hashedPassword = PasswordSecurity.hashPassword(newPassword, salt);
                    
                    String updateSql = "UPDATE users SET password_hash = ?, salt = ?, " +
                                     "last_password_change = CURRENT_TIMESTAMP, " +
                                     "failed_login_attempts = 0, lockout_until = NULL " +
                                     "WHERE user_id = ?";
                    
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, hashedPassword);
                        updateStmt.setString(2, salt);
                        updateStmt.setInt(3, userId);
                        
                        int rowsAffected = updateStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("\n✅ SUCCESS: Admin password has been reset!");
                            System.out.println("Username: admin");
                            System.out.println("Password: admin123");
                            System.out.println("\nYou can now login with these credentials.");
                            System.out.println("Please change the password after logging in for security.");
                        } else {
                            System.out.println("❌ Failed to update password");
                        }
                    }
                    
                } else {
                    System.out.println("❌ Admin user not found! Creating new admin user...");
                    
                    // Create new admin user
                    String salt = PasswordSecurity.generateSalt();
                    String hashedPassword = PasswordSecurity.hashPassword("admin123", salt);
                    
                    String insertSql = "INSERT INTO users (username, password_hash, salt, email, full_name, role, created_by) " +
                                     "VALUES ('admin', ?, ?, 'admin@company.com', 'System Administrator', 'ADMIN', 'SYSTEM')";
                    
                    try (Connection conn2 = DatabaseConnection.getConnection();
                         PreparedStatement insertStmt = conn2.prepareStatement(insertSql)) {
                        insertStmt.setString(1, hashedPassword);
                        insertStmt.setString(2, salt);
                        
                        int rowsAffected = insertStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("✅ New admin user created!");
                            System.out.println("Username: admin");
                            System.out.println("Password: admin123");
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
