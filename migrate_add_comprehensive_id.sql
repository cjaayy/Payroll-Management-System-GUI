-- Migration script to add comprehensive_employee_id column
-- Run this script on your existing payroll_system database

USE payroll_system;

-- Check if the column already exists before adding it
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'payroll_system' 
    AND TABLE_NAME = 'employees' 
    AND COLUMN_NAME = 'comprehensive_employee_id'
);

-- Add the column only if it doesn't exist
SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE employees ADD COLUMN comprehensive_employee_id VARCHAR(50) AFTER employee_id', 
    'SELECT "Column comprehensive_employee_id already exists" as Message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verify the column was added
DESCRIBE employees;

-- Show current table structure
SELECT 'Table structure after migration:' as Message;
SHOW COLUMNS FROM employees;

-- Display success message
SELECT 'Migration completed successfully!' as Status;
