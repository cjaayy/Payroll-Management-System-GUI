# MySQL JDBC Driver Manual Installation

## Download MySQL JDBC Driver Manually

1. **Visit MySQL Official Site**:
   - Go to: https://dev.mysql.com/downloads/connector/j/
   - Click "Download" for Platform Independent version
   - Choose the ZIP Archive option

2. **Extract and Copy**:
   - Extract the downloaded ZIP file
   - Find the JAR file (e.g., `mysql-connector-j-8.2.0.jar`)
   - Copy it to the `lib/` directory in your project

3. **Alternative Download Sources**:
   - Maven Central: https://repo1.maven.org/maven2/mysql/mysql-connector-java/
   - Choose version 8.0.33 or higher
   - Download the JAR file directly

## Quick Test

After placing the JAR file in the `lib/` directory:

```bash
# Test compilation
javac -cp "lib/*;src" -d classes src/database/*.java src/models/*.java src/managers/*.java src/gui/*.java

# Run the application
java -cp "lib/*;classes" gui.PayrollManagementSystemGUI
```

## Common Issues

1. **Wrong JAR file**: Make sure you downloaded the actual JAR file, not an HTML page
2. **File path**: Ensure the JAR is in the `lib/` directory
3. **MySQL Server**: Make sure MySQL Server is running on localhost:3306
4. **Credentials**: Update username/password in `DatabaseConfig.java`

## MySQL Configuration

Before running the application, make sure to:

1. **Start MySQL Server**
2. **Update Database Credentials** in `src/database/DatabaseConfig.java`:
   ```java
   public static final String DB_USERNAME = "your_username";
   public static final String DB_PASSWORD = "your_password";
   ```

3. **Test Connection** (optional):
   ```bash
   mysql -u your_username -p
   ```

## File Structure After Setup

```
Payroll Management System GUI/
├── lib/
│   └── mysql-connector-java-8.0.33.jar  # MySQL JDBC Driver
├── src/
│   ├── database/
│   │   ├── DatabaseConnection.java
│   │   ├── DatabaseConfig.java
│   │   ├── DatabaseDAO.java
│   │   └── MySQLDatabaseDAO.java
│   └── ... (other source files)
└── classes/  # Compiled .class files
```
