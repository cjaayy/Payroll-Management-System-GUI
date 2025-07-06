package database;

import models.Employee;
import models.Payroll;
import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL Database Access Object implementation
 */
public class MySQLDatabaseDAO implements DatabaseDAO {
    
    @Override
    public User authenticateUser(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_LOGIN)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean insertUser(User user) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_INSERT_USER)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_SELECT_ALL_EMPLOYEES);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                try {
                    Employee employee = new Employee(
                        rs.getString("employee_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getDate("hire_date"),
                        rs.getDouble("salary")
                    );
                    employees.add(employee);
                } catch (Exception e) {
                    System.err.println("Error creating employee from DB record: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }
    
    @Override
    public Employee getEmployeeById(String employeeId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_SELECT_EMPLOYEE_BY_ID)) {
            
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Employee(
                    rs.getString("employee_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("department"),
                    rs.getString("position"),
                    rs.getDate("hire_date"),
                    rs.getDouble("salary")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employee: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean insertEmployee(Employee employee) {
        System.out.println("Inserting employee: " + employee.getFullName() + " with phone: " + employee.getPhone());
        
        // Try to insert with the given ID, if it fails due to duplicate, try with a higher ID
        int maxRetries = 10;
        int originalId = employee.getEmployeeId();
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_INSERT_EMPLOYEE)) {
                
                int currentId = originalId + attempt;
                String employeeIdString = String.format("EMP%03d", currentId);
                
                stmt.setString(1, employeeIdString);
                stmt.setString(2, employee.getFirstName());
                stmt.setString(3, employee.getLastName());
                stmt.setString(4, employee.getEmail());
                stmt.setString(5, employee.getPhone() == null || employee.getPhone().trim().isEmpty() ? null : employee.getPhone());
                stmt.setString(6, employee.getDepartment());
                stmt.setString(7, employee.getPosition());
                stmt.setDate(8, java.sql.Date.valueOf(employee.getHireDate()));
                stmt.setDouble(9, employee.getSalary());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Update the employee object with the actual ID used
                    employee.setEmployeeId(currentId);
                    System.out.println("Employee inserted successfully with ID: " + employeeIdString);
                    return true;
                }
                
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate entry") && attempt < maxRetries - 1) {
                    System.out.println("ID conflict for EMP" + String.format("%03d", originalId + attempt) + 
                                     ", trying next ID...");
                    continue; // Try next ID
                } else {
                    System.err.println("Error inserting employee: " + e.getMessage());
                    if (attempt == maxRetries - 1) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        System.err.println("Failed to insert employee after " + maxRetries + " attempts");
        return false;
    }
    
    @Override
    public boolean updateEmployee(Employee employee) {
        System.out.println("Updating employee: " + employee.getFullName() + 
                          " (ID: " + employee.getEmployeeId() + ", Email: " + employee.getEmail() + ")");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_UPDATE_EMPLOYEE)) {
            
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPhone() == null || employee.getPhone().trim().isEmpty() ? null : employee.getPhone());
            stmt.setString(5, employee.getDepartment());
            stmt.setString(6, employee.getPosition());
            stmt.setDate(7, java.sql.Date.valueOf(employee.getHireDate()));
            stmt.setDouble(8, employee.getSalary());
            String empIdString = String.format("EMP%03d", employee.getEmployeeId());
            stmt.setString(9, empIdString);
            
            System.out.println("Executing update for employee ID: " + empIdString);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Update rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            
            // Check if it's an email duplicate issue
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
                System.err.println("Email conflict detected. Checking if email belongs to same employee...");
                
                // Try to find if there's another employee with the same email
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement checkStmt = conn.prepareStatement(
                         "SELECT employee_id FROM employees WHERE email = ? AND employee_id != ?")) {
                    
                    checkStmt.setString(1, employee.getEmail());
                    checkStmt.setString(2, String.format("EMP%03d", employee.getEmployeeId()));
                    
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        System.err.println("Found another employee with same email: " + rs.getString("employee_id"));
                        return false;
                    } else {
                        System.err.println("No other employee has this email. This might be a database issue.");
                    }
                } catch (SQLException checkEx) {
                    System.err.println("Error checking email conflict: " + checkEx.getMessage());
                }
            }
            
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean deleteEmployee(String employeeId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_DELETE_EMPLOYEE)) {
            
            stmt.setString(1, employeeId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public List<Payroll> getAllPayroll() {
        List<Payroll> payrolls = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_SELECT_ALL_PAYROLL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Payroll payroll = new Payroll(
                    rs.getString("employee_id"),
                    rs.getDate("pay_period_start"),
                    rs.getDate("pay_period_end"),
                    rs.getDouble("basic_salary"),
                    rs.getDouble("overtime_hours"),
                    rs.getDouble("overtime_rate"),
                    rs.getDouble("bonus"),
                    rs.getDouble("deductions")
                );
                payroll.setId(rs.getInt("id"));
                payroll.setStatus(rs.getString("status"));
                payrolls.add(payroll);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching payroll records: " + e.getMessage());
            e.printStackTrace();
        }
        return payrolls;
    }
    
    @Override
    public List<Payroll> getPayrollByEmployee(String employeeId) {
        List<Payroll> payrolls = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_SELECT_PAYROLL_BY_EMPLOYEE)) {
            
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Payroll payroll = new Payroll(
                    rs.getString("employee_id"),
                    rs.getDate("pay_period_start"),
                    rs.getDate("pay_period_end"),
                    rs.getDouble("basic_salary"),
                    rs.getDouble("overtime_hours"),
                    rs.getDouble("overtime_rate"),
                    rs.getDouble("bonus"),
                    rs.getDouble("deductions")
                );
                payroll.setId(rs.getInt("id"));
                payroll.setStatus(rs.getString("status"));
                payrolls.add(payroll);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching payroll records for employee: " + e.getMessage());
            e.printStackTrace();
        }
        return payrolls;
    }
    
    @Override
    public boolean insertPayroll(Payroll payroll) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_INSERT_PAYROLL)) {
            
            stmt.setString(1, String.valueOf(payroll.getEmployeeId()));
            stmt.setDate(2, new java.sql.Date(payroll.getPayPeriodStart().getTime()));
            stmt.setDate(3, new java.sql.Date(payroll.getPayPeriodEnd().getTime()));
            stmt.setDouble(4, payroll.getBasicSalary());
            stmt.setDouble(5, payroll.getOvertimeHours());
            stmt.setDouble(6, payroll.getOvertimeRate());
            stmt.setDouble(7, payroll.getBonus());
            stmt.setDouble(8, payroll.getDeductions());
            stmt.setDouble(9, payroll.getGrossPay());
            stmt.setDouble(10, payroll.getTaxDeduction());
            stmt.setDouble(11, payroll.getNetPay());
            stmt.setString(12, payroll.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting payroll record: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean updatePayroll(Payroll payroll) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_UPDATE_PAYROLL)) {
            
            stmt.setDouble(1, payroll.getBasicSalary());
            stmt.setDouble(2, payroll.getOvertimeHours());
            stmt.setDouble(3, payroll.getOvertimeRate());
            stmt.setDouble(4, payroll.getBonus());
            stmt.setDouble(5, payroll.getDeductions());
            stmt.setDouble(6, payroll.getGrossPay());
            stmt.setDouble(7, payroll.getTaxDeduction());
            stmt.setDouble(8, payroll.getNetPay());
            stmt.setString(9, payroll.getStatus());
            stmt.setInt(10, payroll.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payroll record: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean deletePayroll(int payrollId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_DELETE_PAYROLL)) {
            
            stmt.setInt(1, payrollId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting payroll record: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean testConnection() {
        return DatabaseConnection.testConnection();
    }
    
    @Override
    public void closeConnection() {
        DatabaseConnection.closeConnection();
    }
}
