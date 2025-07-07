package database;

import models.Employee;
import models.Payroll;
import models.User;
import models.SalaryComponent;
import models.EmployeeSalaryComponent;
import java.util.List;

/**
 * Data Access Object interface for database operations
 */
public interface DatabaseDAO {
    // User operations
    User authenticateUser(String username, String password);
    boolean insertUser(User user);
    
    // Employee operations
    List<Employee> getAllEmployees();
    List<Employee> getAllEmployeesForIdCheck(); // Get all employees (including inactive) for ID conflict checking
    Employee getEmployeeById(String employeeId);
    boolean insertEmployee(Employee employee);
    boolean updateEmployee(Employee employee);
    boolean deleteEmployee(String employeeId);
    
    // Payroll operations
    List<Payroll> getAllPayroll();
    List<Payroll> getPayrollByEmployee(String employeeId);
    boolean insertPayroll(Payroll payroll);
    boolean updatePayroll(Payroll payroll);
    boolean deletePayroll(int payrollId);
    
    // Salary Component operations
    List<SalaryComponent> getAllSalaryComponents();
    SalaryComponent getSalaryComponentById(int id);
    boolean insertSalaryComponent(SalaryComponent component);
    boolean updateSalaryComponent(SalaryComponent component);
    boolean deleteSalaryComponent(int id);
    
    // Employee Salary Component operations
    List<EmployeeSalaryComponent> getEmployeeSalaryComponents(String employeeId);
    boolean insertEmployeeSalaryComponent(EmployeeSalaryComponent empComponent);
    boolean updateEmployeeSalaryComponent(EmployeeSalaryComponent empComponent);
    boolean deleteEmployeeSalaryComponent(int id);
    
    // Database management
    boolean testConnection();
    void closeConnection();
}
