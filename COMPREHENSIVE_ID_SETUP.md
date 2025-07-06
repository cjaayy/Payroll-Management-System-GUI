# Comprehensive Employee ID Setup Guide

## Quick Start

### 1. Database Setup
1. Run the updated `setup_database.sql` to add the new `comprehensive_employee_id` field
2. Optionally run `test_comprehensive_ids.sql` to see example data

### 2. Compile and Run
```bash
# Compile the application
javac -d classes -cp "lib/*;src" src\models\*.java src\managers\*.java src\gui\*.java src\database\*.java

# Run the application
java -cp "lib/*;classes" gui.PayrollManagementSystemGUI
```

### 3. Using the New ID System

#### Creating New Employees
1. Click "Add Employee" in the Employee Management panel
2. Fill in employee details including:
   - **Department**: Choose from IT, HR, Finance, Marketing, etc.
   - **Hire Date**: Select the employee's start date
3. The system will automatically generate a comprehensive ID like `IT-202501-001`

#### Employee ID Formats Supported
- **Comprehensive**: `IT-202501-001` (Department-YearMonth-Sequence)
- **Legacy**: `EMP001` (for backward compatibility)
- **Numeric**: `1` (internal ID number)

#### Searching and Lookups
You can search for employees using any of these formats:
- Search: `IT-202501-001` → Finds IT employee hired in Jan 2025, sequence 001
- Search: `EMP001` → Finds employee with legacy ID EMP001
- Search: `1` → Finds employee with internal ID 1
- Search: `John` → Finds employees with "John" in their name

## Department Code Reference

| Department | Code | Example ID |
|------------|------|------------|
| Information Technology | IT | IT-202501-001 |
| Human Resources | HR | HR-202501-001 |
| Finance | FIN | FIN-202501-001 |
| Marketing | MKT | MKT-202501-001 |
| Sales | SAL | SAL-202501-001 |
| Operations | OPS | OPS-202501-001 |
| Engineering | ENG | ENG-202501-001 |
| Research & Development | RND | RND-202501-001 |
| Customer Service | CS | CS-202501-001 |
| Administration | ADM | ADM-202501-001 |

## Examples of Generated IDs

### Scenario: New Employees in January 2025
- **First IT Employee**: `IT-202501-001`
- **Second IT Employee**: `IT-202501-002`
- **First HR Employee**: `HR-202501-001`
- **First Finance Employee**: `FIN-202501-001`

### Scenario: New Employees in February 2025
- **First IT Employee (Feb)**: `IT-202502-001`
- **Third IT Employee (Feb)**: `IT-202502-003`

## Benefits of the New System

### 1. Meaningful IDs
- Instantly see employee's department from the ID
- Know the hiring period (year and month)
- Understand the sequence within that department/month

### 2. Professional Organization
- IDs follow a consistent, logical pattern
- Easy to organize and manage employees by department
- Clear hiring chronology within departments

### 3. Flexible Usage
- Accept multiple ID formats for user convenience
- Backward compatible with existing data
- Smart search across all formats

## Troubleshooting

### Common Issues and Solutions

#### "Invalid employee ID format"
- **Solution**: Use one of these formats:
  - Comprehensive: `IT-202501-001`
  - Legacy: `EMP001`
  - Numeric: `1`

#### Employee not found
- **Solution**: Check if you're using the correct ID format
- Try searching by name instead of ID
- Verify the employee is active (not deleted)

#### Database connection issues
- **Solution**: Ensure MySQL is running
- Check database credentials in `DatabaseConfig.java`
- Verify the database exists and tables are created

### Getting Help
1. Check the console output for detailed error messages
2. Review the `EMPLOYEE_ID_FEATURES.md` for complete documentation
3. Use the test data in `test_comprehensive_ids.sql` to verify setup

## Advanced Features

### Custom Department Codes
The system automatically generates department codes, but you can customize them by modifying the `getDepartmentCode()` method in `EmployeeManager.java`.

### Reporting by Department
Use the comprehensive IDs to generate reports grouped by department:
```sql
SELECT 
    LEFT(comprehensive_employee_id, 3) as Department,
    COUNT(*) as EmployeeCount
FROM employees 
WHERE status = 'ACTIVE'
GROUP BY LEFT(comprehensive_employee_id, 3);
```

### Historical Analysis
Track hiring patterns by analyzing the date component:
```sql
SELECT 
    SUBSTRING(comprehensive_employee_id, 5, 6) as YearMonth,
    COUNT(*) as HiresCount
FROM employees 
GROUP BY SUBSTRING(comprehensive_employee_id, 5, 6)
ORDER BY YearMonth;
```
