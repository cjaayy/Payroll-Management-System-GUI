package managers;

import models.User;
import models.Role;
import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive User Manager with security features and audit trail
 */
public class UserManager {
    private AuditTrailManager auditTrailManager;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;
    
    @SuppressWarnings("this-escape")
    public UserManager() {
        this.auditTrailManager = new AuditTrailManager();
        initializeDefaultAdmin();
    }
    
    /**
     * Initialize default admin user if no users exist
     */
    private void initializeDefaultAdmin() {
        try {
            if (getAllUsers().isEmpty()) {
                createDefaultAdmin();
            }
        } catch (Exception e) {
            System.err.println("Error initializing default admin: " + e.getMessage());
        }
    }
    
    /**
     * Creates default admin user
     */
    private void createDefaultAdmin() {
        try {
            String salt = PasswordSecurity.generateSalt();
            String hashedPassword = PasswordSecurity.hashPassword("admin123", salt);
            
            User admin = new User("admin", hashedPassword, salt, "admin@company.com", 
                                "System Administrator", Role.ADMIN, "SYSTEM");
            
            createUser(admin, "SYSTEM");
            System.out.println("Default admin user created. Username: admin, Password: admin123");
            System.out.println("Please change the default password immediately!");
        } catch (Exception e) {
            System.err.println("Error creating default admin: " + e.getMessage());
        }
    }
    
    /**
     * Creates a new user with audit trail
     */
    public boolean createUser(User user, String createdBy) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        
        // Check if username already exists
        if (getUserByUsername(user.getUsername()) != null) {
            auditTrailManager.logAction(createdBy, "CREATE_USER_FAILED", "users", 
                                      user.getUsername(), "Username already exists");
            return false;
        }
        
        // Validate password if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            PasswordSecurity.PasswordStrength strength = 
                PasswordSecurity.validatePasswordStrength(user.getPassword());
            if (!strength.isValid()) {
                auditTrailManager.logAction(createdBy, "CREATE_USER_FAILED", "users", 
                                          user.getUsername(), "Weak password: " + strength.getMessage());
                return false;
            }
            
            // Hash the password
            String salt = PasswordSecurity.generateSalt();
            String hashedPassword = PasswordSecurity.hashPassword(user.getPassword(), salt);
            user.setPasswordHash(hashedPassword);
            user.setSalt(salt);
        }
        
        String sql = "INSERT INTO users (username, password_hash, salt, email, full_name, role, " +
                    "is_active, created_at, last_password_change, failed_login_attempts, " +
                    "created_by, last_modified_by, last_modified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getSalt());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getFullName());
            pstmt.setString(6, user.getRole().name());
            pstmt.setBoolean(7, user.isActive());
            pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(10, 0);
            pstmt.setString(11, createdBy);
            pstmt.setString(12, createdBy);
            pstmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
                
                auditTrailManager.logAction(createdBy, "CREATE_USER", "users", 
                                          String.valueOf(user.getUserId()), 
                                          "User created: " + user.getUsername());
                return true;
            }
            
        } catch (SQLException e) {
            auditTrailManager.logAction(createdBy, "CREATE_USER_FAILED", "users", 
                                      user.getUsername(), "Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Authenticates user and returns user object if successful
     */
    public User authenticateUser(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty()) {
            return null;
        }
        
        User user = getUserByUsername(username);
        if (user == null) {
            auditTrailManager.logAction(username, "LOGIN_FAILED", "users", 
                                      username, "User not found");
            return null;
        }
        
        // Check if account is locked
        if (user.isLocked()) {
            auditTrailManager.logAction(username, "LOGIN_FAILED", "users", 
                                      String.valueOf(user.getUserId()), "Account is locked");
            return null;
        }
        
        // Check if account is active
        if (!user.isActive()) {
            auditTrailManager.logAction(username, "LOGIN_FAILED", "users", 
                                      String.valueOf(user.getUserId()), "Account is inactive");
            return null;
        }
        
        // Verify password
        boolean passwordValid = false;
        if (user.getPasswordHash() != null && user.getSalt() != null) {
            passwordValid = PasswordSecurity.verifyPassword(password, user.getPasswordHash(), user.getSalt());
        } else if (user.getPassword() != null) {
            // Legacy password check
            passwordValid = user.getPassword().equals(password);
        }
        
        if (passwordValid) {
            // Reset failed login attempts
            resetFailedLoginAttempts(user.getUserId());
            
            // Update last login
            updateLastLogin(user.getUserId());
            
            auditTrailManager.logAction(username, "LOGIN_SUCCESS", "users", 
                                      String.valueOf(user.getUserId()), "Successful login");
            
            return user;
        } else {
            // Increment failed login attempts
            incrementFailedLoginAttempts(user.getUserId());
            
            auditTrailManager.logAction(username, "LOGIN_FAILED", "users", 
                                      String.valueOf(user.getUserId()), "Invalid password");
            return null;
        }
    }
    
    /**
     * Gets user by username
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Updates user information
     */
    public boolean updateUser(User user, String modifiedBy) {
        String sql = "UPDATE users SET email = ?, full_name = ?, role = ?, is_active = ?, " +
                    "last_modified_by = ?, last_modified = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getFullName());
            pstmt.setString(3, user.getRole().name());
            pstmt.setBoolean(4, user.isActive());
            pstmt.setString(5, modifiedBy);
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(7, user.getUserId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                auditTrailManager.logAction(modifiedBy, "UPDATE_USER", "users", 
                                          String.valueOf(user.getUserId()), 
                                          "User updated: " + user.getUsername());
                return true;
            }
            
        } catch (SQLException e) {
            auditTrailManager.logAction(modifiedBy, "UPDATE_USER_FAILED", "users", 
                                      String.valueOf(user.getUserId()), 
                                      "Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Changes user password
     */
    public boolean changePassword(int userId, String newPassword, String changedBy) {
        // Validate password strength
        PasswordSecurity.PasswordStrength strength = 
            PasswordSecurity.validatePasswordStrength(newPassword);
        if (!strength.isValid()) {
            auditTrailManager.logAction(changedBy, "CHANGE_PASSWORD_FAILED", "users", 
                                      String.valueOf(userId), "Weak password: " + strength.getMessage());
            return false;
        }
        
        String salt = PasswordSecurity.generateSalt();
        String hashedPassword = PasswordSecurity.hashPassword(newPassword, salt);
        
        String sql = "UPDATE users SET password_hash = ?, salt = ?, last_password_change = ?, " +
                    "failed_login_attempts = 0, lockout_until = NULL, last_modified_by = ?, " +
                    "last_modified = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, salt);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(4, changedBy);
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(6, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                auditTrailManager.logAction(changedBy, "CHANGE_PASSWORD", "users", 
                                          String.valueOf(userId), "Password changed");
                return true;
            }
            
        } catch (SQLException e) {
            auditTrailManager.logAction(changedBy, "CHANGE_PASSWORD_FAILED", "users", 
                                      String.valueOf(userId), "Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Deactivates a user
     */
    public boolean deactivateUser(int userId, String deactivatedBy) {
        String sql = "UPDATE users SET is_active = FALSE, last_modified_by = ?, last_modified = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, deactivatedBy);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(3, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                auditTrailManager.logAction(deactivatedBy, "DEACTIVATE_USER", "users", 
                                          String.valueOf(userId), "User deactivated");
                return true;
            }
            
        } catch (SQLException e) {
            auditTrailManager.logAction(deactivatedBy, "DEACTIVATE_USER_FAILED", "users", 
                                      String.valueOf(userId), "Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Helper methods
    private void incrementFailedLoginAttempts(int userId) {
        String sql = "UPDATE users SET failed_login_attempts = failed_login_attempts + 1, " +
                    "lockout_until = CASE WHEN failed_login_attempts + 1 >= ? THEN ? ELSE lockout_until END " +
                    "WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, MAX_LOGIN_ATTEMPTS);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES)));
            pstmt.setInt(3, userId);
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void resetFailedLoginAttempts(int userId) {
        String sql = "UPDATE users SET failed_login_attempts = 0, lockout_until = NULL WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, userId);
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setSalt(rs.getString("salt"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        
        String roleStr = rs.getString("role");
        if (roleStr != null) {
            try {
                user.setRole(Role.valueOf(roleStr));
            } catch (IllegalArgumentException e) {
                user.setRole(Role.EMPLOYEE);
            }
        }
        
        user.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        Timestamp lastPasswordChange = rs.getTimestamp("last_password_change");
        if (lastPasswordChange != null) {
            user.setLastPasswordChange(lastPasswordChange.toLocalDateTime());
        }
        
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        
        Timestamp lockoutUntil = rs.getTimestamp("lockout_until");
        if (lockoutUntil != null) {
            user.setLockoutUntil(lockoutUntil.toLocalDateTime());
        }
        
        user.setCreatedBy(rs.getString("created_by"));
        user.setLastModifiedBy(rs.getString("last_modified_by"));
        
        Timestamp lastModified = rs.getTimestamp("last_modified");
        if (lastModified != null) {
            user.setLastModified(lastModified.toLocalDateTime());
        }
        
        return user;
    }
    
    public AuditTrailManager getAuditTrailManager() {
        return auditTrailManager;
    }
}
