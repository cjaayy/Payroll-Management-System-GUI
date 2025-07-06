-- ================================================================
-- Payroll Management System - MySQL Workbench Setup Script
-- ================================================================
-- Run this script in MySQL Workbench to set up the database
-- ================================================================

-- Step 1: Create Database
CREATE DATABASE IF NOT EXISTS payroll_system;
USE payroll_system;

-- Step 2: Create Application User (Optional but Recommended)
CREATE USER IF NOT EXISTS 'payroll_user'@'localhost' IDENTIFIED BY 'payroll123';
GRANT ALL PRIVILEGES ON payroll_system.* TO 'payroll_user'@'localhost';
FLUSH PRIVILEGES;

-- Step 3: Create Tables
-- Users table for authentication
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Employees table
CREATE TABLE IF NOT EXISTS employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    department VARCHAR(50),
    position VARCHAR(50),
    hire_date DATE,
    salary DECIMAL(10, 2),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Payroll table
CREATE TABLE IF NOT EXISTS payroll (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL,
    pay_period_start DATE NOT NULL,
    pay_period_end DATE NOT NULL,
    basic_salary DECIMAL(10, 2) NOT NULL,
    overtime_hours DECIMAL(5, 2) DEFAULT 0,
    overtime_rate DECIMAL(5, 2) DEFAULT 0,
    bonus DECIMAL(10, 2) DEFAULT 0,
    deductions DECIMAL(10, 2) DEFAULT 0,
    gross_pay DECIMAL(10, 2) NOT NULL,
    tax_deduction DECIMAL(10, 2) DEFAULT 0,
    net_pay DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE
);

-- Step 4: Insert Default Data
-- Insert default admin user
INSERT IGNORE INTO users (username, password, role) 
VALUES ('admin', 'admin123', 'ADMIN');

-- Insert sample employees (optional)
INSERT IGNORE INTO employees (employee_id, first_name, last_name, email, phone, department, position, hire_date, salary) 
VALUES 
('EMP001', 'John', 'Doe', 'john.doe@company.com', '555-0101', 'IT', 'Software Developer', '2024-01-15', 75000.00),
('EMP002', 'Jane', 'Smith', 'jane.smith@company.com', '555-0102', 'HR', 'HR Manager', '2024-02-01', 65000.00),
('EMP003', 'Mike', 'Johnson', 'mike.johnson@company.com', '555-0103', 'Finance', 'Accountant', '2024-03-01', 55000.00);

-- Step 5: Verify Setup
SELECT 'Database setup completed successfully!' as status;

-- Show created tables
SHOW TABLES;

-- Show users
SELECT 'Users Table:' as info;
SELECT username, role, created_at FROM users;

-- Show employees
SELECT 'Employees Table:' as info;
SELECT employee_id, first_name, last_name, department, position, salary FROM employees;

-- Show database structure
SELECT 'Database Structure:' as info;
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM 
    INFORMATION_SCHEMA.COLUMNS 
WHERE 
    TABLE_SCHEMA = 'payroll_system'
ORDER BY 
    TABLE_NAME, ORDINAL_POSITION;

-- ================================================================
-- Setup Complete! 
-- ================================================================
-- Next Steps:
-- 1. Update src/database/DatabaseConfig.java with your credentials
-- 2. Run: setup-database.bat (to test connection)
-- 3. Run: run.bat (to start the application)
-- 4. Login with: admin / admin123
-- ================================================================
