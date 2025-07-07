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
-- Enhanced Users table for comprehensive authentication and security
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    salt VARCHAR(32),
    password VARCHAR(255), -- Legacy field for backward compatibility
    email VARCHAR(100) UNIQUE,
    full_name VARCHAR(100),
    role ENUM('ADMIN', 'HR_OFFICER', 'PAYROLL_OFFICER', 'EMPLOYEE') NOT NULL DEFAULT 'EMPLOYEE',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    last_password_change TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    failed_login_attempts INT DEFAULT 0,
    lockout_until TIMESTAMP NULL,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_active (is_active)
);

-- Audit Trail table for comprehensive security logging
CREATE TABLE IF NOT EXISTS audit_trail (
    audit_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    action VARCHAR(100) NOT NULL,
    table_name VARCHAR(50),
    record_id VARCHAR(50),
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_success BOOLEAN DEFAULT TRUE,
    error_message TEXT,
    
    INDEX idx_username (username),
    INDEX idx_action (action),
    INDEX idx_timestamp (timestamp),
    INDEX idx_table_record (table_name, record_id)
);

-- Legacy users table mapping for backward compatibility
CREATE TABLE IF NOT EXISTS legacy_users (
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
    total_allowances DECIMAL(10, 2) DEFAULT 0,
    total_custom_deductions DECIMAL(10, 2) DEFAULT 0,
    total_custom_bonuses DECIMAL(10, 2) DEFAULT 0,
    gross_pay DECIMAL(10, 2) NOT NULL,
    tax_deduction DECIMAL(10, 2) DEFAULT 0,
    net_pay DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE
);

-- Salary Components table
CREATE TABLE IF NOT EXISTS salary_components (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL, -- EARNING, DEDUCTION, ALLOWANCE, BONUS
    amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    is_percentage BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_date DATE DEFAULT (CURRENT_DATE),
    last_modified DATE DEFAULT (CURRENT_DATE),
    created_by VARCHAR(50),
    modified_by VARCHAR(50),
    UNIQUE KEY unique_component_name (name, type)
);

-- Employee Salary Components table (linking employees to their specific salary components)
CREATE TABLE IF NOT EXISTS employee_salary_components (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL,
    salary_component_id INT NOT NULL,
    custom_amount DECIMAL(10, 2) NOT NULL,
    is_percentage BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    effective_date DATE NOT NULL,
    end_date DATE NULL,
    created_date DATE DEFAULT (CURRENT_DATE),
    created_by VARCHAR(50),
    remarks TEXT,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (salary_component_id) REFERENCES salary_components(id) ON DELETE CASCADE,
    UNIQUE KEY unique_employee_component (employee_id, salary_component_id, effective_date)
);

-- Step 4: Insert Default Data
-- Insert default admin user with enhanced security
-- Note: Password will be hashed by the application, this is for legacy compatibility
INSERT IGNORE INTO users (username, password, email, full_name, role, created_by, last_modified_by) 
VALUES ('admin', 'admin123', 'admin@company.com', 'System Administrator', 'ADMIN', 'SYSTEM', 'SYSTEM');

-- Insert default HR officer
INSERT IGNORE INTO users (username, password, email, full_name, role, created_by, last_modified_by) 
VALUES ('hr_officer', 'hr123', 'hr@company.com', 'HR Officer', 'HR_OFFICER', 'SYSTEM', 'SYSTEM');

-- Insert default payroll officer
INSERT IGNORE INTO users (username, password, email, full_name, role, created_by, last_modified_by) 
VALUES ('payroll', 'payroll123', 'payroll@company.com', 'Payroll Officer', 'PAYROLL_OFFICER', 'SYSTEM', 'SYSTEM');

-- Insert sample employees (optional)
INSERT IGNORE INTO employees (employee_id, first_name, last_name, email, phone, department, position, job_title, manager, hire_date, salary) 
VALUES 
('EMP001', 'John', 'Doe', 'john.doe@company.com', '555-0101', 'IT', 'Software Developer', 'Senior Software Developer', 'Jane Smith', '2024-01-15', 75000.00),
('EMP002', 'Jane', 'Smith', 'jane.smith@company.com', '555-0102', 'HR', 'HR Manager', 'Human Resources Manager', 'Michael Johnson', '2024-02-01', 65000.00),
('EMP003', 'Mike', 'Johnson', 'mike.johnson@company.com', '555-0103', 'Finance', 'Accountant', 'Senior Accountant', 'Sarah Davis', '2024-03-01', 55000.00);

-- Insert default salary components for Philippine payroll system
INSERT IGNORE INTO salary_components (name, type, amount, description, is_active, is_percentage, created_by, modified_by) VALUES
-- Philippine Standard Allowances
('Rice Allowance', 'EARNING', 2000.00, 'Monthly rice subsidy allowance', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Transportation Allowance', 'EARNING', 2000.00, 'Daily transportation allowance', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Meal Allowance', 'EARNING', 1500.00, 'Daily meal allowance', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Connectivity Allowance', 'EARNING', 1000.00, 'Internet/Communication allowance', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Clothing Allowance', 'EARNING', 6000.00, 'Annual clothing allowance', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Medical Allowance', 'EARNING', 3000.00, 'Medical expenses allowance', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Education Allowance', 'EARNING', 5000.00, 'Education allowance for children', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),

-- Philippine Holiday and Overtime Pay
('Overtime Pay', 'EARNING', 0.00, 'Overtime pay (25% to 100% premium)', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Holiday Pay', 'EARNING', 0.00, 'Holiday premium pay', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Night Differential', 'EARNING', 0.00, 'Night shift differential (10% minimum)', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Hazard Pay', 'EARNING', 0.00, 'Hazard duty pay', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),

-- Philippine Mandatory Deductions
('SSS Contribution', 'DEDUCTION', 0.00, 'Social Security System contribution', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('PhilHealth Contribution', 'DEDUCTION', 0.00, 'Philippine Health Insurance Corporation', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Pag-IBIG Contribution', 'DEDUCTION', 0.00, 'Home Development Mutual Fund', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Withholding Tax', 'DEDUCTION', 0.00, 'Bureau of Internal Revenue tax', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),

-- Other Deductions
('Tardiness', 'DEDUCTION', 0.00, 'Late arrival deduction', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Absences', 'DEDUCTION', 0.00, 'Absence deduction', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Cash Advance', 'DEDUCTION', 0.00, 'Employee cash advance deduction', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Loan Payment', 'DEDUCTION', 0.00, 'Employee loan payment', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),

-- Philippine Special Pay
('13th Month Pay', 'EARNING', 0.00, 'Mandatory 13th month pay (1/12 of annual basic salary)', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Performance Bonus', 'EARNING', 0.00, 'Performance-based bonus', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Service Incentive Leave', 'EARNING', 0.00, 'Monetized leave credits', TRUE, FALSE, 'SYSTEM', 'SYSTEM'),
('Tax Refund', 'EARNING', 0.00, 'Tax over-withholding refund', TRUE, FALSE, 'SYSTEM', 'SYSTEM');

-- Insert sample Philippine employee salary components
INSERT IGNORE INTO employee_salary_components (employee_id, salary_component_id, custom_amount, is_percentage, effective_date, is_active, created_by, remarks) VALUES
-- John Doe (EMP001) - IT Developer (PHP 50,000 monthly)
('EMP001', 1, 2000.00, FALSE, '2024-01-15', TRUE, 'SYSTEM', 'Standard rice allowance'),  -- Rice Allowance
('EMP001', 2, 2500.00, FALSE, '2024-01-15', TRUE, 'SYSTEM', 'IT transport allowance'),  -- Transportation Allowance
('EMP001', 3, 2000.00, FALSE, '2024-01-15', TRUE, 'SYSTEM', 'Daily meal allowance'),  -- Meal Allowance
('EMP001', 4, 1500.00, FALSE, '2024-01-15', TRUE, 'SYSTEM', 'IT connectivity allowance'),  -- Connectivity Allowance
('EMP001', 13, 240.00, FALSE, '2024-01-15', TRUE, 'SYSTEM', 'SSS employee contribution'),  -- SSS Contribution (employee share)
('EMP001', 14, 1250.00, FALSE, '2024-01-15', TRUE, 'SYSTEM', 'PhilHealth employee contribution'), -- PhilHealth Contribution (employee share)
('EMP001', 15, 100.00, FALSE, '2024-01-15', TRUE, 'SYSTEM', 'Pag-IBIG employee contribution'),  -- Pag-IBIG Contribution (employee share)
('EMP001', 16, 4500.00, FALSE, '2024-01-15', TRUE, 'SYSTEM', 'Monthly withholding tax'), -- Withholding Tax

-- Jane Smith (EMP002) - HR Manager (PHP 45,000 monthly)
('EMP002', 1, 2000.00, FALSE, '2024-02-01', TRUE, 'SYSTEM', 'Standard rice allowance'),  -- Rice Allowance
('EMP002', 2, 2200.00, FALSE, '2024-02-01', TRUE, 'SYSTEM', 'HR transport allowance'),  -- Transportation Allowance
('EMP002', 3, 1800.00, FALSE, '2024-02-01', TRUE, 'SYSTEM', 'Daily meal allowance'),  -- Meal Allowance
('EMP002', 4, 1200.00, FALSE, '2024-02-01', TRUE, 'SYSTEM', 'HR connectivity allowance'),  -- Connectivity Allowance
('EMP002', 6, 3500.00, FALSE, '2024-02-01', TRUE, 'SYSTEM', 'Enhanced medical allowance'),  -- Medical Allowance
('EMP002', 13, 217.50, FALSE, '2024-02-01', TRUE, 'SYSTEM', 'SSS employee contribution'),  -- SSS Contribution (employee share)
('EMP002', 14, 1125.00, FALSE, '2024-02-01', TRUE, 'SYSTEM', 'PhilHealth employee contribution'), -- PhilHealth Contribution (employee share)
('EMP002', 15, 90.00, FALSE, '2024-02-01', TRUE, 'SYSTEM', 'Pag-IBIG employee contribution'),   -- Pag-IBIG Contribution (employee share)
('EMP002', 16, 3200.00, FALSE, '2024-02-01', TRUE, 'SYSTEM', 'Monthly withholding tax'), -- Withholding Tax

-- Mike Johnson (EMP003) - Finance (PHP 40,000 monthly)
('EMP003', 1, 2000.00, FALSE, '2024-03-01', TRUE, 'SYSTEM', 'Standard rice allowance'),  -- Rice Allowance
('EMP003', 2, 2000.00, FALSE, '2024-03-01', TRUE, 'SYSTEM', 'Finance transport allowance'),  -- Transportation Allowance
('EMP003', 3, 1600.00, FALSE, '2024-03-01', TRUE, 'SYSTEM', 'Daily meal allowance'),  -- Meal Allowance
('EMP003', 4, 1000.00, FALSE, '2024-03-01', TRUE, 'SYSTEM', 'Finance connectivity allowance'),  -- Connectivity Allowance
('EMP003', 13, 195.00, FALSE, '2024-03-01', TRUE, 'SYSTEM', 'SSS employee contribution'),  -- SSS Contribution (employee share)
('EMP003', 14, 1000.00, FALSE, '2024-03-01', TRUE, 'SYSTEM', 'PhilHealth employee contribution'), -- PhilHealth Contribution (employee share)
('EMP003', 15, 80.00, FALSE, '2024-03-01', TRUE, 'SYSTEM', 'Pag-IBIG employee contribution'),   -- Pag-IBIG Contribution (employee share)
('EMP003', 16, 2100.00, FALSE, '2024-03-01', TRUE, 'SYSTEM', 'Monthly withholding tax'); -- Withholding Tax

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
