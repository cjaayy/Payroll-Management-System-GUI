package managers;

import models.User;

/**
 * Authentication Manager for user login/logout
 */
public class AuthenticationManager {
    private UserManager userManager;
    private User currentUser;
    
    public AuthenticationManager() {
        this.userManager = new UserManager();
    }
    
    public boolean login(String username, String password) {
        try {
            User user = userManager.authenticateUser(username, password);
            if (user != null) {
                currentUser = user;
                return true;
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return false;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean hasAdminRole() {
        return currentUser != null && currentUser.getRole() != null && 
               currentUser.getRole().name().equals("ADMIN");
    }
    
    public boolean hasHRRole() {
        return currentUser != null && currentUser.getRole() != null && 
               currentUser.getRole().name().equals("HR_OFFICER");
    }
    
    public boolean hasPayrollRole() {
        return currentUser != null && currentUser.getRole() != null && 
               currentUser.getRole().name().equals("PAYROLL_OFFICER");
    }
    
    public boolean hasAdminOrHRRole() {
        return hasAdminRole() || hasHRRole();
    }
    
    public boolean hasAdminOrPayrollRole() {
        return hasAdminRole() || hasPayrollRole();
    }
    
    public boolean hasAdminOrHROrPayrollRole() {
        return hasAdminRole() || hasHRRole() || hasPayrollRole();
    }
    
    public boolean hasEmployeeManagementAccess() {
        return currentUser != null && currentUser.hasPermission("employee.manage");
    }
    
    public boolean hasPayrollManagementAccess() {
        return currentUser != null && currentUser.hasPermission("payroll.manage");
    }
    
    public boolean hasUserManagementAccess() {
        return currentUser != null && currentUser.hasPermission("user.manage");
    }
    
    public UserManager getUserManager() {
        return userManager;
    }
    
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser != null) {
            try {
                // Use UserManager to verify old password and change to new one
                return userManager.changePassword(currentUser.getUserId(), newPassword, currentUser.getUsername());
            } catch (Exception e) {
                System.err.println("Password change error: " + e.getMessage());
            }
        }
        return false;
    }
    
    public boolean refreshCurrentUser() {
        if (currentUser != null) {
            try {
                User refreshedUser = userManager.getUserByUsername(currentUser.getUsername());
                if (refreshedUser != null) {
                    currentUser = refreshedUser;
                    return true;
                }
            } catch (Exception e) {
                System.err.println("User refresh error: " + e.getMessage());
            }
        }
        return false;
    }
}
