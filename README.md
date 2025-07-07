# Payroll Management System GUI - Philippine Edition

A comprehensive Java Swing-based GUI application for managing employee payroll operations with MySQL database integration, specifically designed for Philippine payroll requirements and compliance.

## Features

- **🆕 Multi-User Management & Security**:
  - **Role-Based Access Control**: Admin, HR Officer, Payroll Officer, and Employee roles
  - **User Authentication**: Secure login with password hashing and salt
  - **Password Security**: Strength validation and secure password policies
  - **Account Management**: Create, edit, deactivate user accounts
  - **Session Management**: Multi-user support with proper session handling
  - **Audit Trail**: Complete logging of all user actions for compliance
  - **Access Control**: Menu items and functions restricted by user role
- **User Authentication**: Enhanced login system with multiple user roles
- **Employee Management**: Add, edit, delete, and search employees
  - **Employment Details**: Store comprehensive employment information including:
    - Job Title (specific position title)
    - Manager information
    - Department and position
    - Personal details (name, email, phone)
    - Hire date and salary
- **Philippine Payroll Compliance**: 🆕 ENHANCED PHILIPPINE FEATURES
  - **Government Contributions**: SSS, PhilHealth, Pag-IBIG calculations
  - **Tax Compliance**: TRAIN Law implementation with proper tax brackets
  - **Minimum Wage**: Regional minimum wage validation
  - **Overtime Pay**: 125% regular, 200% holiday overtime calculations
  - **Holiday Pay**: Regular and special holiday pay calculations
  - **Night Differential**: 10% minimum for night shift work
  - **13th Month Pay**: Mandatory annual bonus calculation
  - **Service Incentive Leave**: Leave monetization
- **Philippine Allowances**: 🆕 COMPREHENSIVE ALLOWANCES
  - **Rice Allowance**: PHP 2,000 monthly rice subsidy
  - **Transportation Allowance**: PHP 2,000 monthly transport allowance
  - **Meal Allowance**: PHP 1,500 monthly meal allowance
  - **Connectivity Allowance**: PHP 1,000 monthly internet/communication
  - **Medical Allowance**: Variable medical expenses allowance
  - **Clothing Allowance**: Annual clothing allowance
  - **Education Allowance**: Children's education allowance
- **Salary Components Management**: 🆕 FLEXIBLE COMPONENT SYSTEM
  - **Custom Allowances**: HRA, Medical, Transport, Food, Education, Communication
  - **Deductions**: PF, ESI, Professional Tax, Income Tax, Tardiness, Absences
  - **Bonuses**: Performance, Festival, Overtime, Holiday Pay
  - **Flexible Setup**: Fixed amounts or percentage-based components
  - **Employee-Specific**: Customize components for individual employees
- **Enhanced Payroll Management**: Create, edit, and manage payroll records
  - **Philippine Payroll Dialog**: Specialized dialog for Philippine payroll calculations
  - **Automatic Calculations**: Include salary components in payroll processing
  - **Salary Breakdown**: Detailed view of allowances, deductions, and bonuses
  - **Compliance Checks**: Minimum wage, tax, and contribution validation
  - **Net Salary Calculation**: Comprehensive calculation including all components
  - **Philippine Payslip Generation**: 🆕 COMPREHENSIVE PAYSLIP SYSTEM
    - **Professional Format**: Complete payslip with company header and branding
    - **Employee Details**: Full employee information including TIN
    - **Detailed Breakdown**: Earnings, allowances, deductions, and net pay
    - **Government Compliance**: All mandatory contributions clearly shown
    - **Print & Save**: Print payslips or save as text files
    - **Signature Section**: Professional signature lines for validation
- **Reports**: Generate various reports including employee summary, payroll summary, department reports, and monthly reports
- **Role-based Access Control**: Admin privileges required for certain operations
- **MySQL Database Integration**: Persistent data storage with MySQL database
- **Automated Database Setup**: Automatic database and table creation

## Philippine Payroll Compliance

### Government Contributions
- **SSS (Social Security System)**: Automatic calculation based on salary brackets
- **PhilHealth**: 5% of salary (split between employer and employee)
- **Pag-IBIG**: 1-2% of salary with PHP 100 maximum employee contribution

### Tax Compliance
- **TRAIN Law**: Proper tax brackets implementation
- **Tax-Exempt Threshold**: PHP 250,000 annually
- **Progressive Tax Rates**: 15%, 20%, 25%, 30%, 35%

### Premium Pay
- **Overtime**: 125% of hourly rate for work beyond 8 hours
- **Holiday Pay**: 200% for regular holidays, 130% for special holidays
- **Night Differential**: 10% minimum for 10 PM - 6 AM work

### Special Benefits
- **13th Month Pay**: 1/12 of annual basic salary
- **Service Incentive Leave**: 5 days annual leave with monetization
- **Minimum Wage**: Regional compliance validation

## Prerequisites

- Java 8 or higher
- MySQL Server (5.7 or higher)
- MySQL Workbench (for database management)

## Quick Start

1. **Run the application**:
   ```
   run.bat
   ```

2. **Clean build files**:
   ```
   clean.bat
   ```

3. **Access debug tools**:
   ```
   debug-tools.bat
   ```

## Database Setup

1. **Install MySQL Server** and ensure it's running
2. **Configure Database Credentials** using system properties:
   ```bash
   # Set database password as system property
   java -Ddb.password=your_password -cp "lib/*;classes" gui.PayrollManagementSystemGUI
   ```
   Or modify `src/database/DatabaseConfig.java` if needed.
3. **Download MySQL JDBC Driver** (automatic with scripts)

## Quick Start

### Method 1: Using Batch File (Windows)
```bash
# Compile and run
run.bat
```

### Method 2: Manual Setup
```bash
# Download MySQL JDBC driver to lib/ directory
# Then compile:
javac -cp "lib/*;src" -d classes src/gui/*.java src/database/*.java src/models/*.java src/managers/*.java

# Run:
java -cp "lib/*;classes" gui.PayrollManagementSystemGUI
```

## Default Login
- **Username**: `admin`
- **Password**: `admin123`

## 🆕 User Management Features

### User Roles & Permissions
- **Admin**: Full system access, can manage users, employees, and payroll
- **HR Officer**: Can manage employees and view payroll, limited user management
- **Payroll Officer**: Can manage payroll and view employee data
- **Employee**: Can view own payroll information (self-service)

### Security Features
- **Password Hashing**: SHA-256 with salt for secure password storage
- **Password Strength**: Enforced minimum requirements (8+ chars, mixed case, numbers, symbols)
- **Account Lockout**: Automatic lockout after failed login attempts
- **Session Management**: Secure session handling with proper logout
- **Audit Trail**: Complete logging of all user actions with timestamps

### User Management Panel
- **Add Users**: Create new user accounts with role assignment
- **Edit Users**: Modify user details, roles, and permissions
- **Deactivate Users**: Temporarily disable user accounts
- **Password Reset**: Admin can reset user passwords
- **User Search**: Find users by name, email, or role
- **Audit Trail Viewing**: View complete action history for any user

### Default User Accounts
The system comes with pre-configured user accounts:
- **admin/admin123** - System Administrator (full access)
- **hr.officer/hr123** - HR Officer (employee and limited user management)
- **payroll.officer/payroll123** - Payroll Officer (payroll management)

### Access Control
- Menu items are dynamically shown/hidden based on user permissions
- All sensitive operations require appropriate role permissions
- Failed access attempts are logged in the audit trail

## Project Structure

```
Payroll Management System GUI/
├── src/
│   ├── models/
│   │   ├── User.java           # Enhanced user model with roles and security
│   │   ├── Role.java           # User role enumeration
│   │   ├── AuditTrail.java     # Audit trail model for action logging
│   │   ├── Employee.java       # Employee data model
│   │   └── Payroll.java        # Payroll data model
│   ├── managers/
│   │   ├── AuthenticationManager.java  # User authentication logic
│   │   ├── UserManager.java           # User CRUD and management operations
│   │   ├── AuditTrailManager.java     # Audit trail logging and retrieval
│   │   ├── PasswordSecurity.java      # Password hashing and validation
│   │   ├── EmployeeManager.java       # Employee management logic
│   │   └── PayrollManager.java        # Payroll management logic
│   ├── database/
│   │   ├── DatabaseConnection.java     # MySQL database connection
│   │   ├── DatabaseConfig.java         # Database configuration
│   │   ├── DatabaseDAO.java            # Data access object interface
│   │   └── MySQLDatabaseDAO.java       # MySQL DAO implementation
│   └── gui/
│       ├── PayrollManagementSystemGUI.java  # Main application window
│       ├── LoginPanel.java              # Login interface
│       ├── MainMenuPanel.java           # Main menu interface with role-based access
│       ├── UserManagementPanel.java     # 🆕 User management interface
│       ├── UserDialog.java              # 🆕 User add/edit dialog
│       ├── AuditTrailDialog.java        # 🆕 Audit trail viewing dialog
│       ├── EmployeePanel.java           # Employee management interface
│       ├── EmployeeDialog.java          # Employee add/edit dialog
│       ├── PayrollPanel.java            # Payroll management interface
│       ├── PayrollDialog.java           # Payroll add/edit dialog
│       └── ReportsPanel.java            # Reports interface
├── classes/                    # Compiled Java classes (created during compilation)
├── setup_database.sql          # 🆕 Enhanced database schema with user management
├── run.bat                     # Batch file to compile and run the application
└── README.md                   # This file
```

## How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Windows operating system (for the batch file)

### Running the Application

1. **Using the Batch File (Recommended)**:
   - Double-click `run.bat`
   - The batch file will compile all Java files and run the application

2. **Manual Compilation and Execution**:
   ```bash
   # Navigate to the project directory
   cd "Payroll Management System GUI"
   
   # Create classes directory
   mkdir classes
   
   # Compile the application
   javac -d classes -cp src src\models\*.java src\managers\*.java src\gui\*.java
   
   # Run the application
   java -cp classes gui.PayrollManagementSystemGUI
   ```

## Default Login Credentials

- **Admin User**: 
  - Username: `admin`
  - Password: `admin123`

- **HR User**:
  - Username: `hr`
  - Password: `hr123`

## User Guide

### Login
1. Enter your username and password
2. Click "Login" or press Enter

### Employee Management
- **Add Employee**: Click "Add Employee" (Admin only)
- **Edit Employee**: Select an employee and click "Edit Employee" (Admin only)
- **Delete Employee**: Select an employee and click "Delete Employee" (Admin only)
- **Search**: Enter keywords and click "Search"

### Payroll Management
- **Create Payroll**: Click "Create Payroll"
- **Edit Payroll**: Select a payroll record and click "Edit Payroll" (Admin only)
- **Delete Payroll**: Select a payroll record and click "Delete Payroll" (Admin only)
- **View by Employee**: Enter Employee ID and click "View by Employee"

### Reports
- **Employee Summary**: Overview of all employees and salary statistics
- **Payroll Summary**: Overview of all payroll records and financial statistics
- **Department Report**: Employee breakdown by department
- **Monthly Report**: Enter month (YYYY-MM format) to view monthly payroll data

## Sample Data

The application comes with sample employee data:
- John Doe (Software Developer, IT Department)
- Jane Smith (HR Manager, HR Department)
- Bob Johnson (Accountant, Finance Department)

## Features by Role

### Admin User
- Full access to all features
- Can add, edit, and delete employees
- Can edit and delete payroll records
- Can view all reports

### HR User
- Can view employee information
- Can create payroll records
- Can view all reports
- Cannot add, edit, or delete employees
- Cannot edit or delete payroll records

## Technical Details

- **Language**: Java
- **GUI Framework**: Swing
- **Architecture**: MVC (Model-View-Controller) pattern
- **Data Storage**: In-memory (HashMap-based storage)
- **Date Handling**: Java 8 LocalDate API

## Future Enhancements

- Database integration (MySQL, PostgreSQL)
- File export functionality (PDF, Excel)
- Email notifications
- Advanced reporting features
- Multi-language support
- Employee photo management

## 🆕 Latest Updates

### Philippine Payroll Calculation Process
- **Standard Four-Step Calculation**: Implements the industry-standard payroll calculation methodology
- **Comprehensive Breakdown**: Detailed calculation summaries showing each step
- **Government Compliance**: Accurate SSS, PhilHealth, Pag-IBIG, and BIR tax calculations
- **Minimum Wage Validation**: Automatic compliance checking with regional minimum wage laws
- **Professional Documentation**: Complete calculation formulas and examples

### Key Features Added:
1. **`calculateStandardPayroll()`** - Main calculation method following Philippine standards
2. **`getPayrollCalculationSummary()`** - Detailed breakdown of all calculations
3. **`isMinimumWageCompliant()`** - Compliance validation with minimum wage laws
4. **`PayrollCalculationResult`** - Comprehensive data structure for all payroll components

## ✅ **Unified Payroll Calculation System**

The Philippine Payroll Management System now uses a **single, standardized payroll calculation method** that follows the industry-standard four-step process:

### **Single Calculation Method**
- **`calculateStandardPayroll()`** - The **only** payroll calculation method in the system
- **Unified Process** - All payroll calculations now use the same standardized approach
- **Consistent Results** - No more duplicate or conflicting calculation methods

### **Previous Cleanup**
- **Removed**: Old `calculatePhilippinePayroll()` method that returned Map<String, Object>
- **Retained**: New `calculateStandardPayroll()` method that returns `PayrollCalculationResult`
- **Updated**: All GUI components now use the unified calculation method

### **Benefits of Unified System**
1. **Consistency** - All payroll calculations follow the same process
2. **Maintainability** - Single method to update for calculation changes
3. **Reliability** - Reduces chances of conflicting results
4. **Compliance** - Ensures all calculations follow Philippine labor standards

### **Integration Points**
- **PhilippinePayrollDialog** - Uses unified calculation method
- **PayslipGenerator** - Compatible with unified calculation results
- **PayrollPanel** - All payroll operations use the same calculation
- **SalaryComponentManager** - Central source for all payroll calculations

### **Testing Verification**
The unified system has been tested and verified to work correctly with:
- Basic salary calculations
- Overtime and premium pay
- Government mandatory deductions
- Other deductions
- 13th month pay
- Minimum wage compliance

**Result**: The payroll management system now has **one unified calculation method** that ensures consistency, compliance, and reliability across all payroll operations.

## ✅ **Final System Optimization**

### **Single Payroll Creation**
- **Removed**: Duplicate "Create Payroll" button
- **Unified**: Now only one "Create Payroll" button that uses the Philippine standard calculation
- **Simplified UI**: Streamlined interface with consistent functionality

### **Philippine Peso Currency (₱)**
- **Updated**: All currency displays now use Philippine peso symbol (₱)
- **Consistent**: Replaced "PHP" with "₱" throughout the system
- **Professional**: Proper currency formatting for Philippine users

### **Benefits**
1. **User-Friendly**: Single, clear payroll creation option
2. **Professional**: Proper Philippine peso currency symbol
3. **Consistent**: All payroll operations use the same method
4. **Compliant**: Follows Philippine payroll standards

The system now provides a clean, professional, and unified payroll management experience specifically designed for Philippine businesses.

## ✅ **UI/UX Improvements**

### **Improved Payroll Panel Layout**
- **Multi-row Button Layout**: Organized buttons into logical groups across three rows
  - **Search Row**: Employee ID search functionality
  - **Action Row**: Main payroll operations (Create, Edit, View Payslip, Delete)
  - **Navigation Row**: Back button for easy navigation
- **Better Accessibility**: All buttons are now easily clickable without UI overflow
- **Professional Spacing**: Improved spacing and padding for better visual appeal

### **Enhanced Currency Display**
- **Table Currency**: All payroll amounts in the table now display with ₱ symbol
- **Consistent Formatting**: Uniform currency formatting across all UI components
- **Philippine Standard**: Proper peso formatting for local users

### **Layout Benefits**
1. **User-Friendly**: Easy access to all buttons without scrolling or overflow
2. **Organized**: Logical grouping of related functions
3. **Professional**: Clean, modern interface design
4. **Responsive**: Better adaptation to different screen sizes

The payroll management interface is now more intuitive and user-friendly for Philippine businesses.
