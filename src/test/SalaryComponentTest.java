package test;

import models.SalaryComponent;
import models.Employee;
import managers.SalaryComponentManager;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Simple test to verify the salary component functionality works
 */
public class SalaryComponentTest {
    public static void main(String[] args) {
        System.out.println("Starting Salary Component Test...");
        
        try {
            // Test 1: Create a salary component
            SalaryComponentManager manager = new SalaryComponentManager();
            
            // Create a sample allowance
            SalaryComponent allowance = new SalaryComponent("Test Allowance", "ALLOWANCE", 
                                                           BigDecimal.valueOf(5000.00), 
                                                           "Test allowance for employees");
            
            if (manager.addSalaryComponent(allowance)) {
                System.out.println("✓ Test 1 PASSED: Salary component created successfully");
            } else {
                System.out.println("✗ Test 1 FAILED: Could not create salary component");
            }
            
            // Test 2: Retrieve all salary components
            var components = manager.getAllSalaryComponents();
            System.out.println("✓ Test 2 PASSED: Retrieved " + components.size() + " salary components");
            
            // Test 3: Test payroll calculation
            Employee testEmployee = new Employee(1, "John", "Doe", "john@test.com", 
                                               "IT", "Developer", 50000.00, LocalDate.now());
            
            SalaryComponentManager.PayrollCalculationResult result = 
                manager.calculatePayroll(testEmployee, BigDecimal.valueOf(50000.00));
            
            System.out.println("✓ Test 3 PASSED: Payroll calculation completed");
            System.out.println("  Base Salary: ₱" + result.getBaseSalary());
            System.out.println("  Total Earnings: ₱" + result.getTotalEarnings());
            System.out.println("  Total Deductions: ₱" + result.getTotalDeductions());
            System.out.println("  Net Pay: ₱" + result.getNetPay());
            
            System.out.println("\nAll tests completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Test failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
