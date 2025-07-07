package models;

/**
 * Enum representing different user roles in the system
 */
public enum Role {
    ADMIN("Admin", "Full system access including user management"),
    HR_OFFICER("HR Officer", "HR and payroll management access"),
    PAYROLL_OFFICER("Payroll Officer", "Payroll processing and employee data access"),
    EMPLOYEE("Employee", "Limited access to own payroll information");
    
    private final String displayName;
    private final String description;
    
    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
