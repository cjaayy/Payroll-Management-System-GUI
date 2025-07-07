package models;

import java.time.LocalDateTime;

/**
 * Enhanced User model with comprehensive security and audit features
 */
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String salt;
    private String email;
    private String fullName;
    private Role role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private LocalDateTime lastPasswordChange;
    private int failedLoginAttempts;
    private LocalDateTime lockoutUntil;
    private String createdBy;
    private String lastModifiedBy;
    private LocalDateTime lastModified;
    
    // Legacy fields for backward compatibility
    private String password; // Deprecated - use passwordHash instead
    
    // Constructors
    public User() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.failedLoginAttempts = 0;
    }
    
    public User(String username, String password, String role) {
        this();
        this.username = username;
        this.password = password; // Legacy constructor
        this.role = Role.valueOf(role.toUpperCase().replace(" ", "_"));
    }
    
    public User(String username, String passwordHash, String salt, String email, 
                String fullName, Role role, String createdBy) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.createdBy = createdBy;
        this.lastModifiedBy = createdBy;
        this.lastModified = LocalDateTime.now();
        this.lastPasswordChange = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getSalt() {
        return salt;
    }
    
    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public LocalDateTime getLastPasswordChange() {
        return lastPasswordChange;
    }
    
    public void setLastPasswordChange(LocalDateTime lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }
    
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
    
    public LocalDateTime getLockoutUntil() {
        return lockoutUntil;
    }
    
    public void setLockoutUntil(LocalDateTime lockoutUntil) {
        this.lockoutUntil = lockoutUntil;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }
    
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
    
    public LocalDateTime getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
    
    // Legacy methods for backward compatibility
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRoleString() {
        return role != null ? role.name().toLowerCase() : "employee";
    }
    
    public void setRoleString(String role) {
        try {
            this.role = Role.valueOf(role.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            this.role = Role.EMPLOYEE;
        }
    }
    
    // Utility methods
    public boolean isLocked() {
        return lockoutUntil != null && lockoutUntil.isAfter(LocalDateTime.now());
    }
    
    public boolean requiresPasswordChange() {
        return lastPasswordChange != null && 
               lastPasswordChange.isBefore(LocalDateTime.now().minusDays(90));
    }
    
    public boolean hasPermission(String permission) {
        if (role == null) return false;
        
        switch (role) {
            case ADMIN:
                return true; // Admin has all permissions
            case HR_OFFICER:
                return permission.startsWith("employee.") || 
                       permission.startsWith("payroll.") ||
                       permission.startsWith("reports.") ||
                       permission.equals("user.manage") || // HR can manage users
                       permission.startsWith("user.read") ||
                       permission.startsWith("user.create") ||
                       permission.startsWith("user.edit") ||
                       permission.startsWith("user.deactivate");
            case PAYROLL_OFFICER:
                return permission.startsWith("payroll.") ||
                       permission.startsWith("employee.read") ||
                       permission.startsWith("reports.");
            case EMPLOYEE:
                return permission.startsWith("employee.read.own") ||
                       permission.startsWith("payroll.read.own");
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %s", 
                           fullName != null ? fullName : username, 
                           username, 
                           role != null ? role.getDisplayName() : "Unknown");
    }
}
