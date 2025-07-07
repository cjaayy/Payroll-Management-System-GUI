# Philippine Payroll System Documentation

## Overview
This Payroll Management System has been enhanced specifically for Philippine payroll requirements, including all mandatory government contributions, allowances, and compliance with local labor laws.

## Philippine Payroll Components

### 1. Basic Salary Components

#### Basic Salary
- **Description**: Fixed compensation amount received per pay period
- **Type**: Base component (not included in allowances/deductions)
- **Notes**: Does not include benefits, bonuses, or allowances
- **Implementation**: Stored in `employees.salary` field

#### Minimum Wage Compliance
- **NCR (Metro Manila)**: PHP 610.00 per day (2024 rates)
- **Region IV-A (CALABARZON)**: PHP 470.00 per day
- **Implementation**: Validated through `PhilippinePayrollCalculator.meetsMinimumWage()`
- **Regional Support**: Expandable for all Philippine regions

### 2. Overtime and Premium Pay

#### Overtime Pay
- **Regular Overtime**: 125% of hourly rate (work beyond 8 hours)
- **Holiday Overtime**: 200% for regular holidays, 130% for special holidays
- **Implementation**: `PhilippinePayrollCalculator.calculateOvertimePay()`
- **Database**: Stored in `payroll.overtime_hours` and calculated dynamically

#### Holiday Pay
- **Regular Holiday (no work)**: 100% of daily rate
- **Regular Holiday (with work)**: 200% of daily rate
- **Special Holiday (with work)**: 130% of daily rate
- **Implementation**: `PhilippinePayrollCalculator.calculateHolidayPay()`

#### Night Differential
- **Rate**: Minimum 10% of hourly rate for work between 10 PM and 6 AM
- **Implementation**: `PhilippinePayrollCalculator.calculateNightDifferential()`

### 3. Allowances (Non-Taxable)

#### Rice Allowance
- **Amount**: PHP 2,000.00 per month
- **Purpose**: Monthly rice subsidy allowance
- **Tax Status**: Non-taxable

#### Transportation Allowance
- **Amount**: PHP 2,000.00 per month
- **Purpose**: Daily transportation allowance
- **Tax Status**: Non-taxable

#### Meal Allowance
- **Amount**: PHP 1,500.00 per month
- **Purpose**: Daily meal allowance
- **Tax Status**: Non-taxable

#### Connectivity Allowance
- **Amount**: PHP 1,000.00 per month
- **Purpose**: Internet/Communication allowance
- **Tax Status**: Non-taxable

#### Clothing Allowance
- **Amount**: PHP 6,000.00 per year
- **Purpose**: Annual clothing allowance
- **Tax Status**: Non-taxable

#### Medical Allowance
- **Amount**: PHP 3,000.00 per month
- **Purpose**: Medical expenses allowance
- **Tax Status**: Non-taxable

#### Education Allowance
- **Amount**: PHP 5,000.00 per month
- **Purpose**: Education allowance for children
- **Tax Status**: Non-taxable

### 4. Mandatory Government Deductions

#### SSS (Social Security System) Contributions
- **Employee Share**: Varies by salary bracket (typically 4.5% of monthly salary)
- **Employer Share**: Varies by salary bracket (typically 8.5% of monthly salary)
- **Maximum**: PHP 1,800.00 total contribution
- **Implementation**: `PhilippinePayrollCalculator.calculateSSSContribution()`
- **Benefits**: Retirement, disability, death benefits

#### PhilHealth (Philippine Health Insurance Corporation)
- **Total Contribution**: 5% of monthly salary
- **Employee Share**: 2.5% (split equally with employer)
- **Employer Share**: 2.5%
- **Minimum**: PHP 500.00 per month
- **Maximum**: PHP 5,000.00 per month
- **Implementation**: `PhilippinePayrollCalculator.calculatePhilHealthContribution()`

#### Pag-IBIG (Home Development Mutual Fund)
- **Employee Rate**: 1% (salary ≤ PHP 1,500) or 2% (salary > PHP 1,500)
- **Employee Maximum**: PHP 100.00 per month
- **Employer Share**: Matches employee contribution
- **Implementation**: `PhilippinePayrollCalculator.calculatePagIBIGContribution()`
- **Benefits**: Housing loans and benefits

#### Withholding Tax (Bureau of Internal Revenue)
- **Tax-Exempt Threshold**: PHP 250,000 annually
- **Tax Brackets (TRAIN Law)**:
  - PHP 250,001 - 400,000: 15%
  - PHP 400,001 - 800,000: 20%
  - PHP 800,001 - 2,000,000: 25%
  - PHP 2,000,001 - 8,000,000: 30%
  - PHP 8,000,001 and above: 35%
- **Implementation**: `PhilippinePayrollCalculator.calculateWithholdingTax()`

### 5. Other Deductions

#### Tardiness
- **Calculation**: Deduction based on late arrival time
- **Implementation**: Manual entry in payroll system

#### Absences
- **Calculation**: Daily rate deduction for unauthorized absences
- **Implementation**: Manual entry in payroll system

#### Cash Advance
- **Purpose**: Employee cash advance deduction
- **Implementation**: Manual entry in payroll system

#### Loan Payment
- **Purpose**: Employee loan payment deduction
- **Implementation**: Manual entry in payroll system

### 6. Special Pay and Bonuses

#### 13th Month Pay
- **Amount**: 1/12 of annual basic salary
- **Requirement**: Mandatory for all employees
- **Due Date**: On or before December 24 each year
- **Implementation**: `PhilippinePayrollCalculator.calculate13thMonthPay()`
- **Tax Status**: Tax-exempt up to PHP 90,000

#### Performance Bonus
- **Amount**: Variable based on performance
- **Implementation**: Manual entry in payroll system

#### Service Incentive Leave (SIL)
- **Entitlement**: 5 days per year for employees with 1+ years of service
- **Monetization**: Unused leave converted to cash
- **Implementation**: `PhilippinePayrollCalculator.calculateSILMonetization()`

#### Tax Refund
- **Purpose**: Over-withholding tax refund
- **Implementation**: Manual entry in payroll system

### 7. System Implementation

#### Database Schema
- **salary_components**: Master list of all salary components
- **employee_salary_components**: Employee-specific component assignments
- **payroll**: Payroll records with calculated totals

#### Salary Component Types
- **ALLOWANCE**: Non-taxable benefits
- **DEDUCTION**: Mandatory and voluntary deductions
- **BONUS**: Additional compensation

#### Philippine Payroll Dialog
- **Features**:
  - Automatic calculation of government contributions
  - Minimum wage validation
  - Holiday pay calculations
  - Overtime premium calculations
  - Comprehensive salary breakdown
  - Compliance checks

#### Calculation Flow
1. **Base Salary**: Employee's monthly basic salary
2. **Add Allowances**: All non-taxable allowances
3. **Add Bonuses**: Overtime, holiday pay, bonuses
4. **Calculate Gross Pay**: Base + Allowances + Bonuses
5. **Calculate Deductions**: SSS, PhilHealth, Pag-IBIG, Withholding Tax
6. **Calculate Net Pay**: Gross Pay - Total Deductions

### 8. How to Use the System

#### Creating Philippine Payroll
1. Navigate to Payroll Management
2. Click "Philippine Payroll" button
3. Enter employee details and work hours
4. System automatically calculates all components
5. Review and save payroll record

#### Managing Salary Components
1. Go to Main Menu → "Salary Components"
2. View/edit all Philippine payroll components
3. Assign custom amounts to specific employees
4. Set effective dates for changes

#### Viewing Reports
1. Access Reports section
2. Generate payroll summaries with breakdown
3. View employee-specific salary components
4. Export reports for compliance

## Philippine Payroll Calculation Process

The system now implements the **Standard Philippine Payroll Calculation Process** following the four-step methodology:

### Step 1: Gross Pay Calculation
- **Basic Salary**: Employee's base monthly salary
- **Allowances**: All taxable and non-taxable allowances (rice, transportation, meal, etc.)
- **Bonuses**: Performance bonuses, incentives, and other additional compensation
- **Premium Pay**: Overtime pay, night differential, holiday pay
- **13th Month Pay**: Prorated 13th month pay when applicable

**Formula**: `Gross Pay = Basic Salary + Allowances + Bonuses + Premium Pay + 13th Month Pay`

### Step 2: Mandatory Deductions Calculation
Government-mandated contributions that must be deducted from every employee's salary:
- **SSS Contribution**: Social Security System employee share
- **PhilHealth Contribution**: Philippine Health Insurance Corporation employee share
- **Pag-IBIG Contribution**: Home Development Mutual Fund employee share
- **Withholding Tax**: Bureau of Internal Revenue tax based on tax brackets

**Formula**: `Mandatory Deductions = SSS + PhilHealth + Pag-IBIG + Withholding Tax`

### Step 3: Other Deductions Calculation
Non-mandatory deductions that vary by employee:
- **Loan Payments**: SSS loans, Pag-IBIG loans, company loans
- **Cash Advances**: Salary advances and other cash advances
- **Tardiness/Absences**: Deductions for tardiness or unauthorized absences
- **Other Deductions**: Insurance premiums, union dues, etc.

**Formula**: `Other Deductions = Sum of all non-mandatory deductions`

### Step 4: Net Pay Calculation
Final calculation of the employee's take-home pay:

**Formula**: `Net Pay = Gross Pay - Mandatory Deductions - Other Deductions`

### Implementation Features

#### SalaryComponentManager Methods

1. **`calculateStandardPayroll()`** - Main method implementing the four-step process
2. **`getPayrollCalculationSummary()`** - Generates detailed calculation breakdown
3. **`isMinimumWageCompliant()`** - Checks compliance with minimum wage laws

#### PayrollCalculationResult Class

Comprehensive data structure containing:
- All calculation components and results
- Detailed breakdown of each step
- Government contribution details
- Tax calculations

#### Example Usage

```java
SalaryComponentManager manager = new SalaryComponentManager();

// Calculate payroll for employee
PayrollCalculationResult result = manager.calculateStandardPayroll(
    employeeId,      // Employee ID
    basicSalary,     // Monthly basic salary
    overtimeHours,   // Overtime hours worked
    nightDiffHours,  // Night differential hours
    holidayDays,     // Holiday days worked
    isRegularHoliday, // True if regular holiday
    workedOnHoliday, // True if worked on holiday
    include13thMonth // True to include 13th month pay
);

// Get detailed calculation summary
String summary = manager.getPayrollCalculationSummary(result);

// Check minimum wage compliance
boolean isCompliant = manager.isMinimumWageCompliant(result);
```

### Sample Calculation Output

```
=== PAYROLL CALCULATION SUMMARY ===
Employee ID: 1001

STEP 1: GROSS PAY CALCULATION
Basic Salary: ₱25,000.00
Allowances: ₱0.00
Bonuses: ₱0.00
GROSS PAY: ₱25,000.00

STEP 2: MANDATORY DEDUCTIONS
SSS Contribution: ₱600.00
PhilHealth Contribution: ₱625.00
Pag-IBIG Contribution: ₱100.00
Withholding Tax: ₱625.00
TOTAL MANDATORY DEDUCTIONS: ₱1,950.00

STEP 3: OTHER DEDUCTIONS
Other Deductions: ₱0.00
TOTAL OTHER DEDUCTIONS: ₱0.00

STEP 4: NET PAY CALCULATION
Total Deductions: ₱1,950.00
NET PAY: ₱23,050.00

Formula: Net Pay = Gross Pay - Mandatory Deductions - Other Deductions
₱23,050.00 = ₱25,000.00 - ₱1,950.00 - ₱0.00
```

### Compliance Features

- **Minimum Wage Validation**: Automatically checks if net pay meets regional minimum wage requirements
- **Government Contribution Accuracy**: Uses official SSS, PhilHealth, and Pag-IBIG contribution tables
- **Tax Calculation**: Implements current BIR withholding tax brackets
- **13th Month Pay**: Proper calculation and prorating of mandatory 13th month pay

### 9. Legal Compliance Notes

#### Labor Code Requirements
- 8-hour work day standard
- Overtime pay for work beyond 8 hours
- Holiday pay for designated holidays
- Night differential for night work
- Service incentive leave entitlement

#### Tax Compliance
- TRAIN Law implementation
- Proper tax bracket calculations
- 13th month pay tax exemption
- Accurate withholding tax computation

#### Government Contributions
- SSS enrollment and remittance
- PhilHealth enrollment and remittance
- Pag-IBIG enrollment and remittance
- Accurate employer/employee share calculations

This implementation ensures full compliance with Philippine labor laws and provides a comprehensive payroll management system tailored to local requirements.
