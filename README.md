# Payroll Management System GUI

A comprehensive Java Swing-based GUI application for managing employee payroll operations with MySQL database integration.

## Features

- **User Authentication**: Login system with admin and HR roles
- **Employee Management**: Add, edit, delete, and search employees
  - **Employment Details**: Store comprehensive employment information including:
    - Job Title (specific position title)
    - Manager information
    - Department and position
    - Personal details (name, email, phone)
    - Hire date and salary
- **Payroll Management**: Create, edit, and manage payroll records
- **Reports**: Generate various reports including employee summary, payroll summary, department reports, and monthly reports
- **Role-based Access Control**: Admin privileges required for certain operations
- **MySQL Database Integration**: Persistent data storage with MySQL database
- **Automated Database Setup**: Automatic database and table creation

## Prerequisites

- Java 8 or higher
- MySQL Server (5.7 or higher)
- MySQL Workbench (for database management)

## Quick Start

1. **Run the application**:
   ```
   run.bat
   ```

2. **Build only** (without running):
   ```
   build.bat
   ```

3. **Clean build files**:
   ```
   clean.bat
   ```

## Database Setup

1. **Install MySQL Server** and ensure it's running
2. **Configure Database Credentials** in `src/database/DatabaseConfig.java`:
   ```java
   public static final String DB_USERNAME = "root";        // Your MySQL username
   public static final String DB_PASSWORD = "your_password"; // Your MySQL password
   ```
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

## Project Structure

```
Payroll Management System GUI/
├── src/
│   ├── models/
│   │   ├── User.java           # User authentication model
│   │   ├── Employee.java       # Employee data model
│   │   └── Payroll.java        # Payroll data model
│   ├── managers/
│   │   ├── AuthenticationManager.java  # User authentication logic
│   │   ├── EmployeeManager.java        # Employee management logic
│   │   └── PayrollManager.java         # Payroll management logic
│   ├── database/
│   │   ├── DatabaseConnection.java     # MySQL database connection
│   │   ├── DatabaseConfig.java         # Database configuration
│   │   ├── DatabaseDAO.java            # Data access object interface
│   │   └── MySQLDatabaseDAO.java       # MySQL DAO implementation
│   └── gui/
│       ├── PayrollManagementSystemGUI.java  # Main application window
│       ├── LoginPanel.java              # Login interface
│       ├── MainMenuPanel.java           # Main menu interface
│       ├── EmployeePanel.java           # Employee management interface
│       ├── EmployeeDialog.java          # Employee add/edit dialog
│       ├── PayrollPanel.java            # Payroll management interface
│       ├── PayrollDialog.java           # Payroll add/edit dialog
│       └── ReportsPanel.java            # Reports interface
├── classes/                    # Compiled Java classes (created during compilation)
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
