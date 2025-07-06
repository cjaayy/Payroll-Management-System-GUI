# Project Structure

## Directory Layout

```
Payroll Management System GUI/
├── src/                    # Source code
│   ├── models/            # Data models
│   ├── managers/          # Business logic managers
│   ├── gui/               # User interface components
│   └── database/          # Database connection and operations
├── lib/                   # External libraries
│   └── mysql-connector-j-*.jar  # MySQL JDBC driver
├── classes/               # Compiled Java classes (auto-generated)
├── build.bat             # Build project without running
├── clean.bat             # Clean compiled files
├── run.bat               # Build and run application
├── configure-database.bat # Database configuration utility
├── setup_database.sql    # Database schema
├── MYSQL_SETUP.md        # MySQL setup instructions
└── README.md             # Project documentation
```

## Build Scripts

- **run.bat**: Compiles and runs the application
- **build.bat**: Only compiles the project
- **clean.bat**: Removes compiled classes and temporary files
- **configure-database.bat**: Updates database configuration

## Source Code Organization

- **models/**: Employee, User, Payroll, EmployeeDocument classes
- **managers/**: Business logic for authentication, employee management, payroll processing
- **gui/**: Swing GUI components for the application interface
- **database/**: Database connection, configuration, and data access objects

## Notes

- The `classes/` directory is auto-generated during compilation
- Keep the `lib/` directory with the MySQL JDBC driver
- Configuration files are generated as needed
