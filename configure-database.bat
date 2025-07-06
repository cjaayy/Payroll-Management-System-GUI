@echo off
echo Configuring Database Settings...
echo.

REM Check if database.config exists
if not exist "database.config" (
    echo database.config file not found!
    echo Please create database.config with your MySQL settings.
    pause
    exit /b 1
)

REM Read database.config and extract values
for /f "tokens=1,2 delims==" %%a in ('type database.config ^| findstr /v "^#" ^| findstr /v "^$"') do (
    if "%%a"=="DB_USERNAME" set DB_USERNAME=%%b
    if "%%a"=="DB_PASSWORD" set DB_PASSWORD=%%b
    if "%%a"=="DB_HOST" set DB_HOST=%%b
    if "%%a"=="DB_PORT" set DB_PORT=%%b
    if "%%a"=="DB_NAME" set DB_NAME=%%b
)

echo Database Configuration:
echo Host: %DB_HOST%
echo Port: %DB_PORT%
echo Database: %DB_NAME%
echo Username: %DB_USERNAME%
echo Password: %DB_PASSWORD%
echo.

REM Update DatabaseConfig.java
echo Updating DatabaseConfig.java...
(
echo package database;
echo.
echo /**
echo  * Database Configuration Constants
echo  */
echo public class DatabaseConfig {
echo     // Database connection settings
echo     public static final String DB_HOST = "%DB_HOST%";
echo     public static final String DB_PORT = "%DB_PORT%";
echo     public static final String DB_NAME = "%DB_NAME%";
echo     public static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
echo.
echo     // Database credentials
echo     public static final String DB_USERNAME = "%DB_USERNAME%";
echo     public static final String DB_PASSWORD = "%DB_PASSWORD%";
echo.
echo     // Connection pool settings
echo     public static final int MAX_CONNECTIONS = 10;
echo     public static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
echo.
echo     // SQL Queries
echo     public static final String QUERY_LOGIN = "SELECT * FROM users WHERE username = ? AND password = ?";
echo     public static final String QUERY_INSERT_USER = "INSERT INTO users (username, password, role^) VALUES (?, ?, ?^)";
echo     public static final String QUERY_SELECT_ALL_EMPLOYEES = "SELECT * FROM employees WHERE status = 'ACTIVE'";
echo     public static final String QUERY_INSERT_EMPLOYEE = "INSERT INTO employees (employee_id, first_name, last_name, email, phone, department, position, hire_date, salary^) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?^)";
echo     public static final String QUERY_UPDATE_EMPLOYEE = "UPDATE employees SET first_name = ?, last_name = ?, email = ?, phone = ?, department = ?, position = ?, salary = ? WHERE employee_id = ?";
echo     public static final String QUERY_DELETE_EMPLOYEE = "UPDATE employees SET status = 'INACTIVE' WHERE employee_id = ?";
echo     public static final String QUERY_SELECT_EMPLOYEE_BY_ID = "SELECT * FROM employees WHERE employee_id = ? AND status = 'ACTIVE'";
echo.
echo     // Payroll queries
echo     public static final String QUERY_INSERT_PAYROLL = "INSERT INTO payroll (employee_id, pay_period_start, pay_period_end, basic_salary, overtime_hours, overtime_rate, bonus, deductions, gross_pay, tax_deduction, net_pay, status^) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?^)";
echo     public static final String QUERY_SELECT_PAYROLL_BY_EMPLOYEE = "SELECT * FROM payroll WHERE employee_id = ? ORDER BY pay_period_start DESC";
echo     public static final String QUERY_SELECT_ALL_PAYROLL = "SELECT p.*, e.first_name, e.last_name FROM payroll p JOIN employees e ON p.employee_id = e.employee_id WHERE e.status = 'ACTIVE' ORDER BY p.pay_period_start DESC";
echo     public static final String QUERY_UPDATE_PAYROLL = "UPDATE payroll SET basic_salary = ?, overtime_hours = ?, overtime_rate = ?, bonus = ?, deductions = ?, gross_pay = ?, tax_deduction = ?, net_pay = ?, status = ? WHERE id = ?";
echo     public static final String QUERY_DELETE_PAYROLL = "DELETE FROM payroll WHERE id = ?";
echo.
echo     // Database initialization
echo     public static final String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
echo }
) > "src\database\DatabaseConfig.java"

echo DatabaseConfig.java updated successfully!
echo.
echo Testing database connection...
javac -cp "lib/*;src" -d classes src/database/DatabaseTest.java src/database/*.java src/models/*.java
java -cp "lib/*;classes" database.DatabaseTest

echo.
echo Configuration complete! You can now run the application with:
echo run.bat
echo.
pause
