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
    
    public EmployeeManager() {
        databaseDAO = new MySQLDatabaseDAO();
        // Don't pre-calculate nextEmployeeId, calculate it fresh each time
        // Don't initialize sample data - let the DatabaseConnection handle it
    }
    
    private int getNextEmployeeId() {
        List<Employee> employees = databaseDAO.getAllEmployees();
        System.out.println("Found " + employees.size() + " existing employees in database:");
        int maxId = 0;
        for (Employee emp : employees) {
            System.out.println("  - Employee ID: " + emp.getEmployeeId() + " (" + emp.getFullName() + ")");
            if (emp.getEmployeeId() > maxId) {
                maxId = emp.getEmployeeId();
            }
        }
        int nextId = maxId + 1;
        System.out.println("Next available ID: " + nextId);
        return nextId;
    }
    
    private String generateEmployeeStringId(int numericId) {
        return String.format("EMP%03d", numericId);
    }
    
    public void addEmployee(Employee employee) {
        // Set a proper ID for database storage
        employee.setEmployeeId(getNextEmployeeId());
        databaseDAO.insertEmployee(employee);
    }
    
    public Employee createEmployee(String firstName, String lastName, String email, 
                                 String department, String position, double baseSalary, LocalDate hireDate) {
        Employee employee = new Employee(getNextEmployeeId(), firstName, lastName, email, 
                                       department, position, baseSalary, hireDate);
        if (databaseDAO.insertEmployee(employee)) {
            return employee;
        }
        return null;
    }
    
    public Employee createEmployee(String firstName, String lastName, String email, String phone,
                                 String department, String position, double baseSalary, LocalDate hireDate) {
        System.out.println("Creating employee with phone: " + phone);
        Employee employee = new Employee(getNextEmployeeId(), firstName, lastName, email, 
                                       department, position, baseSalary, hireDate);
        employee.setPhone(phone);
        System.out.println("Employee created with ID: " + employee.getEmployeeId());
        if (databaseDAO.insertEmployee(employee)) {
            System.out.println("Employee successfully inserted");
            return employee;
        }
        System.out.println("Failed to insert employee");
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
                                String phone, String department, String position, double baseSalary, LocalDate hireDate) {
        System.out.println("EmployeeManager: Updating employee ID " + employeeId + " with email: " + email + " and hire date: " + hireDate);
        String stringId = generateEmployeeStringId(employeeId);
        Employee employee = databaseDAO.getEmployeeById(stringId);
        if (employee != null) {
            System.out.println("Found employee: " + employee.getFullName() + " (current email: " + employee.getEmail() + ", current hire date: " + employee.getHireDate() + ")");
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setEmail(email);
            employee.setPhone(phone);
            employee.setDepartment(department);
            employee.setPosition(position);
            employee.setBaseSalary(baseSalary);
            employee.setHireDate(hireDate);
            return databaseDAO.updateEmployee(employee);
        } else {
            System.err.println("Employee not found for ID: " + stringId);
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
                              emp.getPosition().toLowerCase().contains(keyword.toLowerCase()) ||
                              emp.getEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                              (emp.getPhone() != null && emp.getPhone().toLowerCase().contains(keyword.toLowerCase())) ||
                              String.valueOf(emp.getEmployeeId()).contains(keyword))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Check if an email is already used by another employee
     * @param email The email to check
     * @param excludeEmployeeId The employee ID to exclude from the check (for updates)
     * @return true if email is available, false if already in use
     */
    public boolean isEmailAvailable(String email, int excludeEmployeeId) {
        List<Employee> employees = databaseDAO.getAllEmployees();
        for (Employee emp : employees) {
            if (emp.getEmail().equalsIgnoreCase(email) && emp.getEmployeeId() != excludeEmployeeId) {
                return false;
            }
        }
        return true;
    }
}
