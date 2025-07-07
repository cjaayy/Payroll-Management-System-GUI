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
    comprehensive_employee_id VARCHAR(50),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    department VARCHAR(50),
    position VARCHAR(50),
    job_title VARCHAR(100),
    manager VARCHAR(100),
    hire_date DATE,
    salary DECIMAL(10, 2),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    
    -- Employment Status and Key Dates
    employment_status VARCHAR(20) DEFAULT 'ACTIVE',
    joining_date DATE,
    probation_end_date DATE,
    exit_date DATE,
    exit_reason TEXT,
    
    -- Bank and Payment Details
    bank_name VARCHAR(100),
    account_number VARCHAR(50),
    account_holder_name VARCHAR(100),
    bank_branch VARCHAR(100),
    routing_number VARCHAR(50),
    payment_method VARCHAR(20) DEFAULT 'BANK_TRANSFER',
    payment_frequency VARCHAR(20) DEFAULT 'MONTHLY',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Employee Contact Information table
CREATE TABLE IF NOT EXISTS employee_contact_info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL,
    personal_email VARCHAR(100),
    work_phone VARCHAR(20),
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    street_address TEXT,
    barangay VARCHAR(100),
    city VARCHAR(50),
    province_state VARCHAR(50),
    country VARCHAR(50),
    zip_code VARCHAR(10),
    birth_date DATE,
    social_security_number VARCHAR(20),
    nationality VARCHAR(50),
    marital_status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE
);

-- Address Reference Tables for Dropdown Data
CREATE TABLE IF NOT EXISTS countries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    country_code VARCHAR(3) UNIQUE NOT NULL,
    country_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS provinces_states (
    id INT AUTO_INCREMENT PRIMARY KEY,
    country_code VARCHAR(3) NOT NULL,
    province_state_code VARCHAR(10) NOT NULL,
    province_state_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (country_code) REFERENCES countries(country_code),
    UNIQUE KEY unique_province_state (country_code, province_state_code)
);

CREATE TABLE IF NOT EXISTS cities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    country_code VARCHAR(3) NOT NULL,
    province_state_code VARCHAR(10) NOT NULL,
    city_code VARCHAR(10) NOT NULL,
    city_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (country_code) REFERENCES countries(country_code),
    FOREIGN KEY (province_state_code) REFERENCES provinces_states(province_state_code),
    UNIQUE KEY unique_city (country_code, province_state_code, city_code)
);

CREATE TABLE IF NOT EXISTS barangays (
    id INT AUTO_INCREMENT PRIMARY KEY,
    country_code VARCHAR(3) NOT NULL,
    province_state_code VARCHAR(10) NOT NULL,
    city_code VARCHAR(10) NOT NULL,
    barangay_code VARCHAR(10) NOT NULL,
    barangay_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (country_code) REFERENCES countries(country_code),
    FOREIGN KEY (province_state_code) REFERENCES provinces_states(province_state_code),
    FOREIGN KEY (city_code) REFERENCES cities(city_code),
    UNIQUE KEY unique_barangay (country_code, province_state_code, city_code, barangay_code)
);

-- Employee Documents table
CREATE TABLE IF NOT EXISTS employee_documents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500),
    file_data LONGBLOB,
    file_size BIGINT,
    mime_type VARCHAR(100),
    description TEXT,
    uploaded_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE
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
INSERT IGNORE INTO employees (employee_id, first_name, last_name, email, phone, department, position, job_title, manager, hire_date, salary) 
VALUES 
('EMP001', 'John', 'Doe', 'john.doe@company.com', '555-0101', 'IT', 'Software Developer', 'Senior Software Developer', 'Jane Smith', '2024-01-15', 75000.00),
('EMP002', 'Jane', 'Smith', 'jane.smith@company.com', '555-0102', 'HR', 'HR Manager', 'Human Resources Manager', 'Michael Johnson', '2024-02-01', 65000.00),
('EMP003', 'Mike', 'Johnson', 'mike.johnson@company.com', '555-0103', 'Finance', 'Accountant', 'Senior Accountant', 'Sarah Davis', '2024-03-01', 55000.00);

-- Insert Address Reference Data
-- Countries
INSERT IGNORE INTO countries (country_code, country_name) VALUES
('PH', 'Philippines'),
('US', 'United States'),
('CA', 'Canada'),
('AU', 'Australia'),
('UK', 'United Kingdom'),
('SG', 'Singapore'),
('MY', 'Malaysia'),
('TH', 'Thailand');

-- Provinces/States for Philippines
INSERT IGNORE INTO provinces_states (country_code, province_state_code, province_state_name) VALUES
('PH', 'NCR', 'National Capital Region (Metro Manila)'),
('PH', 'CL', 'Central Luzon'),
('PH', 'CAL', 'CALABARZON'),
('PH', 'CV', 'Central Visayas'),
('PH', 'WV', 'Western Visayas'),
('PH', 'DR', 'Davao Region'),
('PH', 'NL', 'Northern Luzon'),
('PH', 'SL', 'Southern Luzon');

-- Provinces/States for United States
INSERT IGNORE INTO provinces_states (country_code, province_state_code, province_state_name) VALUES
('US', 'CA', 'California'),
('US', 'NY', 'New York'),
('US', 'TX', 'Texas'),
('US', 'FL', 'Florida'),
('US', 'IL', 'Illinois');

-- Cities for Philippines - Metro Manila
INSERT IGNORE INTO cities (country_code, province_state_code, city_code, city_name) VALUES
('PH', 'NCR', 'MNL', 'Manila'),
('PH', 'NCR', 'QC', 'Quezon City'),
('PH', 'NCR', 'MKT', 'Makati'),
('PH', 'NCR', 'PSG', 'Pasig'),
('PH', 'NCR', 'TAG', 'Taguig'),
('PH', 'NCR', 'MDL', 'Mandaluyong'),
('PH', 'NCR', 'MRK', 'Marikina'),
('PH', 'NCR', 'CAL', 'Caloocan'),
('PH', 'NCR', 'LP', 'Las Piñas'),
('PH', 'NCR', 'MUN', 'Muntinlupa'),
('PH', 'NCR', 'PAR', 'Parañaque'),
('PH', 'NCR', 'VAL', 'Valenzuela'),
('PH', 'NCR', 'MAL', 'Malabon'),
('PH', 'NCR', 'NAV', 'Navotas'),
('PH', 'NCR', 'SJC', 'San Juan'),
('PH', 'NCR', 'PAT', 'Pateros');

-- Cities for United States - California
INSERT IGNORE INTO cities (country_code, province_state_code, city_code, city_name) VALUES
('US', 'CA', 'LA', 'Los Angeles'),
('US', 'CA', 'SF', 'San Francisco'),
('US', 'CA', 'SD', 'San Diego'),
('US', 'CA', 'SAC', 'Sacramento'),
('US', 'CA', 'SJ', 'San Jose');

-- Barangays for Makati
INSERT IGNORE INTO barangays (country_code, province_state_code, city_code, barangay_code, barangay_name) VALUES
('PH', 'NCR', 'MKT', 'BA', 'Bel-Air'),
('PH', 'NCR', 'MKT', 'FP', 'Forbes Park'),
('PH', 'NCR', 'MKT', 'LV', 'Legaspi Village'),
('PH', 'NCR', 'MKT', 'SV', 'Salcedo Village'),
('PH', 'NCR', 'MKT', 'SL', 'San Lorenzo'),
('PH', 'NCR', 'MKT', 'PO', 'Poblacion'),
('PH', 'NCR', 'MKT', 'MA', 'Magallanes'),
('PH', 'NCR', 'MKT', 'RW', 'Rockwell'),
('PH', 'NCR', 'MKT', 'UC', 'Urdaneta'),
('PH', 'NCR', 'MKT', 'KA', 'Kasilawan');

-- Barangays for Quezon City
INSERT IGNORE INTO barangays (country_code, province_state_code, city_code, barangay_code, barangay_name) VALUES
('PH', 'NCR', 'QC', 'BPA', 'Bagong Pag-asa'),
('PH', 'NCR', 'QC', 'BH', 'Batasan Hills'),
('PH', 'NCR', 'QC', 'COM', 'Commonwealth'),
('PH', 'NCR', 'QC', 'CUB', 'Cubao'),
('PH', 'NCR', 'QC', 'DIL', 'Diliman'),
('PH', 'NCR', 'QC', 'FV', 'Fairview'),
('PH', 'NCR', 'QC', 'LIB', 'Libis'),
('PH', 'NCR', 'QC', 'NOV', 'Novaliches'),
('PH', 'NCR', 'QC', 'UP', 'UP Campus'),
('PH', 'NCR', 'QC', 'KAM', 'Kamuning');

-- Barangays for Manila
INSERT IGNORE INTO barangays (country_code, province_state_code, city_code, barangay_code, barangay_name) VALUES
('PH', 'NCR', 'MNL', 'ERM', 'Ermita'),
('PH', 'NCR', 'MNL', 'MAL', 'Malate'),
('PH', 'NCR', 'MNL', 'INT', 'Intramuros'),
('PH', 'NCR', 'MNL', 'BIN', 'Binondo'),
('PH', 'NCR', 'MNL', 'SMP', 'Sampaloc'),
('PH', 'NCR', 'MNL', 'STA', 'Santa Ana'),
('PH', 'NCR', 'MNL', 'TON', 'Tondo'),
('PH', 'NCR', 'MNL', 'PAC', 'Paco'),
('PH', 'NCR', 'MNL', 'PAN', 'Pandacan'),
('PH', 'NCR', 'MNL', 'STC', 'Santa Cruz');

-- Insert sample address reference data
-- Countries
INSERT IGNORE INTO countries (country_code, country_name) VALUES
('PH', 'Philippines'),
('US', 'United States'),
('CA', 'Canada'),
('AU', 'Australia'),
('UK', 'United Kingdom'),
('SG', 'Singapore'),
('MY', 'Malaysia'),
('TH', 'Thailand'),
('VN', 'Vietnam'),
('ID', 'Indonesia');

-- Provinces/States for Philippines
INSERT IGNORE INTO provinces_states (country_code, province_state_code, province_state_name) VALUES
('PH', 'NCR', 'National Capital Region (Metro Manila)'),
('PH', 'CAR', 'Cordillera Administrative Region'),
('PH', 'I', 'Ilocos Region'),
('PH', 'II', 'Cagayan Valley'),
('PH', 'III', 'Central Luzon'),
('PH', 'IV-A', 'CALABARZON'),
('PH', 'IV-B', 'MIMAROPA'),
('PH', 'V', 'Bicol Region'),
('PH', 'VI', 'Western Visayas'),
('PH', 'VII', 'Central Visayas'),
('PH', 'VIII', 'Eastern Visayas'),
('PH', 'IX', 'Zamboanga Peninsula'),
('PH', 'X', 'Northern Mindanao'),
('PH', 'XI', 'Davao Region'),
('PH', 'XII', 'SOCCSKSARGEN'),
('PH', 'XIII', 'Caraga'),
('PH', 'ARMM', 'Autonomous Region in Muslim Mindanao'),
('PH', 'BARMM', 'Bangsamoro Autonomous Region in Muslim Mindanao');

-- US States (sample)
INSERT IGNORE INTO provinces_states (country_code, province_state_code, province_state_name) VALUES
('US', 'CA', 'California'),
('US', 'NY', 'New York'),
('US', 'TX', 'Texas'),
('US', 'FL', 'Florida'),
('US', 'IL', 'Illinois'),
('US', 'PA', 'Pennsylvania'),
('US', 'OH', 'Ohio'),
('US', 'GA', 'Georgia'),
('US', 'NC', 'North Carolina'),
('US', 'MI', 'Michigan');

-- Cities for Metro Manila (NCR)
INSERT IGNORE INTO cities (country_code, province_state_code, city_code, city_name) VALUES
('PH', 'NCR', 'MNL', 'Manila'),
('PH', 'NCR', 'QC', 'Quezon City'),
('PH', 'NCR', 'CLT', 'Caloocan'),
('PH', 'NCR', 'LAS', 'Las Piñas'),
('PH', 'NCR', 'MKT', 'Makati'),
('PH', 'NCR', 'MLD', 'Malabon'),
('PH', 'NCR', 'MND', 'Mandaluyong'),
('PH', 'NCR', 'MAR', 'Marikina'),
('PH', 'NCR', 'MNT', 'Muntinlupa'),
('PH', 'NCR', 'NAV', 'Navotas'),
('PH', 'NCR', 'PAR', 'Parañaque'),
('PH', 'NCR', 'PAS', 'Pasay'),
('PH', 'NCR', 'PSG', 'Pasig'),
('PH', 'NCR', 'PAT', 'Pateros'),
('PH', 'NCR', 'SJN', 'San Juan'),
('PH', 'NCR', 'TAG', 'Taguig'),
('PH', 'NCR', 'VAL', 'Valenzuela');

-- Sample Barangays for Makati
INSERT IGNORE INTO barangays (country_code, province_state_code, city_code, barangay_code, barangay_name) VALUES
('PH', 'NCR', 'MKT', 'BEL', 'Bel-Air'),
('PH', 'NCR', 'MKT', 'CMT', 'Cementerio'),
('PH', 'NCR', 'MKT', 'FOR', 'Forbes Park'),
('PH', 'NCR', 'MKT', 'KAR', 'Kasilawan'),
('PH', 'NCR', 'MKT', 'LEG', 'Legaspi Village'),
('PH', 'NCR', 'MKT', 'MAG', 'Magallanes'),
('PH', 'NCR', 'MKT', 'OLO', 'Olympia'),
('PH', 'NCR', 'MKT', 'PAL', 'Palanan'),
('PH', 'NCR', 'MKT', 'POP', 'Poblacion'),
('PH', 'NCR', 'MKT', 'RCH', 'Rockwell'),
('PH', 'NCR', 'MKT', 'SAL', 'Salcedo Village'),
('PH', 'NCR', 'MKT', 'SAN', 'San Antonio'),
('PH', 'NCR', 'MKT', 'SLO', 'San Lorenzo'),
('PH', 'NCR', 'MKT', 'SIN', 'Singkamas'),
('PH', 'NCR', 'MKT', 'TEJ', 'Tejeros'),
('PH', 'NCR', 'MKT', 'URS', 'Urdaneta'),
('PH', 'NCR', 'MKT', 'VAL', 'Valenzuela');

-- Sample Barangays for Quezon City
INSERT IGNORE INTO barangays (country_code, province_state_code, city_code, barangay_code, barangay_name) VALUES
('PH', 'NCR', 'QC', 'BAG', 'Bagong Pag-asa'),
('PH', 'NCR', 'QC', 'BAH', 'Bahay Toro'),
('PH', 'NCR', 'QC', 'BAL', 'Balingasa'),
('PH', 'NCR', 'QC', 'BAT', 'Batasan Hills'),
('PH', 'NCR', 'QC', 'COM', 'Commonwealth'),
('PH', 'NCR', 'QC', 'CUB', 'Cubao'),
('PH', 'NCR', 'QC', 'DIL', 'Diliman'),
('PH', 'NCR', 'QC', 'FAI', 'Fairview'),
('PH', 'NCR', 'QC', 'KAM', 'Kamuning'),
('PH', 'NCR', 'QC', 'LIB', 'Libis'),
('PH', 'NCR', 'QC', 'MAL', 'Malibay'),
('PH', 'NCR', 'QC', 'NOV', 'Novaliches'),
('PH', 'NCR', 'QC', 'PAY', 'Payatas'),
('PH', 'NCR', 'QC', 'PHI', 'Philam'),
('PH', 'NCR', 'QC', 'SIK', 'Sikatuna Village'),
('PH', 'NCR', 'QC', 'TIM', 'Timog'),
('PH', 'NCR', 'QC', 'UP', 'UP Campus');

-- Step 5: Verify Setup
SELECT 'Database setup completed successfully!' as status;

-- Show created tables
SHOW TABLES;

-- Show users
SELECT 'Users Table:' as info;
SELECT username, role, created_at FROM users;

-- Show employees
SELECT 'Employees Table:' as info;
SELECT employee_id, first_name, last_name, department, position, job_title, manager, salary FROM employees;

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
