# Debug Utilities

This directory contains utility classes for debugging and maintenance of the Payroll Management System.

## Available Tools

### 1. `AuthDebug.java`
- **Purpose**: Debug authentication functionality
- **Usage**: `java -cp "lib/*;classes" utils.AuthDebug`
- **Function**: Tests password verification and authentication methods

### 2. `DatabaseDebug.java`
- **Purpose**: Inspect database structure and contents
- **Usage**: `java -cp "lib/*;classes" utils.DatabaseDebug`
- **Function**: Shows database tables, structure, and user data

### 3. `HRAccessTest.java`
- **Purpose**: Test HR user access permissions
- **Usage**: `java -cp "lib/*;classes" utils.HRAccessTest`
- **Function**: Validates HR user roles and permissions

### 4. `HRUserSetup.java`
- **Purpose**: Create and manage HR user accounts
- **Usage**: `java -cp "lib/*;classes" utils.HRUserSetup`
- **Function**: Creates default HR users with secure passwords

### 5. `PasswordReset.java`
- **Purpose**: Reset admin password to default
- **Usage**: `java -cp "lib/*;classes" utils.PasswordReset`
- **Function**: Resets admin password to 'admin123'

## Quick Access

Use the `debug-tools.bat` script in the root directory for interactive access to all debug tools.

## Requirements

- All tools require the application to be compiled first (`run.bat`)
- Database connection must be properly configured
- MySQL server must be running

## Note

Password change functionality testing is available in `src/test/PasswordChangeTest.java`
