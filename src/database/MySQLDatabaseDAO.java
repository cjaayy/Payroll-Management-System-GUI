package database;

import models.Employee;
import models.EmployeeDocument;
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
                        rs.getString("job_title"),
                        rs.getString("manager"),
                        rs.getDate("hire_date"),
                        rs.getDouble("salary")
                    );
                    // Set comprehensive employee ID if available
                    try {
                        String comprehensiveId = rs.getString("comprehensive_employee_id");
                        if (comprehensiveId != null && !comprehensiveId.trim().isEmpty()) {
                            employee.setComprehensiveEmployeeId(comprehensiveId);
                        }
                    } catch (SQLException e) {
                        // Column might not exist in older database versions
                        System.out.println("Note: comprehensive_employee_id column not found. Using legacy ID format.");
                    }
                    
                    // Set employment status and dates
                    try {
                        String employmentStatus = rs.getString("employment_status");
                        if (employmentStatus != null) {
                            employee.setEmploymentStatus(employmentStatus);
                        }
                        
                        Date joiningDate = rs.getDate("joining_date");
                        if (joiningDate != null) {
                            employee.setJoiningDate(joiningDate.toLocalDate());
                        }
                        
                        Date probationEndDate = rs.getDate("probation_end_date");
                        if (probationEndDate != null) {
                            employee.setProbationEndDate(probationEndDate.toLocalDate());
                        }
                        
                        Date exitDate = rs.getDate("exit_date");
                        if (exitDate != null) {
                            employee.setExitDate(exitDate.toLocalDate());
                        }
                        
                        String exitReason = rs.getString("exit_reason");
                        if (exitReason != null) {
                            employee.setExitReason(exitReason);
                        }
                        
                        // Set bank and payment details
                        String bankName = rs.getString("bank_name");
                        if (bankName != null) employee.setBankName(bankName);
                        
                        String accountNumber = rs.getString("account_number");
                        if (accountNumber != null) employee.setAccountNumber(accountNumber);
                        
                        String accountHolderName = rs.getString("account_holder_name");
                        if (accountHolderName != null) employee.setAccountHolderName(accountHolderName);
                        
                        String bankBranch = rs.getString("bank_branch");
                        if (bankBranch != null) employee.setBankBranch(bankBranch);
                        
                        String routingNumber = rs.getString("routing_number");
                        if (routingNumber != null) employee.setRoutingNumber(routingNumber);
                        
                        String paymentMethod = rs.getString("payment_method");
                        if (paymentMethod != null) {
                            employee.setPaymentMethod(paymentMethod);
                        }
                        
                        String paymentFrequency = rs.getString("payment_frequency");
                        if (paymentFrequency != null) {
                            employee.setPaymentFrequency(paymentFrequency);
                        }
                        
                    } catch (SQLException e) {
                        // New columns might not exist in older database versions
                        System.out.println("Note: Some employment or payment columns not found. Using default values.");
                    }
                    
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
    public List<Employee> getAllEmployeesForIdCheck() {
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConfig.QUERY_SELECT_ALL_EMPLOYEES_FOR_ID_CHECK);
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
                        rs.getString("job_title"),
                        rs.getString("manager"),
                        rs.getDate("hire_date"),
                        rs.getDouble("salary")
                    );
                    // Set comprehensive employee ID if available
                    try {
                        String comprehensiveId = rs.getString("comprehensive_employee_id");
                        if (comprehensiveId != null && !comprehensiveId.trim().isEmpty()) {
                            employee.setComprehensiveEmployeeId(comprehensiveId);
                        }
                    } catch (SQLException e) {
                        // Column might not exist in older database versions
                        System.out.println("Note: comprehensive_employee_id column not found. Using legacy ID format.");
                    }
                    employees.add(employee);
                } catch (Exception e) {
                    System.err.println("Error creating employee from DB record: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employees for ID check: " + e.getMessage());
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
                Employee employee = new Employee(
                    rs.getString("employee_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("department"),
                    rs.getString("position"),
                    rs.getString("job_title"),
                    rs.getString("manager"),
                    rs.getDate("hire_date"),
                    rs.getDouble("salary")
                );
                // Set comprehensive employee ID if available
                try {
                    String comprehensiveId = rs.getString("comprehensive_employee_id");
                    if (comprehensiveId != null && !comprehensiveId.trim().isEmpty()) {
                        employee.setComprehensiveEmployeeId(comprehensiveId);
                    }
                } catch (SQLException e) {
                    // Column might not exist in older database versions
                    System.out.println("Note: comprehensive_employee_id column not found for employee lookup. Using legacy ID format.");
                }
                
                // Set employment status and dates
                try {
                    String employmentStatus = rs.getString("employment_status");
                    if (employmentStatus != null) {
                        employee.setEmploymentStatus(employmentStatus);
                    }
                    
                    Date joiningDate = rs.getDate("joining_date");
                    if (joiningDate != null) {
                        employee.setJoiningDate(joiningDate.toLocalDate());
                    }
                    
                    Date probationEndDate = rs.getDate("probation_end_date");
                    if (probationEndDate != null) {
                        employee.setProbationEndDate(probationEndDate.toLocalDate());
                    }
                    
                    Date exitDate = rs.getDate("exit_date");
                    if (exitDate != null) {
                        employee.setExitDate(exitDate.toLocalDate());
                    }
                    
                    String exitReason = rs.getString("exit_reason");
                    if (exitReason != null) {
                        employee.setExitReason(exitReason);
                    }
                    
                    // Set bank and payment details
                    String bankName = rs.getString("bank_name");
                    if (bankName != null) employee.setBankName(bankName);
                    
                    String accountNumber = rs.getString("account_number");
                    if (accountNumber != null) employee.setAccountNumber(accountNumber);
                    
                    String accountHolderName = rs.getString("account_holder_name");
                    if (accountHolderName != null) employee.setAccountHolderName(accountHolderName);
                    
                    String bankBranch = rs.getString("bank_branch");
                    if (bankBranch != null) employee.setBankBranch(bankBranch);
                    
                    String routingNumber = rs.getString("routing_number");
                    if (routingNumber != null) employee.setRoutingNumber(routingNumber);
                    
                    String paymentMethod = rs.getString("payment_method");
                    if (paymentMethod != null) {
                        employee.setPaymentMethod(paymentMethod);
                    }
                    
                    String paymentFrequency = rs.getString("payment_frequency");
                    if (paymentFrequency != null) {
                        employee.setPaymentFrequency(paymentFrequency);
                    }
                    
                } catch (SQLException e) {
                    // New columns might not exist in older database versions
                    System.out.println("Note: Some employment or payment columns not found in getEmployeeById. Using default values.");
                }
                
                return employee;
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
        
        // Check if comprehensive_employee_id column exists
        boolean hasComprehensiveIdColumn = checkComprehensiveIdColumnExists();
        
        // Use appropriate query based on column availability
        String insertQuery = hasComprehensiveIdColumn ? 
            DatabaseConfig.QUERY_INSERT_EMPLOYEE : 
            "INSERT INTO employees (employee_id, first_name, last_name, email, phone, department, position, hire_date, salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Try to insert with the given ID, if it fails due to duplicate, try with a higher ID
        int maxRetries = 10;
        int originalId = employee.getEmployeeId();
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                
                int currentId = originalId + attempt;
                String employeeIdString = String.format("EMP%03d", currentId);
                
                stmt.setString(1, employeeIdString);
                
                if (hasComprehensiveIdColumn) {
                    stmt.setString(2, employee.getComprehensiveEmployeeId());
                    stmt.setString(3, employee.getFirstName());
                    stmt.setString(4, employee.getLastName());
                    stmt.setString(5, employee.getEmail());
                    stmt.setString(6, employee.getPhone() == null || employee.getPhone().trim().isEmpty() ? null : employee.getPhone());
                    stmt.setString(7, employee.getDepartment());
                    stmt.setString(8, employee.getPosition());
                    stmt.setString(9, employee.getJobTitle());
                    stmt.setString(10, employee.getManager());
                    stmt.setDate(11, java.sql.Date.valueOf(employee.getHireDate()));
                    stmt.setDouble(12, employee.getSalary());
                    
                    // Set new employment status and dates
                    stmt.setString(13, employee.getEmploymentStatus());
                    stmt.setDate(14, employee.getJoiningDate() != null ? java.sql.Date.valueOf(employee.getJoiningDate()) : null);
                    stmt.setDate(15, employee.getProbationEndDate() != null ? java.sql.Date.valueOf(employee.getProbationEndDate()) : null);
                    stmt.setDate(16, employee.getExitDate() != null ? java.sql.Date.valueOf(employee.getExitDate()) : null);
                    stmt.setString(17, employee.getExitReason());
                    
                    // Set bank and payment details
                    stmt.setString(18, employee.getBankName());
                    stmt.setString(19, employee.getAccountNumber());
                    stmt.setString(20, employee.getAccountHolderName());
                    stmt.setString(21, employee.getBankBranch());
                    stmt.setString(22, employee.getRoutingNumber());
                    stmt.setString(23, employee.getPaymentMethod());
                    stmt.setString(24, employee.getPaymentFrequency());
                } else {
                    stmt.setString(2, employee.getFirstName());
                    stmt.setString(3, employee.getLastName());
                    stmt.setString(4, employee.getEmail());
                    stmt.setString(5, employee.getPhone() == null || employee.getPhone().trim().isEmpty() ? null : employee.getPhone());
                    stmt.setString(6, employee.getDepartment());
                    stmt.setString(7, employee.getPosition());
                    stmt.setDate(8, java.sql.Date.valueOf(employee.getHireDate()));
                    stmt.setDouble(9, employee.getSalary());
                }
                
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
        
        // Check if comprehensive_employee_id column exists
        boolean hasComprehensiveIdColumn = checkComprehensiveIdColumnExists();
        
        // Use appropriate query based on column availability
        String updateQuery = hasComprehensiveIdColumn ? 
            DatabaseConfig.QUERY_UPDATE_EMPLOYEE : 
            "UPDATE employees SET first_name = ?, last_name = ?, email = ?, phone = ?, department = ?, position = ?, hire_date = ?, salary = ? WHERE employee_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            
            if (hasComprehensiveIdColumn) {
                stmt.setString(1, employee.getComprehensiveEmployeeId());
                stmt.setString(2, employee.getFirstName());
                stmt.setString(3, employee.getLastName());
                stmt.setString(4, employee.getEmail());
                stmt.setString(5, employee.getPhone() == null || employee.getPhone().trim().isEmpty() ? null : employee.getPhone());
                stmt.setString(6, employee.getDepartment());
                stmt.setString(7, employee.getPosition());
                stmt.setString(8, employee.getJobTitle());
                stmt.setString(9, employee.getManager());
                stmt.setDate(10, java.sql.Date.valueOf(employee.getHireDate()));
                stmt.setDouble(11, employee.getSalary());
                
                // Set new employment status and dates
                stmt.setString(12, employee.getEmploymentStatus());
                stmt.setDate(13, employee.getJoiningDate() != null ? java.sql.Date.valueOf(employee.getJoiningDate()) : null);
                stmt.setDate(14, employee.getProbationEndDate() != null ? java.sql.Date.valueOf(employee.getProbationEndDate()) : null);
                stmt.setDate(15, employee.getExitDate() != null ? java.sql.Date.valueOf(employee.getExitDate()) : null);
                stmt.setString(16, employee.getExitReason());
                
                // Set bank and payment details
                stmt.setString(17, employee.getBankName());
                stmt.setString(18, employee.getAccountNumber());
                stmt.setString(19, employee.getAccountHolderName());
                stmt.setString(20, employee.getBankBranch());
                stmt.setString(21, employee.getRoutingNumber());
                stmt.setString(22, employee.getPaymentMethod());
                stmt.setString(23, employee.getPaymentFrequency());
                
                String empIdString = String.format("EMP%03d", employee.getEmployeeId());
                stmt.setString(24, empIdString);
            } else {
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
            }
            
            System.out.println("Executing update for employee ID: " + String.format("EMP%03d", employee.getEmployeeId()));
            
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
    
    /**
     * Check if the comprehensive_employee_id column exists in the employees table
     * @return true if the column exists, false otherwise
     */
    private boolean checkComprehensiveIdColumnExists() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                 "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = 'employees' AND COLUMN_NAME = 'comprehensive_employee_id'")) {
            
            stmt.setString(1, DatabaseConfig.DB_NAME);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking for comprehensive_employee_id column: " + e.getMessage());
        }
        return false;
    }
    
    // Contact Information Methods
    public boolean insertEmployeeContactInfo(Employee employee) {
        String query = "INSERT INTO employee_contact_info (employee_id, personal_email, work_phone, " +
                      "emergency_contact, emergency_phone, street_address, barangay, city, province_state, " +
                      "country, zip_code, birth_date, social_security_number, nationality, marital_status) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE " +
                      "personal_email = VALUES(personal_email), " +
                      "work_phone = VALUES(work_phone), " +
                      "emergency_contact = VALUES(emergency_contact), " +
                      "emergency_phone = VALUES(emergency_phone), " +
                      "street_address = VALUES(street_address), " +
                      "barangay = VALUES(barangay), " +
                      "city = VALUES(city), " +
                      "province_state = VALUES(province_state), " +
                      "country = VALUES(country), " +
                      "zip_code = VALUES(zip_code), " +
                      "birth_date = VALUES(birth_date), " +
                      "social_security_number = VALUES(social_security_number), " +
                      "nationality = VALUES(nationality), " +
                      "marital_status = VALUES(marital_status)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Use the basic employee ID format (EMP###) for foreign key constraint
            String employeeId = String.format("EMP%03d", employee.getEmployeeId());
            System.out.println("Saving contact info for employee: " + employeeId + " (formatted: " + employee.getFormattedEmployeeId() + ")");
            
            stmt.setString(1, employeeId);
            stmt.setString(2, employee.getPersonalEmail());
            stmt.setString(3, employee.getWorkPhone());
            stmt.setString(4, employee.getEmergencyContact());
            stmt.setString(5, employee.getEmergencyPhone());
            stmt.setString(6, employee.getStreetAddress());
            stmt.setString(7, employee.getBarangay());
            stmt.setString(8, employee.getCity());
            stmt.setString(9, employee.getProvinceState());
            stmt.setString(10, employee.getCountry());
            stmt.setString(11, employee.getZipCode());
            stmt.setDate(12, employee.getBirthDate() != null ? 
                java.sql.Date.valueOf(employee.getBirthDate()) : null);
            stmt.setString(13, employee.getSocialSecurityNumber());
            stmt.setString(14, employee.getNationality());
            stmt.setString(15, employee.getMaritalStatus());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Contact info save result: " + rowsAffected + " rows affected");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting employee contact info: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean loadEmployeeContactInfo(Employee employee) {
        String query = "SELECT personal_email, work_phone, emergency_contact, emergency_phone, " +
                      "street_address, barangay, city, province_state, country, zip_code, birth_date, " +
                      "social_security_number, nationality, marital_status " +
                      "FROM employee_contact_info " +
                      "WHERE employee_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Use the basic employee ID format (EMP###) for foreign key constraint
            String employeeId = String.format("EMP%03d", employee.getEmployeeId());
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                employee.setPersonalEmail(rs.getString("personal_email"));
                employee.setWorkPhone(rs.getString("work_phone"));
                employee.setEmergencyContact(rs.getString("emergency_contact"));
                employee.setEmergencyPhone(rs.getString("emergency_phone"));
                employee.setStreetAddress(rs.getString("street_address"));
                employee.setBarangay(rs.getString("barangay"));
                employee.setCity(rs.getString("city"));
                employee.setProvinceState(rs.getString("province_state"));
                employee.setCountry(rs.getString("country"));
                employee.setZipCode(rs.getString("zip_code"));
                
                java.sql.Date birthDate = rs.getDate("birth_date");
                if (birthDate != null) {
                    employee.setBirthDate(birthDate.toLocalDate());
                }
                
                employee.setSocialSecurityNumber(rs.getString("social_security_number"));
                employee.setNationality(rs.getString("nationality"));
                employee.setMaritalStatus(rs.getString("marital_status"));
                
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error loading employee contact info: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Document Management Methods
    public boolean insertEmployeeDocument(EmployeeDocument document) {
        String query = "INSERT INTO employee_documents (employee_id, document_type, file_name, " +
                      "file_path, file_data, file_size, mime_type, description, uploaded_by) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            // Use the simple EMP format that matches what's in the database
            String employeeIdString = String.format("EMP%03d", document.getEmployeeId());
            System.out.println("Inserting document for employee ID: " + employeeIdString + " (Document: " + document.getFileName() + ")");
            
            stmt.setString(1, employeeIdString);
            stmt.setString(2, document.getDocumentType());
            stmt.setString(3, document.getFileName());
            stmt.setString(4, document.getFilePath());
            stmt.setBytes(5, document.getFileData());
            stmt.setLong(6, document.getFileSize());
            stmt.setString(7, document.getMimeType());
            stmt.setString(8, document.getDescription());
            stmt.setString(9, document.getUploadedBy());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        document.setDocumentId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Document inserted successfully with ID: " + document.getDocumentId());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting employee document: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public List<EmployeeDocument> getEmployeeDocuments(Employee employee) {
        List<EmployeeDocument> documents = new ArrayList<>();
        String query = "SELECT id, document_type, file_name, file_path, file_data, " +
                      "file_size, mime_type, description, uploaded_by, created_at " +
                      "FROM employee_documents " +
                      "WHERE employee_id = ? " +
                      "ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Use the simple EMP format that matches how documents are saved
            String employeeIdString = String.format("EMP%03d", employee.getEmployeeId());
            System.out.println("Loading documents for employee ID: " + employeeIdString + " (Formatted ID: " + employee.getFormattedEmployeeId() + ")");
            stmt.setString(1, employeeIdString);
            ResultSet rs = stmt.executeQuery();
            
            int documentCount = 0;
            while (rs.next()) {
                EmployeeDocument document = new EmployeeDocument(
                    rs.getInt("id"),
                    employee.getEmployeeId(),
                    rs.getString("document_type"),
                    rs.getString("file_name"),
                    rs.getString("file_path"),
                    rs.getBytes("file_data"),
                    rs.getString("description"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("uploaded_by")
                );
                documents.add(document);
                documentCount++;
                System.out.println("Loaded document [" + documentCount + "]: " + document.getFileName() + " (ID: " + document.getDocumentId() + ")");
            }
            System.out.println("Total loaded " + documentCount + " documents for employee: " + employeeIdString);
        } catch (SQLException e) {
            System.err.println("Error loading employee documents: " + e.getMessage());
            e.printStackTrace();
        }
        return documents;
    }
    
    public boolean deleteEmployeeDocument(int documentId) {
        String query = "DELETE FROM employee_documents WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            System.out.println("Attempting to delete document with ID: " + documentId);
            stmt.setInt(1, documentId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Document deletion - rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting employee document: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
