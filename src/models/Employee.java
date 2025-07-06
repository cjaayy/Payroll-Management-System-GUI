package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Employee class representing an employee in the payroll system
 */
public class Employee {
    private int employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String position;
    private double baseSalary;
    private LocalDate hireDate;
    private boolean isActive;
    
    public Employee(int employeeId, String firstName, String lastName, String email, 
                   String department, String position, double baseSalary, LocalDate hireDate) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = null;
        this.department = department;
        this.position = position;
        this.baseSalary = baseSalary;
        this.hireDate = hireDate;
        this.isActive = true;
    }
    
    // Constructor for database operations
    public Employee(String employeeId, String firstName, String lastName, String email, 
                   String phone, String department, String position, java.util.Date hireDate, double baseSalary) {
        // Extract numeric part from string ID (e.g., "EMP001" -> 1)
        if (employeeId.startsWith("EMP")) {
            try {
                this.employeeId = Integer.parseInt(employeeId.substring(3));
            } catch (NumberFormatException e) {
                System.err.println("Error parsing employee ID: " + employeeId);
                this.employeeId = 0; // Default value
            }
        } else {
            try {
                this.employeeId = Integer.parseInt(employeeId);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing employee ID: " + employeeId);
                this.employeeId = 0; // Default value
            }
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone != null ? phone : "";
        this.department = department;
        this.position = position;
        this.baseSalary = baseSalary;
        this.hireDate = new java.sql.Date(hireDate.getTime()).toLocalDate();
        this.isActive = true;
    }
    
    // Getters and setters
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }
    public double getSalary() { return baseSalary; } // Alias for database compatibility
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return String.format("ID: %d | %s | %s | %s | $%.2f | %s", 
                employeeId, getFullName(), department, position, baseSalary, 
                hireDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}
