package utils;

import database.DatabaseConnection;
import managers.PasswordSecurity;
import java.sql.*;

/**
 * HR User Setup Tool - Creates HR users and shows their credentials
 */
public class HRUserSetup {
    public static void main(String[] args) {
        try {
            System.out.println("=== HR User Setup Tool ===");
            
            // Initialize database
            DatabaseConnection.initializeDatabase();
            
            // Check existing HR users
            System.out.println("\n--- Current HR Users ---");
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT user_id, username, email, full_name, role, is_active FROM users WHERE role IN ('HR_OFFICER', 'PAYROLL_OFFICER')")) {
                
                ResultSet rs = pstmt.executeQuery();
                boolean hasHRUsers = false;
                while (rs.next()) {
                    hasHRUsers = true;
                    System.out.println("User: " + rs.getString("username"));
                    System.out.println("  Full Name: " + rs.getString("full_name"));
                    System.out.println("  Email: " + rs.getString("email"));
                    System.out.println("  Role: " + rs.getString("role"));
                    System.out.println("  Active: " + rs.getBoolean("is_active"));
                    System.out.println();
                }
                
                if (!hasHRUsers) {
                    System.out.println("No HR users found. Creating default HR users...");
                    createDefaultHRUsers(conn);
                } else {
                    System.out.println("HR users exist. Do you want to reset their passwords? (y/n)");
                    // For automation, we'll reset them
                    resetHRPasswords(conn);
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createDefaultHRUsers(Connection conn) throws SQLException {
        System.out.println("\nCreating default HR users...");
        
        // Create HR Officer
        createUser(conn, "hr.officer", "hr123", "hr.officer@company.com", "HR Officer", "HR_OFFICER");
        
        // Create Payroll Officer
        createUser(conn, "payroll.officer", "payroll123", "payroll.officer@company.com", "Payroll Officer", "PAYROLL_OFFICER");
        
        // Create sample Employee user
        createUser(conn, "employee1", "emp123", "employee1@company.com", "John Employee", "EMPLOYEE");
        
        System.out.println("\n✅ Default users created successfully!");
        printCredentials();
    }
    
    private static void resetHRPasswords(Connection conn) throws SQLException {
        System.out.println("\nResetting HR user passwords...");
        
        // Reset known HR users to default passwords
        resetUserPassword(conn, "hr.officer", "hr123");
        resetUserPassword(conn, "payroll.officer", "payroll123");
        resetUserPassword(conn, "employee1", "emp123");
        
        System.out.println("\n✅ HR user passwords reset!");
        printCredentials();
    }
    
    private static void createUser(Connection conn, String username, String password, String email, String fullName, String role) throws SQLException {
        String salt = PasswordSecurity.generateSalt();
        String hashedPassword = PasswordSecurity.hashPassword(password, salt);
        
        String sql = "INSERT IGNORE INTO users (username, password_hash, salt, email, full_name, role, created_by, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 'SYSTEM', true)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, salt);
            pstmt.setString(4, email);
            pstmt.setString(5, fullName);
            pstmt.setString(6, role);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Created user: " + username + " (" + role + ")");
            } else {
                System.out.println("User already exists: " + username);
            }
        }
    }
    
    private static void resetUserPassword(Connection conn, String username, String password) throws SQLException {
        String salt = PasswordSecurity.generateSalt();
        String hashedPassword = PasswordSecurity.hashPassword(password, salt);
        
        String sql = "UPDATE users SET password_hash = ?, salt = ?, last_password_change = CURRENT_TIMESTAMP, " +
                    "failed_login_attempts = 0, lockout_until = NULL WHERE username = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, salt);
            pstmt.setString(3, username);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Reset password for: " + username);
            }
        }
    }
    
    private static void printCredentials() {
        System.out.println("\n=== LOGIN CREDENTIALS ===");
        System.out.println("🔑 ADMIN ACCESS:");
        System.out.println("   Username: admin");
        System.out.println("   Password: admin123");
        System.out.println("   Role: Full system access");
        
        System.out.println("\n👥 HR OFFICER ACCESS:");
        System.out.println("   Username: hr.officer");
        System.out.println("   Password: hr123");
        System.out.println("   Role: Employee & user management");
        
        System.out.println("\n💰 PAYROLL OFFICER ACCESS:");
        System.out.println("   Username: payroll.officer");
        System.out.println("   Password: payroll123");
        System.out.println("   Role: Payroll processing & reports");
        
        System.out.println("\n👤 EMPLOYEE ACCESS:");
        System.out.println("   Username: employee1");
        System.out.println("   Password: emp123");
        System.out.println("   Role: View own payroll information");
        
        System.out.println("\n📝 NOTE: Please change default passwords after first login for security!");
    }
}
