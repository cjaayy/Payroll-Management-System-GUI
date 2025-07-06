package database;

/**
 * Database Configuration Constants
 */
public class DatabaseConfig {
    // Database connection settings
    public static final String DB_HOST = "localhost";
    public static final String DB_PORT = "3306";
    public static final String DB_NAME = "payroll_system";
    public static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    // Database credentials
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "Jisoo@010322";

    // Connection pool settings
    public static final int MAX_CONNECTIONS = 10;
    public static final int CONNECTION_TIMEOUT = 30000; // 30 seconds

    // SQL Queries
    public static final String QUERY_LOGIN = "SELECT * FROM users WHERE username = ? AND password = ?";
    public static final String QUERY_INSERT_USER = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
    public static final String QUERY_SELECT_ALL_EMPLOYEES = "SELECT * FROM employees WHERE status = 'ACTIVE'";
    public static final String QUERY_INSERT_EMPLOYEE = "INSERT INTO employees (employee_id, first_name, last_name, email, phone, department, position, hire_date, salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String QUERY_UPDATE_EMPLOYEE = "UPDATE employees SET first_name = ?, last_name = ?, email = ?, phone = ?, department = ?, position = ?, salary = ? WHERE employee_id = ?";
    public static final String QUERY_DELETE_EMPLOYEE = "UPDATE employees SET status = 'INACTIVE' WHERE employee_id = ?";
    public static final String QUERY_SELECT_EMPLOYEE_BY_ID = "SELECT * FROM employees WHERE employee_id = ? AND status = 'ACTIVE'";

    // Payroll queries
    public static final String QUERY_INSERT_PAYROLL = "INSERT INTO payroll (employee_id, pay_period_start, pay_period_end, basic_salary, overtime_hours, overtime_rate, bonus, deductions, gross_pay, tax_deduction, net_pay, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String QUERY_SELECT_PAYROLL_BY_EMPLOYEE = "SELECT * FROM payroll WHERE employee_id = ? ORDER BY pay_period_start DESC";
    public static final String QUERY_SELECT_ALL_PAYROLL = "SELECT p.*, e.first_name, e.last_name FROM payroll p JOIN employees e ON p.employee_id = e.employee_id WHERE e.status = 'ACTIVE' ORDER BY p.pay_period_start DESC";
    public static final String QUERY_UPDATE_PAYROLL = "UPDATE payroll SET basic_salary = ?, overtime_hours = ?, overtime_rate = ?, bonus = ?, deductions = ?, gross_pay = ?, tax_deduction = ?, net_pay = ?, status = ? WHERE id = ?";
    public static final String QUERY_DELETE_PAYROLL = "DELETE FROM payroll WHERE id = ?";

    // Database initialization
    public static final String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
}
