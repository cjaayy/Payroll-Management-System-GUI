package managers;

import models.Employee;
import database.DatabaseDAO;
import database.MySQLDatabaseDAO;
import java.time.LocalDate;
import java.util.*;

/**
 * Employee Manager for managing employee operations with database persistence
 */
public class EmployeeManager {
    private DatabaseDAO databaseDAO;
    private int nextEmployeeId;
    
    public EmployeeManager() {
        databaseDAO = new MySQLDatabaseDAO();
        nextEmployeeId = getNextEmployeeId();
        // Don't initialize sample data - let the DatabaseConnection handle it
    }
    
    private int getNextEmployeeId() {
        List<Employee> employees = databaseDAO.getAllEmployees();
        int maxId = 0;
        for (Employee emp : employees) {
            if (emp.getEmployeeId() > maxId) {
                maxId = emp.getEmployeeId();
            }
        }
        return maxId + 1;
    }
    
    private String generateEmployeeStringId(int numericId) {
        return String.format("EMP%03d", numericId);
    }
    
    public void addEmployee(Employee employee) {
        // Set a proper string ID for database storage
        employee.setEmployeeId(nextEmployeeId++);
        databaseDAO.insertEmployee(employee);
    }
    
    public Employee createEmployee(String firstName, String lastName, String email, 
                                 String department, String position, double baseSalary, LocalDate hireDate) {
        Employee employee = new Employee(nextEmployeeId++, firstName, lastName, email, 
                                       department, position, baseSalary, hireDate);
        if (databaseDAO.insertEmployee(employee)) {
            return employee;
        }
        return null;
    }
    
    public Employee getEmployee(int employeeId) {
        String stringId = generateEmployeeStringId(employeeId);
        return databaseDAO.getEmployeeById(stringId);
    }
    
    public List<Employee> getAllEmployees() {
        return databaseDAO.getAllEmployees();
    }
    
    public List<Employee> getActiveEmployees() {
        return databaseDAO.getAllEmployees().stream()
                .filter(Employee::isActive)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public boolean updateEmployee(int employeeId, String firstName, String lastName, String email, 
                                String department, String position, double baseSalary) {
        String stringId = generateEmployeeStringId(employeeId);
        Employee employee = databaseDAO.getEmployeeById(stringId);
        if (employee != null) {
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setEmail(email);
            employee.setDepartment(department);
            employee.setPosition(position);
            employee.setBaseSalary(baseSalary);
            return databaseDAO.updateEmployee(employee);
        }
        return false;
    }
    
    public boolean deleteEmployee(int employeeId) {
        String stringId = generateEmployeeStringId(employeeId);
        return databaseDAO.deleteEmployee(stringId);
    }
    
    public List<Employee> searchEmployees(String keyword) {
        return databaseDAO.getAllEmployees().stream()
                .filter(emp -> emp.getFullName().toLowerCase().contains(keyword.toLowerCase()) ||
                              emp.getDepartment().toLowerCase().contains(keyword.toLowerCase()) ||
                              emp.getPosition().toLowerCase().contains(keyword.toLowerCase()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
