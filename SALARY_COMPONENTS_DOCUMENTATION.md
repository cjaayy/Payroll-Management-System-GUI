# Salary Components Feature Documentation

## Overview
The Payroll Management System now includes comprehensive salary components functionality, allowing for flexible management of employee allowances, deductions, and bonuses.

## New Features Added

### 1. Custom Salary Components
- **Allowances**: HRA, Medical, Transport, Food, Education, Communication
- **Deductions**: PF, ESI, Professional Tax, Income Tax
- **Bonuses**: Performance Bonus, Festival Bonus, Overtime

### 2. Salary Component Types
- **Fixed Amount**: Specific monetary value (e.g., $1500 transport allowance)
- **Percentage**: Percentage of base salary (e.g., 40% HRA)
- **Active/Inactive**: Components can be enabled or disabled

### 3. Employee-Specific Components
- Each employee can have customized salary components
- Override default amounts with employee-specific values
- Effective date tracking for component changes
- Historical tracking with start and end dates

## New Classes Added

### Models
1. **SalaryComponent.java** - Represents a salary component (allowance, deduction, bonus)
2. **EmployeeSalaryComponent.java** - Links employees to their specific salary components

### Managers
1. **SalaryComponentManager.java** - Handles business logic for salary components

### GUI Components
1. **SalaryComponentDialog.java** - Dialog for creating/editing salary components
2. **SalaryComponentPanel.java** - Main panel for managing salary components
3. **EmployeeSalaryComponentDialog.java** - Dialog for managing employee salary components
4. **AddEmployeeComponentDialog.java** - Dialog for adding components to employees
5. **EditEmployeeComponentDialog.java** - Dialog for editing employee components

## Database Schema Updates

### New Tables
1. **salary_components** - Stores master salary component definitions
2. **employee_salary_components** - Links employees to their specific components
3. **payroll** - Updated to include salary component totals

### Sample Data
- Pre-populated with common allowances and deductions
- Sample employee assignments for demonstration

## How to Use

### 1. Managing Salary Components
- Access via Main Menu → "Salary Components"
- Add new components (allowances, deductions, bonuses)
- Edit existing components
- Set default amounts and percentages
- Enable/disable components

### 2. Employee Salary Management
- Access via Employee Management → Select Employee → "Manage Salary"
- Add components to employees
- Customize amounts for specific employees
- Set effective dates for changes
- View salary breakdown

### 3. Enhanced Payroll Processing
- Payroll calculations now include salary components
- Automatic calculation of allowances and deductions
- Salary breakdown view in payroll dialogs
- Net salary calculation includes all components

## Key Features

### Flexible Component System
- Support for both fixed amounts and percentages
- Employee-specific customization
- Historical tracking of changes
- Easy addition of new component types

### Comprehensive Salary Breakdown
- Base salary + allowances + bonuses - deductions = net salary
- Detailed breakdown available in payroll processing
- Component-wise calculation display

### Common Allowances Included
- **HRA (House Rent Allowance)**: Typically 30-50% of base salary
- **Medical Allowance**: Fixed amount for medical expenses
- **Transport Allowance**: Fixed amount for transportation
- **Food Allowance**: Fixed amount for meals
- **Education Allowance**: For children's education expenses
- **Communication Allowance**: For phone/internet expenses

### Standard Deductions
- **PF (Provident Fund)**: Typically 12% of base salary
- **ESI (Employee State Insurance)**: Typically 1.75% of base salary
- **Professional Tax**: Fixed amount based on location
- **Income Tax**: Variable based on salary slabs

## Sample Usage Scenarios

### Scenario 1: Setting up HRA for an Employee
1. Go to Employee Management
2. Select the employee
3. Click "Manage Salary"
4. Click "Add Component"
5. Select "HRA" from dropdown
6. Set amount as 40% (percentage)
7. Set effective date
8. Save

### Scenario 2: Creating a New Allowance
1. Go to Main Menu → "Salary Components"
2. Click "Add Component"
3. Enter name (e.g., "Gym Allowance")
4. Select type "ALLOWANCE"
5. Set default amount (e.g., $500)
6. Add description
7. Save

### Scenario 3: Processing Payroll with Components
1. Go to Payroll Management
2. Click "Create Payroll"
3. Enter employee ID
4. Click "Salary Breakdown" to view all components
5. System automatically calculates total with components
6. Save payroll

## Technical Implementation

### Architecture
- **Model Layer**: SalaryComponent, EmployeeSalaryComponent classes
- **Manager Layer**: SalaryComponentManager for business logic
- **GUI Layer**: Multiple dialogs and panels for user interaction
- **Database Layer**: New tables with foreign key relationships

### Integration
- Integrated with existing Employee and Payroll systems
- Seamless data flow between components
- Consistent UI/UX with existing application

### Data Flow
1. Master components defined in salary_components table
2. Employee assignments in employee_salary_components table
3. Payroll calculations use component data
4. Historical tracking maintained

## Benefits

### For HR/Payroll Teams
- Standardized salary component management
- Easy setup of new employees
- Flexible component customization
- Historical tracking of changes

### For Employees
- Transparent salary breakdown
- Clear understanding of components
- Accurate payroll calculations

### For Management
- Consistent salary structure
- Easy reporting and analysis
- Compliance with standard practices

## Future Enhancements Possible

1. **Tax Calculation Integration**: Automatic tax calculations based on components
2. **Reporting**: Component-wise salary reports
3. **Approval Workflow**: Multi-level approval for component changes
4. **Bulk Operations**: Mass assignment of components to employees
5. **Component Templates**: Pre-defined component sets for different roles
6. **Integration**: Export to accounting systems

## Troubleshooting

### Common Issues
1. **Component not showing**: Check if component is active
2. **Incorrect calculation**: Verify percentage vs fixed amount setting
3. **Missing component**: Ensure effective date is correct
4. **Database errors**: Check database connection and table structure

### Support
- All salary component data is stored in the database
- Changes are tracked with timestamps
- System maintains data integrity with foreign keys
- Error handling provides user-friendly messages

## Conclusion

The salary components feature provides a comprehensive solution for managing employee compensation structures. It offers flexibility, transparency, and ease of use while maintaining data integrity and supporting complex payroll calculations.

The system is designed to be extensible, allowing for easy addition of new component types and customization based on organizational needs.
