package utils;

import managers.AuthenticationManager;
import managers.UserManager;
import models.User;

/**
 * Test HR user access levels and permissions
 */
public class HRAccessTest {
    
    private static final String[] PERMISSIONS = {
        "user.manage",
        "employee.manage", 
        "employee.create",
        "employee.edit",
        "employee.delete",
        "payroll.manage",
        "payroll.create",
        "payroll.edit",
        "reports.view"
    };
    
    public static void main(String[] args) {
        System.out.println("=== HR User Access Level Test ===");
        
        try {
            // Initialize UserManager
            UserManager userManager = new UserManager();
            
            // Test HR user permissions
            testHRUserPermissions(userManager);
            
            // Test AuthenticationManager role checking methods
            testAuthenticationManagerRoleChecks();
            
            // Test Admin user for comparison
            testAdminUserPermissions(userManager);
            
            System.out.println("\n=== Test Complete ===");
            
        } catch (Exception e) {
            System.err.println("Error during HR access test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testHRUserPermissions(UserManager userManager) {
        System.out.println("\n1. Testing HR user permissions:");
        User hrUser = userManager.getUserByUsername("hr.officer");
        if (hrUser != null) {
            System.out.println("HR User found: " + hrUser.getFullName() + " (" + hrUser.getRole() + ")");
            testUserPermissions(hrUser);
        } else {
            System.out.println("HR user not found!");
        }
    }
    
    private static void testAuthenticationManagerRoleChecks() {
        System.out.println("\n2. Testing AuthenticationManager role checks:");
        AuthenticationManager authManager = new AuthenticationManager();
        
        // Login as HR user
        if (authManager.login("hr.officer", "hr123")) {
            System.out.println("HR login successful");
            
            System.out.println("hasAdminRole(): " + authManager.hasAdminRole());
            System.out.println("hasHRRole(): " + authManager.hasHRRole());
            System.out.println("hasAdminOrHRRole(): " + authManager.hasAdminOrHRRole());
            System.out.println("hasEmployeeManagementAccess(): " + authManager.hasEmployeeManagementAccess());
            System.out.println("hasPayrollManagementAccess(): " + authManager.hasPayrollManagementAccess());
            System.out.println("hasUserManagementAccess(): " + authManager.hasUserManagementAccess());
        } else {
            System.out.println("HR login failed!");
        }
    }
    
    private static void testAdminUserPermissions(UserManager userManager) {
        System.out.println("\n3. Testing Admin user for comparison:");
        User adminUser = userManager.getUserByUsername("admin");
        if (adminUser != null) {
            System.out.println("Admin User found: " + adminUser.getFullName() + " (" + adminUser.getRole() + ")");
            testUserPermissions(adminUser);
        } else {
            System.out.println("Admin user not found!");
        }
    }
    
    private static void testUserPermissions(User user) {
        for (String permission : PERMISSIONS) {
            boolean hasPermission = user.hasPermission(permission);
            System.out.println("  " + permission + ": " + (hasPermission ? "✓ GRANTED" : "✗ DENIED"));
        }
    }
}
